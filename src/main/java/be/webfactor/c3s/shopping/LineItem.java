package be.webfactor.c3s.shopping;

public class LineItem {

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
}
