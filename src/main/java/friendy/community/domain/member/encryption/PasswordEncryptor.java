package friendy.community.domain.member.encryption;

public interface PasswordEncryptor {
    String encrypt(String plainPassword, String salt);
}
