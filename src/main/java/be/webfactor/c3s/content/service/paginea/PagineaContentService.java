package be.webfactor.c3s.content.service.paginea;

import be.webfactor.c3s.content.service.ContentService;
import be.webfactor.c3s.content.service.domain.ContentApi;
import be.webfactor.c3s.repository.RepositoryConnection;
import be.webfactor.c3s.repository.RepositoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("request")
@RequiredArgsConstructor
public class PagineaContentService implements ContentService {

    private ContentApi api;

    @Override
    public void initialize(RepositoryConnection connection) {
        api = new PagineaContentApi(connection.getRepositoryId());
    }

    @Override
    public ContentApi getApi() {
        return api;
    }

    @Override
    public RepositoryType getType() {
        return RepositoryType.PAGINEA;
    }
}
