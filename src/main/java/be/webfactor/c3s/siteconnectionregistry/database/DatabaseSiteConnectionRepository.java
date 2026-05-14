package be.webfactor.c3s.siteconnectionregistry.database;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DatabaseSiteConnectionRepository extends JpaRepository<DatabaseSiteConnection, Long> {

	DatabaseSiteConnection findByVirtualHost(String virtualHost);
}
