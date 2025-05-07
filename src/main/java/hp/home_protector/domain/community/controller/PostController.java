package hp.home_protector.domain.community.controller;

import hp.home_protector.domain.community.dto.*;
import hp.home_protector.domain.community.entity.BoardType;
import hp.home_protector.domain.community.entity.Comment;
import hp.home_protector.domain.community.entity.Post;
import hp.home_protector.domain.community.entity.User;
import hp.home_protector.domain.community.service.LikeService;
import hp.home_protector.domain.community.service.PostService;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/communities")
public class PostController {
    private final PostService postService;
    private final LikeService likeService;

    public PostController(PostService postService,
                          LikeService likeService) {
        this.postService = postService;
        this.likeService = likeService;
    }

    //게시글 작성
    @PostMapping("/post")
    public ApiResponse<PostResponseDTO> createPost(
            @RequestHeader("userId") String userIdHex,
            @Valid @RequestBody PostRequestDTO dto
    ) {
        Post saved = postService.createPost(userIdHex, dto);
        PostResponseDTO result = PostResponseDTO.builder()
                .postId(saved.getPostId().toHexString())
                .userId(saved.getUserId().toHexString())
                .title(saved.getTitle())
                .content(saved.getContent())
                .boardType(saved.getBoardType())
                .attachments(saved.getAttachments())
                .likeCount(saved.getLikeCount())
                .build();
        return ApiResponse.success("COMMON200", "게시글이 생성되었습니다.", result);
    }

    //게시글 좋아요
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
    @GetMapping("/free")
    public ApiResponse<List<PostResponseDTO>> getFreePosts() {
        List<PostResponseDTO> list = postService.getPostsByCategory(BoardType.FREE);
        return ApiResponse.success("COMMON200", "자유게시판 조회 성공", list);
    }

    // 정보공유게시판 게시르 조회
    @GetMapping("/info")
    public ApiResponse<List<PostResponseDTO>> getInfoPosts() {
        List<PostResponseDTO> list = postService.getPostsByCategory(BoardType.INFO);
        return ApiResponse.success("COMMON200", "정보공유게시판 조회 성공", list);
    }

    //게시글 수정
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
                .boardType(updated.getBoardType())
                .attachments(updated.getAttachments())
                .likeCount(updated.getLikeCount())
                .build();
        return ApiResponse.success("COMMON200", "게시글이 수정되었습니다.", result);
    }

    //게시글 삭제
    @DeleteMapping("/{postId}")
    public ApiResponse<Void> deletePost(
            @RequestHeader("userId") String userIdHex,
            @PathVariable String postId
    ) {
        postService.deletePost(userIdHex, postId);
        return ApiResponse.success("COMMON200", "게시글이 삭제되었습니다.", null);
    }

    // 댓글 작성
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
    @GetMapping("/detail/{postId}")
    public ApiResponse<PostDetailResponseDTO> getPostDetail(
            @PathVariable String postId
    ) {
        PostDetailResponseDTO detail = postService.getPostDetail(postId);
        return ApiResponse.success("COMMON200", "게시글 상세 조회 성공", detail);
    }

    //댓글 수정
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
    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Void> deleteComment(
            @RequestHeader("userId") String userIdHex,
            @PathVariable String commentId
    ) {
        postService.deleteComment(userIdHex, commentId);
        return ApiResponse.success("COMMON200", "댓글이 삭제되었습니다.", null);
    }
}
