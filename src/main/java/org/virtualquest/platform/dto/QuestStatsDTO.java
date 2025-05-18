package org.virtualquest.platform.dto;

public record QuestStatsDTO(
        long started,  // количество начавших квест
        long completed // количество завершивших квест
) {}