package friendy.community.domain.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "게시판 생성")
public record PostCreateRequest(

        @Schema(description = "게시글 내용", example = "프렌디게시글내용")
        @NotBlank(message = "게시글 내용이 입력되지 않았습니다.")
        @Size(max = 2200, message = "게시글은 2200자 이내로 작성해주세요.")
        String content

) {
}