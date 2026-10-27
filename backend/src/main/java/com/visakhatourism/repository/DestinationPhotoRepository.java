package com.visakhatourism.repository;

import com.visakhatourism.entity.DestinationPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DestinationPhotoRepository extends JpaRepository<DestinationPhoto, Long> {
    List<DestinationPhoto> findByDestinationIdOrderByDisplayOrderAscIdAsc(Long destinationId);
    List<DestinationPhoto> findByDestinationId(Long destinationId);
}
