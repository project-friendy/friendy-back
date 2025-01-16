package friendy.community.domain.post.service;

import friendy.community.domain.auth.jwt.JwtTokenProvider;
import friendy.community.domain.member.dto.request.MemberSignUpRequest;
import friendy.community.domain.member.fixture.MemberFixture;
import friendy.community.domain.member.model.Member;
import friendy.community.domain.member.repository.MemberRepository;
import friendy.community.domain.member.service.MemberService;
import friendy.community.domain.post.dto.request.PostCreateRequest;
import friendy.community.global.exception.ErrorCode;
import friendy.community.global.exception.FriendyException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Objects;
import java.util.Optional;

import static friendy.community.domain.auth.fixtures.TokenFixtures.CORRECT_REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
@DirtiesContext
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private MemberService memberService;

    private Member member;

    @Autowired
    private MemberRepository memberRepository;


    void setUp() {
        member = MemberFixture.memberFixture();
        MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest(member.getEmail(), member.getNickname(), member.getPassword(), member.getBirthDate());

        memberService.signUp(memberSignUpRequest);
    }

    @Test
    @DisplayName("게시글이 성공적으로 생성되면 게시글 ID를 반환한다")
    void createPostSuccessfullyReturnsPostId() {

        //Given
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        String token = "Bearer " + CORRECT_REFRESH_TOKEN;
        httpServletRequest.addHeader("Authorization", token);

        String content = "This is a new post content.";
        PostCreateRequest postCreateRequest = new PostCreateRequest(content);

        setUp();

        //When
        Long postId = postService.savePost(postCreateRequest, httpServletRequest);

        //Then
        assertThat(postId).isEqualTo(1L);

    }

    @Test
    @DisplayName("이메일이 존재하지 않으면 FriendyException 던진다")
    void throwsExceptionWhenEmailNotFound() {
        // Given
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        String token = "Bearer " + CORRECT_REFRESH_TOKEN;
        httpServletRequest.addHeader("Authorization", token);

        String content = "This is a new post content.";
        PostCreateRequest postCreateRequest = new PostCreateRequest(content);

        member = MemberFixture.memberFixture();
        MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest("nonemali.friendy.com", member.getNickname(), member.getPassword(), member.getBirthDate());

        memberService.signUp(memberSignUpRequest);

        // When & Then
        assertThatThrownBy(() -> postService.savePost(postCreateRequest, httpServletRequest))
            .isInstanceOf(FriendyException.class)  // 예외 타입이 FriendyException인지 확인
            .hasMessageContaining("해당 이메일의 회원이 존재하지 않습니다.")  // 예외 메시지가 포함된 부분 검증
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNAUTHORIZED_EMAIL);  // errorCode 필드 검증
    }
}
