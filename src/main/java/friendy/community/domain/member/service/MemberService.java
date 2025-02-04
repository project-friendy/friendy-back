package friendy.community.domain.member.service;

import friendy.community.domain.common.S3service;
import friendy.community.domain.member.dto.request.PasswordRequest;
import friendy.community.domain.auth.service.AuthService;
import friendy.community.domain.member.dto.request.MemberSignUpRequest;
import friendy.community.domain.member.encryption.PasswordEncryptor;
import friendy.community.domain.member.encryption.SaltGenerator;
import friendy.community.domain.member.model.Member;
import friendy.community.domain.member.model.MemberImage;
import friendy.community.domain.member.repository.MemberRepository;
import friendy.community.global.exception.ErrorCode;
import friendy.community.global.exception.FriendyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository  memberRepository;
    private final SaltGenerator saltGenerator;
    private final PasswordEncryptor passwordEncryptor;
    private final AuthService authService;
    private final S3service s3service;

    public Long signUp(MemberSignUpRequest request, MultipartFile image) {
        validateUniqueMemberAttributes(request);
        final String salt = saltGenerator.generate();
        final String encryptedPassword = passwordEncryptor.encrypt(request.password(), salt);
        String imageUrl = "default-profile-image-url";
        Member member = new Member(request, encryptedPassword, salt);

        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = s3service.upload(image, "profile");
                String storedFileName = s3service.generateStoredFileName(image, "profile");
                String fileType = s3service.getFileType(image);
                final MemberImage memberImage = MemberImage.of(imageUrl, storedFileName, fileType);
                member.setMemberImage(memberImage);
            } catch (Exception e) {
                throw new RuntimeException("이미지 업로드 실패", e);
            }
        }
        memberRepository.save(member);

        return member.getId();
    }

    public void resetPassword(PasswordRequest request) {
        Member member = authService.getMemberByEmail(request.email());

        final String salt = saltGenerator.generate();
        final String encryptedPassword = passwordEncryptor.encrypt(request.newPassword(), salt);

        member.resetPassword(encryptedPassword, salt);
        memberRepository.save(member);
    }

    public void validateUniqueMemberAttributes(MemberSignUpRequest request) {
        assertUniqueEmail(request.email());
        assertUniqueName(request.nickname());
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
