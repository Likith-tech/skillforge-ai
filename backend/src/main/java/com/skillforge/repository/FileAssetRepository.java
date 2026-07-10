package com.skillforge.repository;

import com.skillforge.model.FileAsset;
import com.skillforge.model.FileAssetType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileAssetRepository extends JpaRepository<FileAsset, Long> {

    Optional<FileAsset> findByOwnerIdAndAssetType(Long ownerId, FileAssetType assetType);

    List<FileAsset> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
}