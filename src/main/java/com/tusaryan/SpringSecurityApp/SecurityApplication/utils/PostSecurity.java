package com.tusaryan.SpringSecurityApp.SecurityApplication.utils;


import com.tusaryan.SpringSecurityApp.SecurityApplication.dto.PostDTO;
import com.tusaryan.SpringSecurityApp.SecurityApplication.entities.PostEntity;
import com.tusaryan.SpringSecurityApp.SecurityApplication.entities.User;
import com.tusaryan.SpringSecurityApp.SecurityApplication.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


//W6.6

//This class is Responsible for securing all my posts
@Service
@RequiredArgsConstructor
public class PostSecurity {

    private final PostService postService;

    //only posts created by this user should be allowed to be viewed, updated or deleted by this user
    //to check if he is the owner of this post id or not
    //to make it accessible to the controller, we need to making it public
    public boolean isOwnerOfPost(Long postId) {
        //get the current user, since the user is inside principal of security context holder.
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //get the post
        PostDTO post = postService.getPostById(postId);

        //if both the ids are same then that means this user is the owner of this post,
        //so this method will return true in that case
        return post.getAuthor().getId().equals(user.getId());
    }
}
