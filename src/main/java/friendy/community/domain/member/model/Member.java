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

    public void resetPassword(final String password) {
        this.password = password;
    }

    public Member(final String email, final String nickname, final String encryptedPassword, final String salt, final LocalDate birthDate) {
        this(null, email, nickname, encryptedPassword, salt, birthDate);
    }

    public static Member of(final MemberSignUpRequest request, final String encryptedPassword, final String salt) {
        return new Member(request.email(), request.nickname(), encryptedPassword, salt, request.birthDate());
    }

    public boolean matchPassword(String password) {
        return this.password.equals(password);
    }
}