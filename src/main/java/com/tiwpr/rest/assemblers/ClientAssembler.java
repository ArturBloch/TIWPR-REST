package com.tiwpr.rest.assemblers;

import com.tiwpr.rest.controller.ClientController;
import com.tiwpr.rest.controller.TransactionController;
import com.tiwpr.rest.models.Client;
import com.tiwpr.rest.models.Transaction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ClientAssembler implements RepresentationModelAssembler<Client, EntityModel<Client>> {
	@Override
	public EntityModel<Client> toModel(Client client) {
		EntityModel<Client> entity = EntityModel.of(client,
													linkTo(methodOn(ClientController.class).getById(client.getIdClient())).withSelfRel());
		for (Transaction transaction : client.getTransactionList()) {
			entity.add(linkTo(methodOn(
				TransactionController.class).getById(transaction.getIdTransaction())).withRel(
				"transaction " + transaction.getIdTransaction()));
		}
		return entity;
	}
}