package com.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dto.LoginDto;
import com.entity.CustomerEntity;
import com.repository.CustomerRepository;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
	
	@Autowired
	BCryptPasswordEncoder encoder;
	
	@Autowired
	CustomerRepository customerRepository;
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getCustomerById(@PathVariable("id") Integer id) {
		Optional<CustomerEntity> op = customerRepository.findById(id);
		
		if(op.isPresent()) {
			return ResponseEntity.ok(op.get());
		}else {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
	}
	
	@GetMapping
	public ResponseEntity<List<CustomerEntity>> getAllCustomer() {
		return ResponseEntity.ok(customerRepository.findAll());
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteCustomerById(@PathVariable("id") Integer id) {
		Optional<CustomerEntity> op = customerRepository.findById(id);
		
		if(op.isPresent()) {
			customerRepository.deleteById(id);
			return ResponseEntity.ok("Success");
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> updateCustomer(@PathVariable("id") Integer id,@RequestBody CustomerEntity customerEntity) {
		Optional<CustomerEntity> op = customerRepository.findById(id);
		
		if(op.isPresent()) {
			customerEntity.setPassword(encoder.encode(customerEntity.getPassword()));
			customerRepository.save(customerEntity);
			return ResponseEntity.ok("Success");
		}else {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
	}
}
