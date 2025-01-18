package friendy.community.domain.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import friendy.community.domain.post.dto.request.PostCreateRequest;
import friendy.community.domain.post.dto.request.PostUpdateRequest;
import friendy.community.domain.post.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        mockMvc.perform(post("/posts").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(postCreateRequest))).andDo(print()).andExpect(status().isCreated()).andExpect(header().string("Location", "/posts/1"));

    }

    @Test
    @DisplayName("포스트 생성에 content가 없으면 400 Bad Request를 반환한다")
    void createPostWithoutContentReturns400BadRequest() throws Exception {
        //Given
        PostCreateRequest postCreateRequest = new PostCreateRequest(null);

        //When
        when(postService.savePost(any(PostCreateRequest.class), any(HttpServletRequest.class))).thenReturn(1L);

        //Then
        mockMvc.perform(post("/posts").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(postCreateRequest))).andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("content가 2200자가 넘어가면 400 Bad Request를 반환한다")
    void createPostWithContentExceedingMaxLengthReturns400BadRequest() throws Exception {
        //Given
        PostCreateRequest postCreateRequest = new PostCreateRequest(generateLongContent(2300));

        //When
        when(postService.savePost(any(PostCreateRequest.class), any(HttpServletRequest.class))).thenReturn(1L);

        //Then
        mockMvc.perform(post("/posts").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(postCreateRequest))).andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("포스트 수정 요청이 성공적으로 처리되면 200 OK와 함께 응답을 반환한다")
    public void updatePostSuccessfullyReturns201Created() throws Exception {
        //Given
        Long postId = 1L;
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest("this is updated content");

        //When
        when(postService.updatePost(any(PostUpdateRequest.class), any(HttpServletRequest.class), anyLong())).thenReturn(1L);


        //Then
        mockMvc.perform(post("/posts/{postId}", postId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(postUpdateRequest))).andDo(print()).andExpect(status().isCreated());
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
        mockMvc.perform(post("/posts/{postId}", postId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(postUpdateRequest))).andDo(print()).andExpect(status().isBadRequest());
    }


}