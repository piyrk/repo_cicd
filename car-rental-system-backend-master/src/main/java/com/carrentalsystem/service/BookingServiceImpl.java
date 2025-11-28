package com.carrentalsystem.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carrentalsystem.dao.BookingDao;
import com.carrentalsystem.entity.Booking;
import com.carrentalsystem.entity.User;
import com.carrentalsystem.entity.Vehicle;

@Service
public class BookingServiceImpl implements BookingService {

	@Autowired
	private BookingDao bookingDao;
	
	@Override
	public Booking addBooking(Booking booking) {
		// TODO Auto-generated method stub
		return bookingDao.save(booking);
	}

	@Override
	public Booking updateBooking(Booking booking) {
		// TODO Auto-generated method stub
		return bookingDao.save(booking);
	}

	@Override
	public Booking getById(int bookingId) {
		// TODO Auto-generated method stub
		
		Optional<Booking> optional = bookingDao.findById(bookingId);
		
		if(optional.isPresent()) {
			return optional.get();
		}
		
		return null;
	}

	@Override
	public Booking getByBookingId(String bookingId) {
		// TODO Auto-generated method stub
		return bookingDao.findByBookingId(bookingId);
	}

	@Override
	public List<Booking> getByCustomer(User customer) {
		// TODO Auto-generated method stub
		return bookingDao.findByCustomer(customer);
	}

	@Override
	public List<Booking> getByStatus(String status) {
		// TODO Auto-generated method stub
		return bookingDao.findByStatus(status);
	}

	@Override
	public List<Booking> getByVehicle(Vehicle vehicle) {
		// TODO Auto-generated method stub
		return bookingDao.findByVehicle(vehicle);
	}

	@Override
	public List<Booking> getAllBookings() {
		// TODO Auto-generated method stub
		return bookingDao.findAll();
	}

}
