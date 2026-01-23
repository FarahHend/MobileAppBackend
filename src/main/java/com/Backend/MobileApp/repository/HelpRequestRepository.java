package com.Backend.MobileApp.repository;

import com.Backend.MobileApp.model.HelpRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HelpRequestRepository extends JpaRepository<HelpRequest, Long> {

    List<HelpRequest> findByUserId(Long userId);
}
