package com.carrentalsystem.resource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.carrentalsystem.dto.AddBookingRequest;
import com.carrentalsystem.dto.BookingResponse;
import com.carrentalsystem.dto.CommonApiResponse;
import com.carrentalsystem.dto.CustomerBookingPaymentRequest;
import com.carrentalsystem.entity.Booking;
import com.carrentalsystem.entity.Payment;
import com.carrentalsystem.entity.User;
import com.carrentalsystem.entity.Variant;
import com.carrentalsystem.entity.Vehicle;
import com.carrentalsystem.exception.BookingSaveFailedException;
import com.carrentalsystem.service.BookingService;
import com.carrentalsystem.service.PaymentService;
import com.carrentalsystem.service.UserService;
import com.carrentalsystem.service.VariantService;
import com.carrentalsystem.service.VehicleService;
import com.carrentalsystem.utility.Constants.ActiveStatus;
import com.carrentalsystem.utility.Constants.BookingStatus;
import com.carrentalsystem.utility.Helper;

@Component
public class BookingResource {

	private final Logger LOG = LoggerFactory.getLogger(BookingResource.class);

	@Autowired
	private BookingService bookingService;

	@Autowired
	private VehicleService vehicleService;

	@Autowired
	private VariantService variantService;

	@Autowired
	private UserService userService;

	@Autowired
	private PaymentService paymentService;

