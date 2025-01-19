package friendy.community.domain.auth.jwt;

import friendy.community.global.exception.FriendyException;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

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
    @DisplayName("엑세스 토큰에서 이메일을 추출한다")
    void extractEmailFromAccessTokenSuccessfully() {
        // given
        String email = "example@friendy.com";
        String accessToken = jwtTokenProvider.generateAccessToken(email);

        // when
        String extractedEmail = jwtTokenProvider.extractEmailFromAccessToken(accessToken);

        // then
        assertThat(extractedEmail).isEqualTo(email);
    }

    @Test
    @DisplayName("잘못된 형식의 엑세스 토큰에서 이메일 추출 시 예외를 발생시킨다")
    void throwExceptionForMalformedAccessTokenEmailExtraction() {
        // given
        String malFormedJwtToken = MALFORMED_JWT_TOKEN;

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.extractEmailFromAccessToken(malFormedJwtToken))
                .isInstanceOf(FriendyException.class)
                .hasMessageContaining("인증 실패(잘못된 액세스 토큰) - 토큰 : " + malFormedJwtToken);
    }

    @Test
    @DisplayName("만료된 엑세스 토큰에서 이메일 추출 시 예외를 발생시킨다")
    void throwExceptionForExpiredAccessTokenEmailExtraction() {
        // given
        String expiredAccessToken = EXPIRED_TOKEN;

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.extractEmailFromAccessToken(expiredAccessToken))
                .isInstanceOf(FriendyException.class)
                .hasMessageContaining("인증 실패(만료된 액세스 토큰) - 토큰 : " + expiredAccessToken);
    }

    @Test
    @DisplayName("엑세스 토큰에 이메일 클레임이 누락된 경우 예외를 발생시킨다")
    void throwExceptionForMissingEmailClaimInAccessToken() {
        // given
        String tokenWithoutEmailClaim = MISSING_CLAIM_TOKEN;

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.extractEmailFromAccessToken(tokenWithoutEmailClaim))
                .isInstanceOf(FriendyException.class)
                .hasMessageContaining("인증 실패(JWT 액세스 토큰 Payload 이메일 누락) - 토큰 : " + tokenWithoutEmailClaim);
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
    @DisplayName("리프레시 토큰에서 이메일을 추출한다")
    void extractEmailFromRefreshTokenSuccessfully() {
        // given
        String email = "example@friendy.com";
        String refreshToken = jwtTokenProvider.generateRefreshToken(email);

        // when
        String extractedEmail = jwtTokenProvider.extractEmailFromRefreshToken(refreshToken);

        // then
        assertThat(extractedEmail).isEqualTo(email);
    }

    @Test
    @DisplayName("잘못된 형식의 리프레시 토큰에서 이메일 추출 시 예외를 발생시킨다")
    void throwExceptionForMalformedRefreshTokenEmailExtraction() {
        // given
        String malFormedJwtToken = MALFORMED_JWT_TOKEN;

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.extractEmailFromRefreshToken(malFormedJwtToken))
                .isInstanceOf(FriendyException.class)
                .hasMessageContaining("인증 실패(잘못된 리프레시 토큰) - 토큰 : " + malFormedJwtToken);
    }

    @Test
    @DisplayName("만료된 리프레시 토큰에서 이메일 추출 시 예외를 발생시킨다")
    void throwExceptionForExpiredRefreshTokenEmailExtraction() {
        // given
        String expiredRefreshToken = EXPIRED_TOKEN;

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.extractEmailFromRefreshToken(expiredRefreshToken))
                .isInstanceOf(FriendyException.class)
                .hasMessageContaining("인증 실패(만료된 리프레시 토큰) - 토큰 : " + expiredRefreshToken);
    }

    @Test
    @DisplayName("리프레시 토큰에 이메일 클레임이 누락된 경우 예외를 발생시킨다")
    void throwExceptionForMissingEmailClaimInRefreshToken() {
        // given
        String tokenWithoutEmailClaim = MISSING_CLAIM_TOKEN;

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.extractEmailFromRefreshToken(tokenWithoutEmailClaim))
                .isInstanceOf(FriendyException.class)
                .hasMessageContaining("인증 실패(JWT 리프레시 토큰 Payload 이메일 누락) - 토큰 : " + tokenWithoutEmailClaim);
    }
}
