package com.tiwpr.rest.controller;

import com.querydsl.core.types.Predicate;
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
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/clients")
public class ClientController {

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@GetMapping
	Page<Client> findAll(@QuerydslPredicate(root = Client.class) Predicate predicate, Pageable page) {
		return clientRepository.findAll(predicate, page);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	Client addClient() {
		Client newClient = new Client();
		newClient = clientRepository.save(newClient);
		return newClient;
	}

	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	Client getById(@PathVariable Integer id) {
		Optional<Client> findClient = clientRepository.findById(id);
		if (findClient.isEmpty()) {
			System.err.println("Client with this ID does not exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This client does not exist");
		}
		return findClient.get();
	}

	@DeleteMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable Integer id) {
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
	Client update(@PathVariable Integer id, @RequestBody Client client) {
		if (clientRepository.findById(id).isEmpty()) {
			System.err.println("Client with this ID does not exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This client does not exist");
		}
		client.setIdClient(id);
		try {
			System.out.println("Trying to update the client PUT REQUEST");
			return clientRepository.save(client);
		} catch (ObjectOptimisticLockingFailureException e) {
			System.err.println("Client optimistic locking ERROR");
			throw new ResponseStatusException(HttpStatus.CONFLICT, "This client entity was modified since loaded from database");
		}
	}

	@Transactional
	@PutMapping(value = "/{id}/returnAllGames")
	@ResponseStatus(HttpStatus.OK)
	Client returnGames(@PathVariable Integer id) {
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
}