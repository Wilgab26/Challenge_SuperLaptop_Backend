package com.company.slaptop.controller;

import com.company.slaptop.dto.LaptopRequest;
import com.company.slaptop.dto.LaptopResponse;
import com.company.slaptop.service.LaptopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/laptops")
@RequiredArgsConstructor
public class LaptopController {

    private final LaptopService laptopService;

    @PostMapping
    public ResponseEntity<LaptopResponse> create(@Valid @RequestBody LaptopRequest request) {
        LaptopResponse response = laptopService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public List<LaptopResponse> findAll() {
        return laptopService.findAll();
    }

    @GetMapping("/{id}")
    public LaptopResponse findById(@PathVariable Long id) {
        return laptopService.findById(id);
    }
}
