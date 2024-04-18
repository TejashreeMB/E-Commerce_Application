package com.retail.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.retail.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

}
