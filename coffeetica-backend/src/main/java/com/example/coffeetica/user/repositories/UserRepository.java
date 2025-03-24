package com.example.coffeetica.user.repositories;


import com.example.coffeetica.user.models.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for {@link UserEntity} persistence,
 * including custom queries for searching by username/email.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query(value = """
        SELECT * FROM users u 
        WHERE (:search IS NULL 
            OR LOWER(u.username) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%')) 
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%')))
        """, nativeQuery = true)
    Page<UserEntity> findBySearch(@Param("search") String search, Pageable pageable);

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<UserEntity> findByUsernameOrEmail(@Param("identifier") String identifier);
}