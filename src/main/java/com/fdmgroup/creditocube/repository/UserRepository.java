package com.fdmgroup.creditocube.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fdmgroup.creditocube.model.User;

public interface UserRepository extends JpaRepository<User, Integer>{


}
