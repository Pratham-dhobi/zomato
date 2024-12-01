package com.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "customers")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer customerId;
	String fullName;
	@Column(unique = true, nullable = false)
	String email;
	@Column(nullable = false)
	String password;
	String birthDate;
	String contactNumber;
	String gender;
	String otp;
	
	@OneToMany(mappedBy = "customer")
	List<CustomerAddressEntity> customerAddresses;
}
