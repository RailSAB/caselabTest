package com.example.caselabtest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Base64;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


@Service
public class FileService {

    @Autowired
    private FileDBWork fileDBWork;

    public Long save(DataTransferObject dataTransferObject) {
        FileStructure fileStructure = new FileStructure();
        fileStructure.setTitle(dataTransferObject.getTitle());
        fileStructure.setDescription(dataTransferObject.getDescription());
        fileStructure.setFileData(Base64.getDecoder().decode(dataTransferObject.getFileData()));
        fileStructure.setCreationDate(dataTransferObject.getCreationDate());

        fileDBWork.save(fileStructure);
        return fileStructure.getId();
    }

    public Optional<DataTransferObject> getById(Long id) {
        Optional<FileStructure> fileStructureOptional = fileDBWork.findById(id);
        if (fileStructureOptional.isPresent()) {
            FileStructure fileStructure = fileStructureOptional.get();
            DataTransferObject dataTransferObject = new DataTransferObject();
            dataTransferObject.setTitle(fileStructure.getTitle());
            dataTransferObject.setDescription(fileStructure.getDescription());
            dataTransferObject.setCreationDate(fileStructure.getCreationDate());
            dataTransferObject.setFileData(Base64.getEncoder().encodeToString(fileStructure.getFileData()));
            return Optional.of(dataTransferObject);
        }
        return Optional.empty();
    }

    public Page<DataTransferObject> getAllFiles(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("creationDate").descending());
        return fileDBWork.findAll(pageRequest).map(fileStructure -> {
                    DataTransferObject dto = new DataTransferObject();
                    dto.setTitle(fileStructure.getTitle());
                    dto.setDescription(fileStructure.getDescription());
                    dto.setCreationDate(fileStructure.getCreationDate());
                    dto.setFileData(Base64.getEncoder().encodeToString(fileStructure.getFileData()));
                    return dto;
        });
    }
}
