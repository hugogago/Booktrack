package es.colegiocalasanz.booktrack.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.colegiocalasanz.booktrack.dto.AnnualGoalRequest;
import es.colegiocalasanz.booktrack.dto.AnnualGoalResponse;
import es.colegiocalasanz.booktrack.dto.DashboardSummaryResponse;
import es.colegiocalasanz.booktrack.dto.StatisticsResponse;
import es.colegiocalasanz.booktrack.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummaryResponse getSummary(Authentication authentication) {
        return dashboardService.getSummary(authentication.getName());
    }

    @GetMapping("/statistics")
    public StatisticsResponse getStatistics(Authentication authentication) {
        return dashboardService.getStatistics(authentication.getName());
    }

    @PutMapping("/goal")
    public AnnualGoalResponse updateAnnualGoal(Authentication authentication, @RequestBody AnnualGoalRequest request) {
        return dashboardService.updateAnnualGoal(authentication.getName(), request.getAnnualGoal());
    }
}