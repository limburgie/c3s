package be.webfactor.c3s.shopping;

import java.io.Serializable;

public class ShippingAddress implements Serializable {

	private final String street;
	private final String postalCode;
	private final String place;
	private final String country;
	private final double shipmentCost;

	public ShippingAddress() {
		this("", "", "", "", 0.0);
	}

	public ShippingAddress(String street, String postalCode, String place, String country, double shipmentCost) {
		this.street = street;
		this.place = place;
		this.postalCode = postalCode;
		this.country = country;
		this.shipmentCost = shipmentCost;
	}

	public String getStreet() {
		return street;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public String getPlace() {
		return place;
	}

	public String getCountry() {
		return country;
	}

	public double getShipmentCost() {
		return shipmentCost;
	}
}
