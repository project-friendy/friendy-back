package friendy.community.domain.post.dto.response;

import java.util.List;

public record PostListResponse(
        List<PostSummaryResponse> posts,
        int currentPage,
        int totalPages,
        long totalElements
) {
}
