package com.carrentalsystem.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carrentalsystem.entity.Booking;
import com.carrentalsystem.entity.User;
import com.carrentalsystem.entity.Vehicle;

@Repository
public interface BookingDao extends JpaRepository<Booking, Integer> {

	Booking findByBookingId(String bookingId);

	List<Booking> findByCustomer(User customer);

	List<Booking> findByStatus(String status);

	List<Booking> findByVehicle(Vehicle vehicle);

}
