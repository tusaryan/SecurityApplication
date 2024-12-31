package com.tusaryan.SpringSecurityApp.SecurityApplication.dto;

import lombok.Data;

//L5.5

@Data
public class SignupDto {
    //can add validators too for that check week 2
    private String email;
    private String password;
    private String name;
}
