package com.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerDto {
	
	Integer customerId;
	String fullName;
	String email;
	String password;
	String birthDate;
	String contactNumber;
	String gender;
	String otp;
}
