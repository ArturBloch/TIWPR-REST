package com.tiwpr.rest.assemblers;

import com.tiwpr.rest.controller.BoardGamesController;
import com.tiwpr.rest.controller.ClientController;
import com.tiwpr.rest.controller.TransactionController;
import com.tiwpr.rest.models.Transaction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TransactionAssembler implements RepresentationModelAssembler<Transaction, EntityModel<Transaction>> {
	@Override
	public EntityModel<Transaction> toModel(Transaction transaction) {
		EntityModel<Transaction> transactionEntityModel = EntityModel.of(transaction,
																		 linkTo(methodOn(TransactionController.class).getById(
																			 transaction.getIdTransaction())).withSelfRel());
		if(transaction.getClient() != null){
			transactionEntityModel.add(linkTo(methodOn(ClientController.class).getById(transaction.getClient().getIdClient())).withRel(
				"client"));
		}
		if(transaction.getBoardGame() != null){
			transactionEntityModel.add(linkTo(methodOn(BoardGamesController.class).getById(transaction.getBoardGame().getIdBoardGame())).withRel(
				"board_game"));
		}
		return transactionEntityModel;
	}
}