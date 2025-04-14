package org.virtualquest.platform.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "categories",
        uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Data
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // Делаем обязательным
    private String name;

    private String description;

    @ManyToMany(mappedBy = "categories")
    private List<Quest> quests;
}