package com.visakhatourism.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class DestinationDtos {

    public static class CategoryDto {
        private Long id;
        private String name;
        private String slug;
        private String description;
        private String icon;

        public CategoryDto() {}

        public CategoryDto(Long id, String name, String slug, String description, String icon) {
            this.id = id;
            this.name = name;
            this.slug = slug;
            this.description = description;
            this.icon = icon;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSlug() { return slug; }
        public void setSlug(String slug) { this.slug = slug; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
    }

    public static class PhotoDto {
        private Long id;
        private String imageUrl;
        private String caption;
        private String altText;
        private boolean primary;
        private int displayOrder;

        public PhotoDto() {}

        public PhotoDto(Long id, String imageUrl, String caption, String altText, boolean primary, int displayOrder) {
            this.id = id;
            this.imageUrl = imageUrl;
            this.caption = caption;
            this.altText = altText;
            this.primary = primary;
            this.displayOrder = displayOrder;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getCaption() { return caption; }
        public void setCaption(String caption) { this.caption = caption; }
        public String getAltText() { return altText; }
        public void setAltText(String altText) { this.altText = altText; }
        public boolean isPrimary() { return primary; }
        public void setPrimary(boolean primary) { this.primary = primary; }
        public int getDisplayOrder() { return displayOrder; }
        public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }
    }

    public static class DestinationSummaryDto {
        private Long id;
        private String name;
        private String slug;
        private String shortDescription;
        private Long categoryId;
        private String categoryName;
        private String categorySlug;
        private String address;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private String primaryImageUrl;
        private boolean featured;
        private boolean published;
        private String directionsUrl;
        private LocalDateTime updatedAt;

        public DestinationSummaryDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSlug() { return slug; }
        public void setSlug(String slug) { this.slug = slug; }
        public String getShortDescription() { return shortDescription; }
        public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        public String getCategorySlug() { return categorySlug; }
        public void setCategorySlug(String categorySlug) { this.categorySlug = categorySlug; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public BigDecimal getLatitude() { return latitude; }
        public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
        public BigDecimal getLongitude() { return longitude; }
        public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
        public String getPrimaryImageUrl() { return primaryImageUrl; }
        public void setPrimaryImageUrl(String primaryImageUrl) { this.primaryImageUrl = primaryImageUrl; }
        public boolean isFeatured() { return featured; }
        public void setFeatured(boolean featured) { this.featured = featured; }
        public boolean isPublished() { return published; }
        public void setPublished(boolean published) { this.published = published; }
        public String getDirectionsUrl() { return directionsUrl; }
        public void setDirectionsUrl(String directionsUrl) { this.directionsUrl = directionsUrl; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }

    public static class DestinationDetailDto {
        private Long id;
        private String name;
        private String slug;
        private String shortDescription;
        private String fullDescription;
        private CategoryDto category;
        private String address;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private String bestTimeToVisit;
        private String openingHours;
        private String ticketPrice;
        private String travelTips;
        private String thingsToDo;
        private String primaryImageUrl;
        private boolean featured;
        private boolean published;
        private String directionsUrl;
        private List<PhotoDto> photos;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public DestinationDetailDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSlug() { return slug; }
        public void setSlug(String slug) { this.slug = slug; }
        public String getShortDescription() { return shortDescription; }
        public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
        public String getFullDescription() { return fullDescription; }
        public void setFullDescription(String fullDescription) { this.fullDescription = fullDescription; }
        public CategoryDto getCategory() { return category; }
        public void setCategory(CategoryDto category) { this.category = category; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public BigDecimal getLatitude() { return latitude; }
        public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
        public BigDecimal getLongitude() { return longitude; }
        public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
        public String getBestTimeToVisit() { return bestTimeToVisit; }
        public void setBestTimeToVisit(String bestTimeToVisit) { this.bestTimeToVisit = bestTimeToVisit; }
        public String getOpeningHours() { return openingHours; }
        public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }
        public String getTicketPrice() { return ticketPrice; }
        public void setTicketPrice(String ticketPrice) { this.ticketPrice = ticketPrice; }
        public String getTravelTips() { return travelTips; }
        public void setTravelTips(String travelTips) { this.travelTips = travelTips; }
        public String getThingsToDo() { return thingsToDo; }
        public void setThingsToDo(String thingsToDo) { this.thingsToDo = thingsToDo; }
        public String getPrimaryImageUrl() { return primaryImageUrl; }
        public void setPrimaryImageUrl(String primaryImageUrl) { this.primaryImageUrl = primaryImageUrl; }
        public boolean isFeatured() { return featured; }
        public void setFeatured(boolean featured) { this.featured = featured; }
        public boolean isPublished() { return published; }
        public void setPublished(boolean published) { this.published = published; }
        public String getDirectionsUrl() { return directionsUrl; }
        public void setDirectionsUrl(String directionsUrl) { this.directionsUrl = directionsUrl; }
        public List<PhotoDto> getPhotos() { return photos; }
        public void setPhotos(List<PhotoDto> photos) { this.photos = photos; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }

    public static class DestinationCreateUpdateDto {
        @NotBlank(message = "Destination name is required")
        @Size(max = 150, message = "Name must not exceed 150 characters")
        private String name;

        @NotBlank(message = "Short description is required")
        @Size(max = 500, message = "Short description must not exceed 500 characters")
        private String shortDescription;

        @NotBlank(message = "Full description is required")
        private String fullDescription;

        @NotNull(message = "Category is required")
        private Long categoryId;

        @NotBlank(message = "Address is required")
        private String address;

        private BigDecimal latitude;
        private BigDecimal longitude;
        private String bestTimeToVisit;
        private String openingHours;
        private String ticketPrice;
        private String travelTips;
        private String thingsToDo;

        @NotBlank(message = "Primary image URL or upload is required")
        private String primaryImageUrl;

        private boolean featured = false;
        private boolean published = true;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getShortDescription() { return shortDescription; }
        public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
        public String getFullDescription() { return fullDescription; }
        public void setFullDescription(String fullDescription) { this.fullDescription = fullDescription; }
        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public BigDecimal getLatitude() { return latitude; }
        public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
        public BigDecimal getLongitude() { return longitude; }
        public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
        public String getBestTimeToVisit() { return bestTimeToVisit; }
        public void setBestTimeToVisit(String bestTimeToVisit) { this.bestTimeToVisit = bestTimeToVisit; }
        public String getOpeningHours() { return openingHours; }
        public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }
        public String getTicketPrice() { return ticketPrice; }
        public void setTicketPrice(String ticketPrice) { this.ticketPrice = ticketPrice; }
        public String getTravelTips() { return travelTips; }
        public void setTravelTips(String travelTips) { this.travelTips = travelTips; }
        public String getThingsToDo() { return thingsToDo; }
        public void setThingsToDo(String thingsToDo) { this.thingsToDo = thingsToDo; }
        public String getPrimaryImageUrl() { return primaryImageUrl; }
        public void setPrimaryImageUrl(String primaryImageUrl) { this.primaryImageUrl = primaryImageUrl; }
        public boolean isFeatured() { return featured; }
        public void setFeatured(boolean featured) { this.featured = featured; }
        public boolean isPublished() { return published; }
        public void setPublished(boolean published) { this.published = published; }
    }

    public static class DestinationStatusDto {
        private boolean published;
        public boolean isPublished() { return published; }
        public void setPublished(boolean published) { this.published = published; }
    }

    public static class NearbyDestinationDto {
        private Long id;
        private String name;
        private String slug;
        private String shortDescription;
        private String categoryName;
        private String address;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private String primaryImageUrl;
        private double distanceKm;
        private String distanceFormatted;
        private String directionsUrl;

        public NearbyDestinationDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSlug() { return slug; }
        public void setSlug(String slug) { this.slug = slug; }
        public String getShortDescription() { return shortDescription; }
        public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public BigDecimal getLatitude() { return latitude; }
        public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
        public BigDecimal getLongitude() { return longitude; }
        public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
        public String getPrimaryImageUrl() { return primaryImageUrl; }
        public void setPrimaryImageUrl(String primaryImageUrl) { this.primaryImageUrl = primaryImageUrl; }
        public double getDistanceKm() { return distanceKm; }
        public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }
        public String getDistanceFormatted() { return distanceFormatted; }
        public void setDistanceFormatted(String distanceFormatted) { this.distanceFormatted = distanceFormatted; }
        public String getDirectionsUrl() { return directionsUrl; }
        public void setDirectionsUrl(String directionsUrl) { this.directionsUrl = directionsUrl; }
    }

    public static class DashboardMetricsDto {
        private long totalDestinations;
        private long publishedDestinations;
        private long unpublishedDestinations;
        private long totalPhotos;
        private Map<String, Long> categoryCounts;
        private List<DestinationSummaryDto> recentDestinations;

        public DashboardMetricsDto() {}

        public DashboardMetricsDto(long totalDestinations, long publishedDestinations, long unpublishedDestinations,
                                   long totalPhotos, Map<String, Long> categoryCounts, List<DestinationSummaryDto> recentDestinations) {
            this.totalDestinations = totalDestinations;
            this.publishedDestinations = publishedDestinations;
            this.unpublishedDestinations = unpublishedDestinations;
            this.totalPhotos = totalPhotos;
            this.categoryCounts = categoryCounts;
            this.recentDestinations = recentDestinations;
        }

        public long getTotalDestinations() { return totalDestinations; }
        public void setTotalDestinations(long totalDestinations) { this.totalDestinations = totalDestinations; }
        public long getPublishedDestinations() { return publishedDestinations; }
        public void setPublishedDestinations(long publishedDestinations) { this.publishedDestinations = publishedDestinations; }
        public long getUnpublishedDestinations() { return unpublishedDestinations; }
        public void setUnpublishedDestinations(long unpublishedDestinations) { this.unpublishedDestinations = unpublishedDestinations; }
        public long getTotalPhotos() { return totalPhotos; }
        public void setTotalPhotos(long totalPhotos) { this.totalPhotos = totalPhotos; }
        public Map<String, Long> getCategoryCounts() { return categoryCounts; }
        public void setCategoryCounts(Map<String, Long> categoryCounts) { this.categoryCounts = categoryCounts; }
        public List<DestinationSummaryDto> getRecentDestinations() { return recentDestinations; }
        public void setRecentDestinations(List<DestinationSummaryDto> recentDestinations) { this.recentDestinations = recentDestinations; }
    }
}
