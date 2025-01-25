package friendy.community.domain.post.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import friendy.community.domain.member.model.QMember;
import friendy.community.domain.post.dto.response.AuthorResponse;
import friendy.community.domain.post.dto.response.PostSummaryResponse;
import friendy.community.domain.post.model.QPost;
import friendy.community.domain.post.repository.interfaces.PostQueryRepository;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostQueryRepositoryImpl implements PostQueryRepository {

    private final EntityManager entityManager;

    public PostQueryRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Page<PostSummaryResponse> findAllPostsWithMember(Pageable pageable) {
        QPost post = QPost.post;
        QMember member = QMember.member;

        JPAQuery<PostSummaryResponse> query = new JPAQuery<>((jakarta.persistence.EntityManager) entityManager);

        query.select(Projections.constructor(
                        PostSummaryResponse.class,
                        post.id,
                        post.content,
                        post.createdDate,
                        post.likeCount,
                        post.commentCount,
                        post.shareCount,
                        Projections.constructor(AuthorResponse.class, member.id, member.nickname)
                ))
                .from(post)
                .join(post.member, member)
                .orderBy(post.createdDate.desc());

        long offset = pageable.getOffset();
        long limit = pageable.getPageSize();

        List<PostSummaryResponse> results = query
                .offset(offset)
                .limit(limit)
                .fetch();

        long totalCount = query.fetch().size();

        return new PageImpl<>(results, pageable, totalCount);
    }
}
