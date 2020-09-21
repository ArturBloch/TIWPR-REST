package com.tiwpr.rest.repository;

import com.tiwpr.rest.models.BoardGame;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardGameRepository extends PagingAndSortingRepository<BoardGame, Integer>, QuerydslPredicateExecutor<BoardGame> {

}