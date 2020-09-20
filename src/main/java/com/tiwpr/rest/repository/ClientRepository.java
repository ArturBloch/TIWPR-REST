package com.tiwpr.rest.repository;

import com.tiwpr.rest.models.BoardGame;
import com.tiwpr.rest.models.Client;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends PagingAndSortingRepository<Client, Integer>, QuerydslPredicateExecutor<Client> {

}