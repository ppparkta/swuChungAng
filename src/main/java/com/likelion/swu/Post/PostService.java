package com.likelion.swu.Post;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final ModelMapper mapper;
    private final PostRepository postRepository;

    //게시글 생성
//    public Post createPost(PostFromDto postFromDto,Building building, Long id) throws Exception {
//        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);;
//
//        Post post = new Post();
//        post.setTitle(postFromDto.getTitle());
//        post.setBody(postFromDto.getBody());
//        post.setDate(LocalDateTime.now());
//        post.setRequest(postFromDto.getRequest());
//        post.setBuilding(building);
//        post.setUser(user);
//
//        return postRepository.save(post);
//    }
    public Post createPost(PostFromDto postFromDto,Building building) throws Exception {
        //User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);;

        Post post = new Post();
        post.setTitle(postFromDto.getTitle());
        post.setBody(postFromDto.getBody());
        post.setDate(LocalDateTime.now());
        //post.setRequest(postFromDto.getRequest());
        post.setBuilding(building);
        //post.setUser(user);

        return postRepository.save(post);
    }

    //게시글 수정
    public Long updatePost(PostFromDto postFromDto) {
        Post post = postRepository.findById(postFromDto.getId()).orElseThrow(EntityNotFoundException::new);
        post.updatePost(postFromDto);

        return post.getId(); //수정한 뒤 id 반환
    }

    //게시글 삭제
    public void deletePost(PostFromDto postFromDto) {
        Post post = postRepository.findById(postFromDto.getId()).orElseThrow(EntityNotFoundException::new);
        postRepository.deleteById(post.getId());
    }

    //게시글 전체 조회 (해당 건물)
    @Transactional(readOnly = true)
    public List<PostListDto> getAllPosts(Building building) {
        List<Post> posts;
        posts = postRepository.findByBuilding(building);

        return posts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    //게시글 상세 조회
    @Transactional(readOnly = true)
    public PostReturnDto getPostDetails(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(EntityNotFoundException::new);
        return convertToDtoDtl(post);
    }


    private PostReturnDto convertToDtoDtl(Post post) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(post, PostReturnDto.class);
    }

    private PostListDto convertToDto(Post post) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(post, PostListDto.class);
    }

    // 게시글 상태 변경 메소드

    public void updatePostStatus(Long postId, RequestStatus status) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        post.setRequest(status);

        postRepository.save(post);
    }
}

