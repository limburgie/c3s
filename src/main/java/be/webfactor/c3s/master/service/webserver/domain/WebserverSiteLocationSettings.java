package be.webfactor.c3s.master.service.webserver.domain;

public class WebserverSiteLocationSettings {

	private String locale;
	private String timeZone;

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
}