package friendy.community.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import friendy.community.domain.auth.service.AuthService;
import friendy.community.domain.member.dto.request.MemberSignUpRequest;
import friendy.community.domain.member.dto.request.PasswordRequest;
import friendy.community.domain.member.service.MemberService;
import friendy.community.global.exception.ErrorCode;
import friendy.community.global.exception.FriendyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

import static friendy.community.domain.member.fixture.MemberFixture.createImageFile;
import static friendy.community.domain.member.fixture.MemberFixture.createJsonRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private MemberService memberService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("회원가입 요청이 성공적으로 처리되면 201 Created와 함께 응답을 반환한다")
    void signUpSuccessfullyReturns201Created() throws Exception {
        // given
        MockMultipartFile requestPart = createJsonRequest("example@friendy.com", "bokSungKim", "password123!",  LocalDate.parse("2002-08-13"));
        MockMultipartFile imageFile = createImageFile("profile.jpg", "image/jpeg", new byte[0]);

        given(memberService.signUp(any(MemberSignUpRequest.class))).willReturn(1L);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/signup")
                .file(requestPart)
                .file(imageFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8"))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/users/1"));
    }

    @Test
    @DisplayName("이메일이 없으면 400 Bad Request를 반환한다")
    void signUpWithoutEmailReturns400BadRequest() throws Exception {
        // Given
        MockMultipartFile requestPart = createJsonRequest("", "bokSungKim", "password123!",  LocalDate.parse("2002-08-13"));
        MockMultipartFile imageFile = createImageFile("profile.jpg", "image/jpeg", new byte[0]);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/signup")
                .file(requestPart)
                .file(imageFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8"))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이메일 형식이 올바르지 않으면 400 Bad Request를 반환한다")
    void signUpWithInvalidEmailReturns400BadRequest() throws Exception {
        // Given
        MockMultipartFile requestPart = createJsonRequest("invalid-email", "bokSungKim", "password123!",  LocalDate.parse("2002-08-13"));
        MockMultipartFile imageFile = createImageFile("profile.jpg", "image/jpeg", new byte[0]);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/signup")
                .file(requestPart)
                .file(imageFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8"))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이메일이 중복되면 409 Conflict를 반환한다")
    void signUpWithDuplicateEmailReturns409Conflict() throws Exception {
        // Given
        MockMultipartFile requestPart = createJsonRequest("example@friendy.com", "bokSungKim", "password123!",  LocalDate.parse("2002-08-13"));
        MockMultipartFile imageFile = createImageFile("profile.jpg", "image/jpeg", new byte[0]);

        when(memberService.signUp(any(MemberSignUpRequest.class)))
            .thenThrow(new FriendyException(ErrorCode.DUPLICATE_EMAIL, "이미 가입된 이메일입니다."));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/signup")
                .file(requestPart)
                .file(imageFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8"))
            .andDo(print())
            .andExpect(status().isConflict())
            .andExpect(result ->
                assertThat(result.getResolvedException().getMessage())
                    .contains("이미 가입된 이메일입니다."));

    }

    @Test
    @DisplayName("닉네임이 없으면 400 Bad Request를 반환한다")
    void signUpWithoutNicknameReturns400BadRequest() throws Exception {
        // Given
        MockMultipartFile requestPart = createJsonRequest("example@friendy.com", "", "password123!",  LocalDate.parse("2002-08-13"));
        MockMultipartFile imageFile = createImageFile("profile.jpg", "image/jpeg", new byte[0]);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/signup")
                .file(requestPart)
                .file(imageFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8"))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @DisplayName("닉네임이 길이 제한을 벗어나면 400 Bad Request를 반환한다")
    @CsvSource({
        "example@friendy.com, a, password123!, 닉네임은 2~20자 사이로 입력해주세요.",
        "example@friendy.com, thisisaveryverylongnickname, password123!, 닉네임은 2~20자 사이로 입력해주세요."
    })
    void signUpWithInvalidNicknameLengthReturns400BadRequest(
        String email, String nickname, String password, String expectedMessage) throws Exception {
        // Given
        MockMultipartFile requestPart = createJsonRequest("example@friendy.com", nickname, "password123!",  LocalDate.parse("2002-08-13"));
        MockMultipartFile imageFile = createImageFile("profile.jpg", "image/jpeg", new byte[0]);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/signup")
                .file(requestPart)
                .file(imageFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8"))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(result ->
                assertThat(result.getResolvedException().getMessage()).contains(expectedMessage));
    }

    @Test
    @DisplayName("닉네임이 중복되면 409 Conflict를 반환한다")
    void signUpWithDuplicateNicknameReturns409Conflict() throws Exception {
        // Given
        MockMultipartFile requestPart = createJsonRequest("example@friendy.com", "duplicateNickname", "password123!",  LocalDate.parse("2002-08-13"));
        MockMultipartFile imageFile = createImageFile("profile.jpg", "image/jpeg", new byte[0]);

        when(memberService.signUp(any(MemberSignUpRequest.class)))
            .thenThrow(new FriendyException(ErrorCode.DUPLICATE_NICKNAME, "닉네임이 이미 존재합니다."));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/signup")
                .file(requestPart)
                .file(imageFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8"))
            .andDo(print())
            .andExpect(status().isConflict())
            .andExpect(result ->
                assertThat(result.getResolvedException().getMessage())
                    .contains("닉네임이 이미 존재합니다."));
    }

    @Test
    @DisplayName("비밀번호가 없으면 400 Bad Request를 반환한다")
    void signUpWithoutPasswordReturns400BadRequest() throws Exception {
        // Given
        MockMultipartFile requestPart = createJsonRequest("example@friendy.com", "Nickname", "",  LocalDate.parse("2002-08-13"));
        MockMultipartFile imageFile = createImageFile("profile.jpg", "image/jpeg", new byte[0]);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/signup")
                .file(requestPart)
                .file(imageFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8"))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @DisplayName("비밀번호가 숫자, 영문자, 특수문자를 포함하지 않으면 400 Bad Request를 반환한다")
    @CsvSource({
        "example@friendy.com, validNickname, simplepassword, 숫자, 영문자, 특수문자(~!@#$%^&*?)를 포함해야 합니다.",
        "example@friendy.com, validNickname, password123, 숫자, 영문자, 특수문자(~!@#$%^&*?)를 포함해야 합니다.",
        "example@friendy.com, validNickname, 12345678, 숫자, 영문자, 특수문자(~!@#$%^&*?)를 포함해야 합니다."
    })
    void signUpWithInvalidPasswordPatternReturns400BadRequest(
        String email, String nickname, String password, String expectedMessage) throws Exception {
        // Given
        MockMultipartFile requestPart = createJsonRequest(email, nickname, password,  LocalDate.parse("2002-08-13"));
        MockMultipartFile imageFile = createImageFile("profile.jpg", "image/jpeg", new byte[0]);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/signup")
                .file(requestPart)
                .file(imageFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8"))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(result ->
                assertThat(result.getResolvedException().getMessage()).contains(expectedMessage));
    }

    @ParameterizedTest
    @DisplayName("비밀번호가 길이 제한을 벗어나면 400 Bad Request를 반환한다")
    @CsvSource({
        "example@friendy.com, bokSungKim, short, 비밀번호는 8~16자 사이로 입력해주세요.",
        "example@friendy.com, bokSungKim, thispasswordiswaytoolong123!, 비밀번호는 8~16자 사이로 입력해주세요."
    })
    void signUpWithInvalidPasswordLengthReturns400BadRequest(
        String email, String nickname, String password, String expectedMessage) throws Exception {
        // Given
        MockMultipartFile requestPart = createJsonRequest(email, nickname, password,  LocalDate.parse("2002-08-13"));
        MockMultipartFile imageFile = createImageFile("profile.jpg", "image/jpeg", new byte[0]);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/signup")
                .file(requestPart)
                .file(imageFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8"))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(result ->
                assertThat(result.getResolvedException().getMessage()).contains(expectedMessage));
    }

    @Test
    @DisplayName("생년월일이 없으면 400 Bad Request를 반환한다")
    void signUpWithoutBirthDateReturns400BadRequest() throws Exception {
        // Given
        MockMultipartFile requestPart = createJsonRequest("example@friendy.com", "Nickname", "password123!", null);
        MockMultipartFile imageFile = createImageFile("profile.jpg", "image/jpeg", new byte[0]);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/signup")
                .file(requestPart)
                .file(imageFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8"))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("비밀번호 변경이 완료되면 200 OK가 반환된다")
    void resetPasswordSuccessfullyReturns200() throws Exception {
        // Given
        PasswordRequest passwordRequest = new PasswordRequest("example@friendy.com", "newPassword123!");

        // When & Then
        mockMvc.perform(post("/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(passwordRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("요청 이메일이 존재하지 않으면 401 UNAUTHORIZED를 반환한다")
    void emailDosentExistReturns401() throws Exception {
        // Given
        PasswordRequest passwordRequest = new PasswordRequest("wrongEmail@friendy.com", "newPassword123!");

        doThrow(new FriendyException(ErrorCode.UNAUTHORIZED_EMAIL, "해당 이메일의 회원이 존재하지 않습니다."))
                .when(memberService)
                .resetPassword(any(PasswordRequest.class));

        // When & Then
        mockMvc.perform(post("/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(passwordRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(result ->
                        assertThat(result.getResolvedException().getMessage())
                                .contains("해당 이메일의 회원이 존재하지 않습니다."));
    }
}