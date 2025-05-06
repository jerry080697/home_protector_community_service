package hp.home_protector.domain.community.controller;

import hp.home_protector.domain.community.dto.ApiResponse;
import hp.home_protector.domain.community.dto.LikeResponseDTO;
import hp.home_protector.domain.community.dto.PostRequestDTO;
import hp.home_protector.domain.community.dto.PostResponseDTO;
import hp.home_protector.domain.community.entity.BoardType;
import hp.home_protector.domain.community.entity.Post;
import hp.home_protector.domain.community.service.LikeService;
import hp.home_protector.domain.community.service.PostService;
import jakarta.validation.Valid;
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

    @PostMapping("/post")
    public ApiResponse<PostResponseDTO> createPost(
            @RequestHeader("X-User-Id") String userIdHex,
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

    @PostMapping("/like/{postId}")
    public ApiResponse<LikeResponseDTO> likePost(
            @RequestHeader("X-User-Id") String userIdHex,
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

    @PatchMapping("/like/{postId}")
    public ApiResponse<LikeResponseDTO> unlikePost(
            @RequestHeader("X-User-Id") String userIdHex,
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
    // 자유게시판
    @GetMapping("/free")
    public ApiResponse<List<PostResponseDTO>> getFreePosts() {
        List<PostResponseDTO> list = postService.getPostsByCategory(BoardType.FREE);
        return ApiResponse.success("COMMON200", "자유게시판 조회 성공", list);
    }

    // 정보공유게시판
    @GetMapping("/info")
    public ApiResponse<List<PostResponseDTO>> getInfoPosts() {
        List<PostResponseDTO> list = postService.getPostsByCategory(BoardType.INFO);
        return ApiResponse.success("COMMON200", "정보공유게시판 조회 성공", list);
    }
}
