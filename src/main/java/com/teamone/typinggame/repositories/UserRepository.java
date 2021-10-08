package com.teamone.typinggame.repositories;

import com.teamone.typinggame.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}