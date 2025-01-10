package com.tusaryan.SpringSecurityApp.SecurityApplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Earlier, W6.6

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Long id;
    private String title;
    private String description;

    //use the same name in entity and dto to map them else use @Mapping annotation, or they will not be mapped
    private UserDto author;
}
