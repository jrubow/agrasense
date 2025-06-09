package com.asterlink.rest.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Paths;
import java.io.File;

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
        String fileName = "script.py";  // Name of the Python file
        String pythonCode = """
                import argparse
                import cv2
                import numpy as np
                import requests
                from PIL import Image
                from io import BytesIO
                import matplotlib.pyplot as plt
                import os
                from shapely.geometry import Polygon, Point
                from scipy.spatial.distance import cdist
                                
                # Parse command-line arguments
                parser = argparse.ArgumentParser(description="Process image and detect buildings")
                parser.add_argument("image_url", help="URL of the image to process")
                args = parser.parse_args()
                                
                def detect_buildings(image_url):
                    print(image_url)
                    response = requests.get(image_url)
                    print(response)
                    image = Image.open(BytesIO(response.content))
                    image = np.array(image)
                                
                    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
                    ret, thresh = cv2.threshold(gray, 200, 200, cv2.THRESH_BINARY)
                                
                    contours, _ = cv2.findContours(thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
                    building_polygons = []
                    for cnt in contours:
                        epsilon = 0.01 * cv2.arcLength(cnt, True)
                        approx = cv2.approxPolyDP(cnt, epsilon, True)
                        if cv2.contourArea(approx) > 100:
                            poly = Polygon([tuple(pt[0]) for pt in approx])
                            building_polygons.append(poly)
                                
                    return building_polygons, image
                                
                def generate_grid_points(image_shape, spacing):
                    height, width = image_shape[:2]
                    points = [(x, y) for y in range(0, height, spacing) for x in range(0, width, spacing)]
                    return points
                                
                def filter_points(points, building_polygons):
                    return [pt for pt in points if not any(building.contains(Point(pt)) for building in building_polygons)]
                                
                def greedy_coverage(target_points, candidate_points, relay_range):
                    selected = []
                    uncovered = set(target_points)
                    candidate_coverage = {}
                    candidate_array = np.array(candidate_points)
                    target_array = np.array(list(uncovered))
                                
                    for cp in candidate_points:
                        cp_arr = np.array(cp).reshape(1, -1)
                        distances = cdist(cp_arr, target_array)[0]
                        covered = set(tuple(pt) for pt, d in zip(target_array, distances) if d <= relay_range)
                        candidate_coverage[cp] = covered
                                
                    while uncovered:
                        best_candidate = None
                        best_cover = set()
                        for cp, covered in candidate_coverage.items():
                            current_cover = covered.intersection(uncovered)
                            if len(current_cover) > len(best_cover):
                                best_cover = current_cover
                                best_candidate = cp
                        if best_candidate is None:
                            break
                        selected.append(best_candidate)
                        uncovered -= best_cover
                    return selected
                                
                def process_image(image_url, output_dir="processed_images"):
                    os.makedirs(output_dir, exist_ok=True)
                    grid_spacing = 40
                    relay_range_ft = 200
                    conversion_factor = 1
                    relay_range = relay_range_ft * conversion_factor
                                
                    building_polygons, image = detect_buildings(image_url)
                    all_points = generate_grid_points(image.shape, spacing=grid_spacing)
                    target_points = filter_points(all_points, building_polygons)
                    candidate_points = target_points.copy()
                    selected_nodes = greedy_coverage(target_points, candidate_points, relay_range)
                                
                    plt.figure(figsize=(10, 10))
                    image_rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
                    plt.imshow(image_rgb)
                                
                    for poly in building_polygons:
                        x, y = poly.exterior.xy
                        plt.plot(x, y, color='red', linewidth=2, label='Detected Obstacle')
                                
                    candidate_x = [pt[0] for pt in candidate_points]
                    candidate_y = [pt[1] for pt in candidate_points]
                    plt.scatter(candidate_x, candidate_y, s=10, color='yellow', label='Candidate Points')
                                
                    for node in selected_nodes:
                        circle = plt.Circle(node, relay_range, color='green', fill=False, linewidth=2)
                        plt.gca().add_patch(circle)
                        plt.plot(node[0], node[1], marker='x', color='blue', markersize=10, label='Selected Node')
                                
                    plt.title("Optimized Relay Node Deployment")
                    plt.legend().set_visible(False)  # Disable the legend
                    plt.axis('off')
                                
                    output_path = os.path.join(output_dir, "processed_image.png")
                    plt.savefig(output_path, bbox_inches='tight')
                    plt.close()
                    return output_path
                                
                if __name__ == "__main__":
                    output_file = process_image(args.image_url)
                    print(output_file)         
                """;

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(pythonCode);
            System.out.println("Python file created successfully: " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing the Python file: " + e.getMessage());
        }

        command.add("python"); // Change to "python3" if necessary
        command.add("script.py"); // Path to script
        command.add(imageUrl); // Pass the image URL as an argument

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // Merge error stream with output

        try {
            Process process = processBuilder.start();

            // Read the Python script's output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            process.waitFor(); // Wait for the process to complete

            return output.toString().trim();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Error processing the image.";
        }
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