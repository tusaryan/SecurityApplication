package com.tusaryan.SpringSecurityApp.SecurityApplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

//L6.1
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginResponseDto {

    private Long id;
    private String accessToken;
    private String refreshToken;
}

