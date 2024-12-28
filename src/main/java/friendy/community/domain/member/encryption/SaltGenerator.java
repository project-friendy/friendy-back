package friendy.community.domain.member.encryption;

@FunctionalInterface
public interface SaltGenerator {
    String generate();
}
