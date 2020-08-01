package be.webfactor.c3s.shopping;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCart implements Serializable {

	public static final String COOKIE_NAME = "C3S_CART";

	private List<LineItem> items;
	private PaymentType paymentType;
	private ShipmentType shipmentType;
	private PersonalDetails personalDetails = new PersonalDetails();
	private ShippingAddress shippingAddress = new ShippingAddress();
	private String remarks;
	private String orderNumber;

	public ShoppingCart() {
		reset();
		setPaymentType(PaymentType.TRANSFER);
		setShipmentType(ShipmentType.SHIPMENT);
	}

	public void add(ProductConfiguration productConfiguration) {
		for (LineItem item : items) {
			if (item.getProductConfiguration().equals(productConfiguration)) {
				item.setAmount(item.getAmount() + 1);
				return;
			}
		}

		items.add(new LineItem(productConfiguration, 1));
	}

	public void delete(ProductConfiguration productConfiguration) {
		items.removeIf(item -> item.getProductConfiguration().equals(productConfiguration));
	}

	public void remove(ProductConfiguration productConfiguration) {
		items.removeIf(item -> item.getProductConfiguration().equals(productConfiguration));
	}

	public void updateAmount(ProductConfiguration productConfiguration, int amount) {
		if (amount < 1) {
			remove(productConfiguration);
			return;
		}

		for (LineItem item : items) {
			if (item.getProductConfiguration().equals(productConfiguration)) {
				item.setAmount(amount);
				return;
			}
		}
	}

	public List<LineItem> getLineItems() {
		return items;
	}

	public double getTotalPrice(boolean shipmentIncluded) {
		double total = 0;

		for (LineItem item : items) {
			total += item.getProductConfiguration().getPrice() * item.getAmount();
		}

		if (shipmentIncluded) {
			total += shippingAddress.getShipmentCost();
		}

		return total;
	}

	public boolean isEmpty() {
		return getTotalItemCount() == 0;
	}

	public int getTotalItemCount() {
		return getLineItems().stream().mapToInt(LineItem::getAmount).sum();
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;

		if (paymentType == PaymentType.CASH_UPON_COLLECTION) {
			shipmentType = ShipmentType.COLLECTION;
		}
	}

	public ShipmentType getShipmentType() {
		return shipmentType;
	}

	public void setShipmentType(ShipmentType shipmentType) {
		this.shipmentType = shipmentType;
	}

	public PersonalDetails getPersonalDetails() {
		return personalDetails;
	}

	public void setPersonalDetails(PersonalDetails personalDetails) {
		this.personalDetails = personalDetails;
	}

	public ShippingAddress getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(ShippingAddress shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public void reset() {
		items = new ArrayList<>();
		orderNumber = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
		setRemarks("");
	}
}
