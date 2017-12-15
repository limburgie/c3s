package be.webfactor.c3s.master.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import be.webfactor.c3s.repository.RepositoryConnection;

@Service
@Scope(scopeName = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MasterServiceFactory {

	@Autowired private List<MasterService> masterServices;

	public MasterService forRepositoryConnection(RepositoryConnection connection) {
		MasterService masterService = getMasterService(connection);
		masterService.initialize(connection);

		return masterService;
	}

	private MasterService getMasterService(RepositoryConnection connection) {
		for (MasterService masterService : masterServices) {
			if (connection.getType() == masterService.getType()) {
				return masterService;
			}
		}

		throw new MasterServiceNotFoundException(connection);
	}
}
