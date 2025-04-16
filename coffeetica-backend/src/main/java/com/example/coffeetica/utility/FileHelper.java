package com.example.coffeetica.utility;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;

public class FileHelper {

    public static void deleteImage(String uploadDirectory, String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            String filename = Paths.get(imageUrl).getFileName().toString();

            Path filePath = Paths.get(uploadDirectory, filename);

            try {
                Files.deleteIfExists(filePath);
                System.out.println("Deleted file: " + filePath);
            } catch (IOException e) {
                System.err.println("Failed to delete file: " + filePath);
                e.printStackTrace();
            }
        }
    }
}
