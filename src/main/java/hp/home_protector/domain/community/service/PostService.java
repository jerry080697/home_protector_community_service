package hp.home_protector.domain.community.service;

import hp.home_protector.domain.community.dto.*;
import hp.home_protector.domain.community.entity.Comment;
import hp.home_protector.domain.community.entity.Post;
import hp.home_protector.domain.community.entity.BoardType;
import hp.home_protector.domain.community.entity.User;
import hp.home_protector.domain.community.repository.CommentRepository;
import hp.home_protector.domain.community.repository.PostRepository;
import hp.home_protector.domain.community.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, CommentRepository commentRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
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
    /** 게시글 수정 */
    @Transactional
    public Post updatePost(String userIdHex, String postIdHex, PostUpdateRequestDTO dto) {
        ObjectId postId = new ObjectId(postIdHex);
        ObjectId userId = new ObjectId(userIdHex);

        // 1) 게시글 존재 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 2) 작성자 검증 (userId 일치 여부)
        if (!post.getUserId().equals(userId)) {
            throw new SecurityException("작성자만 수정할 수 있습니다.");
        }

        // 3) 필드 업데이트
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setBoardType(dto.getBoardType());
        post.setAttachments(dto.getAttachments() != null ? dto.getAttachments() : post.getAttachments());
        post.setUpdatedAt(LocalDateTime.now());

        // 4) 저장 및 반환
        return postRepository.save(post);
    }

    /** 게시글 삭제 */
    @Transactional
    public void deletePost(String userIdHex, String postIdHex) {
        ObjectId postId = new ObjectId(postIdHex);
        ObjectId userId = new ObjectId(userIdHex);

        // 게시글 존재 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 작성자 검증
        if (!post.getUserId().equals(userId)) {
            throw new SecurityException("작성자만 삭제할 수 있습니다.");
        }

        // 게시글 삭제
        postRepository.delete(post);
    }

    /** 댓글 작성 */
    @Transactional
    public CommentResponseDTO addComment(String userIdHex, String postIdHex, CommentRequestDTO dto) {
        ObjectId userId = new ObjectId(userIdHex);
        ObjectId postId = new ObjectId(postIdHex);

        // 게시글 존재 확인
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }

        Comment comment = Comment.builder()
                .postId(postId)
                .userId(userId)
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .build();
        Comment saved = commentRepository.save(comment);

        return CommentResponseDTO.builder()
                .commentId(saved.getCommentId().toHexString())
                .userId(saved.getUserId().toHexString())
                .content(saved.getContent())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    /** 게시글 상세 조회 */
    public PostDetailResponseDTO getPostDetail(String postIdHex) {
        ObjectId postId = new ObjectId(postIdHex);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        List<CommentResponseDTO> comments = commentRepository
                .findByPostId(postId, Sort.by(Sort.Direction.ASC, "createdAt"))
                .stream()
                .map(c -> CommentResponseDTO.builder()
                        .commentId(c.getCommentId().toHexString())
                        .userId(c.getUserId().toHexString())
                        .content(c.getContent())
                        .createdAt(c.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return PostDetailResponseDTO.builder()
                .postId(post.getPostId().toHexString())
                .userId(post.getUserId().toHexString())
                .title(post.getTitle())
                .content(post.getContent())
                .boardType(post.getBoardType())
                .attachments(post.getAttachments())
                .likeCount(post.getLikeCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .comments(comments)
                .build();
    }
    @Transactional
    public CommentResponseDTO updateComment(String userIdHex, String commentIdHex, CommentRequestDTO dto) {
        ObjectId userId    = new ObjectId(userIdHex);
        ObjectId commentId = new ObjectId(commentIdHex);

        // 1) 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        // 2) 작성자 검증
        if (!comment.getUserId().equals(userId)) {
            throw new SecurityException("작성자만 댓글을 수정할 수 있습니다.");
        }

        // 3) 내용 수정
        comment.setContent(dto.getContent());
        Comment saved = commentRepository.save(comment);

        // 4) 응답용 DTO로 변환
        return CommentResponseDTO.builder()
                .commentId(saved.getCommentId().toHexString())
                .userId(saved.getUserId().toHexString())
                .content(saved.getContent())
                .createdAt(saved.getCreatedAt())
                .build();
    }
    /** 댓글 삭제 */
    @Transactional
    public void deleteComment(String userIdHex, String commentIdHex) {
        ObjectId userId    = new ObjectId(userIdHex);
        ObjectId commentId = new ObjectId(commentIdHex);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        boolean isAdmin = "ADMIN".equals(user.getRole());
        if (!comment.getUserId().equals(userId) && !isAdmin) {
            throw new SecurityException("본인 또는 관리자만 댓글을 삭제할 수 있습니다.");
        }
        commentRepository.delete(comment);
    }
}
