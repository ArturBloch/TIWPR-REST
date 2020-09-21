package com.tiwpr.rest.controller;

import com.tiwpr.rest.models.Client;
import com.tiwpr.rest.repository.ClientRepository;
import com.tiwpr.rest.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client-history")
public class ClientHistory {

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private RestService restService;

	@GetMapping
	public Page<Client> getClientHistory(Pageable page) {
		return restService.getOldClients(page);
	}

	@GetMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Client getClientHistoryWithId(@PathVariable Integer id) {
		return restService.getClientHistoryWithId(id);

	}
}