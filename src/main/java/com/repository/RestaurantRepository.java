package com.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entity.RestaurantEntity;
import java.util.List;


public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Integer>{
	Optional<RestaurantEntity> findByEmail(String email);
	List<RestaurantEntity> findByIsVegOnly(Integer isVegOnly);
}
