package friendy.community.domain.post.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import friendy.community.domain.member.model.QMember;
import friendy.community.domain.post.model.Post;
import friendy.community.domain.post.model.QPost;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostQueryDSLRepository{

    private final JPAQueryFactory queryFactory;

    public Optional<Post> findPostById(final Long postId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(QPost.post)
                        .leftJoin(QPost.post.member, QMember.member).fetchJoin()
                        .where(QPost.post.id.eq(postId))
                        .fetchOne()
        );
    }

    public Page<Post> findAllPosts(Pageable pageable) {
        List<Post> posts = queryFactory.selectFrom(QPost.post)
                .leftJoin(QPost.post.member, QMember.member).fetchJoin()
                .orderBy(QPost.post.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.select(QPost.post.count())
                .from(QPost.post)
                .fetchOne();

        return new PageImpl<>(posts, pageable, total);
    }

}
