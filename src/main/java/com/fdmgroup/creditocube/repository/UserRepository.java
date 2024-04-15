package com.fdmgroup.creditocube.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fdmgroup.creditocube.model.User;

public interface UserRepository extends JpaRepository<User, Integer>{

//	@Query("SELECT u FROM User u WHERE u.firstName LIKE :userFirstName")
//	public ArrayList<User> findUserByFirstName(@Param("userFirstName")String userFirstName);
//	@Query("SELECT u FROM User u WHERE u.firstName LIKE :userLastName")
//	public ArrayList<User> findUserByLastName(@Param("userLastName")String userLastName);
//	@Query("SELECT u FROM User u WHERE u.username LIKE :username")
//	public ArrayList<User> findUserByUsername(@Param("username") String username);

}
