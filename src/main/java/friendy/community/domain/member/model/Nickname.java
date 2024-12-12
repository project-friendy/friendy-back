package friendy.community.domain.member.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.regex.Pattern;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(AccessLevel.PROTECTED)
public class Nickname {

    private static final Pattern PATTERN = Pattern.compile("^[a-zA-Z가-힣][a-zA-Z0-9가-힣!@#$%^&*()_+\\-=<>?/]*$");
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 20;

    @Column(name = "nickname", nullable = false, length = 20)
    private String nickname;

    protected Nickname(final String nickname) {
        this.nickname = nickname;
    }

}