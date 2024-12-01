package com.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.entity.CustomerAddressEntity;
import com.repository.CustomerAddressRepository;

@RestController
@RequestMapping("/api/customer_address")
public class CustomerAddressController {
	
	@Autowired
	CustomerAddressRepository customerAddressRepository;
	
	@GetMapping("/{addressId}")
	public ResponseEntity<?> getAddressById(@PathVariable("addressId") Integer addressId) {
		Optional<CustomerAddressEntity> op = customerAddressRepository.findById(addressId);
		
		if(op.isPresent()) {
			return ResponseEntity.ok(op.get());
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@DeleteMapping("/{addressId}")
	public ResponseEntity<?> deleteAddress(@PathVariable("addressId") Integer addressId) {
		Optional<CustomerAddressEntity> op = customerAddressRepository.findById(addressId);
		
		if(op.isPresent()) {
			customerAddressRepository.deleteById(addressId);
			return ResponseEntity.ok("Success");
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@PutMapping("/{addressId}")
	public ResponseEntity<?> updateAddress(@PathVariable("addressId") Integer addressId, @RequestBody CustomerAddressEntity customerAddressEntity) {
		Optional<CustomerAddressEntity> op = customerAddressRepository.findById(addressId);
		
		if(op.isPresent()) {
			customerAddressRepository.save(customerAddressEntity);
			return ResponseEntity.ok("Success");
		}else {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
	}
}
