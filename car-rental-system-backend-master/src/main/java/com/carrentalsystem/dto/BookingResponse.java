package com.carrentalsystem.dto;

import java.util.ArrayList;
import java.util.List;

import com.carrentalsystem.entity.Booking;

public class BookingResponse extends CommonApiResponse {

	private List<Booking> bookings = new ArrayList<>();

	public List<Booking> getBookings() {
		return bookings;
	}

	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings;
	}

}
