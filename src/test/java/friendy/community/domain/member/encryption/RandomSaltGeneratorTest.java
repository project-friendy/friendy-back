package friendy.community.domain.member.encryption;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RandomSaltGeneratorTest {

    private final RandomSaltGenerator randomSaltGenerator = new RandomSaltGenerator();

    @Test
    @DisplayName("Salt가 Base64로 인코딩된 32바이트 크기라면 성공")
    void generateReturnsBase64Encoded32ByteSalt() {
        // When
        String salt = randomSaltGenerator.generate();

        // Then
        byte[] decodedSalt = Base64.getDecoder().decode(salt);
        assertThat(decodedSalt).hasSize(32);
        assertThat(salt).isNotBlank();
    }

    @Test
    @DisplayName("Salt를 여러 번 생성해도 항상 고유한 값을 반환하면 성공")
    void generateReturnsUniqueSaltEachTime() {
        // Given
        Set<String> salts = new HashSet<>();
        int numberOfGenerations = 1000;

        // When
        for (int i = 0; i < numberOfGenerations; i++) {
            salts.add(randomSaltGenerator.generate());
        }

        // Then
        assertThat(salts).hasSize(numberOfGenerations);
    }
}
