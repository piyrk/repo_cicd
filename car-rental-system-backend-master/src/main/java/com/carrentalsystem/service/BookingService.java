package com.carrentalsystem.service;

import java.util.List;

import com.carrentalsystem.entity.Booking;
import com.carrentalsystem.entity.User;
import com.carrentalsystem.entity.Vehicle;

public interface BookingService {

	Booking addBooking(Booking booking);

	Booking updateBooking(Booking booking);

	Booking getById(int bookingId);

	Booking getByBookingId(String bookingId);

	List<Booking> getByCustomer(User customer);

	List<Booking> getByStatus(String status);

	List<Booking> getByVehicle(Vehicle vehicle);
	
	List<Booking> getAllBookings();

}
