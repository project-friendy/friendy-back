package friendy.community.domain.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import friendy.community.domain.post.dto.request.PostCreateRequest;
import friendy.community.domain.post.dto.request.PostUpdateRequest;
import friendy.community.domain.post.dto.response.FindMemberResponse;
import friendy.community.domain.post.dto.response.FindAllPostResponse;
import friendy.community.domain.post.dto.response.FindPostResponse;
import friendy.community.domain.post.service.PostService;
import friendy.community.global.exception.ErrorCode;
import friendy.community.global.exception.FriendyException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostService postService;


    private String generateLongContent(int length) {
        return "a".repeat(length);
    }

    @Test
    @DisplayName("포스트 생성 요청이 성공적으로 처리되면 201 Created와 함께 응답을 반환한다")
    public void createPostSuccessfullyReturns201Created() throws Exception {
        //Given
        PostCreateRequest postCreateRequest = new PostCreateRequest("this is new content");

        //When
        when(postService.savePost(any(PostCreateRequest.class), any(HttpServletRequest.class))).thenReturn(1L);

        //Then
        mockMvc.perform(post("/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(postCreateRequest)))
            .andDo(print()).andExpect(status().isCreated())
            .andExpect(header().string("Location", "/posts/1"));

    }

    @Test
    @DisplayName("포스트 생성에 content가 없으면 400 Bad Request를 반환한다")
    void createPostWithoutContentReturns400BadRequest() throws Exception {
        //Given
        PostCreateRequest postCreateRequest = new PostCreateRequest(null);

        //When
        when(postService.savePost(any(PostCreateRequest.class), any(HttpServletRequest.class))).thenReturn(1L);

        //Then
        mockMvc.perform(post("/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(postCreateRequest)))
            .andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("content가 2200자가 넘어가면 400 Bad Request를 반환한다")
    void createPostWithContentExceedingMaxLengthReturns400BadRequest() throws Exception {
        //Given
        PostCreateRequest postCreateRequest = new PostCreateRequest(generateLongContent(2300));

        //When
        when(postService.savePost(any(PostCreateRequest.class), any(HttpServletRequest.class))).thenReturn(1L);

        //Then
        mockMvc.perform(post("/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(postCreateRequest)))
            .andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("포스트 수정 요청이 성공적으로 처리되면 201 Created와 함께 응답을 반환한다")
    public void updatePostSuccessfullyReturns201Created() throws Exception {
        //Given
        Long postId = 1L;
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest("this is updated content");

        //When
        when(postService.updatePost(any(PostUpdateRequest.class), any(HttpServletRequest.class), anyLong())).thenReturn(1L);


        //Then
        mockMvc.perform(post("/posts/{postId}", postId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(postUpdateRequest)))
            .andDo(print()).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("포스트 수정에 content가 2200자가 넘어가면 400 Bad Request를 반환한다")
    void updatePostWithContentExceedingMaxLengthReturns400BadRequest() throws Exception {
        //Given
        Long postId = 1L;
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest(generateLongContent(2300));

        //When
        when(postService.updatePost(any(PostUpdateRequest.class), any(HttpServletRequest.class), anyLong())).thenReturn(1L);

        //Then
        mockMvc.perform(post("/posts/{postId}", postId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(postUpdateRequest)))
            .andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("포스트 삭제 요청이 성공적으로 처리되면 200 OK와 함께 응답을 반환한다")
    public void deletePostSuccessfullyReturns200Ok() throws Exception {
        //Given
        Long postId = 1L;

        //When
        doNothing().when(postService).deletePost(any(HttpServletRequest.class), eq(postId)); // 서비스 메서드가 아무 동작도 하지 않도록 설정

        //Then
        mockMvc.perform(delete("/posts/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시글 조회 성공 시 200 OK와 게시글 정보를 반환한다")
    void getPostSuccessfullyReturns200Ok() throws Exception {
        // Given
        Long postId = 1L;
        FindPostResponse response = new FindPostResponse(1L, "Post 1", "2025-01-23T10:00:00", 10, 5, 2, new FindMemberResponse(1L, "author1"));

        when(postService.getPost(anyLong())).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("존재하지 않는 게시글을 조회하면 404 Not Found를 반환한다")
    void getPostWithNonExistentIdReturns404NotFound() throws Exception {
        // Given
        Long nonExistentPostId = 999L;

        when(postService.getPost(anyLong()))
                .thenThrow(new FriendyException(ErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 게시글입니다."));

        // When & Then
        mockMvc.perform(get("/posts/{postId}", nonExistentPostId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("존재하지 않는 게시글입니다."));
    }

    @Test
    @DisplayName("게시글 목록 조회 성공 시 200 OK 반환")
    public void getPostsListSuccessfullyReturns200Ok() throws Exception {
        // Given
        List<FindPostResponse> posts = List.of(
                new FindPostResponse(1L, "Post 1", "2025-01-23T10:00:00", 10, 5, 2, new FindMemberResponse(1L, "author1")),
                new FindPostResponse(2L, "Post 2", "2025-01-23T11:00:00", 20, 10, 3, new FindMemberResponse(2L, "author2"))
        );
        when(postService.getAllPosts(any(Pageable.class)))
                .thenReturn(new FindAllPostResponse(posts, 1));

        // Then
        mockMvc.perform(get("/posts/list").param("page", "0"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("없는 페이지 요청 시 404 반환")
    public void getPostsListWithNonExistentPageReturns404NotFound() throws Exception {
        // Given
        when(postService.getAllPosts(any(Pageable.class)))
                .thenThrow(new FriendyException(ErrorCode.RESOURCE_NOT_FOUND, "요청한 페이지가 존재하지 않습니다."));

        // Then
        mockMvc.perform(get("/posts/list").param("page", "100"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("요청한 페이지가 존재하지 않습니다."));
    }

}