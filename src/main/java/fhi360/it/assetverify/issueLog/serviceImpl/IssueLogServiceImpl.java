package fhi360.it.assetverify.issueLog.serviceImpl;

import fhi360.it.assetverify.inventory.repository.InventoryRepository;
import fhi360.it.assetverify.issueLog.model.IssueLog;
import fhi360.it.assetverify.issueLog.repository.IssueLogRepository;
import fhi360.it.assetverify.issueLog.service.IssueLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return issueLogRepository.findByDateReceivedBetween(startDate, endDate, pageable);
    }

    public List<IssueLog> findByDescription(String description) {
        return issueLogRepository.findByDescription(description);
    }

    public List<IssueLog> getIssueLogsByDate(String dateReceived) {
        // Implement logic to retrieve data based on the date
        // This might involve calling methods on the repository
        return issueLogRepository.findByDateReceived(dateReceived);
    }

//    public void exportToCsv(List<IssueLog> issueLogs, PrintWriter writer) {
//        // Write CSV header
//        writer.println("Id,Date,State,Description,Quantity Received, Vendor, Unit,Stock State,PO Number" +
//                "Received From,Issued To,Quantity Received" +
//                "Quantity Issued,Opening Balance,Balance,Inventory Id," +
//                "Total");
//
//        // Write CSV data
//        for (IssueLog issueLog : issueLogs) {
//            writer.println(String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
//                    issueLog.getId(), issueLog.getDate(), issueLog.getState(),
//                    issueLog.getDescription(), issueLog.getQuantityReceived(), issueLog.getVendor(),
//                    issueLog.getUnit(), issueLog.getStockState(), issueLog.getPoNumber(),
//                    issueLog.getQuantityIssued(), issueLog.getOpeningBalance(), issueLog.getBalance(),
//                    issueLog.getTotal()));
//        }
//    }


//    public List<IssueLog> findByItemDescription(String itemDescription) {
//        return issueLogRepository.findByItemDescription(itemDescription);
//    }
}