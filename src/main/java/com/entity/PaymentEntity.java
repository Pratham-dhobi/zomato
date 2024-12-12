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
@Table(name = "orders")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer paymentId;
	
	@ManyToOne
	@JoinColumn(name = "customerId")
	@JsonBackReference("customer-payment")
	CustomerEntity custEntity;
	
	String authCode;
	String transactionId;
	String cardHolderName;
	String cardNumber;
	String expiryDate;
	Integer amount;
	String orderDate;
	
	@ManyToOne
	@JoinColumn(name = "cartId")
	@JsonBackReference("cart-payment")
	CartEntity cartEntt;
}
