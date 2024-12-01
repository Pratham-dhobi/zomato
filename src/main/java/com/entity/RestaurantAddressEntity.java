package com.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "restaurant_address")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RestaurantAddressEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer addressId;
	String restaurantName;
	String address;
	String street;
	String landmark;
	String city;
	String state;
	String pincode;
}
