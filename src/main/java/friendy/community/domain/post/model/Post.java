package friendy.community.domain.post.model;

import friendy.community.domain.common.BaseEntity;
import friendy.community.domain.member.model.Member;
import friendy.community.domain.post.dto.request.PostCreateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String content;

    protected Post(final PostCreateRequest request, final Member member) {
        this.content = request.content();
        this.member = member;
    }

    public static Post of(final PostCreateRequest request, final Member member) {
        return new Post(request, member);
    }

}