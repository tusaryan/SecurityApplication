package com.tusaryan.SpringSecurityApp.SecurityApplication.repositories;

import com.tusaryan.SpringSecurityApp.SecurityApplication.entities.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {

    //JPQL / hibernate will handle this and convert it to corresponding sql query.
    Optional<User> findByEmail(String email);
}
