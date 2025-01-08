package com.tusaryan.SpringSecurityApp.SecurityApplication.repositories;

import com.tusaryan.SpringSecurityApp.SecurityApplication.entities.Session;
import com.tusaryan.SpringSecurityApp.SecurityApplication.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

//L6.3
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByUser(User user);

    Optional<Session> findByRefreshToken(String refreshToken);
}
