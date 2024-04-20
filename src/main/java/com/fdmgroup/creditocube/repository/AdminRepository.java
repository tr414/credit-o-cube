package com.fdmgroup.creditocube.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.fdmgroup.creditocube.model.User;

public interface AdminRepository extends JpaRepository<User, Integer>{

	Optional<User> findByUsername(String username);
}
