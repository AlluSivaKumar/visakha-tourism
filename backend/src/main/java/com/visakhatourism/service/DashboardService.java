package com.visakhatourism.service;

import com.visakhatourism.dto.DestinationDtos.DashboardMetricsDto;
import com.visakhatourism.dto.DestinationDtos.DestinationSummaryDto;
import com.visakhatourism.entity.Destination;
import com.visakhatourism.repository.DestinationPhotoRepository;
import com.visakhatourism.repository.DestinationRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final DestinationRepository destinationRepository;
    private final DestinationPhotoRepository photoRepository;
    private final DestinationService destinationService;

    public DashboardService(DestinationRepository destinationRepository,
                            DestinationPhotoRepository photoRepository,
                            DestinationService destinationService) {
        this.destinationRepository = destinationRepository;
        this.photoRepository = photoRepository;
        this.destinationService = destinationService;
    }

    public DashboardMetricsDto getMetrics() {
        long totalDestinations = destinationRepository.count();
        long publishedDestinations = destinationRepository.countByPublished(true);
        long unpublishedDestinations = destinationRepository.countByPublished(false);
        long totalPhotos = photoRepository.count();

        // Real group by category counts
        List<Object[]> rawCounts = destinationRepository.countDestinationsByCategory();
        Map<String, Long> categoryCounts = new LinkedHashMap<>();
        for (Object[] row : rawCounts) {
            String categoryName = (String) row[0];
            Long count = (Long) row[1];
            categoryCounts.put(categoryName, count);
        }

        // Recent 6 destinations
        List<DestinationSummaryDto> recent = destinationRepository.findTop6ByOrderByUpdatedAtDesc().stream()
                .map(this::toSummaryDto)
                .collect(Collectors.toList());

        return new DashboardMetricsDto(
                totalDestinations,
                publishedDestinations,
                unpublishedDestinations,
                totalPhotos,
                categoryCounts,
                recent
        );
    }

    private DestinationSummaryDto toSummaryDto(Destination d) {
        DestinationSummaryDto dto = new DestinationSummaryDto();
        dto.setId(d.getId());
        dto.setName(d.getName());
        dto.setSlug(d.getSlug());
        dto.setShortDescription(d.getShortDescription());
        dto.setCategoryId(d.getCategory().getId());
        dto.setCategoryName(d.getCategory().getName());
        dto.setCategorySlug(d.getCategory().getSlug());
        dto.setAddress(d.getAddress());
        dto.setLatitude(d.getLatitude());
        dto.setLongitude(d.getLongitude());
        dto.setPrimaryImageUrl(d.getPrimaryImageUrl());
        dto.setFeatured(d.isFeatured());
        dto.setPublished(d.isPublished());
        dto.setDirectionsUrl(destinationService.generateDirectionsUrl(d));
        dto.setUpdatedAt(d.getUpdatedAt());
        return dto;
    }
}
