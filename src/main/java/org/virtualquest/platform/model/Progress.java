package org.virtualquest.platform.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "progress")
@Data
@NoArgsConstructor
public class Progress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "quest_id")
    private Quest quest;

    @ManyToOne
    @JoinColumn(name = "current_step_id")
    private Step currentStep;

    private boolean completed;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}