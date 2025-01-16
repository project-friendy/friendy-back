package friendy.community.domain.post.service;

import friendy.community.domain.member.dto.request.MemberSignUpRequest;
import friendy.community.domain.member.fixture.MemberFixture;
import friendy.community.domain.member.model.Member;
import friendy.community.domain.member.service.MemberService;
import friendy.community.domain.post.dto.request.PostCreateRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;

import static friendy.community.domain.auth.fixtures.TokenFixtures.CORRECT_REFRESH_TOKEN;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DirtiesContext
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        Member member = MemberFixture.memberFixture();
        MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest(member.getEmail(), member.getNickname(), member.getPassword(), member.getBirthDate());

        memberService.signUp(memberSignUpRequest);
    }

    @Test
    @DisplayName("게시글이 성공적으로 생성되면 게시글 ID를 반환한다")
    void createPostSuccessfullyReturnsPostId(){

        //Given
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        String token = "Bearer " + CORRECT_REFRESH_TOKEN;
        httpServletRequest.addHeader("Authorization", token);

        String content = "This is a new post content.";
        PostCreateRequest postCreateRequest = new PostCreateRequest(content);

        //When
        Long postId = postService.savePost(postCreateRequest, httpServletRequest);

        //Then
        assertThat(postId).isEqualTo(1L);

    }

}