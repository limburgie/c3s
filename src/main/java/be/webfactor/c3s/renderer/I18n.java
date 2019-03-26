package be.webfactor.c3s.renderer;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class I18n {

	private ResourceBundle resourceBundle;

	public I18n(ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;
	}

	public String get(String key) {
		return resourceBundle.getString(key);
	}

	public String format(String key, Object... params) {
		return MessageFormat.format(get(key), params);
	}
}
