package be.webfactor.c3s.controller;

import be.webfactor.c3s.shopping.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@Order(1)
public class ShoppingCartController {

	private static final String OPTION_PREFIX = "option_";

	@Autowired private ShoppingCartSerializer shoppingCartSerializer;

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public void addLineItem(@CookieValue(value = ShoppingCart.COOKIE_NAME, required = false) String shoppingCartEncoded, HttpServletRequest request, HttpServletResponse response) {
		ShoppingCart shoppingCart = shoppingCartSerializer.deserialize(shoppingCartEncoded);
		shoppingCart.add(parseProductConfig(request));

		setCookieAndRedirect(shoppingCart, request, response);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public void deleteLineItem(@CookieValue(value = ShoppingCart.COOKIE_NAME, required = false) String shoppingCartEncoded, HttpServletRequest request, HttpServletResponse response) {
		ShoppingCart shoppingCart = shoppingCartSerializer.deserialize(shoppingCartEncoded);
		shoppingCart.delete(parseProductConfig(request));

		setCookieAndRedirect(shoppingCart, request, response);
	}

	@RequestMapping(value = "/update/amount", method = RequestMethod.POST)
	public void updateLineItem(@RequestParam("amount") int amount, @CookieValue(value = ShoppingCart.COOKIE_NAME, required = false) String shoppingCartEncoded, HttpServletRequest request, HttpServletResponse response) {
		ShoppingCart shoppingCart = shoppingCartSerializer.deserialize(shoppingCartEncoded);
		shoppingCart.updateAmount(parseProductConfig(request), amount);

		setCookieAndRedirect(shoppingCart, request, response);
	}

	@RequestMapping(value = "/update/payment", method = RequestMethod.POST)
	public void updatePaymentType(@RequestParam("paymentType") String paymentType, @CookieValue(value = ShoppingCart.COOKIE_NAME, required = false) String shoppingCartEncoded, HttpServletRequest request, HttpServletResponse response) {
		ShoppingCart shoppingCart = shoppingCartSerializer.deserialize(shoppingCartEncoded);
		shoppingCart.setPaymentType(PaymentType.valueOf(paymentType));

		setCookieAndRedirect(shoppingCart, request, response);
	}

	@RequestMapping(value = "/update/shipment", method = RequestMethod.POST)
	public void updateShipmentType(@RequestParam("shipmentType") String shipmentType, @CookieValue(value = ShoppingCart.COOKIE_NAME, required = false) String shoppingCartEncoded, HttpServletRequest request, HttpServletResponse response) {
		ShoppingCart shoppingCart = shoppingCartSerializer.deserialize(shoppingCartEncoded);
		shoppingCart.setShipmentType(ShipmentType.valueOf(shipmentType));

		setCookieAndRedirect(shoppingCart, request, response);
	}

	@RequestMapping(value = "/update/details", method = RequestMethod.POST)
	public void updateDetails(@RequestParam("name") String name, @RequestParam("email") String email, @RequestParam("street") String street,
							  @RequestParam("postalCode") String postalCode, @RequestParam("place") String place, @RequestParam("countryAndShipmentCost") String countryAndShipmentCost,
							  @RequestParam("remarks") String remarks, @CookieValue(value = ShoppingCart.COOKIE_NAME, required = false) String shoppingCartEncoded, HttpServletRequest request, HttpServletResponse response) {
		ShoppingCart shoppingCart = shoppingCartSerializer.deserialize(shoppingCartEncoded);
		shoppingCart.setPersonalDetails(new PersonalDetails(name, email));

		String[] countryAndShipmentCostParts = countryAndShipmentCost.split("_");
		String country = countryAndShipmentCostParts[0];
		double shipmentCost = Double.parseDouble(countryAndShipmentCostParts[1]);

		shoppingCart.setShippingAddress(new ShippingAddress(street, postalCode, place, country, shipmentCost));
		shoppingCart.setRemarks(remarks);

		setCookieAndRedirect(shoppingCart, request, response);
	}

	private ProductConfiguration parseProductConfig(HttpServletRequest request) {
		Map<String, String> options = new LinkedHashMap<>();

		for (String name : request.getParameterMap().keySet()) {
			if (name.startsWith(OPTION_PREFIX)) {
				options.put(name.substring(OPTION_PREFIX.length()), request.getParameter(name));
			}
		}

		String productId = request.getParameter("productId");
		String priceStr = request.getParameter("price");
		double price = (priceStr == null) ? 0 : Double.parseDouble(priceStr);

		return new ProductConfiguration(productId, options, price);
	}

	private void setCookieAndRedirect(ShoppingCart shoppingCart, HttpServletRequest request, HttpServletResponse response) {
		response.addCookie(createShoppingCartCookie(shoppingCart));
		response.setHeader("Location", getReferer(request));
		response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
	}

	private Cookie createShoppingCartCookie(ShoppingCart shoppingCart) {
		Cookie cookie = new Cookie(ShoppingCart.COOKIE_NAME, shoppingCartSerializer.serialize(shoppingCart));

		cookie.setMaxAge(Integer.MAX_VALUE);
		cookie.setHttpOnly(true);
		cookie.setPath("/");

		return cookie;
	}

	private String getReferer(HttpServletRequest request) {
		String refererParam = request.getParameter("referer");

		if (refererParam != null) {
			return refererParam;
		}

		String referer = request.getHeader("Referer");
		String host = request.getScheme() + "://" + request.getHeader("Host");

		if (referer == null || !referer.startsWith(host)) {
			return host;
		}

		return referer;
	}
}
