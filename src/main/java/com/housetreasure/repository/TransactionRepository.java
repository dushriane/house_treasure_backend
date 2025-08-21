package com.housetreasure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.housetreasure.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
