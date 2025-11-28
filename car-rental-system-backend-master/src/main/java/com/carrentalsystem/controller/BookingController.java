package com.carrentalsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carrentalsystem.dto.AddBookingRequest;
import com.carrentalsystem.dto.BookingResponse;
import com.carrentalsystem.dto.CommonApiResponse;
import com.carrentalsystem.dto.CustomerBookingPaymentRequest;
import com.carrentalsystem.resource.BookingResource;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("api/book")
public class BookingController {

	@Autowired
	private BookingResource bookingResource;

	@PostMapping("/add")
	@Operation(summary = "Api to book for customer rent")
	public ResponseEntity<CommonApiResponse> addRentBook(@RequestBody AddBookingRequest request) {
		return bookingResource.addBooking(request);
	}

	@PutMapping("/update/assign/vehicle")
	@Operation(summary = "Api to update the booking status and assign vehicle")
	public ResponseEntity<CommonApiResponse> updateStatusAndAssignVehicle(@RequestBody AddBookingRequest request) {
		return bookingResource.updateStatusAndAssignVehicle(request);
	}

	@GetMapping("/fetch/all")
	@Operation(summary = "Api to fetch all bookings")
	public ResponseEntity<BookingResponse> fetchAllBookings() {
		return bookingResource.fetchAllBookings();
	}

	@GetMapping("/fetch/customer-wise")
	@Operation(summary = "Api to fetch customer bookings")
	public ResponseEntity<BookingResponse> fetchAllCustomerBookings(@RequestParam("customerId") Integer customerId) {
		return bookingResource.fetchAllCustomerBookings(customerId);
	}
	
	@DeleteMapping("/cancel")
	@Operation(summary = "Api to cancel the customer booking")
	public ResponseEntity<CommonApiResponse> cancelbooking(@RequestBody AddBookingRequest request) {
		return bookingResource.cancelbooking(request);
	}

	@PutMapping("/customer/payment")
	@Operation(summary = "Api for customer booking payment")
	public ResponseEntity<CommonApiResponse> customerPaymentForBooking(
			@RequestBody CustomerBookingPaymentRequest request) {
		return bookingResource.customerPaymentForBooking(request);
	}

}
