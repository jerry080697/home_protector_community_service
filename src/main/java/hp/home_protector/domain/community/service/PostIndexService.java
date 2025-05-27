package hp.home_protector.domain.community.service;

import hp.home_protector.domain.community.entity.elasticsearch.PostEsDocument;
import hp.home_protector.domain.community.entity.mongoDB.Post;
import hp.home_protector.domain.community.repository.PostEsRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
public class PostIndexService {

    private final PostEsRepository esRepo;

    public PostIndexService(PostEsRepository esRepo) {
        this.esRepo = esRepo;
    }

    /** MongoDB Post → Elasticsearch 색인 */
    public void index(Post mongo) {
        // LocalDateTime → OffsetDateTime 변환 (UTC 기준)
        OffsetDateTime odt = mongo.getCreatedAt().atOffset(ZoneOffset.UTC);

        PostEsDocument doc = PostEsDocument.builder()
                .postId(mongo.getPostId().toHexString())
                .userId(mongo.getUserId())            // String userId 그대로 사용
                .boardType(mongo.getBoardType())
                .title(mongo.getTitle())
                .content(mongo.getContent())
                .attachments(mongo.getAttachments())
                .likeCount(mongo.getLikeCount())
                .createdAt(odt)
                .build();

        esRepo.save(doc);
    }

    /** MongoDB 에서 게시글 삭제 시 Elasticsearch 에서도 문서 삭제 */
    public void delete(String postIdHex) {
        esRepo.deleteById(postIdHex);
    }
}
