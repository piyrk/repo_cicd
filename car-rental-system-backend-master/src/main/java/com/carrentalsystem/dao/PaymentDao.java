package com.carrentalsystem.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carrentalsystem.entity.Payment;
import com.carrentalsystem.entity.User;

@Repository
public interface PaymentDao extends JpaRepository<Payment, Integer> {

	Payment findByBookingId(String bookingId);
	
	Payment findByTransactionRefId(String transactionId);

	List<Payment> findByCustomer(User user);
	
}
