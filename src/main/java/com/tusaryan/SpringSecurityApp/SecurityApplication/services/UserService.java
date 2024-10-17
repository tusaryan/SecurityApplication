package com.tusaryan.SpringSecurityApp.SecurityApplication.services;


import com.tusaryan.SpringSecurityApp.SecurityApplication.exceptions.ResourceNotFoundException;
import com.tusaryan.SpringSecurityApp.SecurityApplication.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//this is MyUserDetailServiceImpl

//commented the below @Service to use InMemoryUserDetails manager to save and authenticate user
//@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    //Spring provider will use this method to get user details from the repo
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with email: "+ username + "not found"));
    }
}
