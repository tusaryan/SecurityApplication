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

//Earlier, L5.7, 6.6
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

        //get the current "user" from the security context holder which is inside the "Principal"
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        PostEntity postEntity = modelMapper.map(inputPost, PostEntity.class);

        //to assign the current user creating the post
        postEntity.setAuthor(user);

        //to save the post
        return modelMapper.map(postRepository.save(postEntity), PostDTO.class);
    }

    @Override
    public PostDTO getPostById(Long postId) {
        //these two lines are used to get the user details from the token/ user even if user is not present inside the context holder,
        // so this is giving me exception when I am exposing the get post by id api to permit all, so I have to comment these two line,
        //and therefore we're not able getting anything when we hit get request by post id.
//        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        log.info("user {}", user);


        PostEntity postEntity = postRepository
                .findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id : " + postId));
        return modelMapper.map(postEntity, PostDTO.class);
    }
}
