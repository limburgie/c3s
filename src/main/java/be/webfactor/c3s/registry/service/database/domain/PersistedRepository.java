package be.webfactor.c3s.registry.service.database.domain;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import be.webfactor.c3s.repository.RepositoryType;

@Entity
public class PersistedRepository {

	@Id
	private Long id;

	private String virtualHost;

	private String name;

	@Enumerated(EnumType.STRING)
	private RepositoryType type;

	private String connectionId;

	private String accessToken;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVirtualHost() {
		return virtualHost;
	}

	public void setVirtualHost(String virtualHost) {
		this.virtualHost = virtualHost;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RepositoryType getType() {
		return type;
	}

	public void setType(RepositoryType type) {
		this.type = type;
	}

	public String getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(String connectionId) {
		this.connectionId = connectionId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		PersistedRepository that = (PersistedRepository) o;
		return Objects.equals(id, that.id);
	}

	public int hashCode() {
		return Objects.hash(id);
	}
}
