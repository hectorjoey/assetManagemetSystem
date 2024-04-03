package fhi360.it.assetverify.assetLog.service;

import fhi360.it.assetverify.assetLog.model.AssetLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AssetLogService {
    AssetLog addAssetLog(AssetLog assetLog);
    List<AssetLog> getAllAssetLogs();
    Page<AssetLog> searchByDate(String startDate, String endDate, Pageable pageable);
}
