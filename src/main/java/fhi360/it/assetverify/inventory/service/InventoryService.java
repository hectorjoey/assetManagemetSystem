package fhi360.it.assetverify.inventory.service;

import fhi360.it.assetverify.inventory.dto.InventoryDto;
import fhi360.it.assetverify.inventory.model.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InventoryService {
//    Inventory createInventory(InventoryDto inventoryDto);
    List<Inventory> getInventories();
    Page<Inventory> getInventories(Pageable pageable);
    Page<Inventory> searchByDate(String startDate, String endDate, Pageable pageable);
    List<Inventory> findByDateReceivedBetween(String startDate, String endDate);
    Page<Inventory> findByDateReceivedBetween(String startDate, String endDate, Pageable pageable);
    Page<Inventory> findAll(Pageable pageable);
    InventoryDto createInventory(InventoryDto inventoryDTO);

//    Inventory findByDescription(String description);
}