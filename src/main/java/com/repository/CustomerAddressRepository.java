package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entity.CustomerAddressEntity;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddressEntity, Integer>{
	
}