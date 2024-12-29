package com.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entity.MenuEntity;
import com.entity.RestaurantEntity;

public interface MenuRepository extends JpaRepository<MenuEntity, Integer> {
	List<MenuEntity> findByRestaurant(RestaurantEntity restaurant);
}
