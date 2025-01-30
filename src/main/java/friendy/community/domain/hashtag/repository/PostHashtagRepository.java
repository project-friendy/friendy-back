package friendy.community.domain.hashtag.repository;

import friendy.community.domain.post.model.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {
}
