package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entity.CustomerAddressEntity;
import com.entity.CustomerEntity;

import java.util.Optional;


public interface CustomerAddressRepository extends JpaRepository<CustomerAddressEntity, Integer>{
	Optional<CustomerAddressEntity> findByCustomer(CustomerEntity customer);
}