package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entity.CartItemEntity;
import com.entity.CartEntity;
import java.util.List;



public interface CartItemRepository extends JpaRepository<CartItemEntity, Integer>{
	void deleteByCartEntity(CartEntity cart);
	List<CartItemEntity> findByCartEntity(CartEntity cartEntity);
}
