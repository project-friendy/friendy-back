package friendy.community.domain.post.model;

import friendy.community.domain.common.BaseEntity;
import friendy.community.domain.member.model.Member;
import friendy.community.domain.post.dto.request.PostCreateRequest;
import friendy.community.domain.post.repository.PostRepository;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDate createdAt;

    @CreatedDate
    private LocalDate createdDate;

    @LastModifiedDate
    private LocalDate modifiedDate;

    public Post(final Long memberId , final String title, final String content, final LocalDate createdAt) {
        this(null, memberId, title, content, createdAt);
    }

    public static Post of(final PostCreateRequest request,final Long memberId) {
        return new Post(memberId, request.title(), request.content(), request.createdAt());
    }



}
