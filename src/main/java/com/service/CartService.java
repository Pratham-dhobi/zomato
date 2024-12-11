package com.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entity.CartEntity;
import com.entity.CartItemEntity;
import com.repository.CartItemRepository;
import com.repository.CartRepository;

@Service
public class CartService {

	@Autowired
	CartRepository cartRepository;
	
	@Autowired
	CartItemRepository cartItemRepository;
	
	public int softDeleteCartService(Integer cartId) { 
		Optional<CartEntity> op = cartRepository.findById(cartId);
		
		if(op.isPresent()) {
			CartEntity cart = op.get();
			cart.setIsActive(0);
			cartRepository.save(cart);
			return 200;
		}else {
			return 400;
		}
	}

	public int softDeleteCartItemService(Integer cartId) {
		Optional<CartEntity> op = cartRepository.findById(cartId);
		
		if(op.isPresent()) {
			List<CartItemEntity> items = cartItemRepository.findByCartEntity(op.get());
			
			if(items.isEmpty()) {
				return 400;
			}else {
				items.stream().forEach(item -> item.setIsActive(0));
				items.stream().forEach(item -> cartItemRepository.save(item));
				
				return 200;
			}	
		}else {
			return 204;
		}
	}
}
