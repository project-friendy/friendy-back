package friendy.community.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(description = "액세스 토큰", example = "header.payload.signature")
        String accessToken,
        @Schema(description = "리프레쉬 토큰", example = "header.payload.signature")
        String refreshToken
) {
    public static LoginResponse of(final String accessToken, final String refreshToken) {
        return new LoginResponse(accessToken, refreshToken);
    }
}
