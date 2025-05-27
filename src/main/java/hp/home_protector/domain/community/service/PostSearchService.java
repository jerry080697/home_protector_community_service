package hp.home_protector.domain.community.service;

import hp.home_protector.domain.community.dto.PostResponseDTO;
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
     * 키워드 기반 전체 검색
     * 카테고리 파라미터 없이, title 또는 content 에 키워드가 포함된 문서를 반환
     */
    public List<PostResponseDTO> search(String keyword) {
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
                )
                .collect(Collectors.toList());
    }
}