package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entity.CustomerEntity;
import com.entity.PaymentEntity;
import java.util.List;


public interface PaymentRepository extends JpaRepository<PaymentEntity, Integer>{
	List<PaymentEntity> findByCustEntity(CustomerEntity custEntity);
}
