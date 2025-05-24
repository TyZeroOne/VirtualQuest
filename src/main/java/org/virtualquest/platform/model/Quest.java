package org.virtualquest.platform.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    private static final int EASY_LIMIT = 35;
    private static final int MEDIUM_LIMIT = 70;
    private static final int MAX_LIMIT = 100;
    private static final int AUTO_CALCULATION_THRESHOLD = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    private int startedCount;
    private int completedCount;
    private int points;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    @JsonBackReference
    private Users creator;

    @OneToMany(mappedBy = "quest", cascade = CascadeType.ALL)
    private List<Step> steps = new ArrayList<>();

    @OneToMany(mappedBy = "quest")
    private List<Progress> progresses = new ArrayList<>();

    @OneToMany(mappedBy = "quest")
    private List<Rating> ratings = new ArrayList<>();

    @ManyToMany
    @JsonBackReference
    @JoinTable(
            name = "quest_categories",
            joinColumns = @JoinColumn(name = "quest_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories = new ArrayList<>();
    private boolean published;

    @Column(name = "is_rating_considered", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isRatingConsidered; // Учитывается в рейтинге

    @Column(name = "is_editable", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isEditable = true; // Можно редактировать баллы

    @Column(name = "auto_calculated", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean autoCalculated; // Автоматический расчет баллов

    public void calculateDifficulty() {
        this.points = Math.min(this.points, MAX_LIMIT);

        if (this.points <= EASY_LIMIT) {
            this.difficulty = Difficulty.EASY;
        } else if (this.points <= MEDIUM_LIMIT) {
            this.difficulty = Difficulty.MEDIUM;
        } else {
            this.difficulty = Difficulty.HARD;
        }

        if (this.autoCalculated) {
            this.isRatingConsidered = true;
        }
    }

    public void calculateAutoPoints() {
        if (this.autoCalculated && this.startedCount >= AUTO_CALCULATION_THRESHOLD) {
            double completionRate = (double) this.completedCount / this.startedCount;
            this.points = (int) Math.round(completionRate * MAX_LIMIT);
            calculateDifficulty();
        }
    }

}