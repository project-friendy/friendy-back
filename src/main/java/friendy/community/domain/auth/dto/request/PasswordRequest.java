package friendy.community.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "비밀번호 변경")
public record PasswordRequest(
        @Schema(description = "이메일", example = "example@friendy.com")
        @NotBlank(message = "이메일이 입력되지 않았습니다.")
        @Email(message = "이메일 형식으로 입력해주세요.")
        String email,

        @Schema(description = "비밀번호. 숫자, 영문자, 특수문자(~!@#$%^&*?)를 반드시 포함해야 합니다.", example = "password123!")
        @NotBlank(message = "비밀번호가 입력되지 않았습니다.")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*?]).*$", message = "숫자, 영문자, 특수문자(~!@#$%^&*?)를 포함해야 합니다.")
        @Size(min = 8, max = 16, message = "비밀번호는 8~16자 사이로 입력해주세요.")
        String newPassword

) {
}
