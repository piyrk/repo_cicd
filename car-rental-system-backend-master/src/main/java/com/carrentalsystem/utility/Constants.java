package com.carrentalsystem.utility;

public class Constants {

	public enum UserRole {
		ROLE_ADMIN("Admin"), ROLE_CUSTOMER("Customer"), ROLE_DRIVER("Driver");

		private String role;

		private UserRole(String role) {
			this.role = role;
		}

		public String value() {
			return this.role;
		}
	}

	public enum ActiveStatus {
		ACTIVE("Active"), DEACTIVATED("Deactivated");

		private String status;

		private ActiveStatus(String status) {
			this.status = status;
		}

		public String value() {
			return this.status;
		}
	}

	public enum FuelType {
		GASOLINE("Gasoline"), DIESEL("Diesel"), ELECTRIC("Electric"), HYBRID("Hybrid"), PETROL("Petrol");

		private String type;

		private FuelType(String type) {
			this.type = type;
		}

		public String value() {
			return this.type;
		}
		
	}
	
	public enum IsAC {
		YES("Yes"),
		NO("No");

		private String type;

		private IsAC(String type) {
			this.type = type;
		}

		public String value() {
			return this.type;
		}
		
	}
	
	public enum BookingStatus {
		PENDING("Pending"), REJECTED("Rejected") , CANCELLED("Cancelled") , APPROVED("Approved"), PAID_AND_CONFIRMED("Paid & Confirmed");

		private String status;

		private BookingStatus(String status) {
			this.status = status;
		}

		public String value() {
			return this.status;
		}
	}

}
