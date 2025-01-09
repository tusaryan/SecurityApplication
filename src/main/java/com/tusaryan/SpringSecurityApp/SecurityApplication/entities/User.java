package com.tusaryan.SpringSecurityApp.SecurityApplication.entities;

import com.tusaryan.SpringSecurityApp.SecurityApplication.utils.PermissionMapping;
import com.tusaryan.SpringSecurityApp.SecurityApplication.entities.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

//Earlier, W5.6, 5.7, 6.2, 6.4, 6.5

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

/*
    //to fetch all the permissions as soon as we are fetching the user
    @ElementCollection(fetch = FetchType.EAGER)
    //to mark these permissions as Strings inside our DB
    @Enumerated(EnumType.STRING)
    private Set<Permission> permissions;
*/

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        /*//now our authorities have roles and permissions attached to it
        Set<SimpleGrantedAuthority> authorities = roles.stream()
                //convert the roles to SimpleGrantedAuthority, and it implements GrantedAuthority
                //.name is coming from enum type, so basically we're returning the same name as the Role
                //REMEMBER: while we assign a role we should add "ROLE_" before it, because Spring Security expects it to be in this format
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                //since a set is also a collection, so we're returning it.
                .collect(Collectors.toSet());

        //to add permissions attached to that authority
        permissions.forEach(
                //since it is an enum so to get each permission we use permission.name()
                permission -> authorities.add(new SimpleGrantedAuthority(permission.name()))
        );*/

        //to get all the permissions based on a roles, we will go into each of the roles
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        //will go inside roles of this user
        roles.forEach(
                role -> {
                    //to get all the permissions based on a role, it will return a Set of SimpleGrantedAuthority
                    Set<SimpleGrantedAuthority> permissions = PermissionMapping.getAuthoritiesForRole(role);
                    //put all these permissions inside the authorities
                    authorities.addAll(permissions);
                    //also adding/passing the roles inside the authorities
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
                }
        );

        return authorities;
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
