package com.tiwpr.rest.controller;

import com.querydsl.core.types.Predicate;
import com.tiwpr.rest.models.BoardGame;
import com.tiwpr.rest.repository.BoardGameRepository;
import com.tiwpr.rest.service.RestService;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/boardgames")
public class BoardGamesController {

	@Autowired
	private BoardGameRepository boardGameRepository;

	@Autowired
	private RestService restService;

	@GetMapping
	List<BoardGame> findAll(@QuerydslPredicate(root = BoardGame.class) Predicate predicate, Pageable page) {
		return boardGameRepository.findAll(predicate, page).getContent();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	BoardGame addOne() {
		BoardGame newBoardGame = new BoardGame();
		newBoardGame = boardGameRepository.save(newBoardGame);
		return newBoardGame;
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping("/{id}")
	BoardGame getById(@PathVariable Integer id) {
		Optional<BoardGame> boardGame = boardGameRepository.findById(id);
		if (boardGame.isEmpty()) {
			System.err.println("Board game with this ID does not exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This board game does not exist");
		}
		return boardGame.get();
	}

//	@DeleteMapping(value = "/{id}")
//	@ResponseStatus(HttpStatus.OK)
//	public void delete(@PathVariable Integer id) {
//		try {
//			boardGameRepository.deleteById(id);
//		} catch (EmptyResultDataAccessException e) {
//			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This board game does not exist so cannot remove it");
//		} catch (DataIntegrityViolationException e) {
//			throw new ResponseStatusException(HttpStatus.CONFLICT, "You need to close all the transactions connected with this board " +
//				"game to " +
//				"proceed");
//		}
//	}

	@PutMapping(value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	BoardGame update(@PathVariable Integer id, @RequestBody BoardGame boardGame) {
		Optional<BoardGame> existingBoardGame = boardGameRepository.findById(id);
		if (existingBoardGame.isEmpty()) {
			System.err.println("Board game with this ID does not exist");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This board game does not exist");
		}
		boardGame.setIdBoardGame(existingBoardGame.get().getIdBoardGame());
		try {
			System.out.println("Trying to update the board game PUT REQUEST");
			System.out.println(boardGame.toString());
			return boardGameRepository.save(boardGame);
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
	BoardGame patchBoardGame(@PathVariable Integer id, @RequestBody Map<String, Object> changes) {
		System.out.println("PATCH REQUEST BOARD GAME");
		return restService.patchBoardGame(id, changes);
	}
}
