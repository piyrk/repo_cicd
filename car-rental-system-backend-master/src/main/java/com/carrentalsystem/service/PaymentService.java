package com.carrentalsystem.service;

import java.util.List;

import com.carrentalsystem.entity.Payment;
import com.carrentalsystem.entity.User;

public interface PaymentService {

	Payment addPayment(Payment payment);

	Payment updatePayment(Payment payment);

	Payment getById(int paymentId);

	Payment getByBookingId(String bookingId);

	Payment getByTransactionRefId(String transactionId);

	List<Payment> getByCustomer(User user);

}
