package org.virtualquest.platform.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "steps")
@Data
@NoArgsConstructor
public class Step {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quest_id")
    private Quest quest;

    private int stepNumber;
    private String description;

    @Column(columnDefinition = "TEXT")
    private String options;

    @OneToOne
    @JoinColumn(name = "next_step_id")
    private Step nextStep;
}