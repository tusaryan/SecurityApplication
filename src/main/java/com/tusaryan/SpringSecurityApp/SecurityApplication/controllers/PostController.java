package com.tusaryan.SpringSecurityApp.SecurityApplication.controllers;

import com.tusaryan.SpringSecurityApp.SecurityApplication.dto.PostDTO;
import com.tusaryan.SpringSecurityApp.SecurityApplication.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Earlier, W6.6

@RestController
@RequestMapping(path = "/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public List<PostDTO> getAllPosts() {
        return postService.getAllPost();
    }

    @GetMapping("/{postId}")
    //to allow only owners of post to get post by its id.
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN') OR hasAuthority('POST_VIEW')")
    //bean is named by the class name with the first letter in lowercase so to use method we have to get the bean of that class first
    //to pass parameter "postId" of main method(eg: getPostById) to PreAuthorize method(eg: isOwnerOfPost) use "#" and same name as in main method
    @PreAuthorize("@postSecurity.isOwnerOfPost(#postId)")
    public PostDTO getPostById(@PathVariable Long postId) {
        return postService.getPostById(postId);
    }

    @PostMapping
    public PostDTO createNewPost(@RequestBody PostDTO inputPost) {
        return postService.createNewPost(inputPost);
    }
}
