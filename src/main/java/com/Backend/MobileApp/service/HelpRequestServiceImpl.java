package com.Backend.MobileApp.service;

import com.Backend.MobileApp.model.HelpRequest;
import com.Backend.MobileApp.model.HelpRequestRequest;
import com.Backend.MobileApp.model.HelpRequestResponse;
import com.Backend.MobileApp.model.User;
import com.Backend.MobileApp.repository.HelpRequestRepository;
import com.Backend.MobileApp.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class HelpRequestServiceImpl implements HelpRequestService {

    private final HelpRequestRepository helpRequestRepository;

    public HelpRequestServiceImpl(HelpRequestRepository helpRequestRepository) {
        this.helpRequestRepository = helpRequestRepository;
    }

    // ===== UTIL =====
    private HelpRequestResponse mapToResponse(HelpRequest hr) {
        HelpRequestResponse dto = new HelpRequestResponse();
        dto.setId(hr.getId());
        dto.setTitle(hr.getTitle());
        dto.setDescription(hr.getDescription());
        dto.setCreatedAt(hr.getCreatedAt());
        dto.setUpdatedAt(hr.getUpdatedAt());
        dto.setUserId(hr.getUser().getId());
        dto.setUsername(hr.getUser().getUsername());
        return dto;
    }

    // ===== EMPLOYEE =====

    @Override
    public HelpRequestResponse create(HelpRequestRequest request) {
        User user = SecurityUtils.getCurrentUser();

        HelpRequest hr = new HelpRequest();
        hr.setTitle(request.getTitle());
        hr.setDescription(request.getDescription());
        hr.setUser(user);

        return mapToResponse(helpRequestRepository.save(hr));
    }

    @Override
    public List<HelpRequestResponse> getMyRequests() {
        User user = SecurityUtils.getCurrentUser();
        return helpRequestRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public HelpRequestResponse getMyRequest(Long id) throws AccessDeniedException {
        User user = SecurityUtils.getCurrentUser();

        HelpRequest hr = helpRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Help request not found"));

        if (!hr.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        return mapToResponse(hr);
    }

    @Override
    public HelpRequestResponse updateMyRequest(Long id, HelpRequestRequest request) throws AccessDeniedException {
        User user = SecurityUtils.getCurrentUser();

        HelpRequest hr = helpRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Help request not found"));

        if (!hr.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        hr.setTitle(request.getTitle());
        hr.setDescription(request.getDescription());
        hr.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(helpRequestRepository.save(hr));
    }

    @Override
    public void deleteMyRequest(Long id) throws AccessDeniedException {
        User user = SecurityUtils.getCurrentUser();

        HelpRequest hr = helpRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Help request not found"));

        if (!hr.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        helpRequestRepository.delete(hr);
    }

    // ===== ADMIN =====

    @Override
    public List<HelpRequestResponse> getAll() {
        return helpRequestRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public HelpRequestResponse getById(Long id) {
        return helpRequestRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new EntityNotFoundException("Help request not found"));
    }

    @Override
    public void deleteById(Long id) {
        helpRequestRepository.deleteById(id);
    }
}
