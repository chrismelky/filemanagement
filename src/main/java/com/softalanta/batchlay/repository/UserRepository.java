package com.softalanta.batchlay.repository;

import com.softalanta.batchlay.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
