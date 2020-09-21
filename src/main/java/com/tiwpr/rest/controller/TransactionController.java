package com.tiwpr.rest.controller;

import com.querydsl.core.types.Predicate;
import com.tiwpr.rest.assemblers.TransactionAssembler;
import com.tiwpr.rest.models.BoardGame;
import com.tiwpr.rest.models.Transaction;
import com.tiwpr.rest.repository.BoardGameRepository;
import com.tiwpr.rest.repository.ClientRepository;
import com.tiwpr.rest.repository.TransactionRepository;
import com.tiwpr.rest.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private RestService restService;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private BoardGameRepository boardGameRepository;

	@Autowired
	private TransactionAssembler transactionAssembler;

	@GetMapping
	public PagedModel<EntityModel<Transaction>> findAll(@QuerydslPredicate(root = Transaction.class) Predicate predicate, Pageable page,
														PagedResourcesAssembler<Transaction> pagedResourcesAssembler) {
		Page<Transaction> transactionPage = transactionRepository.findAll(predicate, page);
		return pagedResourcesAssembler.toModel(transactionPage, transactionAssembler);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EntityModel<Transaction> addOne() {
		Transaction emptyTransaction = new Transaction();
		emptyTransaction = transactionRepository.save(emptyTransaction);
		return transactionAssembler.toModel(transactionRepository.save(emptyTransaction));
	}

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public EntityModel<Transaction> getById(@PathVariable Integer id) {
		Optional<Transaction> findTransaction = transactionRepository.findById(id);
		if (findTransaction.isEmpty()) {
			System.err.println("Transaction with this ID does not exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This transaction does not exist");
		}
		return transactionAssembler.toModel(findTransaction.get());
	}

	@DeleteMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable Integer id) {
		try {
			Optional<Transaction> transaction = transactionRepository.findById(id);
			transactionRepository.deleteById(id);
			Optional<BoardGame> boardGame = boardGameRepository.findById(transaction.get().getBoardGame().getIdBoardGame());
			if (boardGame.isPresent()) {
				if (transaction.get().isLendTransaction()) {
					boardGame.get().setCopiesToLend(boardGame.get().getCopiesToLend() + transaction.get().getAmount());
					boardGameRepository.save(boardGame.get());
				}
			}
		} catch (EmptyResultDataAccessException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This transaction does not exist so cannot remove it");
		}
	}

	@PutMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public EntityModel<Transaction> update(@PathVariable Integer id, @RequestBody Transaction newTransaction) {
		Optional<Transaction> oldTransaction = transactionRepository.findById(id);
		if (oldTransaction.isEmpty()) {
			System.err.println("Transaction with this ID does not exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This transaction does not exist");
		} else {
			try {
				return transactionAssembler.toModel(restService.saveTransaction(oldTransaction.get(), newTransaction));
			} catch (ObjectOptimisticLockingFailureException e) {
				System.err.println("Transaction optimistic locking ERROR");
				throw new ResponseStatusException(HttpStatus.CONFLICT, "This transaction entity was modified since loaded from database");
			}
		}
	}
}
