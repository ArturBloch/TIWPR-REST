package com.tiwpr.rest.controller;


import com.tiwpr.rest.models.Transaction;
import com.tiwpr.rest.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transaction-history")
public class TransactionHistory {


	@Autowired
	private RestService restService;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Page<Transaction> getTransactionHistory(Pageable page) {
		return restService.getOldTransactions(page);
	}

	@GetMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Transaction getTransactionHistoryWithId(@PathVariable Integer id) {
		return restService.getOldTransactionWithId(id);
	}
}
