package friendy.community.domain.auth.service;

import friendy.community.domain.auth.dto.request.LoginRequest;
import friendy.community.domain.auth.dto.response.TokenResponse;
import friendy.community.domain.auth.jwt.JwtTokenExtractor;
import friendy.community.domain.auth.jwt.JwtTokenProvider;
import friendy.community.domain.member.fixture.MemberFixture;
import friendy.community.domain.member.model.Member;
import friendy.community.domain.member.repository.MemberRepository;
import friendy.community.global.exception.FriendyException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.Console;

import static friendy.community.domain.auth.fixtures.TokenFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@DirtiesContext
class AuthServiceTest {

    @Autowired
    AuthService authService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private StringRedisTemplate redisTemplate;


    @Test
    @DisplayName("로그인 성공 시 액세스 토큰과 리프레시 토큰이 생성된다.")
    void loginSuccessfullyGeneratesTokens() {
        // Redis Mock 셋업
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Given
        Member savedMember = memberRepository.save(MemberFixture.memberFixture());
        LoginRequest loginRequest = new LoginRequest(savedMember.getEmail(), MemberFixture.getFixturePlainPassword());

        // When
        TokenResponse response = authService.login(loginRequest);

        // Then
        assertThat(response.accessToken()).isNotNull();
        assertThat(response.refreshToken()).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인 시 예외를 던진다")
    void throwsExceptionWhenEmailNotFound() {
        // Given
        LoginRequest request = new LoginRequest("nonexistent@example.com", MemberFixture.getFixturePlainPassword());

        // When & Then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(FriendyException.class)
                .hasMessageContaining("해당 이메일의 회원이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 예외를 던진다")
    void throwsExceptionWhenPasswordDoesNotMatch() {
        // Given
        Member savedMember = memberRepository.save(MemberFixture.memberFixture());
        LoginRequest loginRequest = new LoginRequest(savedMember.getEmail(), "wrongPassword");

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(FriendyException.class)
                .hasMessageContaining("로그인에 실패하였습니다. 비밀번호를 확인해주세요.");
    }

    @Test
    @DisplayName("로그아웃에 성공하면 Redis에서 리프레시 토큰이 삭제된다.")
    void logoutSuccessfullyDeleteRefreshTokenInRedis() {
        // Redis Mock 셋업
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Given
        Member savedMember = memberRepository.save(MemberFixture.memberFixture());
        final String memberEmail = savedMember.getEmail();
        final String accessToken = jwtTokenProvider.generateAccessToken(memberEmail);
        final String refreshToken = jwtTokenProvider.generateRefreshToken(memberEmail);

        when(redisTemplate.hasKey(memberEmail)).thenReturn(true);

        // When
        authService.logout(accessToken, refreshToken);

        // Then
        verify(redisTemplate, times(1)).delete(memberEmail);
    }

    @Test
    @DisplayName("로그인 상태가 아닌 사용자가 로그아웃 요청을 하면 예외가 발생한다.")
    void inValidLogoutRequestThrowsException() {
        // Redis Mock 셋업
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Given
        Member savedMember = memberRepository.save(MemberFixture.memberFixture());
        final String memberEmail = savedMember.getEmail();
        String accessToken = jwtTokenProvider.generateAccessToken(memberEmail);
        final String refreshToken = jwtTokenProvider.generateRefreshToken(memberEmail);

        when(redisTemplate.hasKey(memberEmail)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.logout(accessToken, refreshToken))
                .isInstanceOf(FriendyException.class)
                .hasMessageContaining("인증 실패(등록되지 않은 리프레시 토큰) - 토큰 : " + refreshToken);
    }

    @Test
    @DisplayName("만료된 리프레시 토큰으로 로그아웃 요청을 하면 예외가 발생한다.")
    void logoutRequestWithExpiredRefreshTokenThrowsException() {
        // Given
        final String accessToken = jwtTokenProvider.generateAccessToken("example@friendy.com");
        final String expiredRefreshToken = EXPIRED_TOKEN;

        // When & Then
        assertThatThrownBy(() -> authService.logout(accessToken, expiredRefreshToken))
                .isInstanceOf(FriendyException.class)
                .hasMessageContaining("인증 실패(만료된 리프레시 토큰) - 토큰 : " + expiredRefreshToken);
    }

    @Test
    @DisplayName("잘못된 리프레시 토큰으로 로그아웃 요청을 하면 예외가 발생한다.")
    void logoutRequestWithMalformedRefreshTokenThrowsException() {
        // Given
        final String accessToken = jwtTokenProvider.generateAccessToken("example@friendy.com");
        final String malformedToken = MALFORMED_JWT_TOKEN;

        // When & Then
        assertThatThrownBy(() -> authService.logout(accessToken, malformedToken))
                .isInstanceOf(FriendyException.class)
                .hasMessageContaining("인증 실패(잘못된 리프레시 토큰) - 토큰 : " + malformedToken);
    }

    @Test
    @DisplayName("토큰 재발급 성공 시 새로운 액세스 토큰과 리프레시 토큰이 반환된다.")
    void reissueTokenSuccessfullyReturnsNewTokens() {
        // Given
        Member savedMember = memberRepository.save(MemberFixture.memberFixture());
        String refreshToken = CORRECT_REFRESH_TOKEN;

        // Redis Mock 셋업
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.hasKey("example@friendy.com")).thenReturn(true);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // When
        TokenResponse response = authService.reissueToken(refreshToken);

        // Then
        assertThat(response.accessToken()).isNotNull();
        assertThat(response.refreshToken()).isNotNull();
    }

}
