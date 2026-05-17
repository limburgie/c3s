package be.webfactor.c3s.siteconnectionregistry;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteConnectionRegistryFactory {

	private final List<SiteConnectionRegistry> registries;

	@Value("${c3s.siteconnectionregistry.type:#{null}}")
	private String registryType;

	@Getter
    private SiteConnectionRegistry registry;

	@PostConstruct
	public void init() {
        registry = registries.stream()
				.filter(r -> r.getType() == SiteConnectionRegistryType.forKey(registryType))
				.findFirst()
				.orElse(null);
	}
}
