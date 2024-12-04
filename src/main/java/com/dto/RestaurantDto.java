package com.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RestaurantDto {
	
	Integer restaurantId;
	String restaurantName;
	String category;
	String description;
	String timings;
	String contactNumber;
	String latitude;
	String longtitude;
	String pincode;
	String email;
	String password;
	Integer active;
	String otp;
}
