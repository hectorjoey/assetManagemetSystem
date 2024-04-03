package fhi360.it.assetverify.service;

import fhi360.it.assetverify.model.StockStatusReport;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StockStatusReportService {
    StockStatusReport addReport(StockStatusReport stockStatusReport);
//    List<StockStatusReport> findByDateReceivedBetween(String startDate, String endDate);
}
