package be.webfactor.c3s.shopping;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class ProductConfiguration implements Serializable {

	private final String productId;
	private final Map<String, String> options;
	private final double price;

	public ProductConfiguration(String productId, Map<String, String> options, double price) {
		this.productId = productId;
		this.options = options;
		this.price = price;
	}

	public String getProductId() {
		return productId;
	}

	public Map<String, String> getOptions() {
		if (options == null) {
			return Collections.emptyMap();
		}

		return options;
	}

	public double getPrice() {
		return price;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProductConfiguration that = (ProductConfiguration) o;
		return productId.equals(that.productId) &&
				getOptions().equals(that.options) &&
				price == that.price;
	}

	@Override
	public int hashCode() {
		return Objects.hash(productId, options, price);
	}
}
