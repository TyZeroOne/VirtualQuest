package org.virtualquest.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.virtualquest.platform.dto.RatingDTO;
import org.virtualquest.platform.dto.RatingRequestDTO;
import org.virtualquest.platform.exception.GlobalExceptionHandler;
import org.virtualquest.platform.exception.ResourceNotFoundException;
import org.virtualquest.platform.filter.JwtAuthFilter;
import org.virtualquest.platform.model.Rating;
import org.virtualquest.platform.service.RatingService;
import org.virtualquest.platform.util.JwtUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RatingController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class RatingControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RatingService ratingService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    void addRating_Success() throws Exception {
        RatingDTO ratingDTO = new RatingDTO(4, "Отличный квест!");
        RatingRequestDTO request = new RatingRequestDTO();
        request.setUserId(1L);
        request.setQuestId(2L);
        request.setRating(ratingDTO);

        Rating rating = new Rating();
        rating.setId(10L);
        rating.setRating(4);
        rating.setReview("Отличный квест!");

        when(ratingService.addRating(1L, 2L, ratingDTO)).thenReturn(rating);

        mockMvc.perform(post("/api/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.review").value("Отличный квест!"));
    }


    @Test
    void getQuestRatings_Success() throws Exception {
        Long questId = 5L;

        Rating rating1 = new Rating();
        rating1.setId(1L);
        rating1.setRating(3);

        Rating rating2 = new Rating();
        rating2.setId(2L);
        rating2.setRating(5);

        when(ratingService.getRatingsByQuest(questId)).thenReturn(List.of(rating1, rating2));

        mockMvc.perform(get("/api/ratings/quest/{questId}", questId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void deleteRating_Success() throws Exception {
        Long ratingId = 10L;
        Long userId = 20L;

        Mockito.doNothing().when(ratingService).deleteRating(ratingId, userId);

        mockMvc.perform(delete("/api/ratings/{ratingId}", ratingId)
                        .param("userId", userId.toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAverageRating_Success() throws Exception {
        Long questId = 3L;
        double avgRating = 4.25;

        when(ratingService.getAverageRating(questId)).thenReturn(avgRating);

        mockMvc.perform(get("/api/ratings/quest/{questId}/average", questId))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(avgRating)));
    }

    @Test
    void addRating_NotFoundException() throws Exception {
        RatingDTO ratingDTO = new RatingDTO(4, "");  // можно пустой review
        RatingRequestDTO request = new RatingRequestDTO();
        request.setUserId(1L);
        request.setQuestId(2L);
        request.setRating(ratingDTO);

        when(ratingService.addRating(anyLong(), anyLong(), any()))
                .thenThrow(new ResourceNotFoundException("Quest or User not found"));

        mockMvc.perform(post("/api/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Quest or User not found"));
    }


    @Test
    void deleteRating_NotFoundException() throws Exception {
        Long ratingId = 10L;
        Long userId = 20L;

        doThrow(new ResourceNotFoundException("Rating not found"))
                .when(ratingService).deleteRating(ratingId, userId);

        mockMvc.perform(delete("/api/ratings/{ratingId}", ratingId)
                        .param("userId", userId.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Rating not found"));
    }
}
