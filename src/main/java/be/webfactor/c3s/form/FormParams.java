package be.webfactor.c3s.form;

import jakarta.servlet.http.HttpServletRequest;

import java.util.*;

public class FormParams extends HashMap<String, Object> {

	private final HttpServletRequest request;
	private final Map<String, Object> additionalValues = new HashMap<>();

	public FormParams(HttpServletRequest request) {
		this.request = request;
	}

	public String getValue(String paramName) {
		String result = request.getParameter(paramName);

		if (result == null) {
			return (String) additionalValues.get(paramName);
		}

		return result;
	}

	public Object get(Object paramName) {
		Object result = getFromRequest((String) paramName);

		if (result == null) {
			return additionalValues.get(paramName);
		}

		return result;
	}

	private Object getFromRequest(String paramName) {
		String[] values = request.getParameterValues(paramName);

		if (values == null) {
			return null;
		}

		if (values.length > 1) {
			return Arrays.asList(values);
		}

		return values[0];
	}

	public Object put(String paramName, Object value) {
		return additionalValues.put(paramName, value);
	}

	public boolean containsKey(Object key) {
		return request.getParameterMap().containsKey(key);
	}
}
