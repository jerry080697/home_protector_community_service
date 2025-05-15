package hp.home_protector.domain.community.repository;

import hp.home_protector.domain.community.entity.elasticsearch.PostEsDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface PostEsRepository
        extends ElasticsearchRepository<PostEsDocument, String> {
    List<PostEsDocument> findByTitleContainingOrContentContaining(String title, String content);
}

