package be.webfactor.c3s.shopping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCart implements Serializable {

	public static final String COOKIE_NAME = "C3S_CART";

	private final List<LineItem> items;

	public ShoppingCart() {
		items = new ArrayList<>();
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

		return total;
	}

	public boolean isEmpty() {
		return getTotalItemCount() == 0;
	}

	public int getTotalItemCount() {
		return getLineItems().stream().mapToInt(LineItem::getAmount).sum();
	}
}
