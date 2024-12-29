package com.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dto.RestaurantAddressDto;
import com.entity.RestaurantAddressEntity;
import com.entity.RestaurantEntity;
import com.repository.RestaurantAddressRepository;
import com.repository.RestaurantRepository;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/restaurant_address")
public class RestaurantAddressController {

	@Autowired
	RestaurantAddressRepository restaurantAddressRepository;
	
	@Autowired
	RestaurantRepository restaurantRepository;
	
	@PostMapping
	public ResponseEntity<?> addAddress(@RequestBody RestaurantAddressDto restaurantAddressDto, HttpServletRequest req) {
		String email = (String) req.getAttribute("email");
		Optional<RestaurantEntity> op = restaurantRepository.findByEmail(email);
		
		if(op.isPresent()) {
			RestaurantEntity restaurant = op.get();
			RestaurantAddressEntity restaurantAddress = new RestaurantAddressEntity();
			
			restaurantAddress.setRestaurantName(restaurantAddressDto.getRestaurantName());
			restaurantAddress.setAddress(restaurantAddressDto.getAddress());
			restaurantAddress.setStreet(restaurantAddressDto.getStreet());
			restaurantAddress.setLandmark(restaurantAddressDto.getLandmark());
			restaurantAddress.setCity(restaurantAddressDto.getCity());
			restaurantAddress.setState(restaurantAddressDto.getState());
			restaurantAddress.setPincode(restaurantAddressDto.getPincode());
			restaurant.setAddress(restaurantAddress);
			
			restaurantAddressRepository.save(restaurantAddress);
			restaurantRepository.save(restaurant);
			
			return ResponseEntity.ok("Success");
		}else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
	}
	
	@GetMapping("/{addressId}")
	public ResponseEntity<?> getAddressById(@PathVariable("addressId") Integer addressId) {
		Optional<RestaurantAddressEntity> op = restaurantAddressRepository.findById(addressId);
		
		if(op.isPresent()) {
			
			return ResponseEntity.ok(op.get());
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@DeleteMapping("/{addressId}")
	public ResponseEntity<?> deleteAddress(@PathVariable("addressId") Integer addressId) {
		Optional<RestaurantAddressEntity> op = restaurantAddressRepository.findById(addressId);
		
		if(op.isPresent()) {
			restaurantAddressRepository.deleteById(addressId);
			return ResponseEntity.ok("Success");
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@PutMapping("/{addressId}")
	public ResponseEntity<?> updateAddress(@PathVariable("addressId") Integer addressId, @RequestBody RestaurantAddressEntity restaurantAddressEntity) {
		Optional<RestaurantAddressEntity> op = restaurantAddressRepository.findById(addressId);
		
		if(op.isPresent()) {
			restaurantAddressRepository.save(restaurantAddressEntity);
			return ResponseEntity.ok("Success");
		}else {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
	}
}
