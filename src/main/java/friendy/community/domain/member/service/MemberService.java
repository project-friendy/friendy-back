package friendy.community.domain.member.service;

import friendy.community.domain.member.dto.request.MemberSignUpRequest;
import friendy.community.domain.member.model.Member;
import friendy.community.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public boolean checkLoginEmailDuplicate(String email) {
        return memberRepository.existsByEmail(email);
    }

    public boolean checkNicknameDuplicate(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    public void SignUp(MemberSignUpRequest memberSignUpRequest) {

//        if (checkLoginEmailDuplicate(memberSignUpRequest.email())) {
//            //예외추가
//        }
//
//        if (checkNicknameDuplicate(memberSignUpRequest.nickname())) {
//            //예외추가
//        }

        Member member = Member.of(memberSignUpRequest);

        memberRepository.save(member);


    }
}
