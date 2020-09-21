package com.tiwpr.rest.assemblers;

import com.tiwpr.rest.controller.BoardGamesController;
import com.tiwpr.rest.controller.TransactionController;
import com.tiwpr.rest.models.BoardGame;
import com.tiwpr.rest.models.Transaction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BoardGameAssembler implements RepresentationModelAssembler<BoardGame, EntityModel<BoardGame>> {
	@Override
	public EntityModel<BoardGame> toModel(BoardGame boardGame) {
		EntityModel<BoardGame> entity = EntityModel.of(boardGame,
													   linkTo(methodOn(BoardGamesController.class).getById(boardGame.getIdBoardGame())).withSelfRel());
		for (Transaction transaction : boardGame.getTransactionList()) {
			entity.add(linkTo(methodOn(TransactionController.class).getById(transaction.getIdTransaction())).withRel(
				"transaction " + transaction.getIdTransaction()));
		}
		return entity;
	}
}