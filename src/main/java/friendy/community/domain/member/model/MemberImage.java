package friendy.community.domain.member.model;

import friendy.community.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String storedFileName;

    @Column(nullable = false)
    private String fileType;

    public MemberImage(String imageUrl, String storedFileName, String fileType) {
        this.imageUrl = imageUrl;
        this.storedFileName = storedFileName;
        this.fileType = fileType;
    }

    public static MemberImage of(String imageUrl, String storedFileName, String fileType) {
        return new MemberImage(imageUrl, storedFileName, fileType);
    }

    public void updateImage(String imageUrl, String storedFileName, String fileType) {
        this.imageUrl = imageUrl;
        this.storedFileName = storedFileName;
        this.fileType = fileType;
    }



}
