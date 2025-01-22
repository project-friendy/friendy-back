package friendy.community.domain.post.dto.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record PostSummary(
        Long id,
        String content,
        String createdAt,
        String timeAgo,
        Author author
) {

    public PostSummary(Long id, String content, LocalDateTime createdAt,String timeAgo, Author author) {
        this(id, content, createdAt != null ? createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")) : null, timeAgo, author);
    }
}