package com.example.caselabtest;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "files")
@Data
public class FileStructure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "creation_date", nullable = false)
    private String creationDate;

    @Column(length = 500)
    private String description;

    @Lob
    @Column(name = "file_data", nullable = false)
    private byte[] fileData;
}
