package com.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dto.LoginDto;
import com.entity.CustomerEntity;
import com.entity.RestaurantEntity;
import com.repository.CustomerRepository;
import com.repository.RestaurantRepository;
import com.service.EmailService;
import com.service.OtpService;

@RestController
@RequestMapping("/api")
public class SessionController {

	@Autowired
	BCryptPasswordEncoder encoder;
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	RestaurantRepository restaurantRepository;
	
	@Autowired
	OtpService otpService;
	
	@Autowired
	EmailService emailService;
	
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
	
	@PostMapping("/sendotp")
	public ResponseEntity<?> sendOtp(@RequestBody LoginDto loginDto) {
		String email = loginDto.getEmail();
		
		if(loginDto.getRole().toLowerCase().equals("customer")) {
			Optional<CustomerEntity> op = customerRepository.findByEmail(email);
			
			if(op.isPresent()) {
				CustomerEntity customer = op.get();
				String otp = otpService.generateOtp();
				customer.setOtp(otp);
				emailService.sendEmail(email, otp);
				customerRepository.save(customer);
				return ResponseEntity.status(HttpStatus.OK).build();
			}else {
				HashMap<String, String> error = new HashMap<>();
				error.put("message", "Email Id not Found.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
			}
		}else if(loginDto.getRole().toLowerCase().equals("restaurant")) {
			Optional<RestaurantEntity> op1 = restaurantRepository.findByEmail(email);
			
			if(op1.isPresent()) {
				RestaurantEntity restaurant = op1.get();
				String otp = otpService.generateOtp();
				restaurant.setOtp(otp);
				emailService.sendEmail(email, otp);
				restaurantRepository.save(restaurant);
				return ResponseEntity.status(HttpStatus.OK).build();
			}else {
				HashMap<String, String> error = new HashMap<>();
				error.put("message", "Email Id not Found.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
			}
		}else {
			HashMap<String, String> error = new HashMap<>();
			error.put("message", "Invalid Role.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		}
	}
	
	@PostMapping("/updatepassword")
	public ResponseEntity<?> updatePassword(@RequestBody LoginDto loginDto) {
		String email = loginDto.getEmail();
		String otp = loginDto.getOtp();
		String password = loginDto.getPassword();
		
		if(loginDto.getRole().toLowerCase().equals("customer")) {
			Optional<CustomerEntity> op = customerRepository.findByEmail(email);
			
			if(op.isPresent()) {
				CustomerEntity customer = op.get();
				if(customer.getOtp().equals(otp)) {
					customer.setOtp("");
					customer.setPassword(encoder.encode(password));
					customerRepository.save(customer);
				}else {
					HashMap<String, String> error = new HashMap<>();
					error.put("message", "OTP Unmatched");
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
				}
			}else {
				HashMap<String, String> error = new HashMap<>();
				error.put("message", "Email Id not Found.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
			}
		}else if(loginDto.getRole().toLowerCase().equals("restaurant")) {
			Optional<RestaurantEntity> op = restaurantRepository.findByEmail(email);
			
			if(op.isPresent()) {
				RestaurantEntity restaurant = op.get();
				if(restaurant.getOtp().equals(otp)) {
					restaurant.setOtp("");
					restaurant.setPassword(encoder.encode(password));
					restaurantRepository.save(restaurant);
				}else {
					HashMap<String, String> error = new HashMap<>();
					error.put("message", "OTP Unmatched");
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
				}
			}else {
				HashMap<String, String> error = new HashMap<>();
				error.put("message", "Email Id not Found.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
			}
		}else {
			HashMap<String, String> error = new HashMap<>();
			error.put("message", "Invalid Role.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		}
		
		return ResponseEntity.ok("");
	}
}
