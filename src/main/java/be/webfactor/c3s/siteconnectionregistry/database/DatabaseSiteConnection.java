package be.webfactor.c3s.siteconnectionregistry.database;

import be.webfactor.c3s.siteassetstore.SiteAssetStoreType;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "site_connection")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DatabaseSiteConnection {

	@Id
	@EqualsAndHashCode.Include
	private Long id;

	private String virtualHost;

	private String name;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "varchar(16)")
	private SiteAssetStoreType type;

	private String url;

	private String principal;

	private String secret;
}
