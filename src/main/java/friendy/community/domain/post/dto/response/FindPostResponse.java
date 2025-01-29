package friendy.community.domain.post.dto.response;

import friendy.community.domain.post.model.Post;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record FindPostResponse(
        Long id,
        String content,
        String createdAt,
        int likeCount,
        int commentCount,
        int shareCount,
        FindMemberResponse authorResponse
) {
    public static FindPostResponse from(Post post) {
        return new FindPostResponse(
                post.getId(),
                post.getContent(),
                formatDateTime(post.getCreatedDate()),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getShareCount(),
                FindMemberResponse.from(post.getMember())
        );
    }

    private static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null
                ? dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
                : null;
    }
}
