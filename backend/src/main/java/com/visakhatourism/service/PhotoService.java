package com.visakhatourism.service;

import com.visakhatourism.dto.DestinationDtos.PhotoDto;
import com.visakhatourism.entity.Destination;
import com.visakhatourism.entity.DestinationPhoto;
import com.visakhatourism.exception.BadRequestException;
import com.visakhatourism.exception.ResourceNotFoundException;
import com.visakhatourism.repository.DestinationPhotoRepository;
import com.visakhatourism.repository.DestinationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PhotoService {

    private final DestinationPhotoRepository photoRepository;
    private final DestinationRepository destinationRepository;

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "webp");
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/webp"
    );

    public PhotoService(DestinationPhotoRepository photoRepository, DestinationRepository destinationRepository) {
        this.photoRepository = photoRepository;
        this.destinationRepository = destinationRepository;
    }

    public List<PhotoDto> getPhotosByDestination(Long destinationId) {
        return photoRepository.findByDestinationIdOrderByDisplayOrderAscIdAsc(destinationId).stream()
                .map(p -> new PhotoDto(p.getId(), p.getImageUrl(), p.getCaption(), p.getAltText(), p.isPrimary(), p.getDisplayOrder()))
                .collect(Collectors.toList());
    }

    @Transactional
    public PhotoDto uploadPhoto(Long destinationId, MultipartFile file, String caption, String altText, boolean isPrimary) {
        Destination destination = destinationRepository.findById(destinationId)
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found with id: " + destinationId));

        if (file.isEmpty()) {
            throw new BadRequestException("Uploaded file is empty");
        }

        // Validate content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Only JPEG, PNG, and WebP image formats are permitted");
        }

        // Validate extension
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        }
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException("Invalid file extension. Only jpg, jpeg, png, webp allowed");
        }

        try {
            Path targetDir = Paths.get(uploadDir, "destinations").toAbsolutePath().normalize();
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            String filename = UUID.randomUUID().toString() + "." + extension;
            Path targetLocation = targetDir.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = "/uploads/destinations/" + filename;

            List<DestinationPhoto> existingPhotos = photoRepository.findByDestinationId(destinationId);
            int displayOrder = existingPhotos.size() + 1;

            if (isPrimary || existingPhotos.isEmpty()) {
                clearPrimaryFlag(existingPhotos);
                destination.setPrimaryImageUrl(imageUrl);
                destinationRepository.save(destination);
                isPrimary = true;
            }

            DestinationPhoto photo = new DestinationPhoto(
                    destination,
                    imageUrl,
                    caption != null ? caption.trim() : destination.getName(),
                    altText != null ? altText.trim() : destination.getName(),
                    isPrimary,
                    displayOrder
            );

            DestinationPhoto saved = photoRepository.save(photo);
            return new PhotoDto(saved.getId(), saved.getImageUrl(), saved.getCaption(), saved.getAltText(), saved.isPrimary(), saved.getDisplayOrder());

        } catch (IOException ex) {
            throw new BadRequestException("Could not store image file: " + ex.getMessage());
        }
    }

    @Transactional
    public PhotoDto addPhotoByUrl(Long destinationId, String imageUrl, String caption, String altText, boolean isPrimary) {
        Destination destination = destinationRepository.findById(destinationId)
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found with id: " + destinationId));

        if (imageUrl == null || imageUrl.isBlank()) {
            throw new BadRequestException("Image URL is required");
        }

        List<DestinationPhoto> existingPhotos = photoRepository.findByDestinationId(destinationId);
        int displayOrder = existingPhotos.size() + 1;

        if (isPrimary || existingPhotos.isEmpty()) {
            clearPrimaryFlag(existingPhotos);
            destination.setPrimaryImageUrl(imageUrl.trim());
            destinationRepository.save(destination);
            isPrimary = true;
        }

        DestinationPhoto photo = new DestinationPhoto(
                destination,
                imageUrl.trim(),
                caption != null ? caption.trim() : destination.getName(),
                altText != null ? altText.trim() : destination.getName(),
                isPrimary,
                displayOrder
        );

        DestinationPhoto saved = photoRepository.save(photo);
        return new PhotoDto(saved.getId(), saved.getImageUrl(), saved.getCaption(), saved.getAltText(), saved.isPrimary(), saved.getDisplayOrder());
    }

    @Transactional
    public void deletePhoto(Long photoId) {
        DestinationPhoto photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with id: " + photoId));

        // Delete physical file if it was a local upload
        if (photo.getImageUrl() != null && photo.getImageUrl().startsWith("/uploads/destinations/")) {
            String filename = photo.getImageUrl().replace("/uploads/destinations/", "");
            Path filePath = Paths.get(uploadDir, "destinations", filename).toAbsolutePath().normalize();
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException ignored) {}
        }

        Destination destination = photo.getDestination();
        boolean wasPrimary = photo.isPrimary();

        photoRepository.delete(photo);

        // If primary photo was deleted, make the next photo primary if one exists
        if (wasPrimary) {
            List<DestinationPhoto> remaining = photoRepository.findByDestinationIdOrderByDisplayOrderAscIdAsc(destination.getId());
            if (!remaining.isEmpty()) {
                DestinationPhoto nextPrimary = remaining.get(0);
                nextPrimary.setPrimary(true);
                photoRepository.save(nextPrimary);
                destination.setPrimaryImageUrl(nextPrimary.getImageUrl());
                destinationRepository.save(destination);
            }
        }
    }

    @Transactional
    public PhotoDto setPrimaryPhoto(Long photoId) {
        DestinationPhoto photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with id: " + photoId));

        Destination destination = photo.getDestination();
        List<DestinationPhoto> photos = photoRepository.findByDestinationId(destination.getId());

        clearPrimaryFlag(photos);
        photo.setPrimary(true);
        photoRepository.save(photo);

        destination.setPrimaryImageUrl(photo.getImageUrl());
        destinationRepository.save(destination);

        return new PhotoDto(photo.getId(), photo.getImageUrl(), photo.getCaption(), photo.getAltText(), photo.isPrimary(), photo.getDisplayOrder());
    }

    private void clearPrimaryFlag(List<DestinationPhoto> photos) {
        for (DestinationPhoto p : photos) {
            if (p.isPrimary()) {
                p.setPrimary(false);
                photoRepository.save(p);
            }
        }
    }
}
