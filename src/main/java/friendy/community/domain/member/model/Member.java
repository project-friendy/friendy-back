package friendy.community.domain.member.model;

import friendy.community.domain.member.dto.request.MemberSignUpRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Email email;

    @Embedded
    @Column(unique = true, nullable = false)
    private Nickname nickname;

    @Column(nullable = false)
    private Password password;

    @Column(nullable = false)
    private String salt;

    private LocalDate birthDate;

    private Member(final MemberSignUpRequest request, final String encryptedPassword, final String salt) {
        this.email = new Email(request.email());
        this.nickname = new Nickname(request.nickname());
        this.password = new Password(encryptedPassword);
        this.salt = salt;
        this.birthDate = request.birthDate();
    }

    public static Member of(final MemberSignUpRequest request, final String encryptedPassword, final String salt) {
        return new Member(request, encryptedPassword, salt);
    }
}