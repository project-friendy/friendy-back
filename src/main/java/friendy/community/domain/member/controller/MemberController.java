package friendy.community.domain.member.controller;

import friendy.community.domain.member.dto.request.MemberSignUpRequest;
import friendy.community.domain.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody MemberSignUpRequest memberSignUpRequest) {

        if(memberService.checkLoginEmailDuplicate(memberSignUpRequest.email())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 존재하는 이메일입니다.");
        }

        memberService.SignUp(memberSignUpRequest);

        return ResponseEntity.ok("회원가입 성공");

    }
}
