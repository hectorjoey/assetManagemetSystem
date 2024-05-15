package fhi360.it.assetverify.issueLog.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import fhi360.it.assetverify.exception.ResourceNotFoundException;
import fhi360.it.assetverify.inventory.model.Inventory;
import fhi360.it.assetverify.issueLog.dto.IssueLogDto;
import fhi360.it.assetverify.issueLog.model.IssueLog;
import fhi360.it.assetverify.inventory.repository.InventoryRepository;
import fhi360.it.assetverify.issueLog.repository.IssueLogRepository;
import fhi360.it.assetverify.issueLog.service.IssueLogService;
import fhi360.it.assetverify.issueLog.serviceImpl.IssueLogServiceImpl;
import fhi360.it.assetverify.util.DateTimeFormatterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private final InventoryRepository inventoryRepository;

    @GetMapping("all-issuelogs")
    List<IssueLog> getIssueLogs() {
        return issueLogRepository.findAll();
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

    @GetMapping("issuelogs/issuelog/{states}")
    public List<IssueLog> getIssueLogByStates(@PathVariable String states, Pageable pageable) {
        return issueLogRepository.findByStates(states, pageable);
    }

//    @GetMapping("/issuelogss")
//    public List<IssueLog> getIssueLogsByDescription(@RequestParam("description") String description) {
//        return issueLogRepository.findByDescription(description);
//    }

    @GetMapping("issuelogs/search/{states}")
    public Page<IssueLog> search(@PathVariable("states") String states, @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, @RequestParam("page") int page) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatterUtil.createDateTimeFormatter("dd/MM/yyyy");
        LocalDate start = LocalDate.parse(startDate, dateTimeFormatter);
        LocalDate end = LocalDate.parse(endDate, dateTimeFormatter);

        DateTimeFormatter desiredFormats = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedStartDate = start.format(desiredFormats);
        String formattedEndDate = end.format(desiredFormats);

        return issueLogServices.searchByDate(states, formattedStartDate, formattedEndDate, PageRequest.of(page - 1, 10));
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
                document.add(new Paragraph("Date Received: " + issueLog.getDateReceived()));
                document.add(new Paragraph("State: " + issueLog.getStates()));
                // Add other fields as needed
                document.add(new Paragraph("\n")); // Add a newline between entries
            }

            document.close();
        }
    }

    @PatchMapping({"issuelogs/{id}"})
    public ResponseEntity<IssueLog> updateIssueLog(@PathVariable("id") final long id, @Valid @RequestBody final IssueLogDto issueLogDto) {
        Optional<IssueLog> optionalIssueLog = this.issueLogRepository.findById(id);
        if (optionalIssueLog.isPresent()) {
            IssueLog issueLog = optionalIssueLog.get();
            Optional<Inventory> optionalInventory = inventoryRepository.findById(issueLog.getInventoryId());
            if (optionalInventory.isPresent()) {
                Inventory inventory = optionalInventory.get();
                issueLog.setQuantityIssued(issueLogDto.getQuantityIssued());
                issueLog.setIssuedTo(issueLogDto.getIssuedTo());
                issueLog.setBalance(updateBalance(issueLog.getBalance(), inventory.getQuantityIssued(), issueLogDto.getQuantityIssued()));
                inventory.setBalance(issueLog.getBalance());
                inventory.setIssuedTo(issueLogDto.getIssuedTo());
                inventory.setQuantityIssued(issueLogDto.getQuantityIssued());
            }
            return new ResponseEntity<>(issueLogRepository.save(issueLog), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private String updateBalance(String balance, String quantityIssued, String issuedQuantity) {
        int agg = Integer.parseInt(balance) + Integer.parseInt(quantityIssued);
        int totalBalance = agg - Integer.parseInt(issuedQuantity);
        return String.valueOf(totalBalance);
    }

    @GetMapping("issueLog/{states}")
    public List<IssueLog> getLogsByState(@PathVariable("states") String states, Pageable pageable) {
        return issueLogServices.getLogByStates(states, pageable);
    }
}