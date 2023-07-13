package com.likelion.swu.Post;

import com.likelion.swu.User.Account;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.persistence.EntityNotFoundException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostRepository postRepository;

    //    //게시글 생성
//    @PostMapping(value = "/posts")
//    public ResponseEntity<PostReturnDto> createPost(@RequestBody @Valid PostFromDto postFromDto,
//                                                    @RequestBody Long id,
//                                                    @RequestParam("building") Building building) {
//        try {
//            Post post = postService.createPost(postFromDto,building,id);
//            // 게시글 작성 성공 시, 201 Created 상태코드와 생성된 게시글 정보 반환
//            PostReturnDto postReturnDto = convertToDto(post); //생성한 객체 정보 다시 returndto로 넘김, 조회를 위해
//            return ResponseEntity.status(HttpStatus.CREATED).body(postReturnDto);
//        } catch (Exception e) {
//            // 게시글 작성 실패 시, 500 Internal Server Error 반환
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }
    @PostMapping(value = "/posts/{building}")
    public ResponseEntity<PostReturnDto> createPost(@RequestBody @Valid PostFromDto postFromDto,
                                                    @PathVariable("building") Building building, Principal principal) {


        try {
            System.out.println("user: " + principal.getName());
            Post post = postService.createPost(postFromDto, building, principal.getName());
            // 게시글 작성 성공 시, 201 Created 상태코드와 생성된 게시글 정보 반환
            PostReturnDto postReturnDto = convertToDto(post); //생성한 객체 정보 다시 returndto로 넘김, 조회를 위해
            return ResponseEntity.status(HttpStatus.CREATED).body(postReturnDto);
        } catch (Exception e) {
            // 게시글 작성 실패 시, 500 Internal Server Error 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 게시글 수정
    @PutMapping(value = "/posts/edit/{postId}")
    public ResponseEntity<PostReturnDto> updatePost(@PathVariable("postId") Long postId,
                                                    @RequestBody @Valid PostFromDto postFromDto,
                                                    Principal principal) {
        Post post = postRepository.findById(postId).orElseThrow(EntityNotFoundException::new);

        String currentUserId = getCurrentUserId(principal); // 현재 로그인한 사용자의 식별자(ID)를 가져옴

        // 게시글 작성자 식별자(ID)와 현재 로그인한 사용자의 식별자(ID)가 다른 경우
        if (!currentUserId.equals(post.getCreatedByUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // 403 Forbidden 상태코드 반환
        }

        postService.updatePost(postFromDto);

        Post updatedPost = postRepository.save(post);
        PostReturnDto postReturnDto = convertToDto(updatedPost);
        return ResponseEntity.ok(postReturnDto);
    }

    // 게시글 삭제
    @DeleteMapping("/posts/edit/{postId}")
    public ResponseEntity<HttpStatus> deletePost(@PathVariable("postId") Long postId,
                                                 Principal principal) {
        Post post = postRepository.findById(postId).orElseThrow(EntityNotFoundException::new);

        String currentUserId = getCurrentUserId(principal); // 현재 로그인한 사용자의 식별자(ID)를 가져옴

        // 게시글 작성자 식별자(ID)와 현재 로그인한 사용자의 식별자(ID)가 다른 경우
        if (!currentUserId.equals(post.getCreatedByUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden 상태코드 반환
        }

        PostFromDto postFromDto = new PostFromDto();
        postFromDto.setId(postId);

        postService.deletePost(postFromDto);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 현재 로그인한 사용자의 식별자(ID)를 가져오는 도우미 메소드
    private String getCurrentUserId(Principal principal) {
        // Principal에서 사용자의 식별자(ID)를 추출하는 방법에 따라 구현
        // 예시: Principal.getName()을 사용하여 사용자의 이름을 반환하도록 가정
        return principal.getName();
    }


    //게시글 목록 조회
    @GetMapping("/posts")
    public ResponseEntity<List<PostListDto>> getAllPosts(@RequestParam(value = "building", required = false) Building building) {
//        System.out.println(principal.getName());
        List<PostListDto> filteredPosts = postService.getAllPosts(building);
        return ResponseEntity.ok(filteredPosts);
    }

    //게시글 상세 조회
    @GetMapping(value = "posts/{postId}")
    public ResponseEntity<PostReturnDto> getPostDtl(@PathVariable("postId") Long postId) {
        PostReturnDto postReturnDto = postService.getPostDetails(postId);
        return ResponseEntity.status(HttpStatus.OK).body(postReturnDto);
    }

    private PostReturnDto convertToDto(Post post) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(post, PostReturnDto.class);
    }

    //관리자일 때 게시글 전체 조회
    @GetMapping("/posts/admin")
    public ResponseEntity<List<PostListDto>> getAdminAllPosts(@RequestParam(value = "building", required = false) Building building) {
//        System.out.println(principal.getName());
        List<PostListDto> filteredPosts = postService.getAllPosts(building);
        return ResponseEntity.ok(filteredPosts);
    }


    //관리자일 때 게시글 상태변경 시킴
    @PutMapping("/posts/admin/{postId}/status")
    public String updatePostStatus(@PathVariable("postId") Long postId,
                                   @RequestBody @Valid PostFromDto postFromDto) {
        postService.updatePostStatus(postId, postFromDto.getRequest());
        return "redirect:/posts/admin";
    }


}