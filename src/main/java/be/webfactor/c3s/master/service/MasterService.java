package be.webfactor.c3s.master.service;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import be.webfactor.c3s.master.domain.Form;
import be.webfactor.c3s.master.domain.MailSettings;
import be.webfactor.c3s.master.domain.Page;
import be.webfactor.c3s.master.domain.TemplateEngine;
import be.webfactor.c3s.repository.RepositoryConnection;
import be.webfactor.c3s.repository.RepositoryType;

/**
 * The master service is an abstraction for a backend that stores the page structure, themes and assets of a website.
 * It is initialized with a repository connection and has methods to retrieve page, theme and asset information.
 */
public interface MasterService {

	/**
	 * Initializes this master service with the given repository connection.
	 * The master service can use this connection to bootstrap its configuration.
	 */
	void initialize(RepositoryConnection connection);

	/**
	 * Returns the site name for this master service.
	 */
	String getSiteName();

	/**
	 * Retrieves all root pages that are stored inside this master service.
	 * Each root page contains information about its children but the content inside the page is not retrieved (use getPage(String) for that purpose).
	 */
	List<Page> getPages(boolean all);

	/**
	 * Retrieves the template engine in which the templates in this master service are written.
	 */
	TemplateEngine getTemplateEngine();

	/**
	 * Retrieves the repository connection that this master service was bootstrapped with.
	 */
	RepositoryConnection getRepositoryConnection();

	/**
	 * Retrieves the email configuration properties.
	 */
	MailSettings getMailSettings();

	/**
	 * Retrieves the page identified by the provided friendly URL, including its contents and its child pages.
	 * The content of the child pages is not provided.
	 */
	Page getPage(String friendlyUrl);

	/**
	 * Retrieves the index page of this master service, including its contents.
	 * The index page is displayed if a user visits the root of the website.
	 */
	Page getIndexPage();

	/**
	 * Retrieves the error page of this master service, including its contents.
	 * The error page is displayed if an error occurs while providing this page.
	 */
	Page getErrorPage();

	/**
	 * Retrieves the server-relative URL to the asset, identified by the given (relative) asset path.
	 * The returned URL is served by the PageController asset endpoint.
	 */
	String getAssetUrl(String assetPath);

	/**
	 * Retrieves the form with the given name.
	 */
	Form getForm(String name);

	/**
	 * Retrieves the repository type of this master service.
	 */
	RepositoryType getType();

	/**
	 * Returns the base URL or URI from which all site resources are loaded.
	 */
	String getBaseUrl();

	/**
	 * Returns the resource bundle containing resource keys for the currently active locale.
	 */
	ResourceBundle getResourceBundle();

	/**
	 * Returns the supported locales for this service.
	 */
	List<Locale> getLocales();

	/**
	 * Returns the UTF-8 text content of a resource relative to the site's base path.
	 */
	String readResource(String relativePath);

	/**
	 * Returns the raw bytes of an asset relative to the site's base path.
	 */
	byte[] readAsset(String relativePath);
}
