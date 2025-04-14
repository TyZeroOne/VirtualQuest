package org.virtualquest.platform.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String fullName;
    private int rating;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginDate;

    @OneToMany(mappedBy = "creator")
    private List<Quest> createdQuests;

    @OneToMany(mappedBy = "user")
    private List<Progress> progresses;

    @OneToMany(mappedBy = "user")
    private List<Rating> ratings;
}