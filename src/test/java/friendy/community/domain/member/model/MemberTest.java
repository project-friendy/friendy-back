package friendy.community.domain.member.model;

import friendy.community.domain.member.dto.request.MemberSignUpRequest;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MemberTest {

    @Test
    void testMemberCreation() {

        MemberSignUpRequest request = new MemberSignUpRequest("test@example.com", "nickname", "password123", LocalDate.of(1990, 1, 1));

        Member member = Member.of(request);

        assertNotNull(member);
        assertEquals("test@example.com", member.getEmail().getEmail());
        assertEquals("nickname", member.getNickname().getNickname());
        assertEquals("password123", member.getPassword().getPassword());
        assertEquals(LocalDate.of(1990, 1, 1), member.getBirthDate());
    }
}
