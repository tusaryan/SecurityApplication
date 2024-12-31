package com.tusaryan.SpringSecurityApp.SecurityApplication.controllers;

import com.tusaryan.SpringSecurityApp.SecurityApplication.dto.LoginDto;
import com.tusaryan.SpringSecurityApp.SecurityApplication.dto.SignupDto;
import com.tusaryan.SpringSecurityApp.SecurityApplication.dto.UserDto;
import com.tusaryan.SpringSecurityApp.SecurityApplication.services.AuthService;
import com.tusaryan.SpringSecurityApp.SecurityApplication.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//L5.5

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    //to check whether entered user in signUp is valid or not, alternatively we can create auth service
    private final UserService userService;
    private final AuthService authService;


    @PostMapping("/signup")
    //getting signUp request and returning the userDto
    public ResponseEntity<UserDto> signUp(@RequestBody SignupDto signupDto) {

        //to check whether user ser vice is valid or not
        //to check whether the user is already present or not

        //get the user
        UserDto userDto = userService.signUp(signupDto);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto, HttpServletRequest request,
                                        HttpServletResponse response) {
        String token = authService.login(loginDto);

        //to store token in the cookie
        Cookie cookie = new Cookie("token", token);
        //it ensures that cookie cannot be accessed by another means, it can only be found with the help of http method.
        //can only be accessed by http and not by JavaScript.
        //always set the cookie to http only to prevent XSS attacks
        cookie.setHttpOnly(true);
        //only when it is an https request
        //cookie.setSecure(true);

        //so that http only cookies can be passed from our backend to frontend only
        response.addCookie(cookie);

        return ResponseEntity.ok(token);
    }
}
