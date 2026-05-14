package be.webfactor.c3s.contentrepository.paginea;

import be.webfactor.c3s.contentrepository.ContentRepositoryConnection;
import be.webfactor.c3s.contentrepository.ContentRepositoryType;

import be.webfactor.c3s.contentrepository.ContentRepository;
import be.webfactor.c3s.contentrepository.domain.ContentApi;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("request")
@RequiredArgsConstructor
public class PagineaContentRepository implements ContentRepository {

    private ContentApi api;

    @Override
    public void initialize(ContentRepositoryConnection connection) {
        api = new PagineaContentApi(connection.getRepositoryId());
    }

    @Override
    public ContentApi getApi() {
        return api;
    }

    @Override
    public ContentRepositoryType getType() {
        return ContentRepositoryType.PAGINEA;
    }
}
