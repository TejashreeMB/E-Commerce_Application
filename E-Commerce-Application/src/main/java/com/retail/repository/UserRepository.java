package com.retail.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.retail.model.User;
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	boolean existsByEmail(String email);

}
