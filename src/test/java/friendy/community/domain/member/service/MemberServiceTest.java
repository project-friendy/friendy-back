package friendy.community.domain.member.service;

import friendy.community.domain.auth.service.AuthService;
import friendy.community.domain.member.dto.request.MemberSignUpRequest;
import friendy.community.domain.member.dto.request.PasswordRequest;
import friendy.community.domain.member.encryption.PasswordEncryptor;
import friendy.community.domain.member.encryption.SaltGenerator;
import friendy.community.domain.member.fixture.MemberFixture;
import friendy.community.domain.member.model.Member;
import friendy.community.domain.member.model.MemberImage;
import friendy.community.domain.member.repository.MemberRepository;
import friendy.community.global.exception.ErrorCode;
import friendy.community.global.exception.FriendyException;
import friendy.community.infra.storage.s3.service.S3service;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@DirtiesContext
class MemberServiceTest {


    @Autowired
    private MemberRepository memberRepository;  // MemberRepository Mock

    @MockitoBean
    private S3service s3Service;  // S3Service Mock

    @Autowired
    private SaltGenerator saltGenerator;  // SaltGenerator Mock

    @Autowired
    private PasswordEncryptor passwordEncryptor;  // PasswordEncryptor Mock

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberService memberService;  // Mock된 의존성들이 주입된 MemberService

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Mockito 초기화
        resetMemberIdSequence();
    }

    private void resetMemberIdSequence() {
        entityManager.createNativeQuery("ALTER TABLE member AUTO_INCREMENT = 1").executeUpdate();
    }

    @Test
    @DisplayName("회원가입이 성공적으로 처리되면 회원 ID를 반환한다")
    void signUpSuccessfullyReturnsMemberId() {
        // Given
        MemberSignUpRequest request = new MemberSignUpRequest(
            "test@email.com", "testNickname", "password123!",  LocalDate.parse("2002-08-13"),null
        );
        // When
        Long memberId = memberService.signUp(request);
        // Then
        assertThat(memberId).isEqualTo(1L); // 반환 값 검증
    }

    @Test
    @DisplayName("이메일이 중복되면 FriendyException을 던진다")
    void throwsExceptionWhenDuplicateEmail() {
        // Given
        Member savedMember = memberRepository.save(MemberFixture.memberFixture());

        // When & Then
        assertThatThrownBy(() -> memberService.assertUniqueEmail(savedMember.getEmail()))
            .isInstanceOf(FriendyException.class)
            .hasMessageContaining("이미 가입된 이메일입니다.")
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_EMAIL);
    }

    @Test
    @DisplayName("닉네임이 중복되면 FriendyException을 던진다")
    void throwsExceptionWhenDuplicateNickname() {
        // Given
        Member savedMember = memberRepository.save(MemberFixture.memberFixture());

        // When & Then
        assertThatThrownBy(() -> memberService.assertUniqueName(savedMember.getNickname()))
            .isInstanceOf(FriendyException.class)
            .hasMessageContaining("닉네임이 이미 존재합니다.")
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_NICKNAME);
    }

    @Test
    @DisplayName("비밀번호 변경 성공 시 해당 객체의 비밀번호가 변경된다")
    void resetPasswordSuccessfullyPasswordIsChanged() {
        // Given
        Member savedMember = memberRepository.save(MemberFixture.memberFixture());
        PasswordRequest request = new PasswordRequest(savedMember.getEmail(), "newPassword123!");
        String originPassword = savedMember.getPassword();

        // When
        memberService.resetPassword(request);
        Member changedMember = authService.getMemberByEmail(savedMember.getEmail());

        //Then
        assertThat(originPassword).isNotEqualTo(changedMember.getPassword());
    }

    @Test
    @DisplayName("요청받은 이메일이 존재하지 않으면 예외를 던진다")
    void throwsExceptionWhenEmailDosentExists() {
        // Given
        Member savedMember = memberRepository.save(MemberFixture.memberFixture());
        PasswordRequest request = new PasswordRequest("wrongEmail@friendy.com", "newPassword123!");

        // When & Then
        assertThatThrownBy(() -> memberService.resetPassword(request))
                .isInstanceOf(FriendyException.class)
                .hasMessageContaining("해당 이메일의 회원이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("회원가입시 프로필 사진이있는경우")
    void signUpwithimageSuccessfullyReturnsMemberId() {
        // Given

        MemberSignUpRequest request = new MemberSignUpRequest(
            "test@email.com", "testNickname", "password123!",  LocalDate.parse("2002-08-13"),
            "https://friendybucket.s3.us-east-2.amazonaws.com/temp/5f48c9c9-76eb-4309-8fe5-a2f31d9e0d53.jpg"
        );
        // When
        String expectedImageUrl = "https://friendybucket.s3.us-east-2.amazonaws.com/profile/5f48c9c9-76eb-4309-8fe5-a2f31d9e0d53.jpg";
        String expectedFilePath = "profile/5f48c9c9-76eb-4309-8fe5-a2f31d9e0d53.jpg";

        MemberImage expectedMemberImage = new MemberImage("https://www.example.com/test-image.jpg", "mocked-file-path", "image/png");

        when(s3Service.moveS3Object(request.imageUrl(), "profile")).thenReturn(expectedImageUrl);
        when(s3Service.extractFilePath(anyString())).thenReturn(expectedFilePath);
        when(s3Service.getContentTypeFromS3(anyString())).thenReturn("jpeg");


        Long memberId = memberService.signUp(request);

        // Then
        assertThat(memberId).isEqualTo(1L); // 반환 값 검증
        verify(s3Service).moveS3Object(request.imageUrl(), "profile");
    }

}