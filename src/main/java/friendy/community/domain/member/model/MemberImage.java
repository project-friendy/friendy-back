package friendy.community.domain.member.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String storedFileName;

    @Column(nullable = false)
    private String fileType;

    public MemberImage(Member member, String imageUrl, String storedFileName, String fileType) {
        this.member = member;
        this.imageUrl = imageUrl;
        this.storedFileName = storedFileName;
        this.fileType = fileType;
    }

    public static MemberImage of(Member member, String imageUrl, String storedFileName, String fileType) {
        return new MemberImage(member, imageUrl, storedFileName, fileType);
    }

    public void updateImage(String imageUrl, String storedFileName, String fileType) {
        this.imageUrl = imageUrl;
        this.storedFileName = storedFileName;
        this.fileType = fileType;
    }



}
