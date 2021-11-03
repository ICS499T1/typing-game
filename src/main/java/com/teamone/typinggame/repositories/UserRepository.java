package com.teamone.typinggame.repositories;

import com.teamone.typinggame.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.Map;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByUsername(String username);

    @Query(value = "SELECT users.userid, users.username, stats.average_speed FROM users INNER JOIN stats ON stats.userid=users.userid ORDER BY stats.average_speed DESC LIMIT 20", nativeQuery = true)
    Collection<User> fetchTopTwentyUsers();
}