package com.tusaryan.SpringSecurityApp.SecurityApplication.dto;

import com.tusaryan.SpringSecurityApp.SecurityApplication.entities.enums.Role;
import lombok.Data;

import java.util.Set;

//W5.5, W6.4

@Data
public class SignupDto {
    //can add validators too for that check week 2
    private String email;
    private String password;
    private String name;

    //we will put the roles during the signup process only, but this is not ideal in production ready app else everyone will become admin,
    // Since we should do it in the backend later on, to onboard a new creator or admin, like only admins can create a new creator,
    //we should create controllers to pass the role
    // like admins will only call the onBoardNewCreator api and there admin will be passing creator role for any user etc.
    //this is only for testing/demonstration purposes
    private Set<Role> roles;
}
