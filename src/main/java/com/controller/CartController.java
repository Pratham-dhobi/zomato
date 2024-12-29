package com.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.entity.CartEntity;
import com.entity.CartItemEntity;
import com.entity.CustomerEntity;
import com.entity.MenuEntity;
import com.entity.MenuItemEntity;
import com.entity.RestaurantEntity;
import com.repository.CartItemRepository;
import com.repository.CartRepository;
import com.repository.CustomerRepository;
import com.repository.MenuItemRepository;
import com.service.CartService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/api/cart")
public class CartController {
	
	@Autowired
	MenuItemRepository menuItemRepository;
	
	@Autowired
	CartRepository cartRepository;
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	CartItemRepository cartItemRepository;
	
	@Autowired
	CartService cartService;
	
	@PostMapping
	public ResponseEntity<?> addToCart(@RequestParam Integer itemId, HttpServletRequest req) {
		String email = (String) req.getAttribute("email");
		Optional<CustomerEntity> op = customerRepository.findByEmail(email);
		
		if(op.isPresent()) {
			CustomerEntity customer = op.get();
			
			Optional<MenuItemEntity> opt = menuItemRepository.findById(itemId);
			
			if(opt.isPresent()) {
				MenuItemEntity menuItem = opt.get();
				MenuEntity menu = menuItem.getMenu();
				RestaurantEntity restaurant = menu.getRestaurant();
				
				if(restaurant.getActive() == 1) {
					CartEntity cartEntity = cartRepository.findByCustomerEntityAndRestaurant(customer, restaurant).orElse(null);
					
					if(cartEntity == null) {
						cartEntity = new CartEntity();
						cartEntity.setCustomerEntity(customer);
						cartEntity.setRestaurant(restaurant);
					}
					Optional<CartEntity> cart = Optional.of(cartEntity);
					
					CartEntity myCart = cart.get();
					
					Optional<CartItemEntity> cartItem = myCart.getCartItem().stream()
							.filter(item -> item.getMenuItem().getItemId().equals(itemId))
							.findFirst();
					CartItemEntity newCartItem = null;
					if(cartItem.isPresent()) {
						newCartItem = cartItem.get();
						newCartItem.setQty(newCartItem.getQty() + 1);
					}else {
						newCartItem = new CartItemEntity();
						newCartItem.setMenuItem(menuItem);
						newCartItem.setMenu(menu);
						newCartItem.setCartEntity(myCart);
						newCartItem.setQty(1);						
					}
					cartRepository.save(myCart);
					cartItemRepository.save(newCartItem);
					
					return ResponseEntity.ok("success");					
				}else {
					HashMap<String, String> e = new HashMap<>();
					e.put("message", "The Restaurant is not active.");
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
				}
					
			}else {
				HashMap<String, String> error = new HashMap<>();
				error.put("message", "Item not Present.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
			}
			
		}else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
	
	@GetMapping
	public ResponseEntity<?> getAllCarts(HttpServletRequest req) {
		String email = (String) req.getAttribute("email");
		Optional<CustomerEntity> op = customerRepository.findByEmail(email);
		
		if(op.isPresent()) {
			CustomerEntity customer = op.get();
			Optional<CustomerEntity> o = customerRepository.findById(customer.getCustomerId());
			
			if(o.isPresent()) {
				List<CartEntity> carts = cartRepository.findByCustomerEntity(o.get());
				
				if(!carts.isEmpty()) {
					List<HashMap<String, Object>> cartDataList = new ArrayList<>();
					
					for(CartEntity cart: carts) {
						int totalPrice = 0;
						HashMap<String, Object> cartData = new HashMap<>();
						cartData.put("cartId", cart.getCartId());
						cartData.put("customerId", cart.getCustomerEntity().getCustomerId());
						cartData.put("restaurantId", cart.getRestaurant().getRestaurantId());
						
						List<Map<String, Object>> cartItemDataList = new ArrayList<>();
						for(CartItemEntity item: cart.getCartItem()) {
							totalPrice = totalPrice + (item.getMenuItem().getPrice() * item.getQty());
							HashMap<String, Object> cartItemData = new HashMap<>();
							cartItemData.put("cartItemId", item.getCartItemId());
							cartItemData.put("menuItemId", item.getMenu().getMenuId());
							cartItemData.put("quantity", item.getQty());
							cartItemData.put("price", item.getMenuItem().getPrice() * item.getQty());
							cartItemDataList.add(cartItemData);
						}
						cartData.put("totalPrice", totalPrice);
						cartData.put("cartItems", cartItemDataList);
						cartDataList.add(cartData);
					}
					
					return ResponseEntity.ok(cartDataList);
				}else {
					HashMap<String, String> error = new HashMap<>();
					error.put("message", "no carts found.");
					return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
				}
			}else {
				HashMap<String, String> error = new HashMap<>();
				error.put("message", "Customer Id not found! Please Enter Valid Id.");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
			}
		}else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
	
	@GetMapping("/{cartId}")
	public ResponseEntity<?> getCartById(@PathVariable("cartId") Integer cartId) {
		Optional<CartEntity> op = cartRepository.findById(cartId);
		
		if(op.isPresent()) {
			CartEntity cart = op.get();
			HashMap<String, Object> response = new HashMap<>();
			response.put("cartId", cartId);
			response.put("customerId", cart.getCustomerEntity().getCustomerId());
			response.put("restaurantId", cart.getRestaurant().getRestaurantId());
			response.put("items", cart.getCartItem());
			
			return ResponseEntity.ok(response);
		}else {
			HashMap<String, String> error = new HashMap<>();
			error.put("message", "cart id not found.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteCartById(@PathVariable("id") Integer cartId, HttpServletRequest req) {
		String email = (String) req.getAttribute("email");
		Optional<CustomerEntity> op = customerRepository.findByEmail(email);
		
		if(op.isPresent()) {
			CustomerEntity customer = op.get();
			Optional<CartEntity> o = cartRepository.findById(cartId);
			
			if(o.isPresent()) {
				if(o.get().getCustomerEntity().getCustomerId().equals(customer.getCustomerId())) {
					Integer st1 = cartService.softDeleteCartService(cartId);
					Integer st2 = cartService.softDeleteCartItemService(cartId);
					
					if(st1 == 200 && st2 == 200) {
						return ResponseEntity.ok("Success");							
					}else if(st1 == 400 || st2 == 204) {
						return ResponseEntity.status(HttpStatus.NOT_FOUND).body("cart not found");
					}else {
						return ResponseEntity.status(HttpStatus.NOT_FOUND).body("cart item not found");
					}
				}else {
					HashMap<String, String> error = new HashMap<>();
					error.put("message", "You are not authorized to delete this cart.");
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
				}
			}else {
				HashMap<String, String> error = new HashMap<>();
				error.put("message", "cart id not found.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
			}
		}else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
}
