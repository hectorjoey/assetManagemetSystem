package fhi360.it.assetverify.inventory.serviceImpl;

import fhi360.it.assetverify.asset.model.Asset;
import fhi360.it.assetverify.inventory.dto.InventoryDto;
import fhi360.it.assetverify.inventory.model.Inventory;
import fhi360.it.assetverify.inventory.repository.InventoryRepository;
import fhi360.it.assetverify.inventory.service.InventoryService;
import fhi360.it.assetverify.issueLog.model.IssueLog;
import fhi360.it.assetverify.issueLog.repository.IssueLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final IssueLogRepository issueLogRepository;

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<Inventory> getInventories() {
        return inventoryRepository.findAll();
    }

    @Override
    public Page<Inventory> searchByDate(String startDate, String endDate, Pageable pageable) {
        return inventoryRepository.findByDateReceivedBetween(startDate, endDate, pageable);
    }

//    @Override
//    public List<Inventory> findByDateReceivedBetween(String startDate, String endDate) {
//        // Implementation for date range search without pagination
//        return inventoryRepository.findByDateReceivedBetween(startDate, endDate);
//    }

//    @Override
//    public Page<Inventory> findByDateReceivedBetween(String startDate, String endDate, Pageable pageable) {
//        // Implementation for date range search with pagination
//        return inventoryRepository.findByDateReceivedBetween(startDate, endDate, pageable);
//    }

    @Override
    public Page<Inventory> findAll(Pageable pageable) {
        // Implementation for paginated search
        return inventoryRepository.findAll(pageable);
    }


    public InventoryDto createInventory(InventoryDto inventoryDTO) {
        // Map DTO to entity
        Inventory inventory = new Inventory();
        inventory.setDate(inventoryDTO.getDate());
        inventory.setStates(inventoryDTO.getStates());
        inventory.setDescription(inventoryDTO.getDescription()+"("+inventory.getStates()+")");
        inventory.setVendor(inventoryDTO.getVendor());
        inventory.setDateReceived(inventoryDTO.getDateReceived());
        inventory.setPoNumber(inventoryDTO.getPoNumber());
        inventory.setUnit(inventoryDTO.getUnit());
        inventory.setStockState(inventoryDTO.getStockState());
        inventory.setQuantityReceived(inventoryDTO.getQuantityReceived());

        // Retrieve previous balance if description is the same
        String previousBalance = getPreviousBalanceForDescription(inventory.getDescription());

        // Calculate balance
        inventory.setBalance(calculateBalance(inventoryDTO.getQuantityReceived(), previousBalance));

        // Save inventory
        inventoryRepository.save(inventory);

        // Create corresponding issue log
        IssueLog issueLog = createIssueLog(inventory);

        // Save issue log
        issueLogRepository.save(issueLog);

        // Map entity back to DTO and return
        return mapToDTO(inventory);
    }
    private InventoryDto mapToDTO(Inventory inventory) {
        InventoryDto inventoryDto = new InventoryDto();
        inventoryDto.setId(inventory.getId());
        inventoryDto.setDate(inventoryDto.getDate());
        inventoryDto.setStates(inventory.getStates());
        inventoryDto.setDescription(inventory.getDescription());
        inventoryDto.setDateReceived(inventory.getDateReceived());
        inventoryDto.setPoNumber(inventory.getPoNumber());
        inventoryDto.setVendor(inventory.getVendor());
        inventoryDto.setUnit(inventory.getUnit());
        inventoryDto.setStockState(inventory.getStockState());
        inventoryDto.setQuantityReceived(inventory.getQuantityReceived());
        inventoryDto.setBalance(inventory.getBalance());
        return inventoryDto;
    }
    //
    private IssueLog createIssueLog(Inventory inventory) {
        IssueLog issueLog = new IssueLog();
        issueLog.setDate(inventory.getDate());
        issueLog.setStates(inventory.getStates());
        issueLog.setDescription(inventory.getDescription());
        issueLog.setDateReceived(inventory.getDateReceived());
        issueLog.setPoNumber((inventory.getPoNumber()));
        issueLog.setVendor(inventory.getVendor());
        issueLog.setUnit(inventory.getUnit());
        issueLog.setQuantityReceived(inventory.getQuantityReceived());
        issueLog.setStockState(inventory.getStockState());
        issueLog.setInventoryId(inventory.getId());
        issueLog.setBalance(inventory.getBalance());

        // Set other fields accordingly
        return issueLog;
    }

    private String getPreviousBalanceForDescription(String description) {
        String jpql = "SELECT i.balance FROM Inventory i WHERE i.description = :description ORDER BY i.dateReceived DESC";
        Query query = entityManager.createQuery(jpql)
                .setParameter("description", description)
                .setMaxResults(1); // Assuming you want the latest balance

        List<?> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return "0"; // Default balance if no previous balance found
        } else {
            return resultList.get(0).toString();
        }
    }

    private String calculateBalance(String quantityReceived, String previousBalance) {
        int prevBalance = 0;
        if (previousBalance != null) {
            prevBalance = Integer.parseInt(previousBalance);
        }

        int closingBalance = prevBalance + Integer.parseInt(quantityReceived);
        return String.valueOf(closingBalance);
    }


    @Override
    public Page<Inventory> getInventories(Pageable pageable) {
        //        for (Inventory value : result) {
//            value.setShellLife(String.valueOf(calculateSLife(value.getManufactureDate(), value.getExpiryDate())));
//            value.setQuantityReceived(value.getQuantityReceived());
//            value.setQuantityIssued(calQuantityIssued(value.getQuantityIssued()));
        // value.setTotal(calcTotal(value.getQuantityIssued()));
//        }
        return inventoryRepository.findByOrderByIdAsc(pageable);
    }


    public ResponseEntity<Page<Inventory>> getInventoriesByState(String state, Pageable pageable) {
        Page<Inventory> inventories = inventoryRepository.findByStates(state, pageable);
        if (inventories.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(inventories, HttpStatus.OK);
    }



    public Page<Inventory> searchInventory(String query, Pageable pageable) {
        return inventoryRepository.searchInventory(query, pageable);
    }
}