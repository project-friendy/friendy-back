package friendy.community.domain.post.dto.response;

import java.util.List;

public record PostListResponse(
        List<PostSummary> posts,
        int currentPage,
        int totalPages,
        long totalElements
) {
}
