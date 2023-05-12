package com.mjtech.pos.entity;

import com.mjtech.pos.util.ActiveUser;
import com.mjtech.pos.util.UserTerminal;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "username")
    private String username;

    @Column(name = "active")
    private boolean active;

    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "password_hint")
    private String passwordHint;

    @Column(name = "email")
    private String email;

    @Column(name = "created_by")
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable=false)
    private Date createdAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false, updatable=true)
    private Date updatedAt;

    @Column(name = "terminal")
    private String terminal;

    @PrePersist
    public void prePersist() {
        createdAt = new Date();
        createdBy = ActiveUser.getActiveUsername();
        terminal = UserTerminal.getTerminal();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Date();
        updatedBy = ActiveUser.getActiveUsername();
        terminal = UserTerminal.getTerminal();
    }
}
