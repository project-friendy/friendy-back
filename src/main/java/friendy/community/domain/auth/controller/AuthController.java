package friendy.community.domain.auth.controller;

import friendy.community.domain.auth.dto.request.LoginRequest;
import friendy.community.domain.auth.dto.response.LoginResponse;
import friendy.community.domain.auth.service.AuthService;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements SpringDocAuthController{

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        final LoginResponse response = authService.login(loginRequest);

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + response.accessToken())
                .header("Authorization-Refresh", "Bearer " + response.refreshToken())
                .build();
    }
}
