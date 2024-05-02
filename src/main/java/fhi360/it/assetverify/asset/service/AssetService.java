package fhi360.it.assetverify.asset.service;

import fhi360.it.assetverify.asset.model.Asset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public interface AssetService {
    boolean isAssetAlreadyPresent(final Asset asset);
    Asset save(final Asset asset);
    void delete(final int id);
    List<Asset> getAllAssets();

    @Transactional
    Page<Asset> getAssets(Pageable pageable);

}