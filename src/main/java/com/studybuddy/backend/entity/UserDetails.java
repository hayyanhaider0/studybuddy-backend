package com.studybuddy.backend.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "users")
public class UserDetails {
    @Id
    private String id;

    private String email;
    private String username;
    private String password;
    private String role;

    public UserDetails(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }
}
