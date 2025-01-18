package friendy.community.domain.post.controller;

import friendy.community.domain.post.dto.request.PostCreateRequest;
import friendy.community.domain.post.dto.request.PostUpdateRequest;
import friendy.community.global.swagger.error.ApiErrorResponse;
import friendy.community.global.swagger.error.ErrorCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "게시글 API", description = "게시글 생성 API")
public interface SpringDocPostController {

    @Operation(summary = "게시글 생성", description = "새 게시글을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "게시글 생성 성공")
    @ApiErrorResponse(status = HttpStatus.BAD_REQUEST, instance = "/posts", errorCases = {
            @ErrorCase(description = "게시글 내용 없음", exampleMessage = "게시글 내용을 입력해주세요.")
    })
    ResponseEntity<Void> createPost(
            HttpServletRequest httpServletRequest,
            @RequestBody PostCreateRequest postRequest
    );

    @Operation(summary = "게시글 수정", description = "기존 게시글을 수정합니다.")
    @ApiResponse(responseCode = "201", description = "게시글 수정 성공")
    @ApiErrorResponse(status = HttpStatus.BAD_REQUEST, instance = "/posts/{postId}", errorCases = {
            @ErrorCase(description = "게시글 내용 없음", exampleMessage = "게시글 내용을 입력해주세요."),
            @ErrorCase(description = "존재하지 않는 게시글 ID", exampleMessage = "해당 게시글이 존재하지 않습니다.")
    })
    ResponseEntity<Void> updatePost(
            HttpServletRequest httpServletRequest,
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest postUpdateRequest
    );
}