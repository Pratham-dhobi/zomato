package com.controller;

import java.util.HashMap;
import java.util.List;
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

import com.dto.CustomerAddressDto;
import com.entity.CustomerAddressEntity;
import com.entity.CustomerEntity;
import com.repository.CustomerAddressRepository;
import com.repository.CustomerRepository;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/customer_address")
public class CustomerAddressController {
	
	@Autowired
	CustomerAddressRepository customerAddressRepository;
	
	@Autowired
	CustomerRepository customerRepository;
	
	@PostMapping
	public ResponseEntity<?> addAddress(@RequestBody CustomerAddressDto customerAddressDto, HttpServletRequest req) {
		String email = (String) req.getAttribute("email");
		Optional<CustomerEntity> op = customerRepository.findByEmail(email);

		if(op.isPresent()) {
			CustomerEntity customer = op.get();
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
			return ResponseEntity.ok(customerAddress);				
		}else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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
	public ResponseEntity<?> deleteAddress(@PathVariable("addressId") Integer addressId) {
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
	
	@PutMapping("/{addressId}")
	public ResponseEntity<?> updateAddress(@PathVariable("addressId") Integer addressId, @RequestBody CustomerAddressEntity customerAddressEntity, HttpServletRequest req) {
		String email = (String) req.getAttribute("email");
		Optional<CustomerAddressEntity> op = customerAddressRepository.findById(addressId);
		Optional<CustomerEntity> o = customerRepository.findByEmail(email);
		
		if(op.isPresent()) {
			if(o.isPresent()) {
				customerAddressEntity.setCustomer(o.get());
				customerAddressRepository.save(customerAddressEntity);
				return ResponseEntity.ok("Success");				
			}else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
		}else {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}
	}
	
	// read all address
	@GetMapping("/myaddress/{customerId}")
	public ResponseEntity<?> getMyAddress(@PathVariable("customerId") Integer customerId) {
		Optional<CustomerEntity> op = customerRepository.findById(customerId);
		if(op.isPresent()) {
			CustomerEntity customer = op.get();
			List<CustomerAddressEntity> addresses = customer.getCustomerAddresses();
			return ResponseEntity.ok(addresses);
		}else {
			HashMap<String, Object> error = new HashMap<>();
			error.put("message", "Unauthorized Access! Enter valid Credentials.");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
		}
	}
}

