package be.webfactor.c3s.siteassetstore;

import be.webfactor.c3s.siteassetstore.domain.Form;
import be.webfactor.c3s.siteassetstore.domain.mail.MailSettings;
import be.webfactor.c3s.siteassetstore.domain.Page;
import be.webfactor.c3s.templateparser.TemplateEngine;
import be.webfactor.c3s.contentrepository.ContentRepositoryConnection;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The site asset store is an abstraction for a backend that stores the page structure, themes and assets of a website.
 * It is initialized with a site asset store connection and has methods to retrieve page, theme and asset information.
 */
public interface SiteAssetStore {

	/**
	 * Initializes this site asset store with the given connection.
	 * The site asset store can use this connection to bootstrap its configuration.
	 */
	void initialize(SiteAssetStoreConnection connection);

	/**
	 * Returns the site name for this site asset store.
	 */
	String getSiteName();

	/**
	 * Retrieves all root pages that are stored inside this site asset store.
	 * Each root page contains information about its children but the content inside the page is not retrieved (use getPage(String) for that purpose).
	 */
	List<Page> getPages(boolean all);

	/**
	 * Retrieves the template engine in which the templates in this site asset store are written.
	 */
	TemplateEngine getTemplateEngine();

	/**
	 * Retrieves the content repository connection configured in the site's asset store configuration, if any.
	 */
	ContentRepositoryConnection getContentRepositoryConnection();

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
	 * Retrieves the index page of this site asset store, including its contents.
	 * The index page is displayed if a user visits the root of the website.
	 */
	Page getIndexPage();

	/**
	 * Retrieves the error page of this site asset store, including its contents.
	 * The error page is displayed if an error occurs while providing this page.
	 */
	Page getErrorPage();

	/**
	 * Retrieves the form with the given name.
	 */
	Form getForm(String name);

	/**
	 * Retrieves the type of this site asset store.
	 */
	SiteAssetStoreType getType();

	/**
	 * Returns the base URL or URI from which all site resources are loaded.
	 */
	String getBaseUrl();

	/**
	 * Returns the resource bundle containing resource keys for the currently active locale.
	 */
	ResourceBundle getResourceBundle();

	/**
	 * Returns the supported locales for this site asset store.
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
