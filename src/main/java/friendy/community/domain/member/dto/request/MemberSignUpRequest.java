package friendy.community.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "사용자 회원가입")
public record MemberSignUpRequest(

        @Schema(description = "사용자 이메일", example = "example@friendy.com")
        @NotBlank(message = "사용자 이메일이 필요합니다")
        String email,

        @Schema(description = "사용자 닉네임", example = "bokSungKim")
        @NotBlank(message = "사용자 닉네임이 필요합니다")
        String nickname,

        @Schema(description = "사용자 비밀번호", example = "passwordExample")
        @NotBlank(message = "사용자 비밀번호가 필요합니다")
        String password,

        @Schema(description = "사용자 생일", example = "2002-08-13")
        @NotNull(message = "사용자 생일이 필요합니다")
        LocalDate birth

    ) {
}