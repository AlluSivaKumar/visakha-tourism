package com.visakhatourism.controller;

import com.visakhatourism.dto.ApiResponse;
import com.visakhatourism.dto.ItineraryDtos.ItineraryPlanDto;
import com.visakhatourism.service.ItineraryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itineraries")
public class ItineraryController {

    private final ItineraryService itineraryService;

    public ItineraryController(ItineraryService itineraryService) {
        this.itineraryService = itineraryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ItineraryPlanDto>>> getAllItineraries() {
        List<ItineraryPlanDto> plans = itineraryService.getAllItineraries();
        return ResponseEntity.ok(ApiResponse.success("Curated travel plans retrieved successfully", plans));
    }

    @GetMapping("/{days}")
    public ResponseEntity<ApiResponse<ItineraryPlanDto>> getItineraryByDays(@PathVariable int days) {
        ItineraryPlanDto plan = itineraryService.getItineraryByDays(days);
        return ResponseEntity.ok(ApiResponse.success(days + "-Day travel itinerary retrieved", plan));
    }
}
