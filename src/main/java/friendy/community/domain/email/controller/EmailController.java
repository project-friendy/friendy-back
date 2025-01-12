package friendy.community.domain.email.controller;

import friendy.community.domain.email.dto.request.EmailRequest;
import friendy.community.domain.email.dto.request.VerifyCodeRequest;
import friendy.community.domain.email.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController implements SpringDocEmailController{

    private final EmailService emailService;

    @PostMapping("/send-code")
    public ResponseEntity<Void> sendAuthenticatedEmail(@Valid @RequestBody EmailRequest request) {
        emailService.sendAuthenticatedEmail(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Void> verifyAuthCode(@Valid @RequestBody VerifyCodeRequest request) {
        emailService.verifyAuthCode(request);
        return ResponseEntity.ok().build();
    }

}
