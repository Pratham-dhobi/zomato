package com.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.entity.RestaurantEntity;
import com.repository.RestaurantRepository;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

	@Autowired
	RestaurantRepository restaurantRepository;
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getRestaurantById(@PathVariable("id") Integer id) {
		Optional<RestaurantEntity> op = restaurantRepository.findById(id);
		
		if(op.isPresent()) {
			return ResponseEntity.ok(op.get());
		}else {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
	}
	
	@GetMapping
	public ResponseEntity<List<RestaurantEntity>> getAllRestaurants() {
		return ResponseEntity.ok(restaurantRepository.findAll());
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteRestaurant(@PathVariable("id") Integer id) {
		Optional<RestaurantEntity> op = restaurantRepository.findById(id);
		
		if(op.isPresent()) {
			return ResponseEntity.ok(op.get());
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> updateRestaurat(@PathVariable("id") Integer id, RestaurantEntity restaurantEntity) {
		Optional<RestaurantEntity> op = restaurantRepository.findById(id);
		
		if(op.isPresent()) {
			restaurantRepository.save(restaurantEntity);
			return ResponseEntity.ok("Success");
		}else {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
	}
}
