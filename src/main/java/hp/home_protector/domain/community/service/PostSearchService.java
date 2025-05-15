package hp.home_protector.domain.community.service;

import hp.home_protector.domain.community.dto.PostResponseDTO;
import hp.home_protector.domain.community.entity.BoardType;
import hp.home_protector.domain.community.entity.elasticsearch.PostEsDocument;
import hp.home_protector.domain.community.repository.PostEsRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostSearchService {
    private final PostEsRepository esRepo;

    public PostSearchService(PostEsRepository esRepo) {
        this.esRepo = esRepo;
    }

    /**
     * boardType(FREE 또는 INFO) 에서
     * title 이나 content 에 keyword 가 포함된 문서만 조회
     */
    public List<PostResponseDTO> search(String keyword, BoardType category) {
        return esRepo.findByTitleContainingOrContentContaining(keyword, keyword)
                .stream()
                .map(doc -> PostResponseDTO.builder()
                        .postId(doc.getPostId())
                        .userId(doc.getUserId())
                        .title(doc.getTitle())
                        .content(doc.getContent())
                        .category(doc.getBoardType())
                        .attachments(doc.getAttachments())
                        .likeCount(doc.getLikeCount())
                        .build()
                ).collect(Collectors.toList());
    }
}
