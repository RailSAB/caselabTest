package com.example.caselabtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.Arrays;
import java.util.Collections;


public class FileServiceTest {

    @Mock
    private FileDBWork fileDBWork;

    @InjectMocks
    private FileService fileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave() {

        DataTransferObject dto = new DataTransferObject();
        dto.setTitle("Test Title");
        dto.setDescription("Test Description");
        dto.setFileData(Base64.getEncoder().encodeToString("Test Data".getBytes(StandardCharsets.UTF_8)));
        dto.setCreationDate("2024-08-31");
        FileStructure fileStructure = new FileStructure();
        fileStructure.setId(1L);

        when(fileDBWork.save(any(FileStructure.class))).thenReturn(fileStructure);
        Long result = fileService.save(dto);

        assertNotNull(result);
        assertEquals(0L, result);
        verify(fileDBWork, times(1)).save(any(FileStructure.class));
    }

    @Test
    void testGetById_Found() {
        FileStructure fileStructure = new FileStructure();
        fileStructure.setId(1L);
        fileStructure.setTitle("Test Title");
        fileStructure.setDescription("Test Description");
        fileStructure.setFileData("Test Data".getBytes(StandardCharsets.UTF_8));
        fileStructure.setCreationDate("2024-08-31");

        when(fileDBWork.findById(1L)).thenReturn(Optional.of(fileStructure));
        Optional<DataTransferObject> result = fileService.getById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test Title", result.get().getTitle());
        assertEquals("Test Description", result.get().getDescription());
        assertEquals("2024-08-31", result.get().getCreationDate());
        assertEquals(Base64.getEncoder().encodeToString("Test Data".getBytes(StandardCharsets.UTF_8)), result.get().getFileData());
        verify(fileDBWork, times(1)).findById(1L);
    }

    @Test
    void testGetById_NotFound() {
        when(fileDBWork.findById(1L)).thenReturn(Optional.empty());

        Optional<DataTransferObject> result = fileService.getById(1L);

        assertFalse(result.isPresent());
        verify(fileDBWork, times(1)).findById(1L);
    }


    @Test
    void testGetAllFiles() {
        FileStructure file1 = new FileStructure();
        file1.setTitle("File 1");
        file1.setDescription("Description 1");
        file1.setCreationDate("2024-08-31");
        file1.setFileData("Data 1".getBytes(StandardCharsets.UTF_8));
        FileStructure file2 = new FileStructure();
        file2.setTitle("File 2");
        file2.setDescription("Description 2");
        file2.setCreationDate("2024-08-30");
        file2.setFileData("Data 2".getBytes(StandardCharsets.UTF_8));

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("creationDate").descending());
        when(fileDBWork.findAll(pageRequest))
                .thenReturn(new PageImpl<>(Arrays.asList(file1, file2), pageRequest, 2));

        var result = fileService.getAllFiles(0, 10);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("File 1", result.getContent().get(0).getTitle());
        assertEquals("File 2", result.getContent().get(1).getTitle());

        verify(fileDBWork, times(1)).findAll(pageRequest);
    }

    @Test
    void testGetAllFilesEmptyResult() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("creationDate").descending());
        when(fileDBWork.findAll(pageRequest)).thenReturn(new PageImpl<>(Collections.emptyList(), pageRequest, 0));

        var result = fileService.getAllFiles(0, 10);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());

        verify(fileDBWork, times(1)).findAll(pageRequest);
    }
}
