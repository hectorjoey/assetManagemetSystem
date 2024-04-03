package fhi360.it.assetverify.asset.repository;

import fhi360.it.assetverify.asset.model.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    @Query("SELECT a FROM Asset a WHERE " +
            "a.assetId LIKE CONCAT('%',:query, '%')" +
            "Or a.emailAddress LIKE CONCAT('%', :query, '%')" +
            "Or a.serialNumber LIKE CONCAT('%', :query, '%')" +
            "Or a.states LIKE CONCAT('%', :query, '%')")
    List<Asset> searchAssets(String query);
}
