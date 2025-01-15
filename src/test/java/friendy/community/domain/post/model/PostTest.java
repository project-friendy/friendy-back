package friendy.community.domain.post.model;

import friendy.community.domain.member.dto.request.MemberSignUpRequest;
import friendy.community.domain.member.model.Member;
import friendy.community.domain.post.dto.request.PostCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PostTest {

    private Member member;

    @BeforeEach
    void setUp() {
        MemberSignUpRequest signUpRequest = new MemberSignUpRequest(
            "example@friendy.com",
            "bokSungKim",
            "password123!",
            LocalDate.of(2002, 8, 13)
        );

        String encryptedPassword = "encryptedDummyPassword";
        String salt = "dummySalt";
        member = Member.of(signUpRequest,encryptedPassword,salt);


    }

    @Test
    @DisplayName("Post 객체가 생성되는지에 대한 test")
    void testPostCreationWithBaseEntityFields() {
        // Given
        String content = "This is a new post content.";
        PostCreateRequest postCreateRequest = new PostCreateRequest(content);

        // when
        Post post = Post.of(postCreateRequest, member);

        // Assert
        assertNotNull(post);
        assertEquals(content, post.getContent());
        assertEquals(member, post.getMember());
    }
}
