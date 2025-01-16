package friendy.community.domain.post.model;

import friendy.community.domain.member.dto.request.MemberSignUpRequest;
import friendy.community.domain.member.fixture.MemberFixture;
import friendy.community.domain.member.model.Member;
import friendy.community.domain.post.dto.request.PostCreateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class PostTest {

    private Member member;

    @BeforeEach
    void setUp() {

        member = MemberFixture.memberFixture();

    }

    @Test
    @DisplayName("Post 객체가 생성되는지에 대한 test")
    void testPostCreationWithBaseEntityFields() {
        // Given
        String content = "This is a new post content.";
        PostCreateRequest postCreateRequest = new PostCreateRequest(content);

        // When
        Post post = Post.of(postCreateRequest, member);

        // Then
        assertNotNull(post);
        assertEquals(content, post.getContent());
        assertEquals(member, post.getMember());

    }
}
