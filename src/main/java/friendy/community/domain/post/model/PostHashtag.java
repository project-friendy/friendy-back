package friendy.community.domain.post.model;

import friendy.community.domain.hashtag.model.Hashtag;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id", callSuper = false)
public class PostHashtag {

    @Embeddable
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @EqualsAndHashCode
    protected static class TemplateTagId implements Serializable {
        private Long postId;
        private Long hashtagId;
    }

    @EmbeddedId
    private TemplateTagId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("hashtagId")
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;

    public PostHashtag(Post post, Hashtag hashtag) {
        this.id = new TemplateTagId(post.getId(), hashtag.getId());
        this.post = post;
        this.hashtag = hashtag;
    }

}
