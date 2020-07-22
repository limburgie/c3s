package be.webfactor.c3s.shopping;

public class ShoppingCartThreadLocal extends ThreadLocal<String> {

	private static final ShoppingCart DEFAULT = new ShoppingCart();

	private static final ThreadLocal<ShoppingCart> SHOPPING_CART = ThreadLocal.withInitial(() -> DEFAULT);

	public static void setShoppingCart(ShoppingCart shoppingCart) {
		SHOPPING_CART.set(shoppingCart);
	}

	public static ShoppingCart getShoppingCart() {
		ShoppingCart result = SHOPPING_CART.get();

		return result == null ? DEFAULT : result;
	}
}