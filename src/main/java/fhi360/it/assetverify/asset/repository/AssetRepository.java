package fhi360.it.assetverify.asset.repository;

import fhi360.it.assetverify.asset.model.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    Asset findByAssetId(String assetId);

    Asset findBySerialNumber(String serialNumber);
    Page<Asset> findByOrderById(Pageable pageable);

    Page<Asset> findByOrderByIdAsc(Pageable pageable);

    Page<Asset> findByStates(String state, Pageable pageable);

    List<Asset> findByStates(String state);

    @Query("Select a from Asset a where lower (a.assetId) =:keyword OR lower (a.emailAddress) =:keyword OR lower (a.phone) =:keyword OR lower(a.serialNumber)=:keyword ")
    Page<Asset> findAll(final Pageable pageable, @Param("keyword") final String keyword);

    @Query("SELECT a FROM Asset a WHERE " +
            "a.serialNumber LIKE CONCAT('%', :query, '%') " +  // Add space after '%'
            "OR a.assetId LIKE CONCAT('%', :query, '%')")      // Add space after '%'
    Page<Asset> searchAsset(String query, Pageable pageable);

}
