package com.tiwpr.rest.controller;

import com.querydsl.core.types.Predicate;
import com.tiwpr.rest.assemblers.ClientAssembler;
import com.tiwpr.rest.assemblers.TransactionAssembler;
import com.tiwpr.rest.models.BoardGame;
import com.tiwpr.rest.models.Client;
import com.tiwpr.rest.models.Transaction;
import com.tiwpr.rest.repository.ClientRepository;
import com.tiwpr.rest.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clients")
public class ClientController {

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private ClientAssembler clientAssembler;

	@Autowired
	private TransactionAssembler transactionAssembler;

	@GetMapping
	public PagedModel<EntityModel<Client>> findAll(@QuerydslPredicate(root = Client.class) Predicate predicate, Pageable page,
												   PagedResourcesAssembler<Client> pagedResourcesAssembler) {
		Page<Client> clientPage = clientRepository.findAll(predicate, page);
		return pagedResourcesAssembler.toModel(clientPage, clientAssembler);
	}

	@PostMapping
	public ResponseEntity<EntityModel<Client>> addClient() {
		Client newClient = new Client();
		newClient = clientRepository.save(newClient);
		EntityModel<Client> clientEntity = clientAssembler.toModel(newClient);
		return ResponseEntity.created(clientEntity.getLink("self").get().toUri()).eTag(Long.toString(newClient.getVersion())).body(clientEntity);
	}

	@GetMapping("/{id}")
	public ResponseEntity<EntityModel<Client>> getById(@PathVariable Integer id) {
		Optional<Client> findClient = clientRepository.findById(id);
		if (findClient.isEmpty()) {
			System.err.println("Client with this ID does not exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This client does not exist");
		}
		EntityModel<Client> clientEntity = clientAssembler.toModel(findClient.get());
		return ResponseEntity.ok().eTag(Long.toString(findClient.get().getVersion())).body(clientEntity);
	}

	@DeleteMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable Integer id, @RequestHeader("Etag") Integer etag) {
		Optional<Client> client = clientRepository.findById(etag);
		if(client.isEmpty()){
			System.err.println("Client with this ID does not exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This client does not exist");
		}
		try {
			clientRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This client does not exist so cannot remove it");
		} catch (DataIntegrityViolationException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "You need to close all the transactions of this client to proceed");
		}
	}

	@PutMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<EntityModel<Client>> update(@PathVariable Integer id, @RequestBody Client client, @RequestHeader("Etag") Integer etag) {
		Optional<Client> clientDB = clientRepository.findById(id);
		if (clientDB.isEmpty()) {
			System.err.println("Client with this ID does not exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This client does not exist");
		}
		client.setIdClient(id);
		client.setVersion(etag);
		try {
			System.out.println("Trying to update the client PUT REQUEST");
			clientRepository.save(client);
			EntityModel<Client> clientEntity = clientAssembler.toModel(client);
			return ResponseEntity.ok().eTag(Long.toString(client.getVersion())).body(clientEntity);
		} catch (ObjectOptimisticLockingFailureException e) {
			System.err.println("Client optimistic locking ERROR");
			throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "This client entity was modified since loaded from database");
		}
	}

	@Transactional
	@DeleteMapping(value = "/{id}/borrowTransactions")
	@ResponseStatus(HttpStatus.OK)
	public Client returnGames(@PathVariable Integer id) {
		Optional<Client> client = clientRepository.findById(id);
		if (client.isPresent()) {
			List<Transaction> transactionList = client.get().getTransactionList();
			for (Transaction transaction : transactionList) {
				if (transaction.isLendTransaction())
					transactionRepository.deleteById(transaction.getIdTransaction());
			}
		} else {
			System.err.println("Client with this ID does not exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This client does not exist");
		}
		return client.get();
	}

	@GetMapping(value = "/{id}/transactions")
	@ResponseStatus(HttpStatus.OK)
	public CollectionModel<EntityModel<Transaction>> getAllTransactions(@PathVariable Integer id) {
		Optional<Client> client = clientRepository.findById(id);
		if (client.isPresent()) {
			return CollectionModel.of(client.get().getTransactionList().stream().map(transactionAssembler::toModel)
											.collect(Collectors.toList()));
		} else {
			System.err.println("Client with this ID does not exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This client does not exist");
		}
	}
}