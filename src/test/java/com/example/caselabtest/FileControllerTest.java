package com.example.caselabtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;


@WebMvcTest(FileController.class)
public class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateFile() throws Exception {
        // Given
        DataTransferObject dto = new DataTransferObject();
        dto.setTitle("Test Title");
        dto.setDescription("Test Description");
        dto.setFileData(Base64.getEncoder().encodeToString("Test Data".getBytes(StandardCharsets.UTF_8)));
        dto.setCreationDate("2024-08-31");

        when(fileService.save(any(DataTransferObject.class))).thenReturn(1L);

        // When & Then
        mockMvc.perform(post("/api/files")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    void testGetFile_Found() throws Exception {
        // Given
        DataTransferObject dto = new DataTransferObject();
        dto.setTitle("Test Title");
        dto.setDescription("Test Description");
        dto.setFileData(Base64.getEncoder().encodeToString("Test Data".getBytes(StandardCharsets.UTF_8)));
        dto.setCreationDate("2024-08-31");

        when(fileService.getById(1L)).thenReturn(Optional.of(dto));

        // When & Then
        mockMvc.perform(get("/api/files/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.fileData").value(Base64.getEncoder().encodeToString("Test Data".getBytes(StandardCharsets.UTF_8))))
                .andExpect(jsonPath("$.creationDate").value("2024-08-31"));
    }

    @Test
    void testGetFile_NotFound() throws Exception {
        // Given
        when(fileService.getById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/files/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllFiles() throws Exception {
        DataTransferObject dto1 = new DataTransferObject();
        dto1.setTitle("File 1");
        dto1.setDescription("Description 1");
        dto1.setCreationDate("2024-08-31");
        dto1.setFileData(Base64.getEncoder().encodeToString("Data 1".getBytes(StandardCharsets.UTF_8)));
        DataTransferObject dto2 = new DataTransferObject();
        dto2.setTitle("File 2");
        dto2.setDescription("Description 2");
        dto2.setCreationDate("2024-08-30");
        dto2.setFileData(Base64.getEncoder().encodeToString("Data 2".getBytes(StandardCharsets.UTF_8)));

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("creationDate").descending());
        when(fileService.getAllFiles(0, 10))
                .thenReturn(new PageImpl<>(Arrays.asList(dto1, dto2), pageRequest, 2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/files?page=0&size=10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("File 1"))
                .andExpect(jsonPath("$.content[1].title").value("File 2"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }
}

