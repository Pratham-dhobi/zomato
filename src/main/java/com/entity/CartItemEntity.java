package com.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "cartItem")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer cartItemId;
	
	Integer isActive = 1;
	
	@ManyToOne
	@JoinColumn(name = "cartId")
	@JsonBackReference("cart-cartItem")
	CartEntity cartEntity;
	
	@ManyToOne
	@JoinColumn(name = "menuItemId")
	@JsonBackReference("menuitem-cartitem")
	MenuItemEntity menuItem;
	
	@ManyToOne
	@JoinColumn(name = "menuId")
	@JsonBackReference("menu-cartItem")
	MenuEntity menu;
	
	Integer qty;
}
