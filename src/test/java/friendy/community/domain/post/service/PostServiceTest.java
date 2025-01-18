package friendy.community.domain.post.service;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;

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

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private EntityManager entityManager;

    private Member member;

    private MockHttpServletRequest httpServletRequest;

    @BeforeEach
    void setup() {

        httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.addHeader("Authorization", "Bearer " + CORRECT_REFRESH_TOKEN);

        member = MemberFixture.memberFixture();
        MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest(
                member.getEmail(), member.getNickname(), member.getPassword(), member.getBirthDate()
        );
        memberService.signUp(memberSignUpRequest);

        resetPostIdSequence();
    }

    private void resetPostIdSequence() {
        entityManager.createNativeQuery("ALTER TABLE post AUTO_INCREMENT = 1")
                .executeUpdate();
    }


    private void postSetUp(String content) {

        PostCreateRequest postCreateRequest = new PostCreateRequest(content);
        Long postId = postService.savePost(postCreateRequest, httpServletRequest);
    }


    @Test
    @DisplayName("게시글이 성공적으로 생성되면 게시글 ID를 반환한다")
    void createPostSuccessfullyReturnsPostId() {

        //Given
        String content = "This is a new post content.";
        PostCreateRequest postCreateRequest = new PostCreateRequest(content);

        //When
        Long postId = postService.savePost(postCreateRequest, httpServletRequest);

        //Then
        assertThat(postId).isEqualTo(1L);

    }

    @Test
    @DisplayName("이메일이 존재하지 않으면 FriendyException 던진다")
    void throwsExceptionWhenEmailNotFound() {
        // Given

        memberRepository.deleteAll();
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
    void updatePostSuccessfullyReturnsPostId() {

        //Given
        String updateContent = "Update content";
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest(updateContent);

        postSetUp("This is content");

        Post post = postRepository.findById(1L)
            .orElseThrow(() -> new FriendyException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 게시글입니다"));

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
        String updateContent = "Update content";
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest(updateContent);

        postSetUp("This is content");

        // When & Then
        assertThatThrownBy(() -> postService.updatePost(postUpdateRequest,httpServletRequest,999L))
                .isInstanceOf(FriendyException.class)
                .hasMessageContaining("존재하지 않는 게시글입니다")
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESOURCE_NOT_FOUND);
    }

    @Test
    @DisplayName("게시글 작성자가 아닌 사용자가 수정하면 FriendyException을 던진다")
    void throwsExceptionWhenNotPostAuthor() {
        // Given
        String updateContent = "Update content";
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest(updateContent);
        postSetUp("This is content");

        MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest(
                "email2@friendy.com", "홍길동", "password123!", LocalDate.parse("2002-08-13")
        );
        memberService.signUp(memberSignUpRequest);

        httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.addHeader("Authorization", "Bearer " +
                "eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImVtYWlsMkBmcmllbmR5LmNvbSIsImlhdCI6MTczNzE4OTc3NSwiZXhwIjoxNzM3MTkzMzc1fQ.peiTSH0DxDND2QZXeDVwaxklCGOFwiz-PAaznlRpq28");

        // When & Then
        assertThatThrownBy(() -> postService.updatePost(postUpdateRequest, httpServletRequest, 1L)) // 1L: 존재하는 게시글
                .isInstanceOf(FriendyException.class) // 예외 타입 확인
                .hasMessageContaining("작성자만 게시글을 수정할 수 있습니다.") // 예외 메시지 확인
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_ACCESS); // 에러 코드 확인
    }

}
