package com.tusaryan.SpringSecurityApp.SecurityApplication.dto;

import lombok.Data;

//L5.5

@Data
public class UserDto {
    private Long id;
    private String email;
    private String name;
}
