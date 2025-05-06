package hp.home_protector.domain.community.service;

import hp.home_protector.domain.community.dto.PostRequestDTO;
import hp.home_protector.domain.community.dto.PostResponseDTO;
import hp.home_protector.domain.community.entity.Post;
import hp.home_protector.domain.community.entity.BoardType;
import hp.home_protector.domain.community.repository.PostRepository;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post createPost(String userIdHex, PostRequestDTO dto) {
        ObjectId userId = new ObjectId(userIdHex);
        List<String> attachments = dto.getAttachments() != null
                ? dto.getAttachments()
                : Collections.emptyList();

        Post post = Post.builder()
                .userId(userId)
                .boardType(dto.getBoardType())
                .title(dto.getTitle())
                .content(dto.getContent())
                .attachments(attachments)
                .likeCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return postRepository.save(post);
    }

    public List<PostResponseDTO> getPostsByCategory(BoardType category) {
        return postRepository.findByBoardType(
                        category,
                        Sort.by(Sort.Direction.DESC, "createdAt")
                ).stream()
                .map(p -> PostResponseDTO.builder()
                        .postId(p.getPostId().toHexString())
                        .userId(p.getUserId().toHexString())
                        .title(p.getTitle())
                        .content(p.getContent())
                        .boardType(p.getBoardType())
                        .attachments(p.getAttachments())
                        .likeCount(p.getLikeCount())
                        .build()
                )
                .collect(Collectors.toList());
    }
}
