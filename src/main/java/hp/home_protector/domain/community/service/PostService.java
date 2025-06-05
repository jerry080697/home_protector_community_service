//package hp.home_protector.domain.community.service;
//
//import hp.home_protector.domain.community.dto.*;
//import hp.home_protector.domain.community.entity.mongoDB.Comment;
//import hp.home_protector.domain.community.entity.mongoDB.Post;
//import hp.home_protector.domain.community.entity.mongoDB.User;
//import hp.home_protector.domain.community.entity.BoardType;
//import hp.home_protector.domain.community.repository.CommentRepository;
//import hp.home_protector.domain.community.repository.PostRepository;
//import hp.home_protector.domain.community.repository.UserRepository;
//import org.bson.types.ObjectId;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class PostService {
//    private final PostRepository postRepository;
//    private final CommentRepository commentRepository;
//    private final UserRepository userRepository;
//    private final StorageService storageService;
//    private final PostIndexService postIndexService;
//
//    public PostService(PostRepository postRepository,
//                       CommentRepository commentRepository,
//                       UserRepository userRepository,
//                       StorageService storageService,
//                       PostIndexService postIndexService) {
//        this.postRepository = postRepository;
//        this.commentRepository = commentRepository;
//        this.userRepository = userRepository;
//        this.storageService = storageService;
//        this.postIndexService = postIndexService;
//    }
//
//    /** 이미지 없이 게시글 생성 */
//    @Transactional
//    public Post createPost(String userId, PostRequestDTO dto) {
//        List<String> imageUrls = Collections.emptyList();
//        if (dto.getAttachments() != null && !dto.getAttachments().isEmpty()) {
//            imageUrls = storageService.uploadFiles(dto.getAttachments());
//        }
//
//        Post post = Post.builder()
//                .userId(userId)
//                .boardType(dto.getCategory())
//                .title(dto.getTitle())
//                .content(dto.getContent())
//                .attachments(imageUrls)
//                .likeCount(0)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        Post saved = postRepository.save(post);
//        postIndexService.index(saved);
//        return saved;
//    }
//
//    /** 이미지 URL 리스트를 받아 게시글 생성 */
//    @Transactional
//    public Post createPostWithImages(String userId,
//                                     PostRequestDTO dto,
//                                     List<String> imageUrls) {
//        Post post = Post.builder()
//                .userId(userId)
//                .boardType(dto.getCategory())
//                .title(dto.getTitle())
//                .content(dto.getContent())
//                .attachments(imageUrls)
//                .likeCount(0)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        Post saved = postRepository.save(post);
//        postIndexService.index(saved);
//        return saved;
//    }
//
//    /** 카테고리별 게시글 목록 조회 (createdAt, commentCount 포함) */
//    public List<PostResponseDTO> getPostsByCategory(BoardType category) {
//        return postRepository.findByBoardType(
//                        category,
//                        Sort.by(Sort.Direction.DESC, "createdAt")
//                ).stream()
//                .map(p -> {
//                    // 댓글 개수를 Repository에서 조회
//                    int commentCnt = commentRepository.countByPostId(p.getPostId());
//
//                    return PostResponseDTO.builder()
//                            .postId(p.getPostId().toHexString())
//                            .userId(p.getUserId())
//                            .title(p.getTitle())
//                            .content(p.getContent())
//                            .category(p.getBoardType())
//                            .attachments(p.getAttachments())
//                            .likeCount(p.getLikeCount())
//                            .createdAt(p.getCreatedAt())
//                            .commentCount(commentCnt)
//                            .build();
//                })
//                .collect(Collectors.toList());
//    }
//
//    /** 게시글 상세 조회 */
//    public PostDetailResponseDTO getPostDetail(String postIdHex) {
//        ObjectId postId = new ObjectId(postIdHex);
//        Post post = postRepository.findById(postId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
//
//        // 1) 댓글 목록
//        List<CommentResponseDTO> comments = commentRepository
//                .findByPostId(postId, Sort.by(Sort.Direction.ASC, "createdAt"))
//                .stream()
//                .map(c -> CommentResponseDTO.builder()
//                        .commentId(c.getCommentId().toHexString())
//                        .userId(c.getUserId())
//                        .content(c.getContent())
//                        .createdAt(c.getCreatedAt())
//                        .build())
//                .collect(Collectors.toList());
//
//        // 2) 댓글 개수
//        int commentCnt = commentRepository.countByPostId(postId);
//
//        return PostDetailResponseDTO.builder()
//                .postId(post.getPostId().toHexString())
//                .userId(post.getUserId())
//                .title(post.getTitle())
//                .content(post.getContent())
//                .boardType(post.getBoardType())
//                .attachments(post.getAttachments())
//                .likeCount(post.getLikeCount())
//                .createdAt(post.getCreatedAt())
//                .updatedAt(post.getUpdatedAt())
//                .comments(comments)
//                .commentCount(commentCnt)
//                .build();
//    }
//
//    /** 게시글 수정 + ES 색인 갱신 */
//    @Transactional
//    public Post updatePost(String userId,
//                           String postIdHex,
//                           PostUpdateRequestDTO dto) {
//        ObjectId postId = new ObjectId(postIdHex);
//
//        Post post = postRepository.findById(postId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
//        if (!post.getUserId().equals(userId)) {
//            throw new SecurityException("작성자만 수정할 수 있습니다.");
//        }
//
//        post.setTitle(dto.getTitle());
//        post.setContent(dto.getContent());
//        post.setBoardType(dto.getBoardType());
//        post.setAttachments(
//                dto.getAttachments() != null
//                        ? dto.getAttachments()
//                        : post.getAttachments()
//        );
//        post.setUpdatedAt(LocalDateTime.now());
//
//        Post updated = postRepository.save(post);
//        postIndexService.index(updated);
//        return updated;
//    }
//
//    /** 게시글 삭제 + ES 문서 삭제 */
//    @Transactional
//    public void deletePost(String userId, String postIdHex) {
//        ObjectId postId = new ObjectId(postIdHex);
//
//        Post post = postRepository.findById(postId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
//        if (!post.getUserId().equals(userId)) {
//            throw new SecurityException("작성자만 삭제할 수 있습니다.");
//        }
//
//        postRepository.delete(post);
//        postIndexService.delete(postIdHex);
//    }
//
//    /** 댓글 작성 */
//    @Transactional
//    public CommentResponseDTO addComment(String userId,
//                                         String postIdHex,
//                                         CommentRequestDTO dto) {
//        ObjectId postId = new ObjectId(postIdHex);
//        if (!postRepository.existsById(postId)) {
//            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
//        }
//
//        Comment comment = Comment.builder()
//                .postId(postId)
//                .userId(userId)
//                .content(dto.getContent())
//                .createdAt(LocalDateTime.now())
//                .build();
//        Comment saved = commentRepository.save(comment);
//
//        return CommentResponseDTO.builder()
//                .commentId(saved.getCommentId().toHexString())
//                .userId(saved.getUserId())
//                .content(saved.getContent())
//                .createdAt(saved.getCreatedAt())
//                .build();
//    }
//
//    /** 댓글 수정 */
//    @Transactional
//    public CommentResponseDTO updateComment(String userId,
//                                            String commentIdHex,
//                                            CommentRequestDTO dto) {
//        ObjectId commentId = new ObjectId(commentIdHex);
//
//        Comment comment = commentRepository.findById(commentId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
//        if (!comment.getUserId().equals(userId)) {
//            throw new SecurityException("작성자만 댓글을 수정할 수 있습니다.");
//        }
//
//        comment.setContent(dto.getContent());
//        Comment saved = commentRepository.save(comment);
//
//        return CommentResponseDTO.builder()
//                .commentId(saved.getCommentId().toHexString())
//                .userId(saved.getUserId())
//                .content(saved.getContent())
//                .createdAt(saved.getCreatedAt())
//                .build();
//    }
//
//    /** 댓글 삭제 (작성자 or ADMIN) */
//    @Transactional
//    public void deleteComment(String userId, String commentIdHex) {
//        ObjectId commentId = new ObjectId(commentIdHex);
//
//        Comment comment = commentRepository.findById(commentId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
//        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());
//
//        if (!comment.getUserId().equals(userId) && !isAdmin) {
//            throw new SecurityException("본인 또는 관리자만 댓글을 삭제할 수 있습니다.");
//        }
//        commentRepository.delete(comment);
//    }
//}
package hp.home_protector.domain.community.service;

