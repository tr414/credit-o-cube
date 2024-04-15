package com.fdmgroup.creditocube.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fdmgroup.creditocube.model.DebitAccount;

@Repository
public interface DebitAccountRepository extends JpaRepository<DebitAccount, Long> {

}
