package com.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.entity.CustomerEntity;
import com.repository.CustomerRepository;

@Service
public class CustomerService {

	@Autowired
	CustomerRepository customerRepository;
	
	public HttpStatus softDeleteCustomer(Integer customerId) {
		Optional<CustomerEntity> op = customerRepository.findById(customerId);
		
		if(op.isPresent()) {
			CustomerEntity customer = op.get();
			customer.setIsActive(0);
			customerRepository.save(customer);
			return HttpStatus.OK;
		}else {
			return HttpStatus.NOT_FOUND;
		}
	}
}
