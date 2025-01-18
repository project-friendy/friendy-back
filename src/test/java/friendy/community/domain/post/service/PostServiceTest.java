package friendy.community.domain.post.service;

import com.querydsl.jpa.hibernate.HibernateDeleteClause;
import friendy.community.domain.member.dto.request.MemberSignUpRequest;
import friendy.community.domain.member.fixture.MemberFixture;
import friendy.community.domain.member.model.Member;
import friendy.community.domain.member.repository.MemberRepository;
import friendy.community.domain.member.service.MemberService;
import friendy.community.domain.post.dto.request.PostCreateRequest;
import friendy.community.domain.post.dto.request.PostUpdateRequest;
import friendy.community.domain.post.model.Post;
import friendy.community.domain.post.repository.PostRepository;
import friendy.community.global.exception.ErrorCode;
import friendy.community.global.exception.FriendyException;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;

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

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private EntityManager entityManager;


    public void clean() {
        entityManager.createNativeQuery("ALTER TABLE post AUTO_INCREMENT = 1")
                .executeUpdate();
    }


    void signUpSetUp() {
        member = MemberFixture.memberFixture();
        MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest(member.getEmail(), member.getNickname(), member.getPassword(), member.getBirthDate());

        memberService.signUp(memberSignUpRequest);
    }

    void postSetUp() {
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        String token = "Bearer " + CORRECT_REFRESH_TOKEN;
        httpServletRequest.addHeader("Authorization", token);

        String content = "This is a new post content.";
        PostCreateRequest postCreateRequest = new PostCreateRequest(content);

        signUpSetUp();

        Long postId = postService.savePost(postCreateRequest, httpServletRequest);

    }

    @Test
    @DisplayName("게시글이 성공적으로 생성되면 게시글 ID를 반환한다")
    @Transactional
    void createPostSuccessfullyReturnsPostId() {

        //Given
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        String token = "Bearer " + CORRECT_REFRESH_TOKEN;
        httpServletRequest.addHeader("Authorization", token);

        String content = "This is a new post content.";
        PostCreateRequest postCreateRequest = new PostCreateRequest(content);

        signUpSetUp();

        //When
        Long postId = postService.savePost(postCreateRequest, httpServletRequest);

        //Then
        assertThat(postId).isEqualTo(1L);

    }

    @Test
    @DisplayName("이메일이 존재하지 않으면 FriendyException 던진다")
    @Transactional
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
            .isInstanceOf(FriendyException.class)
            .hasMessageContaining("해당 이메일의 회원이 존재하지 않습니다.")
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNAUTHORIZED_EMAIL);
    }

    @Test
    @DisplayName("게시글이 성공적으로 수정되면 게시글 id를 반환한다")
    @Transactional
    void updatePostSuccessfullyReturnsPostId() {

        //Given
        clean();
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        String token = "Bearer " + CORRECT_REFRESH_TOKEN;
        httpServletRequest.addHeader("Authorization", token);

        String updateContent = "Update content";
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest(updateContent);

        postSetUp();


        Post post = postRepository.findById(1L)
            .orElseThrow(() -> new FriendyException(ErrorCode.POST_NOT_FOUND, "존재하지 않는 게시글입니다"));

        //When
        Long postId = postService.updatePost(postUpdateRequest, httpServletRequest ,1L);

        //Then
        assertThat(postId).isEqualTo(1L);
        assertThat(post.getContent()).isEqualTo(updateContent);
    }

    @Test
    @DisplayName("존재하지 않는 게시물을 수정하면 FriendyException을 던진다")
    void throwsExceptionWhenPostNotFound() {
        // Given

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        String token = "Bearer " + CORRECT_REFRESH_TOKEN;
        httpServletRequest.addHeader("Authorization", token);

        String updateContent = "Update content";
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest(updateContent);

        postSetUp();

        // When & Then
        assertThatThrownBy(() -> postService.updatePost(postUpdateRequest,httpServletRequest,999L))
                .isInstanceOf(FriendyException.class)
                .hasMessageContaining("존재하지 않는 게시글입니다")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POST_NOT_FOUND);
    }
}
