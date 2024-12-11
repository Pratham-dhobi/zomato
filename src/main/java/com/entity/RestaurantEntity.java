package com.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "restaurants")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RestaurantEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer restaurantId;
	String restaurantName;
	String category;
	String description;
	String timings;
	String contactNumber;
	String latitude;
	String longtitude;
	String pincode;
	@Column(unique = true, nullable = false)
	String email;
	@Column(nullable = false)
	String password;
	Integer active = 1;
	String otp;
	Integer isVegOnly;
	
	@OneToOne
	@JoinColumn(name = "addressId")
	RestaurantAddressEntity address;
	
	@OneToMany(mappedBy = "restaurant")
	@JsonManagedReference("restaurant-menu")
	List<MenuEntity> menu;
	
	@OneToMany(mappedBy = "restaurant")
	@JsonManagedReference("restaurant-cart")
	List<CartEntity> carts;
}