	public ResponseEntity<CommonApiResponse> addBooking(AddBookingRequest request) {

		LOG.info("Request received for adding rent book");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null || request.getStartDate() == null || request.getEndDate() == null
				|| request.getCustomerId() == null || request.getVehicleId() == null) {
			response.setResponseMessage("bad request - invalid request");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Variant variant = this.variantService.getById(request.getVehicleId());

		if (variant == null) {
			response.setResponseMessage("bad request - variant not found");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User customer = this.userService.getUserById(request.getCustomerId());

		if (customer == null) {
			response.setResponseMessage("bad request - customer not found");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		String bookingId = Helper.generateBookingId();
		String bookingTime = String
				.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

		// Convert the strings to LocalDate objects
		LocalDate startDate = LocalDate.parse(request.getStartDate());
		LocalDate endDate = LocalDate.parse(request.getEndDate());

		Integer totalDay = getTotalDaysInclusive(startDate, endDate);

		BigDecimal perDayRentPrice = variant.getPricePerDay();

		Booking booking = new Booking();
		booking.setBookingId(bookingId);
		booking.setBookingTime(bookingTime);
		booking.setStartDate(request.getStartDate());
		booking.setEndDate(request.getEndDate());
		booking.setCustomer(customer);
		booking.setVariant(variant);
		booking.setTotalDay(totalDay);
		booking.setTotalPrice(perDayRentPrice.multiply(BigDecimal.valueOf(totalDay)));
		booking.setStatus(BookingStatus.PENDING.value());

		Booking addedBooking = this.bookingService.addBooking(booking);

		if (addedBooking == null) {
			throw new BookingSaveFailedException("failed to book for vehicle rent");
		}

		response.setResponseMessage("Booking Added Successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	private static int getTotalDaysInclusive(LocalDate startDate, LocalDate endDate) {
		// Calculate the number of days between the start and end dates, including both
		// start and end dates
		return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
	}

	public ResponseEntity<BookingResponse> fetchAllBookings() {

		LOG.info("Request received for fetching all the bookings");

		BookingResponse response = new BookingResponse();

		List<Booking> bookings = this.bookingService.getAllBookings();

		if (bookings == null) {
			response.setResponseMessage("bookings not found");
			response.setSuccess(false);

			return new ResponseEntity<BookingResponse>(response, HttpStatus.OK);
		}

		response.setBookings(bookings);
		response.setResponseMessage("Booking Fetched Successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<BookingResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<BookingResponse> fetchAllCustomerBookings(Integer customerId) {

		LOG.info("Request received for fetching customer bookings");

		BookingResponse response = new BookingResponse();

		if (customerId == null) {
			response.setResponseMessage("bad request - customer id missing");
			response.setSuccess(false);

			return new ResponseEntity<BookingResponse>(response, HttpStatus.OK);
		}

		User customer = this.userService.getUserById(customerId);

		if (customer == null) {
			response.setResponseMessage("bad request - customer not found");
			response.setSuccess(false);

			return new ResponseEntity<BookingResponse>(response, HttpStatus.OK);
		}

		List<Booking> bookings = this.bookingService.getByCustomer(customer);

		if (bookings == null) {
			response.setResponseMessage("bookings not found");
			response.setSuccess(false);

			return new ResponseEntity<BookingResponse>(response, HttpStatus.OK);
		}

		response.setBookings(bookings);
		response.setResponseMessage("Booking Fetched Successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<BookingResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> updateStatusAndAssignVehicle(AddBookingRequest request) {

		LOG.info("Request received for updating booking status and assign vehicle");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null || request.getBookingId() == null || request.getStatus() == null) {
			response.setResponseMessage("bad request - invalid request");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getStatus().equals(BookingStatus.APPROVED.value()) && request.getVehicleId() == null) {
			response.setResponseMessage("Please assign Vehicle for the Booking!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Booking booking = this.bookingService.getById(request.getBookingId());

		if (booking == null) {
			response.setResponseMessage("booking not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getStatus().equals(BookingStatus.REJECTED.value())) {
			booking.setStatus(request.getStatus());
		} else if (request.getStatus().equals(BookingStatus.APPROVED.value())) {
			booking.setStatus(request.getStatus());

			Vehicle vehicle = this.vehicleService.getById(request.getVehicleId());
			booking.setVehicle(vehicle);
		}

		Booking addedBooking = this.bookingService.updateBooking(booking);

		if (addedBooking == null) {
			throw new BookingSaveFailedException("failed to update the booking");
		}

		response.setResponseMessage("Booking Updated Successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> customerPaymentForBooking(CustomerBookingPaymentRequest request) {

		LOG.info("Request received for updating booking status and assign vehicle");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null || request.getBookingId() == null || request.getCardNo() == null || request.getCvv() == null
				|| request.getExpiryDate() == null || request.getNameOnCard() == null) {
			response.setResponseMessage("bad request - invalid request");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Booking booking = this.bookingService.getById(request.getBookingId());

		if (booking == null) {
			response.setResponseMessage("booking not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		booking.setStatus(BookingStatus.PAID_AND_CONFIRMED.value());

		Payment payment = new Payment();
		payment.setAmount(booking.getTotalPrice());
		payment.setBookingId(booking.getBookingId());
		payment.setCardNo(request.getCardNo());
		payment.setCustomer(booking.getCustomer());
		payment.setCvv(request.getCvv());
		payment.setExpiryDate(request.getExpiryDate());
		payment.setNameOnCard(request.getNameOnCard());
		payment.setTransactionRefId(Helper.generateTransactionRefId());
		payment.setTransactionTime(
				String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));

		Payment addedPayment = this.paymentService.addPayment(payment);

		if (addedPayment == null) {
			throw new BookingSaveFailedException("Payment Failed for Booking!!!");
		}

		booking.setPayment(addedPayment);

		Booking addedBooking = this.bookingService.updateBooking(booking);

		if (addedBooking == null) {
			throw new BookingSaveFailedException("Payment Failed for Booking!!!");
		}

		response.setResponseMessage("Congratulations!!! Payment Successful, Booking Confirmed!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> cancelbooking(AddBookingRequest request) {

		LOG.info("Request received for cancelling the booking");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null || request.getBookingId() == null || request.getStatus() == null) {
			response.setResponseMessage("bad request - invalid request");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Booking booking = this.bookingService.getById(request.getBookingId());

		if (booking == null) {
			response.setResponseMessage("booking not found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		booking.setStatus(ActiveStatus.DEACTIVATED.value());

		Booking addedBooking = this.bookingService.updateBooking(booking);

		if (addedBooking == null) {
			throw new BookingSaveFailedException("failed to cancel the booking");
		}

		response.setResponseMessage("Booking Cancelled Successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

}
