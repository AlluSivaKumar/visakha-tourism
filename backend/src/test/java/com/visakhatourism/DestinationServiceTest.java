package com.visakhatourism;

import com.visakhatourism.entity.Category;
import com.visakhatourism.entity.Destination;
import com.visakhatourism.repository.CategoryRepository;
import com.visakhatourism.repository.DestinationPhotoRepository;
import com.visakhatourism.repository.DestinationRepository;
import com.visakhatourism.service.DestinationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DestinationServiceTest {

    @Mock
    private DestinationRepository destinationRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private DestinationPhotoRepository photoRepository;

    @InjectMocks
    private DestinationService destinationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHaversineDistanceCalculation() {
        // RK Beach (17.7143, 83.3236) to Rushikonda Beach (17.7825, 83.3852)
        double distance = destinationService.calculateHaversineDistance(
                17.7143, 83.3236,
                17.7825, 83.3852
        );

        // Geodesic distance is approximately 9.9 - 10.3 km
        assertTrue(distance > 8.0 && distance < 12.0, "Calculated distance should be approximately 10 km, was: " + distance);
    }

    @Test
    void testNearbyDestinationsSorting() {
        Category cat = new Category("Beaches", "beaches", "Beach desc", "umbrella");
        cat.setId(1L);

        Destination rkBeach = new Destination();
        rkBeach.setId(1L);
        rkBeach.setName("RK Beach");
        rkBeach.setCategory(cat);
        rkBeach.setLatitude(new BigDecimal("17.7143"));
        rkBeach.setLongitude(new BigDecimal("83.3236"));

        Destination rushikonda = new Destination();
        rushikonda.setId(2L);
        rushikonda.setName("Rushikonda Beach");
        rushikonda.setCategory(cat);
        rushikonda.setLatitude(new BigDecimal("17.7825"));
        rushikonda.setLongitude(new BigDecimal("83.3852")); // ~10 km away

        Destination kailasagiri = new Destination();
        kailasagiri.setId(3L);
        kailasagiri.setName("Kailasagiri");
        kailasagiri.setCategory(cat);
        kailasagiri.setLatitude(new BigDecimal("17.7490"));
        kailasagiri.setLongitude(new BigDecimal("83.3423")); // ~4.3 km away (closer than Rushikonda)

        when(destinationRepository.findById(1L)).thenReturn(Optional.of(rkBeach));
        when(destinationRepository.findPublishedWithCoordinatesExcluding(1L))
                .thenReturn(Arrays.asList(rushikonda, kailasagiri));

        var nearby = destinationService.getNearbyDestinations(1L, 2);

        assertEquals(2, nearby.size());
        // Closest should be Kailasagiri first
        assertEquals("Kailasagiri", nearby.get(0).getName());
        assertEquals("Rushikonda Beach", nearby.get(1).getName());
        assertTrue(nearby.get(0).getDistanceKm() < nearby.get(1).getDistanceKm());
    }

    @Test
    void testDirectionsUrlGeneration() {
        Destination dest = new Destination();
        dest.setName("Kailasagiri");
        dest.setLatitude(new BigDecimal("17.7490000"));
        dest.setLongitude(new BigDecimal("83.3423000"));

        String url = destinationService.generateDirectionsUrl(dest);
        assertTrue(url.contains("destination=17.7490000,83.3423000"), "URL must contain coordinates format");
        assertTrue(url.startsWith("https://www.google.com/maps/dir/?api=1&destination="));
    }
}
