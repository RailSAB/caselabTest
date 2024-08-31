package com.example.caselabtest;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileDBWork  extends JpaRepository<FileStructure, Long>  {
}
