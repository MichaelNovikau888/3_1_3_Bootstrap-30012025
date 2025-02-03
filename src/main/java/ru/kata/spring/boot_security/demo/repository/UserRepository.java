package ru.kata.spring.boot_security.demo.repository;

import org.springframework.data.repository.query.Param;
import ru.kata.spring.boot_security.demo.model.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
   @Query("select u from User u join fetch u.roles where u.email = :email")
    Optional<User> findUserAndFetchRoles( String email);
    @Query("SELECT u FROM User u JOIN FETCH u.roles ORDER BY u.id ASC")
    List<User> findAllWithRoles();
}
