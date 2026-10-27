package com.visakhatourism.controller;

import com.visakhatourism.dto.ApiResponse;
import com.visakhatourism.dto.DestinationDtos.*;
import com.visakhatourism.dto.PageResponse;
import com.visakhatourism.service.DashboardService;
import com.visakhatourism.service.DestinationService;
import com.visakhatourism.service.PhotoService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final DashboardService dashboardService;
    private final DestinationService destinationService;
    private final PhotoService photoService;

    public AdminController(DashboardService dashboardService,
                           DestinationService destinationService,
                           PhotoService photoService) {
        this.dashboardService = dashboardService;
        this.destinationService = destinationService;
        this.photoService = photoService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardMetricsDto>> getDashboardMetrics() {
        DashboardMetricsDto metrics = dashboardService.getMetrics();
        return ResponseEntity.ok(ApiResponse.success("Dashboard metrics loaded successfully", metrics));
    }

    @GetMapping("/destinations")
    public ResponseEntity<ApiResponse<PageResponse<DestinationSummaryDto>>> getAdminDestinations(
            @RequestParam(required = false) Boolean published,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "updated_desc") String sort) {

        PageResponse<DestinationSummaryDto> response = destinationService.getAdminDestinations(
                published, categoryId, search, page, size, sort);
        return ResponseEntity.ok(ApiResponse.success("Admin destinations fetched successfully", response));
    }

    @GetMapping("/destinations/{id}")
    public ResponseEntity<ApiResponse<DestinationDetailDto>> getDestinationById(@PathVariable Long id) {
        DestinationDetailDto detail = destinationService.getDestinationById(id);
        return ResponseEntity.ok(ApiResponse.success("Destination details retrieved", detail));
    }

    @PostMapping("/destinations")
    public ResponseEntity<ApiResponse<DestinationDetailDto>> createDestination(
            @Valid @RequestBody DestinationCreateUpdateDto dto) {
        DestinationDetailDto created = destinationService.createDestination(dto);
        return ResponseEntity.ok(ApiResponse.success("Destination created successfully!", created));
    }

    @PutMapping("/destinations/{id}")
    public ResponseEntity<ApiResponse<DestinationDetailDto>> updateDestination(
            @PathVariable Long id,
            @Valid @RequestBody DestinationCreateUpdateDto dto) {
        DestinationDetailDto updated = destinationService.updateDestination(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Destination updated successfully!", updated));
    }

    @DeleteMapping("/destinations/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDestination(@PathVariable Long id) {
        destinationService.deleteDestination(id);
        return ResponseEntity.ok(ApiResponse.success("Destination deleted successfully"));
    }

    @PatchMapping("/destinations/{id}/status")
    public ResponseEntity<ApiResponse<DestinationSummaryDto>> updateStatus(
            @PathVariable Long id,
            @RequestBody DestinationStatusDto statusDto) {
        DestinationSummaryDto updated = destinationService.updatePublishedStatus(id, statusDto.isPublished());
        return ResponseEntity.ok(ApiResponse.success("Destination publication status updated", updated));
    }

    @PostMapping(value = "/destinations/{id}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PhotoDto>> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "caption", required = false) String caption,
            @RequestParam(value = "altText", required = false) String altText,
            @RequestParam(value = "isPrimary", defaultValue = "false") boolean isPrimary) {

        PhotoDto photo = photoService.uploadPhoto(id, file, caption, altText, isPrimary);
        return ResponseEntity.ok(ApiResponse.success("Photo uploaded successfully!", photo));
    }

    public static class AddPhotoUrlRequest {
        private String imageUrl;
        private String caption;
        private String altText;
        private boolean isPrimary;
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getCaption() { return caption; }
        public void setCaption(String caption) { this.caption = caption; }
        public String getAltText() { return altText; }
        public void setAltText(String altText) { this.altText = altText; }
        public boolean isPrimary() { return isPrimary; }
        public void setPrimary(boolean isPrimary) { this.isPrimary = isPrimary; }
    }

    @PostMapping("/destinations/{id}/photos/url")
    public ResponseEntity<ApiResponse<PhotoDto>> addPhotoByUrl(
            @PathVariable Long id,
            @RequestBody AddPhotoUrlRequest request) {
        PhotoDto photo = photoService.addPhotoByUrl(id, request.getImageUrl(), request.getCaption(), request.getAltText(), request.isPrimary());
        return ResponseEntity.ok(ApiResponse.success("Photo added successfully!", photo));
    }

    @DeleteMapping("/photos/{photoId}")
    public ResponseEntity<ApiResponse<Void>> deletePhoto(@PathVariable Long photoId) {
        photoService.deletePhoto(photoId);
        return ResponseEntity.ok(ApiResponse.success("Photo deleted successfully"));
    }

    @PatchMapping("/photos/{photoId}/primary")
    public ResponseEntity<ApiResponse<PhotoDto>> setPrimaryPhoto(@PathVariable Long photoId) {
        PhotoDto photo = photoService.setPrimaryPhoto(photoId);
        return ResponseEntity.ok(ApiResponse.success("Primary photo updated successfully", photo));
    }
}
