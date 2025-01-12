package friendy.community.domain.email.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "이메일 인증코드 검증")
public record VerifyCodeRequest(

        @Schema(description = "이메일 주소", example = "example@friendy.com")
        @NotBlank(message = "이메일이 입력되지 않았습니다.")
        @Email(message = "유효한 이메일 주소를 입력해주세요.")
        String email,

        @Schema(description = "인증 코드", example = "123456")
        @NotBlank(message = "인증 코드가 입력되지 않았습니다.")
        String authCode

) {
}
