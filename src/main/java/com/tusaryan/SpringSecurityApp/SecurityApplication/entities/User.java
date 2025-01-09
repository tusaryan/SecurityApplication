package com.tusaryan.SpringSecurityApp.SecurityApplication.entities;

import com.tusaryan.SpringSecurityApp.SecurityApplication.entities.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//Earlier, L5.6, L5.7, 6.2, 6.4

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //since email has to be unique
    @Column(unique = true)
    private String email;
    private String password;
    private String name;

    public User(Long id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    //to store a unique set of roles
    //since we are storing as a set, we need to use @ElementCollection
    //with FetchType.EAGER, we are also fetching the roles as soon as we are fetching the user (so we are not doing it lazily i.e., to get it later on)
    @ElementCollection(fetch = FetchType.EAGER)
    //by default, it will be stored as Ordinal, but we can also set it to String
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                //convert the roles to SimpleGrantedAuthority and it implements GrantedAuthority
                //.name is coming from enum type, so basically we returning the same name as the Role
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                //since set is also a collection so we're returning it.
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
