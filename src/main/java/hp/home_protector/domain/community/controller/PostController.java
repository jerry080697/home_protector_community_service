package hp.home_protector.domain.community.controller;

import hp.home_protector.domain.community.dto.*;
import hp.home_protector.domain.community.entity.BoardType;
import hp.home_protector.domain.community.entity.mongoDB.Post;
import hp.home_protector.domain.community.service.LikeService;
import hp.home_protector.domain.community.service.PostSearchService;
import hp.home_protector.domain.community.service.PostService;
import hp.home_protector.domain.community.service.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@RestController
@RequestMapping("/communities")
public class PostController {
    private final PostService postService;
    private final LikeService likeService;
    private final StorageService storageService;
    private final PostSearchService postSearchService;

    public PostController(PostService postService, LikeService likeService, StorageService storageService, PostSearchService postSearchService) {
        this.postService = postService;
        this.likeService = likeService;
        this.storageService = storageService;
        this.postSearchService = postSearchService;
    }


    //게시글 작성
    @Operation(
            summary = "새 게시글 작성 API",
            description = "자유게시판에 올리고 싶으면 FREE, 정보공유게시판에 올리고 싶으면 INFO로 설정해주시고 사진을 업로드 하고 싶으면 사진도 업로드 해주세요"
    )
    @PostMapping(
            path = "/post",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ApiResponse<PostResponseDTO> createPost(
            @RequestHeader("userId") String userIdHex,
            @Valid @ModelAttribute PostRequestDTO dto
    ) {
        List<MultipartFile> files = dto.getAttachments();
        List<String> imageUrls = (files != null && !files.isEmpty())
                ? storageService.uploadFiles(files)
                : Collections.emptyList();
        Post saved = postService.createPostWithImages(userIdHex, dto, imageUrls);
        PostResponseDTO result = PostResponseDTO.builder()
                .postId(saved.getPostId().toHexString())
                .userId(saved.getUserId().toHexString())
                .title(saved.getTitle())
                .content(saved.getContent())
                .category(saved.getBoardType())
                .attachments(imageUrls)
                .likeCount(saved.getLikeCount())
                .build();

        return ApiResponse.success("COMMON200", "게시글이 생성되었습니다.", result);
    }

    //게시글 좋아요
    @Operation(
            summary = "게시글 좋아요 API",
            description = "userId와 좋아요를 누르고 싶은 게시글의 postId를 같이 요청해주세요"
    )
    @PostMapping("/like/{postId}")
    public ApiResponse<LikeResponseDTO> likePost(
            @RequestHeader("userId") String userIdHex,
            @PathVariable String postId
    ) {
        long newCount = likeService.likePost(userIdHex, postId);
        LikeResponseDTO dto = LikeResponseDTO.builder()
                .postId(postId)
                .userId(userIdHex)
                .likeCount(newCount)
                .build();
        return ApiResponse.success("COMMON200", "좋아요가 등록되었습니다.", dto);
    }

    //게시글 좋아요 취소
    @Operation(
            summary = "게시글 좋아요 취소 API",
            description = "userId와 좋아요를 취소하고 싶은 게시글의 postId를 같이 요청해주세요"
    )
    @PatchMapping("/like/{postId}")
    public ApiResponse<LikeResponseDTO> unlikePost(
            @RequestHeader("userId") String userIdHex,
            @PathVariable String postId
    ) {
        long newCount = likeService.unlikePost(userIdHex, postId);
        LikeResponseDTO dto = LikeResponseDTO.builder()
                .postId(postId)
                .userId(userIdHex)
                .likeCount(newCount)
                .build();
        return ApiResponse.success("COMMON200", "좋아요가 취소되었습니다.", dto);
    }

    // 자유게시판 게시글 조회
    @Operation(
            summary = "자유게시판 게시글 조회 API"
    )
    @GetMapping("/free")
    public ApiResponse<List<PostResponseDTO>> getFreePosts() {
        List<PostResponseDTO> list = postService.getPostsByCategory(BoardType.FREE);
        return ApiResponse.success("COMMON200", "자유게시판 조회 성공", list);
    }

    // 정보공유게시판 게시글 조회
    @Operation(
            summary="정보공유게시판 게시글 조회 API"
    )
    @GetMapping("/info")
    public ApiResponse<List<PostResponseDTO>> getInfoPosts() {
        List<PostResponseDTO> list = postService.getPostsByCategory(BoardType.INFO);
        return ApiResponse.success("COMMON200", "정보공유게시판 조회 성공", list);
    }

    //게시글 수정
    @Operation(
            summary="게시글 수정 API",
            description = "userId와 좋아요를 수정하고 싶은 게시글의 postId를 같이 요청해주세요"
    )
    @PatchMapping("/{postId}")
    public ApiResponse<PostResponseDTO> updatePost(
            @RequestHeader("userId") String userIdHex,
            @PathVariable String postId,
            @Valid @RequestBody PostUpdateRequestDTO dto
    ) {
        Post updated = postService.updatePost(userIdHex, postId, dto);
        PostResponseDTO result = PostResponseDTO.builder()
                .postId(updated.getPostId().toHexString())
                .userId(updated.getUserId().toHexString())
                .title(updated.getTitle())
                .content(updated.getContent())
                .category(updated.getBoardType())
                .attachments(updated.getAttachments())
                .likeCount(updated.getLikeCount())
                .build();
        return ApiResponse.success("COMMON200", "게시글이 수정되었습니다.", result);
    }

    //게시글 삭제
    @Operation(
            summary="게시글 삭제 API",
            description = "userId와 삭제하고 싶은 게시글의 postId를 같이 요청해주세요"
    )
    @DeleteMapping("/{postId}")
    public ApiResponse<Void> deletePost(
            @RequestHeader("userId") String userIdHex,
            @PathVariable String postId
    ) {
        postService.deletePost(userIdHex, postId);
        return ApiResponse.success("COMMON200", "게시글이 삭제되었습니다.", null);
    }

    // 댓글 작성
    @Operation(
            summary="댓글 작성 API",
            description = "userId와 댓글을 작성하고 싶은 게시글의 postId를 같이 요청해주세요"
    )
    @PostMapping("/comments/{postId}")
    public ApiResponse<CommentResponseDTO> addComment(
            @RequestHeader("userId") String userIdHex,
            @PathVariable String postId,
            @Valid @RequestBody CommentRequestDTO dto
    ) {
        CommentResponseDTO result = postService.addComment(userIdHex, postId, dto);
        return ApiResponse.success("COMMON200", "댓글이 작성되었습니다.", result);
    }

    // 게시글 상세 조회
    @Operation(
            summary="게시글 상세 조회 API",
            description = "상세조회 하고 싶은 게시글의 postId를 같이 요청해주세요"
    )
    @GetMapping("/detail/{postId}")
    public ApiResponse<PostDetailResponseDTO> getPostDetail(
            @PathVariable String postId
    ) {
        PostDetailResponseDTO detail = postService.getPostDetail(postId);
        return ApiResponse.success("COMMON200", "게시글 상세 조회 성공", detail);
    }

    //댓글 수정
    @Operation(
            summary="댓글 수정 API",
            description = "userId와 수정하고 싶은 댓글의 commentId를 같이 요청해주세요"
    )
    @PatchMapping("/comments/{commentId}")
    public ApiResponse<CommentResponseDTO> updateComment(
            @RequestHeader("userId") String userIdHex,
            @PathVariable String commentId,
            @Valid @RequestBody CommentRequestDTO dto
    ) {
        CommentResponseDTO updated = postService.updateComment(userIdHex, commentId, dto);
        return ApiResponse.success("COMMON200", "댓글이 수정되었습니다.", updated);
    }
    //댓글 삭제
    @Operation(
            summary="댓글 삭제 API",
            description = "userId와 삭제하고 싶은 댓글의 commentId를 같이 요청해주세요"
    )
    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Void> deleteComment(
            @RequestHeader("userId") String userIdHex,
            @PathVariable String commentId
    ) {
        postService.deleteComment(userIdHex, commentId);
        return ApiResponse.success("COMMON200", "댓글이 삭제되었습니다.", null);
    }

    //게시글 검색(엘라스틱서치)
    @Operation(summary = "게시글 검색 API",
            description = "FREE 또는 INFO 보드에서 title/content 에 keyword 가 포함된 게시글을 검색합니다.")
    @GetMapping("/search")
    public ApiResponse<List<PostResponseDTO>> searchPosts(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "category") BoardType category  // 선택 파라미터로 변경
    ) {
        List<PostResponseDTO> results = postSearchService.search(keyword, category);
        return ApiResponse.success("COMMON200", "검색 결과입니다.", results);
    }



}
