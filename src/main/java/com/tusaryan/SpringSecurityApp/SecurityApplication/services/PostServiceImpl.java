package com.tusaryan.SpringSecurityApp.SecurityApplication.services;

import com.tusaryan.SpringSecurityApp.SecurityApplication.dto.PostDTO;
import com.tusaryan.SpringSecurityApp.SecurityApplication.entities.PostEntity;
import com.tusaryan.SpringSecurityApp.SecurityApplication.entities.User;
import com.tusaryan.SpringSecurityApp.SecurityApplication.exceptions.ResourceNotFoundException;
import com.tusaryan.SpringSecurityApp.SecurityApplication.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

//Earlier, L5.7
@Slf4j
@Service @RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<PostDTO> getAllPost() {
        return postRepository
                .findAll()
                .stream()
                .map(postEntity -> modelMapper.map(postEntity, PostDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public PostDTO createNewPost(PostDTO inputPost) {
        PostEntity postEntity = modelMapper.map(inputPost, PostEntity.class);
        return modelMapper.map(postRepository.save(postEntity), PostDTO.class);
    }

    @Override
    public PostDTO getPostById(Long postId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("user {}", user);
        PostEntity postEntity = postRepository
                .findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id : " + postId));
        return modelMapper.map(postEntity, PostDTO.class);
    }
}
