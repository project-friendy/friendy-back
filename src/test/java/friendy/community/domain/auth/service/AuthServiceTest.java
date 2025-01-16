package friendy.community.domain.auth.service;

import friendy.community.domain.auth.dto.request.LoginRequest;
import friendy.community.domain.member.dto.request.PasswordRequest;
import friendy.community.domain.auth.dto.response.TokenResponse;
import friendy.community.domain.member.fixture.MemberFixture;
import friendy.community.domain.member.model.Member;
import friendy.community.domain.member.repository.MemberRepository;
import friendy.community.global.exception.FriendyException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static friendy.community.domain.auth.fixtures.TokenFixtures.CORRECT_REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DirtiesContext
class AuthServiceTest {

    @Autowired
    AuthService authService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("로그인 성공 시 액세스 토큰과 리프레시 토큰이 생성된다.")
    void loginSuccessfullyGeneratesTokens() {
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
    @DisplayName("토큰 재발급 성공 시 새로운 액세스 토큰과 리프레시 토큰이 반환된다.")
    void reissueTokenSuccessfullyReturnsNewTokens() {
        // Given
        Member savedMember = memberRepository.save(MemberFixture.memberFixture());
        String refreshToken = CORRECT_REFRESH_TOKEN;

        // When
        TokenResponse response = authService.reissueToken(refreshToken);

        // Then
        assertThat(response.accessToken()).isNotNull();
        assertThat(response.refreshToken()).isNotNull();
    }

}
