package friendy.community.domain.post.controller;

import friendy.community.domain.post.dto.request.PostCreateRequest;
import friendy.community.domain.post.dto.request.PostUpdateRequest;
import friendy.community.domain.post.dto.response.PostListResponse;
import friendy.community.domain.post.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import org.springframework.data.domain.Pageable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController implements SpringDocPostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Void> createPost(
            HttpServletRequest httpServletRequest,
            @Valid @RequestBody PostCreateRequest postCreateRequest
    ) {
        Long postId = postService.savePost(postCreateRequest, httpServletRequest);
        return ResponseEntity.created(URI.create("/posts/" + postId)).build();
    }

    @PostMapping("/{postId}")
    public ResponseEntity<Void> updatePost(
            HttpServletRequest httpServletRequest,
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest postUpdateRequest
    ) {
        Long returnPostId = postService.updatePost(postUpdateRequest, httpServletRequest, postId);
        return ResponseEntity.created(URI.create("/posts/" + returnPostId)).build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            HttpServletRequest httpServletRequest,
            @PathVariable Long postId
    ) {
        postService.deletePost(httpServletRequest, postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/list")
    public ResponseEntity<PostListResponse> getAllPosts(
            @RequestParam(defaultValue = "0") int page
    ) {
        Pageable pageable = PageRequest.of(page, 10);
        return ResponseEntity.ok(postService.getAllPosts(pageable));
    }

}