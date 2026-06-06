package com.cyber.playdetector.controller;

import com.cyber.playdetector.model.App;
import com.cyber.playdetector.model.AuditLog;
import com.cyber.playdetector.repository.AppRepository;
import com.cyber.playdetector.repository.AuditLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final AppRepository appRepository;
    private final AuditLogRepository auditLogRepository;

    public DashboardController(AppRepository appRepository, AuditLogRepository auditLogRepository) {
        this.appRepository = appRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        List<App> apps = appRepository.findAll();
        List<AuditLog> recentLogs = auditLogRepository.findTop25ByOrderByTimestampDesc();

        long totalApps = apps.size();
        long safe = 0, medium = 0, high = 0;
        long fraudSpikes = 0;
        double sumRisk = 0;

        for (App app : apps) {
            sumRisk += app.getOverallRiskScore();
            if ("HIGH_RISK".equals(app.getRiskLevel())) {
                high++;
            } else if ("MEDIUM_RISK".equals(app.getRiskLevel())) {
                medium++;
            } else {
                safe++;
            }

            if (app.getFraudResult() != null) {
                if (app.getFraudResult().isDownloadSpikeFlag() || app.getFraudResult().isRankManipulationFlag()) {
                    fraudSpikes++;
                }
            }
        }

        double avgRisk = totalApps > 0 ? (sumRisk / totalApps) : 0.0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalApps", totalApps);
        stats.put("safeApps", safe);
        stats.put("mediumRiskApps", medium);
        stats.put("highRiskApps", high);
        stats.put("fraudSpikes", fraudSpikes);
        stats.put("averageRiskScore", Math.round(avgRisk * 100.0) / 100.0);
        stats.put("recentLogs", recentLogs);

        return ResponseEntity.ok(stats);
    }
}
