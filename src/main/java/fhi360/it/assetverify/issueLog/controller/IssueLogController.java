package fhi360.it.assetverify.issueLog.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import fhi360.it.assetverify.exception.ResourceNotFoundException;
import fhi360.it.assetverify.inventory.model.Inventory;
import fhi360.it.assetverify.issueLog.model.IssueLog;
import fhi360.it.assetverify.inventory.repository.InventoryRepository;
import fhi360.it.assetverify.issueLog.repository.IssueLogRepository;
import fhi360.it.assetverify.issueLog.service.IssueLogService;
import fhi360.it.assetverify.issueLog.serviceImpl.IssueLogServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/")
public class IssueLogController {
    private final IssueLogRepository issueLogRepository;
    private final IssueLogService issueLogService;
    private final IssueLogServiceImpl issueLogServices;

    @GetMapping("all-issuelogs")
    List<IssueLog> getIssueLogs() {
        return issueLogService.getAllIssueLogs();
    }

    @GetMapping("issuelogs")
    public Page<IssueLog> getAllBinCards(Pageable pageable) {
        return issueLogService.getAllIssueLogs(pageable);
    }

    @PostMapping("issuelogs")
    public ResponseEntity<IssueLog> addIssueLog(@RequestBody IssueLog issueLog) {
        return issueLogServices.createIssueLog(issueLog);
    }


    @GetMapping("issuelogs/{id}")
    public ResponseEntity<IssueLog> getBinCardById(@PathVariable(value = "id") Long id)
            throws ResourceNotFoundException {
        IssueLog issueLog = issueLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("IssueLog not found for this id :: " + id));
        return ResponseEntity.ok().body(issueLog);
    }

    // For searching binCard
//    @GetMapping("Issuelog/{keyword}")
//    public Page<IssueLog> getAllIssueLogs(Pageable pageable, @PathVariable("keyword") String keyword) {
//        return issueLogRepository.findAll(pageable, keyword);
//    }

    @GetMapping("issuelogs/inventory/{inventoryId}")
    public List<IssueLog> getIssueLogByInventoryId(@PathVariable Long inventoryId) {
        return issueLogRepository.findByInventoryId(inventoryId);
    }

    @GetMapping("issuelogs/invent/{description}")
    public List<IssueLog> getIssueLogByItemDescription(@PathVariable String description) {
        return issueLogRepository.findByDescription(description);
    }

//    @GetMapping("/issuelogss")
//    public List<IssueLog> getIssueLogsByDescription(@RequestParam("description") String description) {
//        return issueLogRepository.findByDescription(description);
//    }

    @GetMapping("search")
    public Page<IssueLog> search(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, @RequestParam("page") int page) {
        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate start = LocalDate.parse(startDate, inputFormat);
        LocalDate end = LocalDate.parse(endDate, inputFormat);


        DateTimeFormatter desiredFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedStartDate = start.format(desiredFormat);
        String formattedEndDate = end.format(desiredFormat);

        return issueLogService.searchByDate(formattedStartDate, formattedEndDate, PageRequest.of(page - 1, 10));
    }

    @GetMapping("date/all-issuelogs")
    public List<IssueLog> getAllIssueLogs(@RequestParam("date") String date) {
        // Assuming IssueLog is your entity class representing the data
        return issueLogService.getIssueLogsByDate(date);
    }


    @GetMapping("api/pdf/export")
    public void exportToPdf(@RequestParam String description, HttpServletResponse response) throws IOException, DocumentException {
        List<IssueLog> issueLogs = issueLogService.findByDescription(description);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=exported-data.pdf");

        try (OutputStream outputStream = response.getOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);

            document.open();
            document.add(new Paragraph("Bin Card Export"));

            // Add data to the PDF
            for (IssueLog issueLog : issueLogs) {
                document.add(new Paragraph("Date: " + issueLog.getDateReceived()));
                document.add(new Paragraph("Warehouse Name: " + issueLog.getStates()));
                // Add other fields as needed
                document.add(new Paragraph("\n")); // Add a newline between entries
            }

            document.close();
        }
    }
}