package com.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentDto {

	Integer paymentId;
	String authCode;
	String transactionId;
	String cardHolderName;
	String cardNumber;
	String expiryDate;
	Integer amount;
	String orderDate;
	Integer cartId;
}
