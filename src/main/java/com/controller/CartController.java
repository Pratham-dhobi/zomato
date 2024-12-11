package com.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.SessionException;
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

import jakarta.servlet.http.HttpSession;

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
	public ResponseEntity<?> addToCart(@RequestParam Integer itemId, HttpSession session) {
		
		try {
			CustomerEntity customer = (CustomerEntity)session.getAttribute("customer");
			
			if(customer == null) {
				throw new SessionException("Session Exception");
			}else {
				Optional<CustomerEntity> o = customerRepository.findById(customer.getCustomerId());
				
				if(o.isEmpty()) {
					HashMap<String, String> error = new HashMap<>();
					error.put("message", "Customer Id not found! Please Enter Valid Id.");
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
				}else {
					Optional<MenuItemEntity> op = menuItemRepository.findById(itemId);
					
					if(op.isPresent()) {
						MenuItemEntity menuItem = op.get();
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
							
							if(cartItem.isPresent()) {
								CartItemEntity newCartItem = cartItem.get();
								newCartItem.setQty(newCartItem.getQty() + 1);
								cartItemRepository.save(newCartItem);
							}else {
								CartItemEntity newCartItemEntity = new CartItemEntity();
								newCartItemEntity.setMenuItem(menuItem);
								newCartItemEntity.setMenu(menu);
								newCartItemEntity.setCartEntity(myCart);
								newCartItemEntity.setQty(1);
								
								cartItemRepository.save(newCartItemEntity);
							}
							cartRepository.save(myCart);
							
							HashMap<String, Object> cartResponse = new HashMap<>();
							cartResponse.put("cartId", myCart.getCartId());
							cartResponse.put("restaurantId", myCart.getRestaurant().getRestaurantId());
							
							List<Map<String, Object>> cartItemsResponse = myCart.getCartItem().stream()
							    	.map(item -> {
								        Map<String, Object> cartItemData = new HashMap<>();
								        cartItemData.put("cartItemId", item.getCartItemId());
								        cartItemData.put("menuId", item.getMenu().getMenuId());
								        cartItemData.put("menuItemId", item.getMenuItem().getItemId());
								        cartItemData.put("menuItemTitle", item.getMenuItem().getTitle());
								        cartItemData.put("quantity", item.getQty());
								        cartItemData.put("price", item.getMenuItem().getPrice() * item.getQty());
								        return cartItemData;
							    	}).collect(Collectors.toList());
	
							cartResponse.put("cartItems", cartItemsResponse);
							
							Map<String, Object> response = new HashMap<>();
						    response.put("message", "Item added to cart successfully.");
						    response.put("cart", cartResponse);
							
							return ResponseEntity.ok(response);					
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
				}
			}
		}catch(SessionException sessionException) {
			HashMap<String, String> error = new HashMap<>();
			error.put("message", "Customer Id not found! Please Enter Credentials.");
			error.put("exception", sessionException.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
	
	@GetMapping
	public ResponseEntity<?> getAllCarts(HttpSession session) {
		try {
			CustomerEntity customer = (CustomerEntity)session.getAttribute("customer");
			
			if(customer == null) {
				throw new SessionException("Session Exception");
			}else {
				Optional<CustomerEntity> op = customerRepository.findById(customer.getCustomerId());
				
				if(op.isPresent()) {
					List<CartEntity> carts = cartRepository.findByCustomerEntity(op.get());
					
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
			}
		}catch(SessionException sessionException) {
			HashMap<String, String> error = new HashMap<>();
			error.put("message", "Customer Id not found! Please Enter Credentials.");
			error.put("exception", sessionException.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
	
	@GetMapping("/{cartId}")
	public ResponseEntity<?> getCartById(@PathVariable("cartId") Integer cartId, HttpSession session) {
		try {
			CustomerEntity customer = (CustomerEntity)session.getAttribute("customer");
			
			if(customer == null) {
				throw new SessionException("Session Exception");
			}else {
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
		}catch(SessionException sessionException) {
			HashMap<String, String> error = new HashMap<>();
			error.put("message", "Customer Id not found! Please Enter Credentials.");
			error.put("exception", sessionException.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteCartById(@PathVariable("id") Integer cartId, HttpSession session) {
		try {
			CustomerEntity customer = (CustomerEntity)session.getAttribute("customer");
			
			if(customer == null) {
				throw new SessionException("Session Exception");
			}else {
				Optional<CartEntity> op = cartRepository.findById(cartId);
				
				if(op.isPresent()) {
					if(op.get().getCustomerEntity().getCustomerId().equals(customer.getCustomerId())) {
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
			}
		}catch(SessionException sessionException) {
			HashMap<String, String> error = new HashMap<>();
			error.put("message", "Customer Id not found! Please Enter Credentials.");
			error.put("exception", sessionException.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
}
