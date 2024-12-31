package friendy.community.domain.member.service;

import friendy.community.domain.member.dto.request.MemberSignUpRequest;
import friendy.community.domain.member.encryption.PasswordEncryptor;
import friendy.community.domain.member.encryption.SaltGenerator;
import friendy.community.domain.member.model.Member;
import friendy.community.domain.member.repository.MemberRepository;
import friendy.community.global.exception.ErrorCode;
import friendy.community.global.exception.FriendyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository  memberRepository;
    private final SaltGenerator saltGenerator;
    private final PasswordEncryptor passwordEncryptor;

    public Long signUp(MemberSignUpRequest request) {
        assertUniqueEmail(request.email());
        assertUniqueName(request.nickname());
        final String salt = saltGenerator.generate();
        final String encryptedPassword = passwordEncryptor.encrypt(request.password(), salt);
        final Member member = Member.of(request, encryptedPassword, salt);
        memberRepository.save(member);

        return member.getId();
    }

    public void assertUniqueEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new FriendyException(ErrorCode.DUPLICATE_EMAIL, "이미 가입된 이메일입니다.");
        }
    }

    public void assertUniqueName(String name) {
        if (memberRepository.existsByNickname(name)) {
            throw new FriendyException(ErrorCode.DUPLICATE_NICKNAME, "닉네임이 이미 존재합니다.");
        }
    }
}
