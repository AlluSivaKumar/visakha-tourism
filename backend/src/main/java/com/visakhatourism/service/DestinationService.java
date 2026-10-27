package com.visakhatourism.service;

import com.visakhatourism.dto.DestinationDtos.*;
import com.visakhatourism.dto.PageResponse;
import com.visakhatourism.entity.Category;
import com.visakhatourism.entity.Destination;
import com.visakhatourism.entity.DestinationPhoto;
import com.visakhatourism.exception.BadRequestException;
import com.visakhatourism.exception.ResourceNotFoundException;
import com.visakhatourism.repository.CategoryRepository;
import com.visakhatourism.repository.DestinationPhotoRepository;
import com.visakhatourism.repository.DestinationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DestinationService {

    private final DestinationRepository destinationRepository;
    private final CategoryRepository categoryRepository;
    private final DestinationPhotoRepository photoRepository;

    public DestinationService(DestinationRepository destinationRepository,
                              CategoryRepository categoryRepository,
                              DestinationPhotoRepository photoRepository) {
        this.destinationRepository = destinationRepository;
        this.categoryRepository = categoryRepository;
        this.photoRepository = photoRepository;
    }

    public PageResponse<DestinationSummaryDto> getPublishedDestinations(
            Long categoryId, String search, int page, int size, String sort) {

        Sort sortOrder = resolveSortOrder(sort);
        Pageable pageable = PageRequest.of(page, size, sortOrder);

        String searchQuery = (search != null && !search.trim().isEmpty()) ? search.trim() : null;

        Page<Destination> pageResult = destinationRepository.findPublishedWithFilters(categoryId, searchQuery, pageable);
        List<DestinationSummaryDto> dtos = pageResult.getContent().stream()
                .map(this::toSummaryDto)
                .collect(Collectors.toList());

        return PageResponse.of(pageResult, dtos);
    }

    public DestinationDetailDto getDestinationBySlug(String slug) {
        Destination destination = destinationRepository.findBySlugAndPublishedTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found with slug: " + slug));

        return toDetailDto(destination);
    }

    public DestinationDetailDto getDestinationById(Long id) {
        Destination destination = destinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found with id: " + id));

        return toDetailDto(destination);
    }

    public List<DestinationSummaryDto> getFeaturedDestinations() {
        return destinationRepository.findByFeaturedTrueAndPublishedTrue().stream()
                .map(this::toSummaryDto)
                .collect(Collectors.toList());
    }

    public List<NearbyDestinationDto> getNearbyDestinations(Long destinationId, int limit) {
        Destination current = destinationRepository.findById(destinationId)
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found with id: " + destinationId));

        if (current.getLatitude() == null || current.getLongitude() == null) {
            // Fallback: return any 4 other published destinations
            return destinationRepository.findTop6ByOrderByUpdatedAtDesc().stream()
                    .filter(d -> !d.getId().equals(destinationId))
                    .limit(limit)
                    .map(d -> toNearbyDto(d, 0.0, false))
                    .collect(Collectors.toList());
        }

        double lat1 = current.getLatitude().doubleValue();
        double lon1 = current.getLongitude().doubleValue();

        List<Destination> candidates = destinationRepository.findPublishedWithCoordinatesExcluding(destinationId);

        record DestDist(Destination destination, double distanceKm) {}

        List<DestDist> sorted = new ArrayList<>();
        for (Destination d : candidates) {
            double dist = calculateHaversineDistance(
                    lat1, lon1,
                    d.getLatitude().doubleValue(),
                    d.getLongitude().doubleValue()
            );
            sorted.add(new DestDist(d, dist));
        }

        sorted.sort(Comparator.comparingDouble(DestDist::distanceKm));

        return sorted.stream()
                .limit(limit)
                .map(item -> toNearbyDto(item.destination(), item.distanceKm(), true))
                .collect(Collectors.toList());
    }

    public PageResponse<DestinationSummaryDto> getAdminDestinations(
            Boolean published, Long categoryId, String search, int page, int size, String sort) {

        Sort sortOrder = resolveSortOrder(sort);
        Pageable pageable = PageRequest.of(page, size, sortOrder);

        String searchQuery = (search != null && !search.trim().isEmpty()) ? search.trim() : null;

        Page<Destination> pageResult = destinationRepository.findAdminWithFilters(published, categoryId, searchQuery, pageable);
        List<DestinationSummaryDto> dtos = pageResult.getContent().stream()
                .map(this::toSummaryDto)
                .collect(Collectors.toList());

        return PageResponse.of(pageResult, dtos);
    }

    @Transactional
    public DestinationDetailDto createDestination(DestinationCreateUpdateDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new BadRequestException("Invalid category selected"));

        String slug = generateSlug(dto.getName());
        if (destinationRepository.findBySlug(slug).isPresent()) {
            slug = slug + "-" + System.currentTimeMillis() % 10000;
        }

        Destination dest = new Destination();
        copyDtoToEntity(dto, dest, category, slug);

        Destination saved = destinationRepository.save(dest);

        // Add primary photo entry
        if (dto.getPrimaryImageUrl() != null && !dto.getPrimaryImageUrl().isBlank()) {
            DestinationPhoto primaryPhoto = new DestinationPhoto(
                    saved, dto.getPrimaryImageUrl(), saved.getName(), saved.getName(), true, 1
            );
            photoRepository.save(primaryPhoto);
        }

        return toDetailDto(saved);
    }

    @Transactional
    public DestinationDetailDto updateDestination(Long id, DestinationCreateUpdateDto dto) {
        Destination dest = destinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found with id: " + id));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new BadRequestException("Invalid category selected"));

        copyDtoToEntity(dto, dest, category, dest.getSlug());
        Destination updated = destinationRepository.save(dest);

        return toDetailDto(updated);
    }

    @Transactional
    public void deleteDestination(Long id) {
        Destination dest = destinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found with id: " + id));

        destinationRepository.delete(dest);
    }

    @Transactional
    public DestinationSummaryDto updatePublishedStatus(Long id, boolean published) {
        Destination dest = destinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found with id: " + id));

        dest.setPublished(published);
        Destination saved = destinationRepository.save(dest);
        return toSummaryDto(saved);
    }

    // Helper methods

    private void copyDtoToEntity(DestinationCreateUpdateDto dto, Destination dest, Category category, String slug) {
        dest.setName(dto.getName().trim());
        dest.setSlug(slug);
        dest.setShortDescription(dto.getShortDescription().trim());
        dest.setFullDescription(dto.getFullDescription().trim());
        dest.setCategory(category);
        dest.setAddress(dto.getAddress().trim());
        dest.setLatitude(dto.getLatitude());
        dest.setLongitude(dto.getLongitude());
        dest.setBestTimeToVisit(dto.getBestTimeToVisit());
        dest.setOpeningHours(dto.getOpeningHours());
        dest.setTicketPrice(dto.getTicketPrice());
        dest.setTravelTips(dto.getTravelTips());
        dest.setThingsToDo(dto.getThingsToDo());
        dest.setPrimaryImageUrl(dto.getPrimaryImageUrl().trim());
        dest.setFeatured(dto.isFeatured());
        dest.setPublished(dto.isPublished());
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
        dto.setDirectionsUrl(generateDirectionsUrl(d));
        dto.setUpdatedAt(d.getUpdatedAt());
        return dto;
    }

    private DestinationDetailDto toDetailDto(Destination d) {
        DestinationDetailDto dto = new DestinationDetailDto();
        dto.setId(d.getId());
        dto.setName(d.getName());
        dto.setSlug(d.getSlug());
        dto.setShortDescription(d.getShortDescription());
        dto.setFullDescription(d.getFullDescription());
        dto.setCategory(new CategoryDto(
                d.getCategory().getId(),
                d.getCategory().getName(),
                d.getCategory().getSlug(),
                d.getCategory().getDescription(),
                d.getCategory().getIcon()
        ));
        dto.setAddress(d.getAddress());
        dto.setLatitude(d.getLatitude());
        dto.setLongitude(d.getLongitude());
        dto.setBestTimeToVisit(d.getBestTimeToVisit());
        dto.setOpeningHours(d.getOpeningHours());
        dto.setTicketPrice(d.getTicketPrice());
        dto.setTravelTips(d.getTravelTips());
        dto.setThingsToDo(d.getThingsToDo());
        dto.setPrimaryImageUrl(d.getPrimaryImageUrl());
        dto.setFeatured(d.isFeatured());
        dto.setPublished(d.isPublished());
        dto.setDirectionsUrl(generateDirectionsUrl(d));
        dto.setCreatedAt(d.getCreatedAt());
        dto.setUpdatedAt(d.getUpdatedAt());

        List<DestinationPhoto> photos = photoRepository.findByDestinationIdOrderByDisplayOrderAscIdAsc(d.getId());
        dto.setPhotos(photos.stream()
                .map(p -> new PhotoDto(p.getId(), p.getImageUrl(), p.getCaption(), p.getAltText(), p.isPrimary(), p.getDisplayOrder()))
                .collect(Collectors.toList()));

        return dto;
    }

    private NearbyDestinationDto toNearbyDto(Destination d, double distanceKm, boolean hasDistance) {
        NearbyDestinationDto dto = new NearbyDestinationDto();
        dto.setId(d.getId());
        dto.setName(d.getName());
        dto.setSlug(d.getSlug());
        dto.setShortDescription(d.getShortDescription());
        dto.setCategoryName(d.getCategory().getName());
        dto.setAddress(d.getAddress());
        dto.setLatitude(d.getLatitude());
        dto.setLongitude(d.getLongitude());
        dto.setPrimaryImageUrl(d.getPrimaryImageUrl());
        dto.setDistanceKm(distanceKm);
        dto.setDistanceFormatted(hasDistance ? String.format("%.1f km away", distanceKm) : "Regional attraction");
        dto.setDirectionsUrl(generateDirectionsUrl(d));
        return dto;
    }

    public String generateDirectionsUrl(Destination d) {
        if (d.getLatitude() != null && d.getLongitude() != null) {
            return String.format("https://www.google.com/maps/dir/?api=1&destination=%s,%s",
                    d.getLatitude(), d.getLongitude());
        } else if (d.getAddress() != null && !d.getAddress().isBlank()) {
            return "https://www.google.com/maps/search/?api=1&query=" +
                    URLEncoder.encode(d.getName() + ", " + d.getAddress(), StandardCharsets.UTF_8);
        }
        return "https://www.google.com/maps/search/?api=1&query=" +
                URLEncoder.encode(d.getName() + " Visakhapatnam", StandardCharsets.UTF_8);
    }

    /**
     * Haversine formula for spherical distance in kilometers.
     */
    public double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round((R * c) * 10.0) / 10.0;
    }

    private Sort resolveSortOrder(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.ASC, "name");
        }
        return switch (sort.toLowerCase()) {
            case "name_desc" -> Sort.by(Sort.Direction.DESC, "name");
            case "updated_desc" -> Sort.by(Sort.Direction.DESC, "updatedAt");
            case "updated_asc" -> Sort.by(Sort.Direction.ASC, "updatedAt");
            default -> Sort.by(Sort.Direction.ASC, "name");
        };
    }

    private String generateSlug(String input) {
        String nonLatin = "[^\\w\\s]";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String slug = Pattern.compile(nonLatin).matcher(normalized).replaceAll("")
                .trim().replaceAll("\\s+", "-").toLowerCase();
        return slug.isEmpty() ? "destination-" + System.currentTimeMillis() : slug;
    }
}
