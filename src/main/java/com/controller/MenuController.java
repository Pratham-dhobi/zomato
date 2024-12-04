package com.controller;

import java.util.HashMap;
import java.util.Optional;

import org.hibernate.SessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dto.MenuDto;
import com.entity.MenuEntity;
import com.entity.RestaurantEntity;
import com.repository.MenuRepository;
import com.service.MenuService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/menu")
public class MenuController {
	
	@Autowired
	MenuRepository menuRepository;
	
	@Autowired
	MenuService menuService;
	
	@PostMapping
	public ResponseEntity<?> addMenu(@RequestBody MenuDto menuDto, HttpSession session) {
		
		try {
			RestaurantEntity restaurant = (RestaurantEntity)session.getAttribute("restaurant");
			
			if(restaurant == null) {
				throw new SessionException("Session Exception");
			}else {
				HashMap<String, String> error = new HashMap<>();;
				MenuEntity menu = new MenuEntity();
				menu.setTitle(menuDto.getTitle());
				menu.setRestaurant(restaurant);
				
				menuRepository.save(menu);
				
				return ResponseEntity.ok("Success");					
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
	
	@DeleteMapping("/{menuId}")
	public ResponseEntity<?> deleteMenuById(@PathVariable("menuId") Integer menuId, HttpSession session) {
		try {
			RestaurantEntity restaurant = (RestaurantEntity)session.getAttribute("restaurant");
			
			if(restaurant == null) {
				throw new SessionException("Session Exception");
			}else {
				int status = menuService.softDeleteMenuService(menuId);
				HashMap<String, String> message = new HashMap<>();
				String msg = "";
				if(status == 200) {
					msg = "Success";
				}else if(status == 404) {
					msg = "Menu not Found.";
				}
				message.put("message", msg);
				return ResponseEntity.status(HttpStatusCode.valueOf(status)).body(message);
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
	
	@PutMapping("/{menuId}")
	public ResponseEntity<?> updateMenu(@RequestBody MenuDto menuDto, @PathVariable("menuId") Integer menuId, HttpSession session) {
		try {
			RestaurantEntity restaurant = (RestaurantEntity)session.getAttribute("restaurant");
		
			if(restaurant == null) {
				throw new SessionException("Session Exception");
			}else {
				Optional<MenuEntity> op = menuRepository.findById(menuId);
			
				if(op.isPresent()) {
					MenuEntity menu = op.get();
					menu.setTitle(menuDto.getTitle());
					menu.setActive(menuDto.getIsActive());
					
					menuRepository.save(menu);
					
					return ResponseEntity.ok("Success");
				}else {
					HashMap<String, String> error = new HashMap<>();
					error.put("message", "Menu Id not found!");
					return ResponseEntity.status(HttpStatusCode.valueOf(401)).body(error);			
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
