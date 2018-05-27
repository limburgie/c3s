package be.webfactor.c3s.registry.service.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import be.webfactor.c3s.registry.service.database.domain.PersistedRepository;

public interface PersistedRepositoryRepository extends JpaRepository<PersistedRepository, Long> {

	PersistedRepository findByVirtualHost(String virtualHost);
}
