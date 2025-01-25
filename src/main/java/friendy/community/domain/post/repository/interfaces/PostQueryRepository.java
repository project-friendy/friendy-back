package friendy.community.domain.post.repository.interfaces;

import friendy.community.domain.post.dto.response.PostSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostQueryRepository {
    Page<PostSummaryResponse> findAllPostsWithMember(Pageable pageable);
}
