package friendy.community.domain.member.model;

import friendy.community.domain.member.dto.request.MemberSignUpRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String salt;

    @Column(nullable = false)
    private LocalDate birthDate;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private MemberImage memberImage;

    public void resetPassword(final String password, final String salt) {
        this.password = password;
        this.salt = salt;
    }

    public void setMemberImage(MemberImage memberImage) {
        this.memberImage = memberImage;
    }

    public Member(final MemberSignUpRequest request, final String encryptedPassword, final String salt) {
        this.email = request.email();
        this.nickname = request.nickname();
        this.password = encryptedPassword;
        this.birthDate = request.birthDate();
    }

    public static Member of(final MemberSignUpRequest request, final String encryptedPassword, final String salt) {
        return new Member(request, encryptedPassword, salt);
    }

    public boolean matchPassword(String password) {
        return this.password.equals(password);
    }
}