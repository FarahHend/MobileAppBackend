package com.Backend.MobileApp.service;

import com.Backend.MobileApp.model.HelpRequestRequest;
import com.Backend.MobileApp.model.HelpRequestResponse;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface HelpRequestService {

    // EMPLOYEE
    HelpRequestResponse create(HelpRequestRequest request);
    List<HelpRequestResponse> getMyRequests();
    HelpRequestResponse getMyRequest(Long id) throws AccessDeniedException;
    HelpRequestResponse updateMyRequest(Long id, HelpRequestRequest request) throws AccessDeniedException;
    void deleteMyRequest(Long id) throws AccessDeniedException;

    // ADMIN
    List<HelpRequestResponse> getAll();
    HelpRequestResponse getById(Long id);
    void deleteById(Long id);
}

