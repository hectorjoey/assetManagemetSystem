
package fhi360.it.assetverify.inventory.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.opencsv.CSVWriter;
import fhi360.it.assetverify.inventory.dto.InventoryDto;
import fhi360.it.assetverify.exception.ResourceNotFoundException;
import fhi360.it.assetverify.inventory.model.Inventory;
import fhi360.it.assetverify.inventory.repository.InventoryRepository;
import fhi360.it.assetverify.inventory.service.InventoryService;
import fhi360.it.assetverify.inventory.serviceImpl.InventoryServiceImpl;
import fhi360.it.assetverify.issueLog.service.IssueLogService;
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
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/")
public class InventoryController {
    private final InventoryRepository inventoryRepository;
    private final InventoryService inventoryService;
    private  final InventoryServiceImpl inventoryServices;
    private final IssueLogService issueLogService;


    @GetMapping("all-inventories")
    List<Inventory> getInventories() {
        return inventoryRepository.findAll();
    }

    @GetMapping("all-invent")
    List<Inventory> getInventory() {
        return inventoryService.getInventories();
    }

    //get all inventory
    @GetMapping("inventories")
    public Page<Inventory> getAllInventories(Pageable pageable) {
        return inventoryService.getInventories(pageable);
    }


    @PostMapping("/inventory")
    public ResponseEntity<InventoryDto> createInventory(@RequestBody InventoryDto inventoryDTO) {
        return new ResponseEntity<>(inventoryServices.createInventory(inventoryDTO), HttpStatus.CREATED);
    }

    @GetMapping("inventories/{state}")
    public ResponseEntity<Page<Inventory>> getInventoriesByState(@PathVariable String state, Pageable pageable) {
        return inventoryServices.getInventoriesByState(state, pageable);
    }

    //get inventory by Id
    @GetMapping("invent/{id}")
    public ResponseEntity<Inventory> getInventoryById(@PathVariable(value = "id") Long id)
            throws ResourceNotFoundException {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for this id :: " + id));
        return ResponseEntity.ok().body(inventory);
    }

    // For searching inventory
    @GetMapping("inventory/{keyword}")
    public Page<Inventory> getInventories(Pageable pageable, @PathVariable("keyword") String keyword) {
        return inventoryRepository.findAll(pageable, keyword);
    }

    @PutMapping("inventories/{id}")
    public Inventory updateInventory(@PathVariable("id") Long id, @Valid @RequestBody InventoryDto inventoryDto) throws ResourceNotFoundException {
        System.out.println("Update inventory with ID = " + id + "...");
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for this id :: " + id));
//        inventory.setOpeningBalance(inventoryDto.getOpeningBalance());
//        inventory.setBalance(inventoryDto.getBalance());
        final Inventory updatedInventory = inventoryRepository.save(inventory);
        System.out.println("Updated Inventory " + updatedInventory);
        return inventoryRepository.save(updatedInventory);
    }

    @DeleteMapping("inventory/{id}")
    public Map<String, Boolean> deleteInventory(@PathVariable(value = "id") Long id)
            throws ResourceNotFoundException {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for this id :: " + id));
        inventoryRepository.delete(inventory);
//        deleteAssetService.deleteAssetEmail(asset);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }


    @GetMapping("searches")
    public Page<Inventory> search(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, @RequestParam("page") int page) {
        DateTimeFormatter inputFormats = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate start = LocalDate.parse(startDate, inputFormats);
        LocalDate end = LocalDate.parse(endDate, inputFormats);

        DateTimeFormatter desiredFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedStartDate = start.format(desiredFormat);
        String formattedEndDate = end.format(desiredFormat);
        return inventoryService.searchByDate(formattedStartDate, formattedEndDate, PageRequest.of(page - 1, 10));
    }

    @GetMapping("export-consumable-to-pdf")
    public ResponseEntity<byte[]> exportInventoryToPDF() {
        try {
            List<Inventory> data = inventoryService.getInventories();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate()); // Set landscape orientation
            PdfWriter.getInstance(document, byteArrayOutputStream);

            document.open();
            addDataToPDF(document, data);
            document.close();

            byte[] pdfBytes = byteArrayOutputStream.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "IT-Consumable.pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void addDataToPDF(Document document, List<Inventory> data) throws DocumentException {
        PdfPTable table = new PdfPTable(19); // Number of columns
        table.setWidthPercentage(100); // Set table width to 100% of the page

        // Set table headers
        table.addCell(createCell("State", true));
        table.addCell(createCell("Description", true));
        table.addCell(createCell("Date received", true));
        table.addCell(createCell("PO Number", true));
        table.addCell(createCell("Vendor", true));
        table.addCell(createCell("Unit", true));
        table.addCell(createCell("Stock State", true));
        table.addCell(createCell("Quantity Received", true));
        table.addCell(createCell("Issued To", true));
        table.addCell(createCell("Quantity Issued", true));
//        table.addCell(createCell("Opening Balance", true));
//        table.addCell(createCell("Balance", true));

        // Add data rows
        for (Inventory obj : data) {
            table.addCell(createCell(obj.getStates(), false));
            table.addCell(createCell(obj.getDescription(), false));
            table.addCell(createCell(obj.getDateReceived(), false));
            table.addCell(createCell(obj.getPoNumber(), false));
            table.addCell(createCell(obj.getVendor(), false));
            table.addCell(createCell(obj.getUnit(), false));
            table.addCell(createCell(obj.getStockState(), false));
            table.addCell(createCell(obj.getQuantityReceived(), false));
        }

        // Add the table to the document
        document.add(table);
    }

    private PdfPCell createCell(String content, boolean isHeader) {
        PdfPCell cell = new PdfPCell(new Paragraph(content));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(2);

        Font font = isHeader ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 6) : FontFactory.getFont(FontFactory.HELVETICA, 7);
        cell.setPhrase(new Paragraph(content, font));

        return cell;
    }


    @GetMapping("export-consumable-to-csv")
    public ResponseEntity<byte[]> exportInventoryToCSV(HttpServletResponse response) {
        try {
            List<Inventory> data = inventoryService.getInventories();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(outputStream);

            // Create CSVWriter object

            CSVWriter csvWriter = new CSVWriter(writer);

            // Write CSV headers
            String[] headers = {
                    "State", "Description", "Date Received", "PO Number", "Vendor",
                    "Unit", "Stock state", "Quantity Received", "Opening Balance", "Balance"
            };
            csvWriter.writeNext(headers);

            // Write data rows
            for (Inventory obj : data) {
                String[] row = {
                        obj.getStates(), obj.getDescription(), obj.getDateReceived(), obj.getPoNumber(), obj.getVendor(),
                        obj.getUnit(), obj.getStockState(), obj.getQuantityReceived(),
//                        obj.getOpeningBalance(), obj.getBalance()
                };
                csvWriter.writeNext(row);
            }

            csvWriter.flush();
            byte[] csvBytes = outputStream.toByteArray();

            writer.flush();
            writer.close();

            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.TEXT_PLAIN);
            header.setContentDispositionFormData("attachment", "IT-Consumable.csv");

            // Return the byte array as the response
            return new ResponseEntity<>(csvBytes, header, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
