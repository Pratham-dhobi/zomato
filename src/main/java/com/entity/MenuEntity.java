package com.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "menu")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer menuId;
	String title;
	Integer active = 1;
	
	@ManyToOne
	@JoinColumn(name = "restaurantId")
	@JsonBackReference("restaurant-menu")
	RestaurantEntity restaurant;
	
	@OneToMany(mappedBy = "menu")
	@JsonManagedReference
	List<MenuItemEntity> menuItems;
	
	@OneToMany(mappedBy = "menu")
	@JsonManagedReference("menu-cartItem")
	List<CartItemEntity> cartItems;
}
