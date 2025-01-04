package friendy.community.domain.member.fixture;

import friendy.community.domain.member.encryption.PasswordEncryptor;
import friendy.community.domain.member.encryption.SHA2PasswordEncryptor;
import friendy.community.domain.member.model.Member;

import java.time.LocalDate;

public class MemberFixture {

    private static final PasswordEncryptor passwordEncryptor = new SHA2PasswordEncryptor();

    public static Member memberFixture() {
        String encrypted = passwordEncryptor.encrypt(getFixturePlainPassword(), "salt");
        return new Member(
                "example@friendy.com",
                "bokSungKim",
                encrypted,
                "salt",
                LocalDate.parse("2002-08-13")
        );
    }

    public static String getFixturePlainPassword() {
        return "password123!";
    }

}
