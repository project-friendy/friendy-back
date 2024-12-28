package friendy.community.domain.member.encryption;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class RandomSaltGenerator implements SaltGenerator {

    @Override
    public String generate() {
        SecureRandom byteGenerator = new SecureRandom();
        byte[] saltByte = new byte[32];
        byteGenerator.nextBytes(saltByte);
        return Base64.getEncoder().encodeToString(saltByte);
    }

}
