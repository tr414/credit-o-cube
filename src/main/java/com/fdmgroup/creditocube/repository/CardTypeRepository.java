package com.fdmgroup.creditocube.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fdmgroup.creditocube.model.CardType;

@Repository
public interface CardTypeRepository extends JpaRepository<CardType, Long> {

	long count();

	@Query("SELECT c FROM CardType c WHERE c.name LIKE :name")
	public Optional<CardType> findByCardTypeByName(String name);
}
