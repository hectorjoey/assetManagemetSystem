package fhi360.it.assetverify.issueLog.service;

import fhi360.it.assetverify.issueLog.model.IssueLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IssueLogService {
//    IssueLogDto addIssueLog(IssueLogDto issueLogDto);

    List<IssueLog> getAllIssueLogs();

    Page<IssueLog> getAllIssueLogs(Pageable pageable);

    Page<IssueLog> searchByDate(String startDate, String endDate, Pageable pageable);

    List<IssueLog> findByDescription(String description);

    List<IssueLog> getIssueLogsByDate(String date);


//    void exportToCsv(List<IssueLog> issueLogs, PrintWriter writer);

}
