package friendy.community.domain.member.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class MemberTest {

    @Test
    @DisplayName("Member 엔티티가 저장되고 조회되는지 확인")
    void whenMemberIsSaved_thenCanBeFoundById() {
        // Given
        Member member = new Member(null, "test@example.com");

        // Then
        assertThat(member.getEmail()).isEqualTo("test@example.com");
    }

}