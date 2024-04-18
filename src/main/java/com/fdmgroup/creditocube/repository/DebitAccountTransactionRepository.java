package com.fdmgroup.creditocube.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fdmgroup.creditocube.model.DebitAccountTransaction;

@Repository
public interface DebitAccountTransactionRepository extends JpaRepository<DebitAccountTransaction, Long> {

}
