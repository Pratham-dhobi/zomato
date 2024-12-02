package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entity.RestaurantAddressEntity;

public interface RestaurantAddressRepository extends JpaRepository<RestaurantAddressEntity, Integer>{

}
