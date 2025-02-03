package friendy.community.domain.member.controller;

import friendy.community.domain.member.dto.request.PasswordRequest;
import friendy.community.domain.member.dto.request.MemberSignUpRequest;
import friendy.community.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class MemberController implements SpringDocMemberController{

    private final MemberService memberService;

    @PostMapping(value = "/signup", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> signUp(@RequestPart(value = "request") MemberSignUpRequest request,
                                       @RequestPart(value = "file") MultipartFile multipartFile) {
        return ResponseEntity.created(URI.create("/users/" + memberService.signUp(request,multipartFile))).build();
    }

    @PostMapping("/password")
    public ResponseEntity<Void> password(
            @Valid @RequestBody PasswordRequest passwordRequest
    ) {
        memberService.resetPassword(passwordRequest);

        return ResponseEntity.ok().build();
    }
}
