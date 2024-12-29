package com.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "cart")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer cartId;
	
	Integer isActive = 1;
	
	@ManyToOne
	@JoinColumn(name = "customerId")
	@JsonBackReference("customer-cart")
	CustomerEntity customerEntity;
	
	@ManyToOne
	@JoinColumn(name = "restaurantId")
	@JsonBackReference("restaurant-cart")
	RestaurantEntity restaurant;
	
	@OneToMany(mappedBy = "cartEntity")
	@JsonManagedReference("cart-cartItem")
	List<CartItemEntity> cartItem = new ArrayList<>();
	
	@ManyToMany(mappedBy = "cart")
	List<MenuItemEntity> menuItem;
	
	@OneToMany(mappedBy = "cartEntt")
	@JsonManagedReference("cart-payment")
	List<PaymentEntity> payments;
}
