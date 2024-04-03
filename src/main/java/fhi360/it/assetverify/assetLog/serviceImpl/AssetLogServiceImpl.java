package fhi360.it.assetverify.assetLog.serviceImpl;

import fhi360.it.assetverify.asset.model.Asset;
import fhi360.it.assetverify.assetLog.model.AssetLog;
import fhi360.it.assetverify.assetLog.repository.AssetLogRepository;

import fhi360.it.assetverify.assetLog.service.AssetLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AssetLogServiceImpl implements AssetLogService {
    private final AssetLogRepository assetLogRepository;
    @Override
    public AssetLog addAssetLog(AssetLog assetLog) {
        return assetLogRepository.save(assetLog);
    }
    @Override
    public List<AssetLog> getAllAssetLogs() {
        return assetLogRepository.findAll();
    }
    @Override
    public Page<AssetLog> searchByDate(String startDate, String endDate, Pageable pageable) {
        return assetLogRepository.findByDateBetween(startDate, endDate, pageable);
    }


    public ResponseEntity<Page<AssetLog>> getAssetsByState(String state, Pageable pageable) {
        Page<AssetLog> assets = assetLogRepository.findByStates(state, pageable);
        if (assets.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(assets, HttpStatus.OK);
    }
}
