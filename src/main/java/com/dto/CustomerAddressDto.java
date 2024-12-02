package com.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerAddressDto {

	Integer addressId;
	String title;
	String houseNo;
	String apartment;
	String street;
	String landmark;
	String city;
	String state;
	String pincode;
}
