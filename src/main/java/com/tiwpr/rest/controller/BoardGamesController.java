package com.tiwpr.rest.controller;

import com.querydsl.core.types.Predicate;
import com.tiwpr.rest.assemblers.BoardGameAssembler;
import com.tiwpr.rest.models.BoardGame;
import com.tiwpr.rest.repository.BoardGameRepository;
import com.tiwpr.rest.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/boardgames")
public class BoardGamesController {

	@Autowired
	private BoardGameRepository boardGameRepository;

	@Autowired
	private RestService restService;

	@Autowired
	private BoardGameAssembler boardGameAssembler;

	@GetMapping
	public PagedModel<EntityModel<BoardGame>> findAll(@QuerydslPredicate(root = BoardGame.class) Predicate predicate, Pageable page,
								   PagedResourcesAssembler<BoardGame> pagedResourcesAssembler) {
		Page<BoardGame> bgPage = boardGameRepository.findAll(predicate, page);
		return pagedResourcesAssembler.toModel(bgPage, boardGameAssembler);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EntityModel<BoardGame> addOne() {
		BoardGame newBoardGame = new BoardGame();
		newBoardGame = boardGameRepository.save(newBoardGame);
		return boardGameAssembler.toModel(newBoardGame);
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/{id}")
	public EntityModel<BoardGame> getById(@PathVariable Integer id) {
		Optional<BoardGame> boardGame = boardGameRepository.findById(id);
		if (boardGame.isEmpty()) {
			System.err.println("Board game with this ID does not exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This board game does not exist");
		}
		return boardGameAssembler.toModel(boardGame.get());
	}

	@PutMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public EntityModel<BoardGame> update(@PathVariable Integer id, @RequestBody BoardGame boardGame) {
		Optional<BoardGame> existingBoardGame = boardGameRepository.findById(id);
		if (existingBoardGame.isEmpty()) {
			System.err.println("Board game with this ID does not exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This board game does not exist");
		}
		boardGame.setIdBoardGame(existingBoardGame.get().getIdBoardGame());
		try {
			System.out.println("Trying to update the board game PUT REQUEST");
			System.out.println(boardGame.toString());
			return boardGameAssembler.toModel(boardGameRepository.save(boardGame));
		} catch (ObjectOptimisticLockingFailureException e) {
			System.err.println("Board game optimistic locking ERROR");
			throw new ResponseStatusException(HttpStatus.CONFLICT, "This entity was modified since loaded from database");
		} catch (TransactionSystemException e) {
			System.err.println("Didn't provide all the information for PUT request");
			throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Not enough information for PUT request");
		}
	}

	@PatchMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public EntityModel<BoardGame> patchBoardGame(@PathVariable Integer id, @RequestBody Map<String, Object> changes) {
		System.out.println("PATCH REQUEST BOARD GAME");
		return boardGameAssembler.toModel(restService.patchBoardGame(id, changes));
	}
}
