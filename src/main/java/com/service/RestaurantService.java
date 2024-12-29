package com.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.entity.RestaurantEntity;
import com.repository.RestaurantRepository;

@Service
public class RestaurantService {

	@Autowired
	RestaurantRepository restaurantRepository;
	
	public HttpStatus softDeleteRestaurant(Integer restaurantId) {
		Optional<RestaurantEntity> op = restaurantRepository.findById(restaurantId);
		
		if(op.isPresent()) {
			RestaurantEntity restaurant = op.get();
			restaurant.setActive(0);
			restaurantRepository.save(restaurant);
			return HttpStatus.OK;
		}else {
			return HttpStatus.NOT_FOUND;
		}
	}
}
