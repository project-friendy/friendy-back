package friendy.community.domain.post.dto.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record PostSummaryResponse(
    Long id,
    String content,
    String createdAt,
    String timeAgo,
    int likeCount,
    int commentCount,
    int shareCount,
    AuthorResponse authorResponse
) {
    public PostSummaryResponse(Long id, String content, LocalDateTime createdAt, String timeAgo,
                               int likeCount, int commentCount, int shareCount, AuthorResponse authorResponse) {
        this(id, content,
            createdAt != null ? createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")) : null,
            timeAgo, likeCount, commentCount, shareCount, authorResponse);
    }
}