import hp.home_protector.domain.community.dto.*;
import hp.home_protector.domain.community.entity.mongoDB.Comment;
import hp.home_protector.domain.community.entity.mongoDB.Post;
import hp.home_protector.domain.community.entity.mongoDB.User;
import hp.home_protector.domain.community.entity.BoardType;
import hp.home_protector.domain.community.repository.CommentRepository;
import hp.home_protector.domain.community.repository.PostRepository;
import hp.home_protector.domain.community.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    // → 쓰기, 수정, 삭제 등의 작업은 기존 PostRepository (spring-data-mongodb 리포지토리) 를 사용
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;
    private final PostIndexService postIndexService;

    /**
     * 🔍 조회 전용 MongoTemplate 을 주입받습니다.
     *    @Qualifier("readMongoTemplate") 로 읽기 전용 MongoDB(VM4)에 연결하도록 설정.
     */
    private final MongoTemplate readMongoTemplate;

    public PostService(PostRepository postRepository,
                       CommentRepository commentRepository,
                       UserRepository userRepository,
                       StorageService storageService,
                       PostIndexService postIndexService,
                       @Qualifier("readMongoTemplate") MongoTemplate readMongoTemplate) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.storageService = storageService;
        this.postIndexService = postIndexService;
        this.readMongoTemplate = readMongoTemplate;
    }

    /** 이미지 없이 게시글 생성 → 쓰기 전용(default MongoTemplate or postRepository) */
    @Transactional
    public Post createPost(String userId, PostRequestDTO dto) {
        List<String> imageUrls = Collections.emptyList();
        if (dto.getAttachments() != null && !dto.getAttachments().isEmpty()) {
            imageUrls = storageService.uploadFiles(dto.getAttachments());
        }

        Post post = Post.builder()
                .userId(userId)
                .boardType(dto.getCategory())
                .title(dto.getTitle())
                .content(dto.getContent())
                .attachments(imageUrls)
                .likeCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Post saved = postRepository.save(post);   // 쓰기 전용 DB(VM2)에 저장
        postIndexService.index(saved);
        return saved;
    }

    /** 이미지 URL 리스트를 받아 게시글 생성 → 쓰기 전용 */
    @Transactional
    public Post createPostWithImages(String userId,
                                     PostRequestDTO dto,
                                     List<String> imageUrls) {
        Post post = Post.builder()
                .userId(userId)
                .boardType(dto.getCategory())
                .title(dto.getTitle())
                .content(dto.getContent())
                .attachments(imageUrls)
                .likeCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Post saved = postRepository.save(post); // 쓰기 DB(VM2)
        postIndexService.index(saved);
        return saved;
    }

    /**
     * 🔍 카테고리별 게시글 목록 조회 (읽기 전용 MongoTemplate 사용 → VM4에서 select)
     *
     * 이제 postRepository.findByBoardType(...) 대신,
     * readMongoTemplate 을 이용해 직접 조회하거나,
     * PostRepository 인터페이스에 @Qualifier("readMongoTemplate") 를 지정해 줄 수도 있지만,
     * 간단하게 readMongoTemplate을 이용한 예시를 보여 드리겠습니다.
     */
    public List<PostResponseDTO> getPostsByCategory(BoardType category) {
        // readMongoTemplate.find(Query.query(...).withSort(...), Post.class) 등의 방식으로 직접 조회해도 되지만,
        //   이미 Spring Data 레포지토리를 쓰고 싶다면 별도의 Repository 빈을 만들어서 readMongoTemplate에 연결해야 합니다.
        // 여기서는 예시로 readMongoTemplate.findAll() 을 쓰는 방법을 보여 드립니다.
        //
        // 1) Query 를 직접 작성해서 readMongoTemplate.find() 시 api를 호출합니다.
        //
        org.springframework.data.mongodb.core.query.Query query =
                new org.springframework.data.mongodb.core.query.Query(
                        org.springframework.data.mongodb.core.query.Criteria.where("boardType").is(category)
                ).with(Sort.by(Sort.Direction.DESC, "createdAt"));

        List<Post> posts = readMongoTemplate.find(query, Post.class, "posts"); // VM4 읽기 전용

        return posts.stream()
                .map(p -> {
                    // 댓글 개수: readMongoTemplate 사용
                    int commentCnt = Math.toIntExact(
                            readMongoTemplate.count(
                                    new org.springframework.data.mongodb.core.query.Query(
                                            org.springframework.data.mongodb.core.query.Criteria.where("postId").is(p.getPostId())
                                    ), "comments"
                            )
                    );

                    return PostResponseDTO.builder()
                            .postId(p.getPostId().toHexString())
                            .userId(p.getUserId())
                            .title(p.getTitle())
                            .content(p.getContent())
                            .category(p.getBoardType())
                            .attachments(p.getAttachments())
                            .likeCount(p.getLikeCount())
                            .createdAt(p.getCreatedAt())
                            .commentCount(commentCnt)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 🔍 게시글 상세 조회 → 읽기 전용(SELECT) → readMongoTemplate 사용 → VM4에서 조회
     */
    public PostDetailResponseDTO getPostDetail(String postIdHex) {
        ObjectId postId = new ObjectId(postIdHex);

        // 1) readMongoTemplate.findById(...) 로 VM4에서 Post 조회
        Post post = readMongoTemplate.findById(postId, Post.class, "posts");
        if (post == null) {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }

        // 2) 댓글 목록 → readMongoTemplate로 VM4에서 조회
        org.springframework.data.mongodb.core.query.Query commentQuery =
                new org.springframework.data.mongodb.core.query.Query(
                        org.springframework.data.mongodb.core.query.Criteria.where("postId").is(postId)
                ).with(Sort.by(Sort.Direction.ASC, "createdAt"));

        List<CommentResponseDTO> comments = readMongoTemplate.find(commentQuery, Comment.class, "comments")
                .stream()
                .map(c -> CommentResponseDTO.builder()
                        .commentId(c.getCommentId().toHexString())
                        .userId(c.getUserId())
                        .content(c.getContent())
                        .createdAt(c.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        // 3) 댓글 개수
        long commentCntLong = readMongoTemplate.count(
                new org.springframework.data.mongodb.core.query.Query(
                        org.springframework.data.mongodb.core.query.Criteria.where("postId").is(postId)
                ), "comments"
        );
        int commentCnt = (int) commentCntLong;

        return PostDetailResponseDTO.builder()
                .postId(post.getPostId().toHexString())
                .userId(post.getUserId())
                .title(post.getTitle())
                .content(post.getContent())
                .boardType(post.getBoardType())
                .attachments(post.getAttachments())
                .likeCount(post.getLikeCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .comments(comments)
                .commentCount(commentCnt)
                .build();
    }

    /** 게시글 수정 + ES 색인 → 쓰기 전용 VM2 */
    @Transactional
    public Post updatePost(String userId,
                           String postIdHex,
                           PostUpdateRequestDTO dto) {
        ObjectId postId = new ObjectId(postIdHex);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        if (!post.getUserId().equals(userId)) {
            throw new SecurityException("작성자만 수정할 수 있습니다.");
        }

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setBoardType(dto.getBoardType());
        post.setAttachments(
                dto.getAttachments() != null
                        ? dto.getAttachments()
                        : post.getAttachments()
        );
        post.setUpdatedAt(LocalDateTime.now());

        Post updated = postRepository.save(post); // VM2 쓰기
        postIndexService.index(updated);
        return updated;
    }

    /** 게시글 삭제 + ES 문서 삭제 → 쓰기 전용 VM2 */
    @Transactional
    public void deletePost(String userId, String postIdHex) {
        ObjectId postId = new ObjectId(postIdHex);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        if (!post.getUserId().equals(userId)) {
            throw new SecurityException("작성자만 삭제할 수 있습니다.");
        }

        postRepository.delete(post); // VM2 쓰기
        postIndexService.delete(postIdHex);
    }

    /** 댓글 작성 → 쓰기 전용 VM2 */
    @Transactional
    public CommentResponseDTO addComment(String userId,
                                         String postIdHex,
                                         CommentRequestDTO dto) {
        ObjectId postId = new ObjectId(postIdHex);
        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }

        Comment comment = Comment.builder()
                .postId(postId)
                .userId(userId)
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .build();
        Comment saved = commentRepository.save(comment); // VM2

        return CommentResponseDTO.builder()
                .commentId(saved.getCommentId().toHexString())
                .userId(saved.getUserId())
                .content(saved.getContent())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    /** 댓글 수정 → 쓰기 전용 VM2 */
    @Transactional
    public CommentResponseDTO updateComment(String userId,
                                            String commentIdHex,
                                            CommentRequestDTO dto) {
        ObjectId commentId = new ObjectId(commentIdHex);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
        if (!comment.getUserId().equals(userId)) {
            throw new SecurityException("작성자만 댓글을 수정할 수 있습니다.");
        }

        comment.setContent(dto.getContent());
        Comment saved = commentRepository.save(comment); // VM2

        return CommentResponseDTO.builder()
                .commentId(saved.getCommentId().toHexString())
                .userId(saved.getUserId())
                .content(saved.getContent())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    /** 댓글 삭제(작성자 or ADMIN) → 쓰기 전용 VM2 */
    @Transactional
    public void deleteComment(String userId, String commentIdHex) {
        ObjectId commentId = new ObjectId(commentIdHex);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());

        if (!comment.getUserId().equals(userId) && !isAdmin) {
            throw new SecurityException("본인 또는 관리자만 댓글을 삭제할 수 있습니다.");
        }
        commentRepository.delete(comment); // VM2
    }
}
