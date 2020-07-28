package be.webfactor.c3s.renderer;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class I18n {

	private ResourceBundle resourceBundle;

	public I18n(ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;
	}

	public String get(String key) {
		try {
			return resourceBundle.getString(key);
		} catch (MissingResourceException | NullPointerException e) {
			return String.format("???%s???", key);
		}
	}

	public String format(String key, Object... params) {
		return MessageFormat.format(get(key), params);
	}
}