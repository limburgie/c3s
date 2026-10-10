package be.webfactor.c3s.controller;

import be.webfactor.c3s.controller.helper.site.SiteAssetStoreResolver;
import be.webfactor.c3s.form.FormHandler;
import be.webfactor.c3s.form.FormHandlerFactory;
import be.webfactor.c3s.form.FormParams;
import be.webfactor.c3s.shopping.ShoppingCart;
import be.webfactor.c3s.shopping.ShoppingCartService;
import be.webfactor.c3s.siteassetstore.SiteAssetStore;
import be.webfactor.c3s.siteassetstore.domain.Form;
import be.webfactor.c3s.siteassetstore.domain.LocaleContext;
import be.webfactor.c3s.siteassetstore.domain.LocationThreadLocal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles form submissions of the requested site and redirects back to the page the form was submitted from.
 */
@RestController
@RequiredArgsConstructor
public class FormController {

	private static final String SUBMIT_URI = "/submit";

	private final SiteAssetStoreResolver siteAssetStoreResolver;
	private final FormHandlerFactory formHandlerFactory;
	private final ShoppingCartService shoppingCartService;

	@PostMapping(value = SUBMIT_URI)
	public void submitForm(HttpServletRequest request, HttpServletResponse response, @CookieValue(value = ShoppingCart.COOKIE_NAME, required = false) String shoppingCartEncoded) {
		shoppingCartService.initializeShoppingCart(shoppingCartEncoded);

		SiteAssetStore siteAssetStore = siteAssetStoreResolver.forRequest(request);

		String locale = request.getParameter("locale");
		if (locale != null) {
			siteAssetStore.getLocales().stream().filter(l -> l.toString().equals(locale)).findFirst()
					.ifPresent(l -> LocationThreadLocal.setLocaleContext(new LocaleContext(l)));
		}

		FormHandler formHandler = formHandlerFactory.forSiteAssetStore(siteAssetStore);
		Form form = siteAssetStore.getForm(request.getParameter("form"));

		formHandler.handleForm(form, new FormParams(request));

		String referer = request.getParameter("referer");
		response.setHeader("Location", referer == null ? "/" : referer);
		response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
	}
}
