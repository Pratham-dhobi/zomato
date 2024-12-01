package com.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dto.LoginDto;
import com.entity.CustomerEntity;
import com.entity.RestaurantEntity;
import com.repository.CustomerRepository;
import com.repository.RestaurantRepository;

@RestController
@RequestMapping("/api")
public class SessionController {

	@Autowired
	BCryptPasswordEncoder encoder;
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	RestaurantRepository restaurantRepository;
	
	@PostMapping("/customer")
	public ResponseEntity<String> addCustomer(@RequestBody CustomerEntity customerEntity) {
		customerEntity.setPassword(encoder.encode(customerEntity.getPassword()));
		customerRepository.save(customerEntity);
		return ResponseEntity.ok("Success");
	}
	
	@PostMapping("/restaurant")
	public ResponseEntity<String> addRestEntity(@RequestBody RestaurantEntity restaurantEntity) {
		restaurantEntity.setPassword(encoder.encode(restaurantEntity.getPassword()));
		restaurantRepository.save(restaurantEntity);
		return ResponseEntity.ok("Success");
	}
	
	@PostMapping("/authenticate")
	public ResponseEntity<?> authenticate(@RequestBody LoginDto loginDto) {
		String role = loginDto.getRole().toLowerCase();
		String password = loginDto.getPassword();
		String email = loginDto.getEmail();
		String encPwd;
		
		switch(role) {
			case "customer":
				Optional<CustomerEntity> op = customerRepository.findByEmail(email);
				if(op.isPresent()) {
					CustomerEntity customer = op.get();
					encPwd = customer.getPassword();
					if(encoder.matches(password, encPwd) == true && email.equals(customer.getEmail())) {
						return ResponseEntity.ok("Success");
					}else {
						Map<String, Object> error = new HashMap<>();
						error.put("message", "Invalid Credentials for Customer.");
						return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
					}					
				}else {
					Map<String, Object> error = new HashMap<>();
					error.put("message", "Invalid Credentials for Customer.");
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
				}
			case "restaurant":
				Optional<RestaurantEntity> op1 = restaurantRepository.findByEmail(email);
				
				if(op1.isPresent()) {
					RestaurantEntity restaurant = op1.get();
					encPwd = restaurant.getPassword();
					if(encoder.matches(password, encPwd) == true && email.equals(restaurant.getEmail())) {
						return ResponseEntity.ok("Success");
					}else {
						Map<String, Object> error = new HashMap<>();
						error.put("message", "Invalid Credentials for Restaurant.");
						return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
					}
				}else {
					Map<String, Object> error = new HashMap<>();
					error.put("message", "Invalid Credentials for Restaurant.");
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
				}
			default:
				Map<String, Object> error = new HashMap<>();
				error.put("message", "Invalid Role");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		}
	}
}
