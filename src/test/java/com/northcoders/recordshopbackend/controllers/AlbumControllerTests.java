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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Date;
import java.util.List;

import static org.mockito.Mockito.*;

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

    @Test
    @DisplayName("POST add a new album")
    void postNewAlbum() throws Exception {
        // Arrange
        AlbumDTO timelessDTO = AlbumDTO.builder()
                .title("Timeless")
                .artist("Davido")
                .genre(Genre.AFROBEATS)
                .releaseDate(Date.valueOf("2023-01-12"))
                .stock(4)
                .build();

        when(mockAlbumService.postNewAlbum(timelessDTO)).thenReturn(timelessDTO);

        // Act & Assert
        this.mockMvcController.perform(
                        MockMvcRequestBuilders.post("/api/v1/albums/add")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(timelessDTO)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        verify(mockAlbumService, times(1)).postNewAlbum(timelessDTO);
    }

    @Test
    @DisplayName("GET album by Id")
    void getAlbumById() throws Exception {
        // Arrange
        Long id = 2L;

        AlbumDTO timelessDTO = AlbumDTO.builder()
                .id(id)
                .title("Timeless")
                .artist("Davido")
                .genre(Genre.AFROBEATS)
                .releaseDate(Date.valueOf("2023-01-12"))
                .stock(4)
                .build();

        when(mockAlbumService.returnAlbumDTOById(id)).thenReturn(timelessDTO);

        // Act & Assert
        this.mockMvcController.perform(
                        MockMvcRequestBuilders.get("/api/v1/albums/2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.artist").value("Davido"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genre").value("AFROBEATS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.stock").value(4));
    }
}