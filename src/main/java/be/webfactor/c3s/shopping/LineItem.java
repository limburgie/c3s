package be.webfactor.c3s.shopping;

import java.io.Serializable;

public class LineItem implements Serializable {

	private final ProductConfiguration productConfiguration;
	private int amount;

	public LineItem(ProductConfiguration productConfiguration, int amount) {
		this.productConfiguration = productConfiguration;
		this.amount = amount;
	}

	public ProductConfiguration getProductConfiguration() {
		return productConfiguration;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public double getPrice() {
		return productConfiguration.getPrice() * amount;
	}
}
