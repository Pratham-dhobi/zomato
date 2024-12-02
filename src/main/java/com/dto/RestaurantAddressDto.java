package com.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RestaurantAddressDto {

	Integer addressId;
	String restaurantName;
	String address;
	String street;
	String landmark;
	String city;
	String state;
	String pincode;
}
