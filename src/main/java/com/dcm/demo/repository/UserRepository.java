package com.dcm.demo.repository;

import com.dcm.demo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(User.Role role);
    Optional<User> findByEmailOrPhone(String email, String phone);
    Optional<User> findByPhone(String phone);

    @Query("""
        SELECT u FROM User u
        WHERE (:keyword IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword , '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword , '%'))
            OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword , '%')))
        AND (:role IS NULL OR u.role = :role)
""")
    Page<User> findAll(Pageable pageable, String keyword, User.Role role);
}
