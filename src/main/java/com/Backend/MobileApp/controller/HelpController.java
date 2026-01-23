package com.Backend.MobileApp.controller;

import com.Backend.MobileApp.model.HelpRequestRequest;
import com.Backend.MobileApp.model.HelpRequestResponse;
import com.Backend.MobileApp.service.HelpRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/help")
public class HelpController {

    private final HelpRequestService helpRequestService;

    public HelpController(HelpRequestService helpRequestService) {
        this.helpRequestService = helpRequestService;
    }

    // ===================== EMPLOYEE =====================

    @PostMapping("/employee")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<HelpRequestResponse> create(
            @RequestBody HelpRequestRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(helpRequestService.create(request));
    }

    @GetMapping("/employee")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<HelpRequestResponse>> getMyRequests() {
        return ResponseEntity.ok(helpRequestService.getMyRequests());
    }

    @GetMapping("/employee/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<HelpRequestResponse> getMyRequest(
            @PathVariable Long id) throws AccessDeniedException {

        return ResponseEntity.ok(helpRequestService.getMyRequest(id));
    }

    @PutMapping("/employee/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<HelpRequestResponse> updateMyRequest(
            @PathVariable Long id,
            @RequestBody HelpRequestRequest request) throws AccessDeniedException {

        return ResponseEntity.ok(helpRequestService.updateMyRequest(id, request));
    }

    @DeleteMapping("/employee/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Void> deleteMyRequest(
            @PathVariable Long id) throws AccessDeniedException {

        helpRequestService.deleteMyRequest(id);
        return ResponseEntity.noContent().build();
    }

    // ===================== ADMIN =====================

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<HelpRequestResponse>> getAll() {
        return ResponseEntity.ok(helpRequestService.getAll());
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HelpRequestResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(helpRequestService.getById(id));
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        helpRequestService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
