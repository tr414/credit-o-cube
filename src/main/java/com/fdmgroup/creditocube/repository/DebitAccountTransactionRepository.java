package com.fdmgroup.creditocube.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fdmgroup.creditocube.model.DebitAccountTransaction;

@Repository
public interface DebitAccountTransactionRepository extends JpaRepository<DebitAccountTransaction, Long> {

	@Query("SELECT d FROM DebitAccountTransaction d WHERE d.toAccount.accountId = :account_Id")
	public List<DebitAccountTransaction> findByToAccount(@Param("account_Id") long accountId);

	@Query("SELECT d FROM DebitAccountTransaction d WHERE d.fromAccount.accountId = :account_Id")
	public List<DebitAccountTransaction> findByFromAccount(@Param("account_Id") long accountId);

}
