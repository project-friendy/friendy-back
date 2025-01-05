package friendy.community.domain.auth.controller;

import friendy.community.domain.auth.dto.request.LoginRequest;
import friendy.community.domain.auth.dto.response.TokenResponse;
import friendy.community.domain.auth.service.AuthService;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements SpringDocAuthController{

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        final TokenResponse response = authService.login(loginRequest);

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + response.accessToken())
                .header("Authorization-Refresh", "Bearer " + response.refreshToken())
                .build();
    }

    @PostMapping("/token/reissue")
    public ResponseEntity<Void> reissueToken(
            @RequestHeader("Authorization-Refresh") String refreshToken
    ) {
        final TokenResponse response = authService.reissueToken(refreshToken.replace("Bearer ", ""));

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + response.accessToken())
                .header("Authorization-Refresh", "Bearer " + response.refreshToken())
                .build();
    }
}
