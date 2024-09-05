package com.tusaryan.SpringSecurityApp.SecurityApplication.repositories;

import com.tusaryan.SpringSecurityApp.SecurityApplication.entities.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
}