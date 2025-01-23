package friendy.community.domain.post.dto.response;

public record AuthorResponse(
        Long memberId,
        String nickname
) {
}