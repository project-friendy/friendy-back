package friendy.community.domain.post.service;

import friendy.community.domain.auth.jwt.JwtTokenExtractor;
import friendy.community.domain.auth.jwt.JwtTokenProvider;
import friendy.community.domain.auth.service.AuthService;
import friendy.community.domain.member.model.Member;
import friendy.community.domain.member.repository.MemberRepository;
import friendy.community.domain.post.dto.request.PostCreateRequest;
import friendy.community.domain.post.model.Post;
import friendy.community.domain.post.repository.PostRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final JwtTokenExtractor jwtTokenExtractor;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    public long savePost(PostCreateRequest postCreateRequest, HttpServletRequest httpServletRequest) {

        String accessToken = jwtTokenExtractor.extractAccessToken(httpServletRequest);

        String email = jwtTokenProvider.extractEmailFromAccessToken(accessToken);

        Member member = authService.getMemberByEmail(email);

        final Post post = Post.of(postCreateRequest, member);
        postRepository.save(post);
        return post.getId();
    }

}
