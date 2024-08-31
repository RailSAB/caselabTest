package com.example.caselabtest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import java.util.Optional;

@RestController
@RequestMapping("/api/files")
public class FileController {
    @Autowired
    private FileService fileService;

    @PostMapping
    public ResponseEntity<Long> createFile(@RequestBody DataTransferObject dataTransferObject) {
        Long id = fileService.save(dataTransferObject);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataTransferObject> getFile(@PathVariable Long id) {
        Optional<DataTransferObject> dataTransferObjectOptional = fileService.getById(id);
        if (dataTransferObjectOptional.isPresent()) {
            return ResponseEntity.ok(dataTransferObjectOptional.get());
        }else{
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping
    public ResponseEntity<Page<DataTransferObject>> getAllFiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<DataTransferObject> filesPage = fileService.getAllFiles(page, size);
        return ResponseEntity.ok(filesPage);
    }

}
