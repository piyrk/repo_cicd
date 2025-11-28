package com.carrentalsystem.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carrentalsystem.dao.PaymentDao;
import com.carrentalsystem.entity.Payment;
import com.carrentalsystem.entity.User;

@Service
public class PaymentServiceImpl implements PaymentService {

	
	@Autowired
	private PaymentDao paymentDao;
	
	@Override
	public Payment addPayment(Payment payment) {
		// TODO Auto-generated method stub
		return paymentDao.save(payment);
	}

	@Override
	public Payment updatePayment(Payment payment) {
		// TODO Auto-generated method stub
		return paymentDao.save(payment);
	}

	@Override
	public Payment getById(int paymentId) {
		// TODO Auto-generated method stub
		Optional<Payment> optional = paymentDao.findById(paymentId);

		if (optional.isPresent()) {
			return optional.get();
		}

		return null;
	}

	@Override
	public Payment getByBookingId(String bookingId) {
		// TODO Auto-generated method stub
		return paymentDao.findByBookingId(bookingId);
	}

	@Override
	public Payment getByTransactionRefId(String transactionId) {
		// TODO Auto-generated method stub
		return paymentDao.findByTransactionRefId(transactionId);
	}

	@Override
	public List<Payment> getByCustomer(User user) {
		// TODO Auto-generated method stub
		return paymentDao.findByCustomer(user);
	}

}
