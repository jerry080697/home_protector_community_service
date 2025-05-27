package hp.home_protector.domain.community.controller;

import hp.home_protector.domain.community.dto.*;
import hp.home_protector.domain.community.entity.BoardType;
import hp.home_protector.domain.community.entity.mongoDB.Post;
import hp.home_protector.domain.community.service.LikeService;
import hp.home_protector.domain.community.service.PostSearchService;
import hp.home_protector.domain.community.service.PostService;
import hp.home_protector.domain.community.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/communities")
public class PostController {
    private final PostService postService;
    private final LikeService likeService;
    private final StorageService storageService;
    private final PostSearchService postSearchService;

    public PostController(PostService postService,
                          LikeService likeService,
                          StorageService storageService,
                          PostSearchService postSearchService) {
        this.postService = postService;
        this.likeService = likeService;
        this.storageService = storageService;
        this.postSearchService = postSearchService;
    }

    @Operation(summary = "새 게시글 작성 API", description = "FREE or INFO, 사진 업로드 가능")
    @PostMapping(path = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostResponseDTO> createPost(
            HttpServletRequest request,
            @Valid @ModelAttribute PostRequestDTO dto
    ) {
        String userId = (String) request.getAttribute("X-User-Id");
        List<String> imageUrls = dto.getAttachments() != null && !dto.getAttachments().isEmpty()
                ? storageService.uploadFiles(dto.getAttachments())
                : Collections.emptyList();

        Post saved = postService.createPostWithImages(userId, dto, imageUrls);
        PostResponseDTO result = PostResponseDTO.builder()
                .postId(saved.getPostId().toHexString())
                .userId(saved.getUserId())
                .title(saved.getTitle())
                .content(saved.getContent())
                .category(saved.getBoardType())
                .attachments(saved.getAttachments())
                .likeCount(saved.getLikeCount())
                .build();

        return ApiResponse.success("COMMON200", "게시글 생성 완료", result);
    }

    @Operation(summary = "게시글 좋아요 API")
    @PostMapping("/like/{postId}")
    public ApiResponse<LikeResponseDTO> likePost(
            HttpServletRequest request,
            @PathVariable String postId
    ) {
        String userId = (String) request.getAttribute("X-User-Id");
        if (!ObjectId.isValid(postId)) {
            throw new IllegalArgumentException("Invalid postId format");
        }
        long newCount = likeService.likePost(userId, postId);
        LikeResponseDTO dto = LikeResponseDTO.builder()
                .postId(postId)
                .userId(userId)
                .likeCount(newCount)
                .build();
        return ApiResponse.success("COMMON200", "좋아요 등록", dto);
    }

    @Operation(summary = "게시글 좋아요 취소 API")
    @PatchMapping("/like/{postId}")
    public ApiResponse<LikeResponseDTO> unlikePost(
            HttpServletRequest request,
            @PathVariable String postId
    ) {
        String userId = (String) request.getAttribute("X-User-Id");
        if (!ObjectId.isValid(postId)) {
            throw new IllegalArgumentException("Invalid postId format");
        }
        long newCount = likeService.unlikePost(userId, postId);
        LikeResponseDTO dto = LikeResponseDTO.builder()
                .postId(postId)
                .userId(userId)
                .likeCount(newCount)
                .build();
        return ApiResponse.success("COMMON200", "좋아요 취소", dto);
    }

    @Operation(summary = "자유게시판 게시글 조회 API")
    @GetMapping("/free")
    public ApiResponse<List<PostResponseDTO>> getFreePosts() {
        List<PostResponseDTO> list = postService.getPostsByCategory(BoardType.FREE);
        return ApiResponse.success("COMMON200", "자유게시판 조회 성공", list);
    }

    @Operation(summary = "정보공유게시판 게시글 조회 API")
    @GetMapping("/info")
    public ApiResponse<List<PostResponseDTO>> getInfoPosts() {
        List<PostResponseDTO> list = postService.getPostsByCategory(BoardType.INFO);
        return ApiResponse.success("COMMON200", "정보공유게시판 조회 성공", list);
    }

    @Operation(summary = "게시글 수정 API")
    @PatchMapping("/{postId}")
    public ApiResponse<PostResponseDTO> updatePost(
            HttpServletRequest request,
            @PathVariable String postId,
            @Valid @RequestBody PostUpdateRequestDTO dto
    ) {
        String userId = (String) request.getAttribute("X-User-Id");
        if (!ObjectId.isValid(postId)) {
            throw new IllegalArgumentException("Invalid postId format");
        }
        Post updated = postService.updatePost(userId, postId, dto);
        PostResponseDTO result = PostResponseDTO.builder()
                .postId(updated.getPostId().toHexString())
                .userId(updated.getUserId())
                .title(updated.getTitle())
                .content(updated.getContent())
                .category(updated.getBoardType())
                .attachments(updated.getAttachments())
                .likeCount(updated.getLikeCount())
                .build();
        return ApiResponse.success("COMMON200", "게시글 수정 완료", result);
    }

    @Operation(summary = "게시글 삭제 API")
    @DeleteMapping("/{postId}")
    public ApiResponse<Void> deletePost(
            HttpServletRequest request,
            @PathVariable String postId
    ) {
        String userId = (String) request.getAttribute("X-User-Id");
        if (!ObjectId.isValid(postId)) {
            throw new IllegalArgumentException("Invalid postId format");
        }
        postService.deletePost(userId, postId);
        return ApiResponse.success("COMMON200", "게시글 삭제 완료", null);
    }

    @Operation(summary = "댓글 작성 API")
    @PostMapping("/comments/{postId}")
    public ApiResponse<CommentResponseDTO> addComment(
            HttpServletRequest request,
            @PathVariable String postId,
            @Valid @RequestBody CommentRequestDTO dto
    ) {
        String userId = (String) request.getAttribute("X-User-Id");
        if (!ObjectId.isValid(postId)) {
            throw new IllegalArgumentException("Invalid postId format");
        }
        CommentResponseDTO result = postService.addComment(userId, postId, dto);
        return ApiResponse.success("COMMON200", "댓글 작성 완료", result);
    }

    @Operation(summary = "댓글 수정 API")
    @PatchMapping("/comments/{commentId}")
    public ApiResponse<CommentResponseDTO> updateComment(
            HttpServletRequest request,
            @PathVariable String commentId,
            @Valid @RequestBody CommentRequestDTO dto
    ) {
        String userId = (String) request.getAttribute("X-User-Id");
        if (!ObjectId.isValid(commentId)) {
            throw new IllegalArgumentException("Invalid commentId format");
        }
        CommentResponseDTO updated = postService.updateComment(userId, commentId, dto);
        return ApiResponse.success("COMMON200", "댓글 수정 완료", updated);
    }

    @Operation(summary = "댓글 삭제 API")
    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Void> deleteComment(
            HttpServletRequest request,
            @PathVariable String commentId
    ) {
        String userId = (String) request.getAttribute("X-User-Id");
        if (!ObjectId.isValid(commentId)) {
            throw new IllegalArgumentException("Invalid commentId format");
        }
        postService.deleteComment(userId, commentId);
        return ApiResponse.success("COMMON200", "댓글 삭제 완료", null);
    }

    @Operation(summary = "게시글 검색 API", description = "키워드 기반 전체 보드 검색")
    @GetMapping("/search")
    public ApiResponse<List<PostResponseDTO>> searchPosts(
            @RequestParam("keyword") String keyword
    ) {
        List<PostResponseDTO> results = postSearchService.search(keyword);
        return ApiResponse.success("COMMON200", "검색 결과", results);
    }
}
