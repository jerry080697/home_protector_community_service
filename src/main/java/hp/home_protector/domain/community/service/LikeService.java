package hp.home_protector.domain.community.service;

import hp.home_protector.domain.community.entity.mongoDB.Like;
import hp.home_protector.domain.community.entity.mongoDB.Post;
import hp.home_protector.domain.community.repository.LikeRepository;
import hp.home_protector.domain.community.repository.PostRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    public LikeService(LikeRepository likeRepository, PostRepository postRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
    }

    // 게시글 좋아요
    @Transactional
    public long likePost(String userId, String postIdHex) {
        // userId는 이미 String이므로 변환 없이 사용
        ObjectId postId = new ObjectId(postIdHex);

        if (likeRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new IllegalStateException("이미 좋아요를 누른 게시글입니다.");
        }

        Like like = Like.builder()
                .userId(userId)
                .postId(postId)
                .createdAt(LocalDateTime.now())
                .build();
        likeRepository.save(like);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);

        return post.getLikeCount();
    }

    // 게시글 좋아요 취소
    @Transactional
    public long unlikePost(String userId, String postIdHex) {
        // userId는 String
        ObjectId postId = new ObjectId(postIdHex);

        if (!likeRepository.existsByUserIdAndPostId(userId, postId)) {
            throw new IllegalStateException("좋아요를 누르지 않은 게시글입니다.");
        }
        likeRepository.deleteByUserIdAndPostId(userId, postId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        int newCount = Math.max(0, post.getLikeCount() - 1);
        post.setLikeCount(newCount);
        postRepository.save(post);

        return newCount;
    }
}
