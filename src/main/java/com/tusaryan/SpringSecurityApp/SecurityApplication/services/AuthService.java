package com.tusaryan.SpringSecurityApp.SecurityApplication.services;

import com.tusaryan.SpringSecurityApp.SecurityApplication.dto.LoginDto;
import com.tusaryan.SpringSecurityApp.SecurityApplication.dto.LoginResponseDto;
import com.tusaryan.SpringSecurityApp.SecurityApplication.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

//Earlier, L6.1, L6.3
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final SessionService sessionService;

    public LoginResponseDto login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        User user = (User) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        //generate a new session inside our db
        sessionService.generateNewSession(user, refreshToken);

        return new LoginResponseDto(user.getId(), accessToken, refreshToken);
    }

    public LoginResponseDto refreshToken(String refreshToken) {
        //to check if refresh token valid and also not expired
        Long userId = jwtService.getUserIdFromToken(refreshToken);
        //if session is not expired then check if the refresh token is valid or not
        sessionService.validateSession(refreshToken);

        User user = userService.getUserById(userId);

        //to generate new access token
        String accessToken = jwtService.generateAccessToken(user);
        return new LoginResponseDto(user.getId(), accessToken, refreshToken);
    }

    //We can also create a Logout controller/method to delete the session/refresh token from the db/backend
    // and also delete the refresh token from the client side,
    // Every time the user logs out.
}
