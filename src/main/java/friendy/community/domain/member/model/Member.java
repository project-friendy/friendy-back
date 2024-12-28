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
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Email email;

    @Embedded
    private Nickname nickname;

    @Column(nullable = false, length = 100)
    private Password password;

    private LocalDate birthDate;

    private Member(final MemberSignUpRequest request) {
        this.email = new Email(request.email());
        this.nickname = new Nickname(request.nickname());
        this.password = new Password(request.password());
        this.birthDate = request.birthDate();
    }

    public static Member of(final MemberSignUpRequest request) {
        return new Member(request);
    }
}