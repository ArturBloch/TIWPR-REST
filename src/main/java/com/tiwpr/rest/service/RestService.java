package com.tiwpr.rest.service;

import com.tiwpr.rest.models.BoardGame;
import com.tiwpr.rest.models.Client;
import com.tiwpr.rest.models.Transaction;
import com.tiwpr.rest.repository.BoardGameRepository;
import com.tiwpr.rest.repository.ClientRepository;
import com.tiwpr.rest.repository.TransactionRepository;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RestService {

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private BoardGameRepository boardGameRepository;

	@Autowired
	private EntityManager entityManager;

	public Transaction saveTransaction(Transaction oldTransaction, Transaction newTransaction) {
		if (!newTransaction.isLendTransaction() && !newTransaction.isSellTransaction()) {
			throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Wrong type of transaction");
		}
		newTransaction.setIdTransaction(oldTransaction.getIdTransaction());
		BoardGame boardgame = newTransaction.getBoardGame();
		Client client = newTransaction.getClient();
		Optional<BoardGame> dbBoardGame = boardGameRepository.findById(boardgame.getIdBoardGame());
		Optional<Client> dbClient = clientRepository.findById(client.getIdClient());
		if (dbBoardGame.isPresent()) {
			System.out.println(dbBoardGame);
			newTransaction.setBoardGame(dbBoardGame.get());
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This board game was not found in the database");
		}
		if (dbClient.isPresent()) {
			newTransaction.setClient(dbClient.get());
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This client was not found in the database");
		}
		if (oldTransaction.getBoardGame() != null && (oldTransaction.getBoardGame().getIdBoardGame() != newTransaction.getBoardGame()
																													  .getIdBoardGame())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "You can't change the board game, create a new transaction if you want" +
				" to change this");
		}
		int oldAmount = oldTransaction.getAmount() == null ? 0 : oldTransaction.getAmount();
		int newAmount = newTransaction.getAmount() == null ? 0 : newTransaction.getAmount();
		if (oldTransaction.isLendTransaction() && newTransaction.isSellTransaction()) {
			if (!newTransaction.getBoardGame().sell(newTransaction.getAmount())) {
				throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Not enough copies to sell");
			}
			newTransaction.getBoardGame().setCopiesToLend(newTransaction.getBoardGame().getCopiesToLend() + oldTransaction.getAmount());
		} else if (oldTransaction.isSellTransaction() && newTransaction.isLendTransaction()) {
			if (!newTransaction.getBoardGame().lend(newTransaction.getAmount())) {
				throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Not enough copies to lend");
			}
			newTransaction.getBoardGame().setCopiesToSell(newTransaction.getBoardGame().getCopiesToSell() + oldTransaction.getAmount());
		} else if (newTransaction.isSellTransaction()) {
			if (oldAmount > newAmount) {
				newTransaction.getBoardGame().setCopiesToSell(newTransaction.getBoardGame().getCopiesToSell() + (oldAmount - newAmount));
			} else if (newAmount > oldAmount) {
				if (!newTransaction.getBoardGame().sell(newAmount - oldAmount)) {
					throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Not enough copies to sell");
				}
			}
		} else if (newTransaction.isLendTransaction()) {
			if (oldAmount > newAmount) {
				newTransaction.getBoardGame().setCopiesToLend(newTransaction.getBoardGame().getCopiesToLend() + (oldAmount - newAmount));
			} else if (newAmount > oldAmount) {
				if (!newTransaction.getBoardGame().lend(newAmount - oldAmount)) {
					throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Not enough copies to lend");
				}
			}
		} else {
			throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Transaction type is not allowed");
		}
		return transactionRepository.save(newTransaction);
	}

	public BoardGame patchBoardGame(int id, Map<String, Object> changes, Integer etag) {
		Optional<BoardGame> boardGame = boardGameRepository.findById(id);
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		if (boardGame.isPresent()) {
			BoardGame patchedBoardGame = new BoardGame(boardGame.get());
			patchedBoardGame.setIdBoardGame(id);
			patchedBoardGame.setVersion(etag);
			changes.forEach(
				(change, value) -> {
					switch (change) {
						case "boardGameName":
							patchedBoardGame.setBoardgameName((String) value);
							break;
						case "communityRating":
							if (value != null) {
								patchedBoardGame.setCommunityRating(Double.valueOf(value.toString()));
							}
							break;
						case "author":
							patchedBoardGame.setAuthor((String) value);
							break;
						case "category":
							patchedBoardGame.setCategory((String) value);
							break;
						case "dateOfPremiere":
							if (value != null) {
								patchedBoardGame.setDateOfPremiere(LocalDate.parse(value.toString(), formatter));
							}
							break;
						case "copiesToSell":
							patchedBoardGame.setCopiesToSell((Integer) value);
							break;
						case "copiesToLend":
							patchedBoardGame.setCopiesToLend((Integer) value);
							break;
					}
				});
			try {
				return boardGameRepository.save(patchedBoardGame);
			} catch (ObjectOptimisticLockingFailureException e) {
				System.err.println("BoardGame optimistic locking ERROR");
				throw new ResponseStatusException(HttpStatus.CONFLICT, "This board game entity was modified since loaded from database");
			}
		} else {
			System.err.println("Board game with this ID does not exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This board game does not exist");
		}
	}

	public Page<Transaction> getOldTransactions(Pageable page) {
		AuditReader reader = AuditReaderFactory.get(entityManager);
		List<Transaction> transactionList = (List<Transaction>) reader.createQuery()
																	  .forRevisionsOfEntity(Transaction.class, true, true)
																	  .add(AuditEntity.revisionNumber()
																					  .maximize()
																					  .computeAggregationInInstanceContext())
																	  .add(AuditEntity.revisionType().eq(RevisionType.DEL))
																	  .addOrder(AuditEntity.revisionNumber().desc())
																	  .getResultList();
		return new PageImpl<>(transactionList, page, transactionList.size());
	}

	public Transaction getOldTransactionWithId(int transactionId) {
		AuditReader reader = AuditReaderFactory.get(entityManager);
		try {
			return (Transaction) reader.createQuery()
									   .forRevisionsOfEntity(Transaction.class, true, true)
									   .add(AuditEntity.revisionNumber().maximize().computeAggregationInInstanceContext())
									   .add(AuditEntity.revisionType().eq(RevisionType.DEL))
									   .add(AuditEntity.id().eq(transactionId))
									   .getSingleResult();
		} catch (NoResultException e) {
			System.err.println("Transaction with this ID does not exist in history");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This transaction does not exist");
		}
	}

	@SuppressWarnings("unchecked")
	public Page<Client> getOldClients(Pageable page) {
		AuditReader reader = AuditReaderFactory.get(entityManager);
		List<Client> clientList = (List<Client>) reader.createQuery()
													   .forRevisionsOfEntity(Client.class, true, true)
													   .add(AuditEntity.revisionNumber().maximize().computeAggregationInInstanceContext())
													   .add(AuditEntity.revisionType().eq(RevisionType.DEL))
													   .addOrder(AuditEntity.revisionNumber().desc())
													   .getResultList();
		return new PageImpl<>(clientList, page, clientList.size());
	}

	public Client getClientHistoryWithId(int clientId) {
		AuditReader reader = AuditReaderFactory.get(entityManager);
		try {
			return (Client) reader.createQuery()
								  .forRevisionsOfEntity(Client.class, true, true)
								  .add(AuditEntity.revisionNumber().maximize().computeAggregationInInstanceContext())
								  .add(AuditEntity.revisionType().eq(RevisionType.DEL))
								  .add(AuditEntity.id().eq(clientId))
								  .getSingleResult();
		} catch (NoResultException e) {
			System.err.println("Client with this ID does not exist in history");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This client does not exist in client history");
		}
	}
}
