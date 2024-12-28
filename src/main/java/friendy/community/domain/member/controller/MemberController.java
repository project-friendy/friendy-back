package friendy.community.domain.member.controller;

import friendy.community.domain.member.dto.request.MemberSignUpRequest;
import friendy.community.domain.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@Valid @RequestBody MemberSignUpRequest request) {
        return ResponseEntity.created(URI.create("/members/" + memberService.signUp(request))).build();
    }
}
