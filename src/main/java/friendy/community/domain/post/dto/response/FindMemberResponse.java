package friendy.community.domain.post.dto.response;

import friendy.community.domain.member.model.Member;

public record FindMemberResponse(
        Long id,
        String nickname
) {
    public static FindMemberResponse from(Member member) {
        return new FindMemberResponse(member.getId(), member.getNickname());
    }
}