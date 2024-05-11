package fhi360.it.assetverify.issueLog.serviceImpl;

import fhi360.it.assetverify.inventory.model.Inventory;
import fhi360.it.assetverify.inventory.repository.InventoryRepository;
import fhi360.it.assetverify.issueLog.model.IssueLog;
import fhi360.it.assetverify.issueLog.repository.IssueLogRepository;
import fhi360.it.assetverify.issueLog.service.IssueLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class IssueLogServiceImpl implements IssueLogService {

    private final InventoryRepository inventoryRepository;
    private final IssueLogRepository issueLogRepository;
    

    @Override
    public List<IssueLog> getAllIssueLogs() {
        return issueLogRepository.findAll();
    }

    @Override
    public Page<IssueLog> getAllIssueLogs(Pageable pageable) {
        Page<IssueLog> result = issueLogRepository.findByOrderByIdAsc(pageable);
//        for (IssueLog value : result) {
//            value.setTotal(calcTotal());
//        }
        return result;
    }

    private String calcTotal() {
        List<IssueLog> issueLogs = issueLogRepository.findAll();
        int total = 0;

        for (IssueLog issueLog : issueLogs) {
            String quantityIssued = issueLog.getQuantityIssued();
            if (quantityIssued != null) {
                total += Integer.parseInt(quantityIssued);
            }
        }
        System.out.println(total);
        return String.valueOf(total);
    }

    @Override
    public Page<IssueLog> searchByDate(String startDate, String endDate, Pageable pageable) {
        return issueLogRepository.findByDateIssuedBetween(startDate, endDate, pageable);
    }

    public List<IssueLog> findByDescription(String description) {
        return issueLogRepository.findByDescription(description);
    }

    public List<IssueLog> getIssueLogsByDate(String dateReceived) {
        // Implement logic to retrieve data based on the date
        // This might involve calling methods on the repository
        return issueLogRepository.findByDateReceived(dateReceived);
    }

    public ResponseEntity<IssueLog> createIssueLog(IssueLog issueLog){
        String quantityIssued = issueLog.getQuantityIssued();
        String issuedTo = issueLog.getIssuedTo();
        Optional<Inventory> optionalInventory = inventoryRepository.findById(issueLog.getInventoryId());

        if (optionalInventory.isPresent()) {
            Inventory inventory = optionalInventory.get();
            inventory.setIssuedTo(issuedTo);
            inventory.setQuantityIssued(quantityIssued);
            inventory.setDateIssued(issueLog.getDateIssued());
            inventory.setBalance(calBalance(issueLog.getBalance(), issueLog.getQuantityIssued()));

            inventoryRepository.save(inventory);

            issueLog.setBalance(calBalance(issueLog.getBalance(), issueLog.getQuantityIssued()));

            System.out.println("issue Log" + issueLog);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        System.out.println("issue Log" + issueLog);
        return new ResponseEntity<>(issueLogRepository.save(issueLog), HttpStatus.CREATED);
    }

    private String calBalance(String balance, String quantityIssued) {
        int stockBalance = Integer.parseInt(balance) - Integer.parseInt(quantityIssued);
        return String.valueOf(stockBalance);
    }
}