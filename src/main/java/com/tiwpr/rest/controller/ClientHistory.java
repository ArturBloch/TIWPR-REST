package com.tiwpr.rest.controller;

import com.querydsl.core.types.Predicate;
import com.tiwpr.rest.models.Client;
import com.tiwpr.rest.models.Transaction;
import com.tiwpr.rest.repository.ClientRepository;
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
@RequestMapping("/client-history")
public class ClientHistory {

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private RestService restService;

	@GetMapping
	List<Client> getClientHistory(@RequestParam("page") int page) {
		return restService.getOldClients(page);
	}

	@GetMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	Client getClientHistoryWithId(@PathVariable Integer id) {
		return restService.getClientHistoryWithId(id);

	}
}