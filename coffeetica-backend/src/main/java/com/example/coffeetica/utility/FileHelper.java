package com.example.coffeetica.utility;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;

public class FileHelper {

    public static void deleteImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Path filePath = Paths.get(imageUrl.substring(1));
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("Failed to delete file: " + filePath);
            }
        }
    }
}
