package com.tusaryan.SpringSecurityApp.SecurityApplication.services;


import com.tusaryan.SpringSecurityApp.SecurityApplication.dto.LoginDto;
import com.tusaryan.SpringSecurityApp.SecurityApplication.dto.SignupDto;
import com.tusaryan.SpringSecurityApp.SecurityApplication.dto.UserDto;
import com.tusaryan.SpringSecurityApp.SecurityApplication.entities.User;
import com.tusaryan.SpringSecurityApp.SecurityApplication.exceptions.ResourceNotFoundException;
import com.tusaryan.SpringSecurityApp.SecurityApplication.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

//Earlier, L5.6

//this is MyUserDetailServiceImpl

//commented the below @Service to use InMemoryUserDetails manager to save and authenticate user
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    //to map the signUpDto to user entity
    private final ModelMapper modelMapper;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //Spring provider will use this method to get user details from the repo
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new BadCredentialsException("User with email: "+ username + " not found"));
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: "+ userId + "not found"));
    }

    //this method create a new user inside our DB
    public UserDto signUp(SignupDto signupDto) {
        //to check if the user is already present inside our DB
        Optional<User> user = userRepository.findByEmail(signupDto.getEmail());
        if (user.isPresent()) {
            throw new BadCredentialsException("User with email already exists: "+ signupDto.getEmail());
        }
        //if not present, then create a new user
        User toBeCreatedUser = modelMapper.map(signupDto, User.class);

        //to encode the password given by user during signup and overwrite it to the new user password
        toBeCreatedUser.setPassword(passwordEncoder.encode(signupDto.getPassword()));

        //save it to repo
        User savedUser = userRepository.save(toBeCreatedUser);
        //return the UserDto
        return modelMapper.map(savedUser, UserDto.class);
    }

}
