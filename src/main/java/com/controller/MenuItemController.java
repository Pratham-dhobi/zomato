package com.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.hibernate.SessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dto.MenuItemDto;
import com.entity.MenuEntity;
import com.entity.MenuItemEntity;
import com.entity.RestaurantEntity;
import com.repository.MenuItemRepository;
import com.repository.MenuRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/menuitem")
public class MenuItemController {
	
	@Autowired
	MenuRepository menuRepository;
	
	@Autowired
	MenuItemRepository menuItemRepository;
	
	@PostMapping("/{menuId}")
	public ResponseEntity<?> addItem(@RequestBody MenuItemDto menuItemDto, @PathVariable("menuId") Integer menuId, HttpSession session) {
		try {
			RestaurantEntity restaurant = (RestaurantEntity)session.getAttribute("restaurant");
			
			if(restaurant == null) {
				throw new SessionException("Session Exception");
			}else {
				Optional<MenuEntity> op = menuRepository.findById(menuId);
				
				if(op.isPresent()) {
					MenuItemEntity item = new MenuItemEntity();
					item.setTitle(menuItemDto.getTitle());
					item.setDescription(menuItemDto.getDescription());
					item.setPrice(menuItemDto.getPrice());
					item.setMenu(op.get());
					
					menuItemRepository.save(item);
					return ResponseEntity.ok("Success");
				}else {
					HashMap<String, String> error = new HashMap<>();
					error.put("message", "Invalid Menu Id.");
					return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
				}
			}
		}catch(SessionException sessionException) {
			HashMap<String, String> error = new HashMap<>();
			error.put("message", "Restaurant Id not found! Please Enter Credentials.");
			error.put("exception", sessionException.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
	
	@GetMapping("/menu/{menuId}")
	public ResponseEntity<?> getAllMenuItem(@PathVariable("menuId") Integer menuId) {
		Optional<MenuEntity> op = menuRepository.findById(menuId);
		HashMap<String, String> response = new HashMap<>();
		
		if(op.isPresent()) {
			Optional<List<MenuItemEntity>> opItem = menuItemRepository.findByMenu(op.get());
			
			if(opItem.isPresent()) {
				return ResponseEntity.ok(opItem.get());
			}else {
				response.put("message", "Items not found.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}
		}else {
			response.put("message", "Menu ot found.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}
	
	@GetMapping("/item/{itemId}")
	public ResponseEntity<?> getItemById(@PathVariable("itemId") Integer itemId) {
		Optional<MenuItemEntity> op = menuItemRepository.findById(itemId);
		HashMap<String, String> response = new HashMap<>();
		
		if(op.isPresent()) {
			return ResponseEntity.ok(op.get());
		}else {
			response.put("message", "Items not found.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}
	
	@PutMapping("/{itemId}")
	public ResponseEntity<?> updateItem(@PathVariable("itemId") Integer itemId, @RequestBody MenuItemDto menuItemDto, HttpSession session) {
		try {
			RestaurantEntity restaurant = (RestaurantEntity)session.getAttribute("restaurant");
			
			if(restaurant == null) {
				throw new SessionException("Session Exception");
			}else {
				Optional<MenuItemEntity> op = menuItemRepository.findById(itemId);
				HashMap<String, String> response = new HashMap<>();
				
				if(op.isPresent()) {
					MenuItemEntity item = op.get();
					item.setTitle(menuItemDto.getTitle());
					item.setDescription(menuItemDto.getDescription());
					item.setPrice(menuItemDto.getPrice());
					
					menuItemRepository.save(item);
					
					return ResponseEntity.ok("Success");
				}else {
					response.put("message", "Item not found.");
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
				}
			}
		}catch(SessionException sessionException) {
			HashMap<String, String> error = new HashMap<>();
			error.put("message", "Restaurant Id not found! Please Enter Credentials.");
			error.put("exception", sessionException.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
	
	@DeleteMapping("/{itemId}")
	public ResponseEntity<?> deleteItem(@PathVariable("itemId") Integer itemId, HttpSession session) {
		try {
			RestaurantEntity restaurant = (RestaurantEntity)session.getAttribute("restaurant");
			
			if(restaurant == null) {
				throw new SessionException("Session Exception");
			}else {
				Optional<MenuItemEntity> op = menuItemRepository.findById(itemId);
				
				if(op.isPresent()) {
					menuItemRepository.deleteById(itemId);
					
					return ResponseEntity.ok("Success");
				}else {
					HashMap<String, String> response = new HashMap<>();
					response.put("message", "Item not found.");
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
				}
			}
		}catch(SessionException sessionException) {
			HashMap<String, String> error = new HashMap<>();
			error.put("message", "Restaurant Id not found! Please Enter Credentials.");
			error.put("exception", sessionException.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
}
