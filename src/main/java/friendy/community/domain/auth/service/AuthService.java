package friendy.community.domain.auth.service;

import friendy.community.domain.auth.dto.request.LoginRequest;
import friendy.community.domain.auth.dto.response.LoginResponse;
import friendy.community.domain.auth.jwt.JwtTokenProvider;
import friendy.community.domain.member.encryption.PasswordEncryptor;
import friendy.community.domain.member.model.Member;
import friendy.community.domain.member.repository.MemberRepository;
import friendy.community.global.exception.ErrorCode;
import friendy.community.global.exception.FriendyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncryptor passwordEncryptor;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(final LoginRequest request) {
        final Member member = getVerifiedMember(request.email(), request.password());
        final String accessToken = jwtTokenProvider.generateAccessToken(request.email());
        final String refreshToken = jwtTokenProvider.generateRefreshToken(request.email());
        
        return LoginResponse.of(accessToken, refreshToken);
    }

    private Member getVerifiedMember(String email, String password) {
        Member member = getMemberByEmail(email);
        validateCorrectPassword(member, password);
        return member;
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new FriendyException(ErrorCode.UNAUTHORIZED_EMAIL, "해당 이메일의 회원이 존재하지 않습니다."));
    }

    private void validateCorrectPassword(Member member, String password) {
        String salt = member.getSalt();
        String encryptedPassword = passwordEncryptor.encrypt(password, salt);
        if (!member.matchPassword(encryptedPassword)) {
            throw new FriendyException(ErrorCode.UNAUTHORIZED_PASSWORD, "로그인에 실패하였습니다. 비밀번호를 확인해주세요.");
        }
    }

}
