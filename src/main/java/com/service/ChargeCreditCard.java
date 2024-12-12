package com.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dto.PaymentDto;
import com.entity.CartEntity;
import com.entity.CustomerEntity;
import com.entity.PaymentEntity;
import com.repository.PaymentRepository;

import net.authorize.Environment;
import net.authorize.api.contract.v1.*;
import net.authorize.api.controller.CreateTransactionController;
import net.authorize.api.controller.base.ApiOperationBase;

@Service
public class ChargeCreditCard {
    
	@Value("${login.key}")
	String apiLoginId;
	
	@Value("${transaction.key}")
	String transactionKey;

	@Autowired
	PaymentRepository paymentRepository;
	
    public ANetApiResponse run(PaymentDto paymentDto, CustomerEntity customerEntity, CartEntity cart) {
    	
        // Set the request to operate in either the sandbox or production environment
        ApiOperationBase.setEnvironment(Environment.SANDBOX);

        // Create object with merchant authentication details
        MerchantAuthenticationType merchantAuthenticationType  = new MerchantAuthenticationType() ;
        merchantAuthenticationType.setName(apiLoginId);
        merchantAuthenticationType.setTransactionKey(transactionKey);

        // Populate the payment data
        PaymentType paymentType = new PaymentType();
        CreditCardType creditCard = new CreditCardType();
        creditCard.setCardNumber(paymentDto.getCardNumber());
        creditCard.setExpirationDate(paymentDto.getExpiryDate());
        paymentType.setCreditCard(creditCard);

        // Set email address (optional)
        CustomerDataType customer = new CustomerDataType();
        customer.setEmail("test@test.com");
    	
        // Create the payment transaction object
        TransactionRequestType txnRequest = new TransactionRequestType();
        txnRequest.setTransactionType(TransactionTypeEnum.AUTH_CAPTURE_TRANSACTION.value());
        txnRequest.setPayment(paymentType);
        txnRequest.setCustomer(customer);
        txnRequest.setAmount(new BigDecimal(paymentDto.getAmount()).setScale(2, RoundingMode.CEILING));
    	
        // Create the API request and set the parameters for this specific request
        CreateTransactionRequest apiRequest = new CreateTransactionRequest();
        apiRequest.setMerchantAuthentication(merchantAuthenticationType);
        apiRequest.setTransactionRequest(txnRequest);

        // Call the controller
        CreateTransactionController controller = new CreateTransactionController(apiRequest);
        controller.execute();

        // Get the response
        CreateTransactionResponse response = new CreateTransactionResponse();
        response = controller.getApiResponse();
        
        // Parse the response to determine results
        if (response!=null) {
            // If API Response is OK, go ahead and check the transaction response
            if (response.getMessages().getResultCode() == MessageTypeEnum.OK) {
                TransactionResponse result = response.getTransactionResponse();
                if (result.getMessages() != null) {
                    System.out.println("Successfully created transaction with Transaction ID: " + result.getTransId());
                    System.out.println("Response Code: " + result.getResponseCode());
                    System.out.println("Message Code: " + result.getMessages().getMessage().get(0).getCode());
                    System.out.println("Description: " + result.getMessages().getMessage().get(0).getDescription());
                    System.out.println("Auth Code: " + result.getAuthCode());
                    
                    PaymentEntity payment = new PaymentEntity();
    				payment.setCardHolderName(paymentDto.getCardHolderName());
    				payment.setCardNumber(paymentDto.getCardNumber());
    				payment.setExpiryDate(paymentDto.getExpiryDate());
    				payment.setAmount(paymentDto.getAmount());
    				payment.setOrderDate(paymentDto.getOrderDate());
    				payment.setCustEntity(customerEntity);
    				payment.setAuthCode(result.getAuthCode());
    				payment.setTransactionId(result.getTransId());
    				payment.setCartEntt(cart);
    				
    				paymentRepository.save(payment);
                } else {
                    System.out.println("Failed Transaction.");
                    if (response.getTransactionResponse().getErrors() != null) {
                        System.out.println("Error Code: " + response.getTransactionResponse().getErrors().getError().get(0).getErrorCode());
                        System.out.println("Error message: " + response.getTransactionResponse().getErrors().getError().get(0).getErrorText());
                    }
                }
            } else {
                System.out.println("Failed Transaction.");
                if (response.getTransactionResponse() != null && response.getTransactionResponse().getErrors() != null) {
                    System.out.println("Error Code: " + response.getTransactionResponse().getErrors().getError().get(0).getErrorCode());
                    System.out.println("Error message: " + response.getTransactionResponse().getErrors().getError().get(0).getErrorText());
                } else {
                    System.out.println("Error Code: " + response.getMessages().getMessage().get(0).getCode());
                    System.out.println("Error message: " + response.getMessages().getMessage().get(0).getText());
                }
            }
        } else {
            // Display the error code and message when response is null 
            ANetApiResponse errorResponse = controller.getErrorResponse();
            System.out.println("Failed to get response");
            if (!errorResponse.getMessages().getMessage().isEmpty()) {
                System.out.println("Error: "+errorResponse.getMessages().getMessage().get(0).getCode()+" \n"+ errorResponse.getMessages().getMessage().get(0).getText());
            }
        }
        
        return response;
    }
}
