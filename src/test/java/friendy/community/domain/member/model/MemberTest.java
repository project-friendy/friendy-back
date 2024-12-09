package friendy.community.domain.member.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")

class MemberTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Member 엔티티가 저장되고 조회되는지 확인")
    void whenMemberIsSaved_thenCanBeFoundById() {
        // Given
        Member member = new Member(null, "test@example.com");

        // When
        Member savedMember = entityManager.persistFlushFind(member);

        // Then
        assertThat(savedMember.getId()).isNotNull();
        assertThat(savedMember.getEmail()).isEqualTo("test@example.com");
    }

}