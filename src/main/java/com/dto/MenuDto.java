package com.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuDto {
	
	Integer menuId;
	String title;
	Integer isActive;
	Integer restaurantId;
}
