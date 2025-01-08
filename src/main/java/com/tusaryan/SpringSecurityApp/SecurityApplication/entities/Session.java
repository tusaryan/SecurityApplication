package com.tusaryan.SpringSecurityApp.SecurityApplication.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

//L6.3

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String refreshToken;

    //to autofill the creation time in this field
    @CreationTimestamp
    private LocalDateTime lastUsedAt;

    //since one user can have many sessions
    @ManyToOne
    private User user;
}
