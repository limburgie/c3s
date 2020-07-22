package be.webfactor.c3s.shopping;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class ShoppingCartSerializer {

	public String serialize(ShoppingCart shoppingCart) {
		if (shoppingCart == null) {
			return "";
		}

		return new String(Base64.getEncoder().encode(new Gson().toJson(shoppingCart).getBytes()));
	}

	public ShoppingCart deserialize(String input) {
		if (input == null) {
			return new ShoppingCart();
		}

		return new Gson().fromJson(new String(Base64.getDecoder().decode(input)), ShoppingCart.class);
	}
}
