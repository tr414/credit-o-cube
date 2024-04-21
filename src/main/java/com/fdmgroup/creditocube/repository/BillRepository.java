package com.fdmgroup.creditocube.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fdmgroup.creditocube.model.Bill;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

}
