package com.cyber.playdetector.controller;

import com.cyber.playdetector.dto.AppScanDto;
import com.cyber.playdetector.model.App;
import com.cyber.playdetector.service.AppAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/apps")
public class AppController {

    private final AppAnalysisService appAnalysisService;

    public AppController(AppAnalysisService appAnalysisService) {
        this.appAnalysisService = appAnalysisService;
    }

    @GetMapping
    public ResponseEntity<List<App>> getAllApps() {
        return ResponseEntity.ok(appAnalysisService.getAllApps());
    }

    @GetMapping("/{id}")
    public ResponseEntity<App> getAppById(@PathVariable Long id) {
        return appAnalysisService.getAppById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/scan")
    public ResponseEntity<App> scanApp(
            @RequestBody AppScanDto scanDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : "system";
        App app = appAnalysisService.scanAndSaveApp(scanDto, username);
        return ResponseEntity.ok(app);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDataset(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : "system";
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Uploaded file is empty");
        }
        try {
            int count = appAnalysisService.parseAndProcessCsv(file.getInputStream(), username);
            return ResponseEntity.ok("Successfully parsed and scanned " + count + " applications from CSV dataset.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to parse CSV dataset: " + e.getMessage());
        }
    }
}
