package com.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.hibernate.SessionException;
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

import com.dto.CustomerAddressDto;
import com.entity.CustomerAddressEntity;
import com.entity.CustomerEntity;
import com.repository.CustomerAddressRepository;
import com.repository.CustomerRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/customer_address")
public class CustomerAddressController {
	
	@Autowired
	CustomerAddressRepository customerAddressRepository;
	
	@Autowired
	CustomerRepository customerRepository;
	
	@PostMapping
	public ResponseEntity<?> addAddress(@RequestBody CustomerAddressDto customerAddressDto, HttpSession session) {
		try {
			CustomerEntity customer = (CustomerEntity)session.getAttribute("customer");
			
			if(customer == null) {
				throw new SessionException("Session Exception");
			}else {
				CustomerAddressEntity customerAddress = new CustomerAddressEntity();
			
				customerAddress.setTitle(customerAddressDto.getTitle());
				customerAddress.setHouseNo(customerAddressDto.getHouseNo());
				customerAddress.setApartment(customerAddressDto.getApartment());
				customerAddress.setStreet(customerAddressDto.getStreet());
				customerAddress.setLandmark(customerAddressDto.getLandmark());
				customerAddress.setCity(customerAddressDto.getCity());
				customerAddress.setState(customerAddressDto.getState());
				customerAddress.setPincode(customerAddressDto.getPincode());
				customerAddress.setCustomer(customer);
				
				customerAddressRepository.save(customerAddress);
				
				return ResponseEntity.ok("Success");
			}
		}catch(SessionException sessionException) {
			HashMap<String, String> error = new HashMap<>();
			error.put("message", "Customer Id not found! Please Enter Credentials.");
			error.put("exception", sessionException.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
	
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
	public ResponseEntity<?> deleteAddress(@PathVariable("addressId") Integer addressId, HttpSession session) {
		try {
			CustomerEntity customer = (CustomerEntity)session.getAttribute("customer");
			
			if(customer == null) {
				throw new SessionException("Session Exception");
			}else {
				Optional<CustomerAddressEntity> op = customerAddressRepository.findById(addressId);
				
				if(op.isPresent()) {
					customerAddressRepository.deleteById(addressId);
					return ResponseEntity.ok("Success");
				}else {
					HashMap<String, String> error = new HashMap<>();
					error.put("message", "Address Id not Found");
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
				}
			}
		}catch(SessionException sessionException) {
			HashMap<String, String> error = new HashMap<>();
			error.put("message", "Unauthorized Access! Please Enter Credentials.");
			error.put("exception", sessionException.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
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
	
	// read all address
	@GetMapping("/myaddress/{customerId}")
	public ResponseEntity<?> getMyAddress(@PathVariable("customerId") Integer customerId, HttpSession session) {
		try {
			CustomerEntity customer = (CustomerEntity)session.getAttribute("customer");
			Optional<CustomerEntity> op = customerRepository.findById(customerId);
			
			if(customer == null) {
				throw new SessionException("Session Exception");
			}else {
				if(customerId == customer.getCustomerId()) {			
					if(op.isPresent()) {
						List<CustomerAddressEntity> addresses = op.get().getCustomerAddresses();
						return ResponseEntity.ok(addresses);
					}else {
						return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
					}
				}else {
					HashMap<String, Object> error = new HashMap<>();
					error.put("message", "Unauthorized Access! Enter valid Credentials.");
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
				}
			}
		}catch(SessionException sessionException) {
			HashMap<String, String> error = new HashMap<>();
			error.put("message", "Unauthorized Access! Please Enter Credentials.");
			error.put("exception", sessionException.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
}
