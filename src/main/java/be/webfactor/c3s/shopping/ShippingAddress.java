package be.webfactor.c3s.shopping;

import java.io.Serializable;

public class ShippingAddress implements Serializable {

	private final String name;
	private final String street;
	private final String city;
	private final String postCode;
	private final String country;

	public ShippingAddress(String name, String street, String city, String postCode, String country) {
		this.name = name;
		this.street = street;
		this.city = city;
		this.postCode = postCode;
		this.country = country;
	}

	public String getName() {
		return name;
	}

	public String getStreet() {
		return street;
	}

	public String getCity() {
		return city;
	}

	public String getPostCode() {
		return postCode;
	}

	public String getCountry() {
		return country;
	}
}
