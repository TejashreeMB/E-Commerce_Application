package com.retail.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.retail.model.Seller;

public interface SellerRepository extends JpaRepository<Seller, Integer>{

}
