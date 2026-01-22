package com.Backend.MobileApp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path root = Paths.get("uploads/profile");

    public String store(MultipartFile file) {
        try {
            Files.createDirectories(root);

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = root.resolve(filename);
            Files.copy(file.getInputStream(), path);

            return "/uploads/profile/" + filename;
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}
