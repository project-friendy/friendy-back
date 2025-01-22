package friendy.community.domain.post.repository;

import friendy.community.domain.post.dto.response.PostSummary;
import friendy.community.domain.post.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("""
    SELECT new friendy.community.domain.post.dto.response.PostSummary(
        p.id,
        p.content,
        p.createdDate,
        null,
        new friendy.community.domain.post.dto.response.Author(m.id, m.nickname)
    )
    FROM Post p
    JOIN p.member m
    ORDER BY p.createdDate DESC
    """)
    Page<PostSummary> findAllPostsWithMember(Pageable pageable);
}