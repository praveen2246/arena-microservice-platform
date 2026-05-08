package com.socialmedia.media.controller;

import com.socialmedia.media.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/media")
public class MediaController {

    @Autowired
    private MediaService mediaService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = mediaService.storeFile(file);
        return ResponseEntity.ok(Map.of("fileName", fileName, "url", "/api/v1/media/download/" + fileName));
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        byte[] data = mediaService.getFile(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // Simple assumption for now
                .body(data);
    }
}
