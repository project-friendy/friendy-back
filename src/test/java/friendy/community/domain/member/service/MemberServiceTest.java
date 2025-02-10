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
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@DirtiesContext
class MemberServiceTest {


    @Mock
    private MemberRepository memberRepository;  // MemberRepository Mock

    @Mock
    private S3service s3Service;  // S3Service Mock

    @Mock
    private SaltGenerator saltGenerator;  // SaltGenerator Mock

    @Mock
    private PasswordEncryptor passwordEncryptor;  // PasswordEncryptor Mock

    @Mock
    private AuthService authService;

    @InjectMocks
    private MemberService memberService;  // Mock된 의존성들이 주입된 MemberService

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Mockito 초기화
    }

    @Test
    @DisplayName("회원가입이 성공적으로 처리되면 회원 ID를 반환한다")
    void signUpSuccessfullyReturnsMemberId() {
        // Given
        Member member = MemberFixture.memberFixture();
        MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest(
            member.getEmail(),
            member.getNickname(),
            member.getPassword(),
            member.getBirthDate(),
            null  // 프로필 사진 URL은 null로 설정
        );

        // mock memberRepository의 동작: 회원을 저장한 후, 저장된 회원을 반환
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // When
        Long savedId = memberService.signUp(memberSignUpRequest);  // 회원 가입 처리
        Optional<Member> actualMember = memberRepository.findById(savedId);  // 저장된 회원 조회

        // Then
        assertThat(actualMember).isPresent();  // 회원이 정상적으로 저장되었는지 확인
        assertThat(actualMember.get().getEmail()).isEqualTo(member.getEmail());  // 이메일이 일치하는지 확인

        // S3Service의 moveS3Object 메서드는 호출되지 않음 (이미지 URL이 null이므로)
        verify(s3Service, never()).moveS3Object(anyString(), anyString());

        // saltGenerator와 passwordEncryptor 메서드 호출 여부 확인 (이미지 URL은 null이므로)
        verify(saltGenerator).generate();
        verify(passwordEncryptor).encrypt(anyString(), anyString());
    }

    @Test
    @DisplayName("회원가입이 성공적으로 처리되면 회원 ID를 반환한다")
    void signUpSuccessfullyReturnsMemberId1() {
        // Given
        Member member = MemberFixture.memberFixture();
        MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest(
            member.getEmail(),
            member.getNickname(),
            member.getPassword(),
            member.getBirthDate(),
            null  // 프로필 사진 URL은 null로 설정
        );

        // mock memberRepository의 동작: 회원을 저장한 후, 저장된 회원을 반환
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member)); // findById mock 추가
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // When
        Long savedId = memberService.signUp(memberSignUpRequest);  // 회원 가입 처리
        Optional<Member> actualMember = memberRepository.findById(savedId);  // 저장된 회원 조회

        // Then
        assertThat(actualMember).isPresent();  // 회원이 정상적으로 저장되었는지 확인
        assertThat(actualMember.get().getEmail()).isEqualTo(member.getEmail());  // 이메일이 일치하는지 확인

        // S3Service의 moveS3Object 메서드는 호출되지 않음 (이미지 URL이 null이므로)
        verify(s3Service, never()).moveS3Object(anyString(), anyString());
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

        Member member = MemberFixture.memberFixture();
        MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest(member.getEmail(), member.getNickname(), member.getPassword(), member.getBirthDate(),"https://friendybucket.s3.us-east-2.amazonaws.com/temp/5f48c9c9-76eb-4309-8fe5-a2f31d9e0d53.jpg");

        // When
        MemberImage expectedMemberImage = new MemberImage("https://www.example.com/test-image.jpg", "mocked-file-path", "image/png");

        when(memberService.saveProfileImage(any(MemberSignUpRequest.class)))
            .thenReturn(expectedMemberImage);

        Long savedId = memberService.signUp(memberSignUpRequest);
        Optional<Member> actualMember = memberRepository.findById(savedId);

        // Then
        assertThat(actualMember).isPresent();
        assertThat(actualMember.get().getEmail()).isEqualTo(member.getEmail());
    }

}