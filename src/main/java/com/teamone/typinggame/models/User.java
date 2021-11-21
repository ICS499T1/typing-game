package com.teamone.typinggame.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.*;

@Component
@Entity
@Table(name = "users")
@Getter
@Setter
@JsonIgnoreProperties(value={"password"})
public class User implements UserDetails {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "userID", nullable = false)
    private Long userID;

    @Column(nullable = false, unique = true)
    private String username;


    @Column(nullable = false)
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, optional = false)
    @PrimaryKeyJoinColumn
    private Stats userStats;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<KeyStats> allKeys;

//    private Boolean locked = false;
//    private Boolean enabled = false;

    public Long getUserID() {
        return userID;
    }

    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("USER");
        return Collections.singletonList(authority);
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserStats(Stats userStats) {
        this.userStats = userStats;
    }

    public Stats getUserStats() {
        return userStats;
    }

    public List<KeyStats> getAllKeys() {
        return allKeys;
    }

    public void setAllKeys(List<KeyStats> allKeys) {
        this.allKeys = allKeys;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof User && ((User) o).getUsername().equals(this.username));
    }

    @Override
    public int hashCode() {
        if (username == null) {
            return 0;
        }
        return username.hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", userStats=" + userStats +
                ", allKeys=" + allKeys +
                '}';
    }
}
