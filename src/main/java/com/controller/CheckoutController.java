package com.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.hibernate.SessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dto.PaymentDto;
import com.entity.CartEntity;
import com.entity.CustomerEntity;
import com.entity.PaymentEntity;
import com.repository.CartRepository;
import com.repository.PaymentRepository;
import com.service.ChargeCreditCard;

import jakarta.servlet.http.HttpSession;
import net.authorize.api.contract.v1.ANetApiResponse;
import net.authorize.api.contract.v1.MessageTypeEnum;

@RestController
@RequestMapping("/api/payment/creditcard")
public class CheckoutController {

	@Autowired
	ChargeCreditCard chargCard;

	@Autowired
	CartRepository cartRepository;

	@Autowired
	PaymentRepository paymentRepository;

	@PostMapping("/checkout/cart")
	public ResponseEntity<?> checkOut(@RequestBody PaymentDto paymentDto, HttpSession session) {
		try {
			CustomerEntity customer = (CustomerEntity) session.getAttribute("customer");
			if (customer == null) {
				throw new SessionException("Session Exception");
			} else {
				Optional<CartEntity> op = cartRepository.findById(paymentDto.getCartId());

				if (op.isPresent()) {
					CartEntity cart = op.get();
					ANetApiResponse response = chargCard.run(paymentDto, customer, cart);
					MessageTypeEnum resultCode = response.getMessages().getResultCode();

					if (resultCode == MessageTypeEnum.OK) {
						return ResponseEntity.ok("Success");
					} else {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");
					}
				} else {
					return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
				}
			}
		} catch (SessionException sessionException) {
			HashMap<String, String> error = new HashMap<>();
			error.put("message", "Customer Id not found! Please Enter Credentials.");
			error.put("exception", sessionException.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("bad request");
		}
	}

	@GetMapping("/history/customer/{customerId}")
	public ResponseEntity<?> getAllOrders(@PathVariable("customerId") Integer customerId, HttpSession session) {
		try {
			CustomerEntity customer = (CustomerEntity) session.getAttribute("customer");

			if (customer == null) {
				throw new SessionException("Session Exception");
			} else {
				if (customerId == customer.getCustomerId()) {
					List<PaymentEntity> payments = paymentRepository.findByCustEntity(customer);

					if (payments.isEmpty()) {
						return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
					} else {
						return ResponseEntity.ok(payments);
					}
				} else {
					HashMap<String, String> error = new HashMap<>();
					error.put("message", "You are not authorized! Enter valid customerId");
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
				}
			}
		} catch (SessionException sessionException) {
			HashMap<String, String> error = new HashMap<>();
			error.put("message", "Customer Id not found! Please Enter Credentials.");
			error.put("exception", sessionException.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@GetMapping("/customer/{customerId}/order/{orderId}")
	public ResponseEntity<?> getOrderById(@PathVariable("customerId") Integer customerId, @PathVariable("orderId") Integer orderId, HttpSession session) {
		try {
			CustomerEntity customer = (CustomerEntity) session.getAttribute("customer");

			if (customer == null) {
				throw new SessionException("Session Exception");
			} else {
				if(customerId == customer.getCustomerId()) {
					Optional<PaymentEntity> op = paymentRepository.findById(orderId);
					
					if(op.isPresent()) {
						PaymentEntity payment = op.get();
						return ResponseEntity.ok(payment);
					}else {
						return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
					}
				}else {
					HashMap<String, String> error = new HashMap<>();
					error.put("message", "You are not authorized! Enter valid customerId");
					return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
				}
			}
		} catch (SessionException sessionException) {
			HashMap<String, String> error = new HashMap<>();
			error.put("message", "Customer Id not found! Please Enter Credentials.");
			error.put("exception", sessionException.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
}
