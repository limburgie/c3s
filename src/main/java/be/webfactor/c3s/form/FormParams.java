package be.webfactor.c3s.form;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FormParams extends HashMap<String, Object> {

	private final HttpServletRequest request;

	public FormParams(HttpServletRequest request) {
		this.request = request;
	}

	public String getValue(String paramName) {
		return request.getParameter(paramName);
	}

	public List<String> getValues(String paramName) {
		String[] values = request.getParameterValues(paramName);

		if (values == null) {
			return Collections.emptyList();
		}

		return Arrays.asList(values);
	}

	public Object get(Object key) {
		String[] values = request.getParameterValues((String) key);

		if (values == null) {
			return null;
		}

		if (values.length > 1) {
			return Arrays.asList(values);
		}

		return values[0];
	}

	public boolean containsKey(Object key) {
		return request.getParameterMap().containsKey(key);
	}
}
