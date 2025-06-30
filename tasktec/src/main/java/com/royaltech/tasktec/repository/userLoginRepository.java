package com.royaltech.tasktec.repository;

import com.royaltech.tasktec.entity.userLogin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface userLoginRepository extends JpaRepository<userLogin, Long> {
    Optional<userLogin> findByUsername(String username); // Custom query method
}
