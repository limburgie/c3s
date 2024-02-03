package be.webfactor.c3s.controller;

import be.webfactor.c3s.form.FormHandler;
import be.webfactor.c3s.form.FormHandlerFactory;
import be.webfactor.c3s.form.FormParams;
import be.webfactor.c3s.master.domain.Form;
import be.webfactor.c3s.master.service.MasterService;
import be.webfactor.c3s.master.service.MasterServiceFactory;
import be.webfactor.c3s.registry.domain.MasterRepository;
import be.webfactor.c3s.registry.service.RepositoryRegistryFactory;
import be.webfactor.c3s.shopping.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@Order(1)
public class ShoppingCartController {

	private static final String OPTION_PREFIX = "option_";

	@Autowired private ShoppingCartSerializer shoppingCartSerializer;
	@Autowired private RepositoryRegistryFactory repositoryRegistryFactory;
	@Autowired private MasterServiceFactory masterServiceFactory;
	@Autowired private FormHandlerFactory formHandlerFactory;

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
	public void updateDetails(@RequestParam("name") String name, @RequestParam("email") String email, @RequestParam(value = "street", required = false) String street,
							  @RequestParam(value = "postalCode", required = false) String postalCode, @RequestParam(value = "place", required = false) String place,
							  @RequestParam(value = "countryAndShipmentCost", required = false) String countryAndShipmentCost,
							  @RequestParam("remarks") String remarks, @CookieValue(value = ShoppingCart.COOKIE_NAME, required = false) String shoppingCartEncoded, HttpServletRequest request, HttpServletResponse response) {
		ShoppingCart shoppingCart = shoppingCartSerializer.deserialize(shoppingCartEncoded);
		shoppingCart.setPersonalDetails(new PersonalDetails(name, email));

		String country = null;
		double shipmentCost = 0.0;

		if (countryAndShipmentCost != null) {
			String[] countryAndShipmentCostParts = countryAndShipmentCost.split("_");
			country = countryAndShipmentCostParts[0];
			shipmentCost = Double.parseDouble(countryAndShipmentCostParts[1]);
		}

		shoppingCart.setShippingAddress(new ShippingAddress(street, postalCode, place, country, shipmentCost));
		shoppingCart.setRemarks(remarks);

		setCookieAndRedirect(shoppingCart, request, response);
	}

	@RequestMapping(value = "/order", method = RequestMethod.POST)
	public void order(@CookieValue(value = ShoppingCart.COOKIE_NAME, required = false) String shoppingCartEncoded, HttpServletRequest request, HttpServletResponse response) {
		ShoppingCart shoppingCart = shoppingCartSerializer.deserialize(shoppingCartEncoded);
		ShoppingCartThreadLocal.setShoppingCart(shoppingCart);

		MasterService masterService = getMasterService(request);
		FormHandler formHandler = formHandlerFactory.forMasterService(masterService);
		Form form = masterService.getForm(request.getParameter("form"));

		FormParams formParams = new FormParams(request);
		formParams.put("name", shoppingCart.getPersonalDetails().getName());
		formParams.put("email", shoppingCart.getPersonalDetails().getEmail());

		formHandler.handleForm(form, formParams);

		shoppingCart.reset();
		setCookieAndRedirect(shoppingCart, request, response);
	}

	private MasterService getMasterService(HttpServletRequest request) {
		MasterRepository repository = repositoryRegistryFactory.getRegistry().findMasterRepository(request.getServerName());

		return masterServiceFactory.forRepositoryConnection(repository.getConnection());
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
