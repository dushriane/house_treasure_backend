package com.housetreasure.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${file.upload-dir:uploads/items}")
    private String uploadDir;

    @Value("${file.base-url:http://localhost:8080}")
    private String baseUrl;

    /**
     * Upload multiple images and return their URLs
     */
    public List<String> uploadImages(MultipartFile[] files) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IOException("File must be an image: " + file.getOriginalFilename());
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ? 
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // Save file
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Generate URL
            String fileUrl = baseUrl + "/" + uploadDir + "/" + uniqueFilename;
            imageUrls.add(fileUrl);

            System.out.println("Uploaded image: " + uniqueFilename + " -> " + fileUrl);
        }

        return imageUrls;
    }

    /**
     * Upload a single image and return its URL
     */
    public String uploadImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }

        List<String> urls = uploadImages(new MultipartFile[]{file});
        return urls.isEmpty() ? null : urls.get(0);
    }

    /**
     * Delete an image file
     */
    public boolean deleteImage(String imageUrl) {
        try {
            // Extract filename from URL
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadDir, filename);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("Deleted image: " + filename);
                return true;
            } else {
                System.out.println("Image not found: " + filename);
                return false;
            }
        } catch (IOException e) {
            System.err.println("Error deleting image: " + imageUrl);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete multiple images
     */
    public int deleteImages(List<String> imageUrls) {
        int deletedCount = 0;
        for (String url : imageUrls) {
            if (deleteImage(url)) {
                deletedCount++;
            }
        }
        return deletedCount;
    }

    /**
     * Validate if file is an allowed image type
     */
    public boolean isValidImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
                contentType.equals("image/jpeg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/webp")
        );
    }

    /**
     * Get file size limit in MB
     */
    public long getMaxFileSizeMB() {
        return 10; // 10 MB limit
    }

    /**
     * Check if file size is within limit
     */
    public boolean isFileSizeValid(MultipartFile file) {
        long maxSizeBytes = getMaxFileSizeMB() * 1024 * 1024;
        return file.getSize() <= maxSizeBytes;
    }
}
