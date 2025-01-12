package friendy.community.domain.email.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "이메일 인증코드 전송")
public record EmailRequest(

        @Schema(description = "이메일", example = "example@friendy.com")
        @NotBlank(message = "이메일이 입력되지 않았습니다.")
        @Email(message = "이메일 형식으로 입력해주세요.")
        String email

) {
}
