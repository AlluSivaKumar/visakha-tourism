package com.visakhatourism.controller;

import com.visakhatourism.dto.ApiResponse;
import com.visakhatourism.dto.DestinationDtos.*;
import com.visakhatourism.dto.PageResponse;
import com.visakhatourism.service.DestinationService;
import com.visakhatourism.service.PhotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/destinations")
public class DestinationController {

    private final DestinationService destinationService;
    private final PhotoService photoService;

    public DestinationController(DestinationService destinationService, PhotoService photoService) {
        this.destinationService = destinationService;
        this.photoService = photoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<DestinationSummaryDto>>> getDestinations(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort) {

        PageResponse<DestinationSummaryDto> response = destinationService.getPublishedDestinations(
                categoryId, search, page, size, sort);
        return ResponseEntity.ok(ApiResponse.success("Destinations fetched successfully", response));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<DestinationDetailDto>> getDestinationBySlug(@PathVariable String slug) {
        DestinationDetailDto detail = destinationService.getDestinationBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success("Destination details retrieved", detail));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<DestinationSummaryDto>>> getFeaturedDestinations() {
        List<DestinationSummaryDto> featured = destinationService.getFeaturedDestinations();
        return ResponseEntity.ok(ApiResponse.success("Featured destinations retrieved", featured));
    }

    @GetMapping("/{id}/photos")
    public ResponseEntity<ApiResponse<List<PhotoDto>>> getDestinationPhotos(@PathVariable Long id) {
        List<PhotoDto> photos = photoService.getPhotosByDestination(id);
        return ResponseEntity.ok(ApiResponse.success("Destination photos retrieved", photos));
    }

    @GetMapping("/{id}/nearby")
    public ResponseEntity<ApiResponse<List<NearbyDestinationDto>>> getNearbyDestinations(
            @PathVariable Long id,
            @RequestParam(defaultValue = "4") int limit) {
        List<NearbyDestinationDto> nearby = destinationService.getNearbyDestinations(id, limit);
        return ResponseEntity.ok(ApiResponse.success("Nearby destinations retrieved", nearby));
    }
}
