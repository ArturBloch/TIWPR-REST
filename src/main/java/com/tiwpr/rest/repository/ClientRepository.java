package com.tiwpr.rest.repository;

import com.tiwpr.rest.models.Client;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends PagingAndSortingRepository<Client, Integer>, QuerydslPredicateExecutor<Client> {

}