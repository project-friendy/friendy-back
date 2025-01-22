package friendy.community.domain.post.service;

import friendy.community.domain.auth.jwt.JwtTokenExtractor;
import friendy.community.domain.auth.jwt.JwtTokenProvider;
import friendy.community.domain.auth.service.AuthService;
import friendy.community.domain.member.model.Member;
import friendy.community.domain.post.dto.request.PostCreateRequest;
import friendy.community.domain.post.dto.request.PostUpdateRequest;
import friendy.community.domain.post.dto.response.PostListResponse;
import friendy.community.domain.post.dto.response.PostSummary;
import friendy.community.domain.post.model.Post;
import friendy.community.domain.post.repository.PostRepository;
import friendy.community.global.exception.ErrorCode;
import friendy.community.global.exception.FriendyException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final JwtTokenExtractor jwtTokenExtractor;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    public long savePost(final PostCreateRequest postCreateRequest, final HttpServletRequest httpServletRequest) {
        final Member member = getMemberFromRequest(httpServletRequest);
        final Post post = Post.of(postCreateRequest, member);

        postRepository.save(post);

        return post.getId();
    }

    public long updatePost(final PostUpdateRequest postUpdateRequest,
                           final HttpServletRequest httpServletRequest,final Long postId) {
        final Member member = getMemberFromRequest(httpServletRequest);
        final Post post = validatePostExistence(postId);
        validatePostAuthor(member,post);
        post.updatePost(postUpdateRequest);

        postRepository.save(post);

        return post.getId();
    }

    public void deletePost(final HttpServletRequest httpServletRequest, final Long postId) {
        final Member member = getMemberFromRequest(httpServletRequest);
        final Post post = validatePostExistence(postId);
        validatePostAuthor(member,post);

        postRepository.delete(post);
    }

    public PostListResponse getAllPosts(Pageable pageable) {
        Pageable defaultPageable = PageRequest.of(pageable.getPageNumber(), 10);
        Page<PostSummary> postSummaryPage = postRepository.findAllPostsWithMember(defaultPageable);

        return new PostListResponse(
                mapToPostSummaryList(postSummaryPage),
                postSummaryPage.getNumber(),
                postSummaryPage.getTotalPages(),
                postSummaryPage.getTotalElements()
        );
    }

    private Post validatePostExistence(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new FriendyException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 게시글입니다."));
    }

    private void validatePostAuthor(Member member, Post post) {
        if (!post.getMember().getId().equals(member.getId())) {
            throw new FriendyException(ErrorCode.FORBIDDEN_ACCESS, "게시글은 작성자 본인만 관리할 수 있습니다.");
        }
    }

    private Member getMemberFromRequest(HttpServletRequest httpServletRequest) {
        final String accessToken = jwtTokenExtractor.extractAccessToken(httpServletRequest);
        final String email = jwtTokenProvider.extractEmailFromAccessToken(accessToken);
        return authService.getMemberByEmail(email);
    }

    private List<PostSummary> mapToPostSummaryList(Page<PostSummary> postSummaryPage) {
        return postSummaryPage.getContent().stream()
                .map(postSummary -> new PostSummary(
                        postSummary.id(),
                        postSummary.content(),
                        postSummary.createdAt(),
                        calculateTimeAgo(postSummary.createdAt()),
                        postSummary.author()
                ))
                .toList();
    }

    private String calculateTimeAgo(String createdAt) {
        LocalDateTime createdDate = LocalDateTime.parse(createdAt, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        Duration duration = Duration.between(createdDate, LocalDateTime.now());
        long hours = duration.toHours();
        long minutes = duration.toMinutes();

        if (hours > 0) {
            return hours + "시간 전";
        } else if (minutes > 0) {
            return minutes + "분 전";
        } else {
            return "방금 전";
        }
    }

}