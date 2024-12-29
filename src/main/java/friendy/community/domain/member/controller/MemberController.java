package friendy.community.domain.member.controller;

import friendy.community.domain.member.dto.request.MemberSignUpRequest;
import friendy.community.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class MemberController implements SpringDocMemberController{

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@Valid @RequestBody MemberSignUpRequest request) {
        return ResponseEntity.created(URI.create("/users/" + memberService.signUp(request))).build();
    }
}
