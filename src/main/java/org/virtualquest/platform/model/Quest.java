package org.virtualquest.platform.model;

import org.virtualquest.platform.model.enums.Difficulty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quests")
@Data
@NoArgsConstructor
public class Quest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Column(columnDefinition = "jsonb")
    private String content;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    private int startedCount;
    private int completedCount;
    private int points;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private Users creator;

    @OneToMany(mappedBy = "quest", cascade = CascadeType.ALL)
    private List<Step> steps = new ArrayList<>();

    @OneToMany(mappedBy = "quest")
    private List<Progress> progresses;

    @OneToMany(mappedBy = "quest")
    private List<Rating> ratings;

    @ManyToMany
    @JoinTable(
            name = "quest_categories",
            joinColumns = @JoinColumn(name = "quest_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;
    private boolean published;
}