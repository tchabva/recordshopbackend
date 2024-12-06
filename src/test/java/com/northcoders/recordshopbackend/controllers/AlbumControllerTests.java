package com.northcoders.recordshopbackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.northcoders.recordshopbackend.dto.AlbumDTO;
import com.northcoders.recordshopbackend.model.Album;
import com.northcoders.recordshopbackend.model.Artist;
import com.northcoders.recordshopbackend.model.Stock;
import com.northcoders.recordshopbackend.model.enums.Genre;
import com.northcoders.recordshopbackend.service.AlbumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Date;
import java.util.List;

import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@SpringBootTest
public class AlbumControllerTests {

    @Mock
    private AlbumService mockAlbumService;

    @InjectMocks
    private AlbumController albumController;

    @Autowired
    private MockMvc mockMvcController;

    private ObjectMapper mapper;


    @BeforeEach
    public  void setup(){
        mockMvcController = MockMvcBuilders.standaloneSetup(albumController).build();
        mapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Get all in stock albums")
    void getInStockAlbums() throws Exception {
        // Arrange
        List<AlbumDTO> albumDTOs = List.of(
                AlbumDTO.builder()
                        .id(1L)
                        .title("Timeless")
                        .artist("Davido")
                        .genre(Genre.AFROBEATS)
                        .releaseDate(Date.valueOf("2023-01-12"))
                        .stock(4)
                        .build(),
                AlbumDTO.builder()
                        .id(2L)
                        .title("A Good Time")
                        .artist("Marie Dahlstrom")
                        .genre(Genre.RNB)
                        .releaseDate(Date.valueOf("2023-06-07"))
                        .stock(3)
                        .build(),
                AlbumDTO.builder()
                        .id(3L)
                        .title("GNX")
                        .artist("Kendrick Lamar")
                        .genre(Genre.RNB)
                        .releaseDate(Date.valueOf("2024-11-22"))
                        .stock(2)
                        .build()
        );

        when(mockAlbumService.getAllInStockAlbumDTOs()).thenReturn(albumDTOs);
        // Act & Assert
        this.mockMvcController.perform(
                        MockMvcRequestBuilders.get("/api/v1/albums"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].artist").value("Davido"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].artist").value("Marie Dahlstrom"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].id").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].artist").value("Kendrick Lamar"));
    }
}
