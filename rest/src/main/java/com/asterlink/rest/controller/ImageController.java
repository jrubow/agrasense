package com.asterlink.rest.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/image")
public class ImageController {

    @PostMapping("/process")
    public ResponseEntity<String> processImage(@RequestBody ImageRequest imageRequest) {
        String imageUrl = imageRequest.getUrl();
        System.out.println("Received image URL: " + imageUrl);

        String processedImageUrl = callPythonScript(imageUrl);
        System.out.println("Processed Image URL: " + processedImageUrl);

        return ResponseEntity.ok(processedImageUrl);
    }

    @GetMapping("/result")
    public ResponseEntity<InputStreamResource> getImage() {
        try {
            // Path to your image in the project directory (in src/main/resources/images folder)
            File imageFile = new File("processed_images/processed_image.png");

            if (!imageFile.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Open the image file as an InputStream
            InputStream inputStream = new FileInputStream(imageFile);

            // Return the image with proper Content-Type and Content-Disposition headers
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + imageFile.getName())
                    .contentType(org.springframework.http.MediaType.IMAGE_JPEG) // or MediaType.IMAGE_PNG if PNG
                    .body(new InputStreamResource(inputStream));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private String callPythonScript(String imageUrl) {
        List<String> command = new ArrayList<>();
        command.add("python"); // Change to "python3" if necessary
        return System.getProperty("user.dir");
//        command.add("rest/src/main/resources/py/script.py"); // Path to script
//        command.add(imageUrl); // Pass the image URL as an argument
//
//        ProcessBuilder processBuilder = new ProcessBuilder(command);
//        processBuilder.redirectErrorStream(true); // Merge error stream with output
//
//        try {
//            Process process = processBuilder.start();
//
//            // Read the Python script's output
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            StringBuilder output = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                output.append(line).append("\n");
//            }
//
//            process.waitFor(); // Wait for the process to complete
//
//            return output.toString().trim();
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//            return "Error processing the image.";
//        }
    }

    public static class ImageRequest {
        private String url;

        public ImageRequest() {
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}