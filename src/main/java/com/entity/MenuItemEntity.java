package com.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "menuItems")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuItemEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer itemId;
	String title;
	String description;
	Integer price;
	
	@ManyToOne
	@JoinColumn(name = "menuId")
	@JsonBackReference
	MenuEntity menu;
	
	@ManyToMany
	@JoinTable(name = "cart_menuitem", joinColumns = @JoinColumn(name = "menuItemId"), inverseJoinColumns = @JoinColumn(name = "cartId"))
	List<CartEntity> cart;
	
	@OneToMany(mappedBy = "menuItem")
	@JsonManagedReference("menuitem-cartitem")
	List<CartItemEntity> cartItem;
}
