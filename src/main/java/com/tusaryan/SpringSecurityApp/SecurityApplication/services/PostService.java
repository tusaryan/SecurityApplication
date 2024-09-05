package com.tusaryan.SpringSecurityApp.SecurityApplication.services;

import com.tusaryan.SpringSecurityApp.SecurityApplication.dto.PostDTO;

import java.util.List;

public interface PostService {

    List<PostDTO> getAllPost();

    PostDTO createNewPost(PostDTO inputPost);

    PostDTO getPostById(Long postId);

}
