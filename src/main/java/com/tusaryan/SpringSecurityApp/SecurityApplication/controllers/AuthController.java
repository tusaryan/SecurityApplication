package com.tusaryan.SpringSecurityApp.SecurityApplication.controllers;

import com.tusaryan.SpringSecurityApp.SecurityApplication.dto.LoginDto;
import com.tusaryan.SpringSecurityApp.SecurityApplication.dto.LoginResponseDto;
import com.tusaryan.SpringSecurityApp.SecurityApplication.dto.SignupDto;
import com.tusaryan.SpringSecurityApp.SecurityApplication.dto.UserDto;
import com.tusaryan.SpringSecurityApp.SecurityApplication.services.AuthService;
import com.tusaryan.SpringSecurityApp.SecurityApplication.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

//L5.5, 6.1
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    //to check whether entered user in signUp is valid or not, alternatively we can create auth service
    private final UserService userService;
    private final AuthService authService;

    @Value("${deploy.env}")
    private String deployEnv;

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
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto, HttpServletRequest request,
                                        HttpServletResponse response) {
        //getting both the tokens and user id as this dto data type
        LoginResponseDto loginResponseDto = authService.login(loginDto);

        //to store token in the cookie/ to set refresh token inside the cookie
        Cookie cookie = new Cookie("refreshToken", loginResponseDto.getRefreshToken());

        //browser will take care that this cookie is not exposed
        //it ensures that cookie cannot be accessed by another means, it can only be found with the help of http method.
        //can only be accessed by http and not by JavaScript.
        //always set the cookie to http only to prevent XSS attacks
        //only logged-in user can access this cookie
        cookie.setHttpOnly(true);

        //only when it is a https request, using TLS certificate of domain.
        //don't use this in development mode (since localhost is not https)
        //use it only inside production mode
        //for this config change the application.properties file as per modes.
        //to set "Secure" to "true" when the "deploy environment"(i.e "deployEnv" variable) is set to "production", Else set it to false i.e., not Secure.
        cookie.setSecure("production".equals(deployEnv));

        //added the cookie to response
        //so that http only cookies can be passed from our backend to frontend only
        response.addCookie(cookie);

        return ResponseEntity.ok(loginResponseDto);
    }

    //to generate new access token using refresh token
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(HttpServletRequest request) {
        //getting refresh token from cookies
        //iterating/stream over all the cookies and find the cookie whose name = refreshToken
        //request.getCookies() will return an array of all the cookies
        String refreshToken = Arrays.stream(request.getCookies()).
                filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                //since from above we are getting cookie so we map it to get the value of the cookie.
                // Alternatives to use
                //.map(cookie -> cookie.getValue())
                .map(Cookie::getValue)
                .orElseThrow(() -> new AuthenticationServiceException("Refresh token not found inside the Cookies"));

        //to get the new access token using refreshToken method in authService
        LoginResponseDto loginResponseDto = authService.refreshToken(refreshToken);

        return ResponseEntity.ok(loginResponseDto);
    }
}
