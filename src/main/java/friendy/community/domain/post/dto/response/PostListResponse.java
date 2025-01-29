package friendy.community.domain.post.dto.response;

import java.util.List;

public record PostListResponse(
        List<FindPostResponse> posts,
        Integer totalPages
) {
}
