package com.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
import com.repository.RestaurantRepository;
import com.service.MenuService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/menu")
public class MenuController {
	
	@Autowired
	MenuRepository menuRepository;
	
	@Autowired
	MenuService menuService;
	
	@Autowired
	RestaurantRepository restaurantRepository;
	
	@PostMapping
	public ResponseEntity<?> addMenu(@RequestBody MenuDto menuDto, HttpServletRequest req) {	
		String email = (String) req.getAttribute("email");
		Optional<RestaurantEntity> op = restaurantRepository.findByEmail(email);
		
		if(op.isPresent()) {
			RestaurantEntity restaurant = op.get();
			
			if(email.equals(restaurant.getEmail())) {
				MenuEntity menu = new MenuEntity();
				menu.setTitle(menuDto.getTitle());
				menu.setRestaurant(restaurant);
				
				menuRepository.save(menu);
				
				return ResponseEntity.ok("Success");												
			}else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("restaurant not found");
		}
	
	}
	
	@DeleteMapping("/{menuId}")
	public ResponseEntity<?> deleteMenuById(@PathVariable("menuId") Integer menuId) {
		HttpStatus status = menuService.softDeleteMenuService(menuId);
		return ResponseEntity.status(status).build();
	}
	
	@PutMapping("/{menuId}")
	public ResponseEntity<?> updateMenu(@RequestBody MenuDto menuDto, @PathVariable("menuId") Integer menuId) {
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
	
	@GetMapping("/restaurant/{restaurantId}")
	public ResponseEntity<?> getMenusByRestaurantId(@PathVariable("restaurantId") Integer restaurantId) {
		Optional<RestaurantEntity> op = restaurantRepository.findById(restaurantId);
		
		if(op.isPresent()) {
			RestaurantEntity restaurant = op.get();
			List<MenuEntity> menus = menuRepository.findByRestaurant(restaurant);
			return ResponseEntity.ok(menus);
		}else {
			HashMap<String, String> error = new HashMap<>();
			error.put("message", "Restaurant Id not found!");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}
	}
}
