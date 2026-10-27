package com.visakhatourism.repository;

import com.visakhatourism.entity.Destination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, Long> {

    Optional<Destination> findBySlug(String slug);

    Optional<Destination> findBySlugAndPublishedTrue(String slug);

    List<Destination> findByFeaturedTrueAndPublishedTrue();

    long countByPublished(boolean published);

    @Query("SELECT d.category.name, COUNT(d) FROM Destination d GROUP BY d.category.name")
    List<Object[]> countDestinationsByCategory();

    List<Destination> findTop6ByOrderByUpdatedAtDesc();

    // Public search with pagination
    @Query("SELECT d FROM Destination d WHERE d.published = true " +
           "AND (:categoryId IS NULL OR d.category.id = :categoryId) " +
           "AND (:query IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(d.shortDescription) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(d.address) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Destination> findPublishedWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("query") String query,
            Pageable pageable);

    // Admin search with pagination (all statuses)
    @Query("SELECT d FROM Destination d WHERE " +
           "(:published IS NULL OR d.published = :published) " +
           "AND (:categoryId IS NULL OR d.category.id = :categoryId) " +
           "AND (:query IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(d.address) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Destination> findAdminWithFilters(
            @Param("published") Boolean published,
            @Param("categoryId") Long categoryId,
            @Param("query") String query,
            Pageable pageable);

    // Fetch published destinations with coordinates for nearby calculation
    @Query("SELECT d FROM Destination d WHERE d.published = true AND d.latitude IS NOT NULL AND d.longitude IS NOT NULL AND d.id <> :excludeId")
    List<Destination> findPublishedWithCoordinatesExcluding(@Param("excludeId") Long excludeId);
}
