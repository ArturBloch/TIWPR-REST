package com.tiwpr.rest.controller;

import com.querydsl.core.types.Predicate;
import com.tiwpr.rest.models.BoardGame;
import com.tiwpr.rest.models.Transaction;
import com.tiwpr.rest.repository.BoardGameRepository;
import com.tiwpr.rest.repository.ClientRepository;
import com.tiwpr.rest.repository.TransactionRepository;
import com.tiwpr.rest.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transaction-history")
public class TransactionHistory {

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private RestService restService;

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private BoardGameRepository boardGameRepository;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	List<Transaction> getTransactionHistory(@RequestParam("page") int page) {
		return restService.getOldTransactions(page);
	}

	@GetMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	Transaction getTransactionHistoryWithId(@PathVariable Integer id) {
		return restService.getOldTransactionWithId(id);
	}
}
