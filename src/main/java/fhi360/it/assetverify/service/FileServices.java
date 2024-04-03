package fhi360.it.assetverify.service;

import fhi360.it.assetverify.asset.model.Asset;
import fhi360.it.assetverify.inventory.model.Inventory;
import fhi360.it.assetverify.model.Item;
import fhi360.it.assetverify.asset.repository.AssetRepository;
import fhi360.it.assetverify.inventory.repository.InventoryRepository;
import fhi360.it.assetverify.repository.ItemRepository;
import fhi360.it.assetverify.util.ExcelUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServices {
    public static String TYPE;
    private final AssetRepository assetRepository;
    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;

    public void save(final MultipartFile files) {
        try {
            log.debug("FileStore::{}", files.getOriginalFilename());
            final List<Asset> lstAsset = ExcelUtils.parseExcelAssetFile(files.getInputStream());
            this.assetRepository.saveAll(lstAsset);
        } catch (IOException e) {
            log.error("Message:::{}", e.getMessage());
            throw new RuntimeException("FAIL! -> message = " + e.getMessage());
        }
    }

     public void saveInventories(final MultipartFile files) {
         try {
             log.debug("FileStore::{}", files.getOriginalFilename());
             final List<Inventory> lstInventories = ExcelUtils.parseExcelInventoryFile(files.getInputStream());
             this.inventoryRepository.saveAll(lstInventories);
         } catch (IOException e) {
             log.error("Message:::{}", e.getMessage());
             throw new RuntimeException("FAIL! -> message = " + e.getMessage());
         }
     }
}
