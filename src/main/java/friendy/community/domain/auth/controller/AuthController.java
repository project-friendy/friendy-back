package friendy.community.domain.auth.controller;

import friendy.community.domain.auth.dto.request.LoginRequest;
import friendy.community.domain.auth.dto.response.TokenResponse;
import friendy.community.domain.auth.jwt.JwtTokenExtractor;
import friendy.community.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController implements SpringDocAuthController{

    private final AuthService authService;
    private final JwtTokenExtractor jwtTokenExtractor;

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

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        HttpServletRequest httpServletRequest
    ) {
        final String refreshToken = jwtTokenExtractor.extractRefreshToken(httpServletRequest);
        authService.logout(refreshToken);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/token/reissue")
    public ResponseEntity<Void> reissueToken(
            HttpServletRequest httpServletRequest
    ) {
        final String refreshToken = jwtTokenExtractor.extractRefreshToken(httpServletRequest);
        final TokenResponse response = authService.reissueToken(refreshToken);

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + response.accessToken())
                .header("Authorization-Refresh", "Bearer " + response.refreshToken())
                .build();
    }
}
