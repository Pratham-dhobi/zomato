package com.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuItemDto {
	
	Integer itemId;
	Integer menuId;
	String title;
	String description;
	Integer price;
}
