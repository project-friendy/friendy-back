package friendy.community.domain.auth.jwt;

import friendy.community.global.exception.FriendyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static friendy.community.domain.auth.fixtures.TokenFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("엑세스 토큰 생성에 성공한다")
    void generateAccessTokenSuccessfully() {
        // given
        String email = "example@friendy.com";

        // when
        String accessToken = jwtTokenProvider.generateAccessToken(email);

        // then
        assertThat(accessToken).isNotNull();
    }

    @Test
    @DisplayName("엑세스 토큰 검증에 성공한다")
    void validateAccessTokenSuccessfully() {
        // given
        String email = "example@friendy.com";
        String accessToken = jwtTokenProvider.generateAccessToken(email);

        // when & then
        jwtTokenProvider.validateAccessToken(accessToken);
    }

    @Test
    @DisplayName("잘못된 형식의 엑세스 토큰이 검증 시 예외를 발생시킨다")
    void throwExceptionForMalformedAccessToken() {
        // given
        String malFormedJwtToken = MALFORMED_JWT_TOKEN;

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.validateAccessToken(malFormedJwtToken))
                .isInstanceOf(FriendyException.class)
                .hasMessageContaining("인증 실패(잘못된 액세스 토큰) - 토큰 : " + malFormedJwtToken);
    }

    @Test
    @DisplayName("만료된 엑세스 토큰이 검증 시 예외를 발생시킨다")
    void throwExceptionForExpiredAccessToken() {
        // given
        String expiredAccessToken = EXPIRED_TOKEN;

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.validateAccessToken(expiredAccessToken))
                .isInstanceOf(FriendyException.class)
                .hasMessageContaining("액세스 토큰이 만료되었습니다.");
    }

    @Test
    @DisplayName("리프레시 토큰 생성에 성공한다")
    void generateRefreshTokenSuccessfully() {
        // given
        String email = "example@friendy.com";

        // when
        String refreshToken = jwtTokenProvider.generateRefreshToken(email);

        // then
        assertThat(refreshToken).isNotNull();
    }

    @Test
    @DisplayName("리프레시 토큰 검증에 성공한다")
    void validateRefreshTokenSuccessfully() {
        // given
        String email = "example@friendy.com";
        String refreshToken = jwtTokenProvider.generateRefreshToken(email);

        // when & then
        jwtTokenProvider.validateRefreshToken(refreshToken);
    }

    @Test
    @DisplayName("잘못된 형식의 리프레시 토큰이 검증 시 예외를 발생시킨다")
    void throwExceptionForMalformedRefreshToken() {
        // given
        String malFormedJwtToken = MALFORMED_JWT_TOKEN;

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.validateRefreshToken(malFormedJwtToken))
                .isInstanceOf(FriendyException.class)
                .hasMessageContaining("인증 실패(잘못된 리프레시 토큰) - 토큰 : " + malFormedJwtToken);
    }

    @Test
    @DisplayName("만료된 리프레시 토큰이 검증 시 예외를 발생시킨다")
    void throwExceptionForExpiredRefreshToken() {
        // given
        String expiredRefreshToken = EXPIRED_TOKEN;

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.validateRefreshToken(expiredRefreshToken))
                .isInstanceOf(FriendyException.class)
                .hasMessageContaining("리프레시 토큰이 만료되었습니다.");
    }
}
