package com.tiwpr.rest.repository;

import com.tiwpr.rest.models.BoardGame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardGameRepository extends PagingAndSortingRepository<BoardGame, Integer>, QuerydslPredicateExecutor<BoardGame> {

	Page<BoardGame> findBoardGamesByCategory(String category, Pageable pageNumber);
	List<BoardGame> findByOrderByPriceAsc();

}