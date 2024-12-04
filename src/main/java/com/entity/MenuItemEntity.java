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
}
