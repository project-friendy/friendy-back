package friendy.community.domain.hashtag.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HashtagTest {

    @Test
    @DisplayName("Hashtag 객체가 요청을 기반으로 생성되는지 테스트")
    void createHashtagSuccessfully() {
        // Given
        String name = "프렌디";

        // When
        Hashtag hashtag = new Hashtag(name);

        // Then
        assertThat(hashtag).isNotNull();
        assertThat(hashtag.getName()).isEqualTo(name);
    }
}