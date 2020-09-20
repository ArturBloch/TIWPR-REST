package com.tiwpr.rest.repository;
import com.tiwpr.rest.models.Transaction;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Integer>, QuerydslPredicateExecutor<Transaction> {

}