package com.company.slaptop.controller;

import com.company.slaptop.dto.ComponentFailureResponse;
import com.company.slaptop.service.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final IncidentService incidentService;

    @GetMapping("/component-failures")
    public List<ComponentFailureResponse> componentFailures() {
        return incidentService.failureReport();
    }
}
