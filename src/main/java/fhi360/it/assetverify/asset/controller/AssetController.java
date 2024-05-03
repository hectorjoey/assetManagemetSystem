package fhi360.it.assetverify.asset.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.opencsv.CSVWriter;
import fhi360.it.assetverify.asset.dto.AssetDto;
import fhi360.it.assetverify.exception.AlreadyExistsException;
import fhi360.it.assetverify.exception.ResourceNotFoundException;
import fhi360.it.assetverify.asset.model.Asset;
import fhi360.it.assetverify.asset.repository.AssetRepository;
import fhi360.it.assetverify.asset.service.AssetService;
import fhi360.it.assetverify.asset.serviceImpl.AssetServiceImpl;
import fhi360.it.assetverify.user.model.Users;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = {"http://localhost:3000"})
@RestController
@RequestMapping({"/api/v1/"})
@RequiredArgsConstructor
@Slf4j
public class AssetController {
    private final AssetRepository assetRepository;
    private final AssetService assetService;
    private final AssetServiceImpl assetServices;

//    @GetMapping({"assets"})
//    public Page<Asset> getAllAssets(final Pageable pageable) {
//        return this.assetRepository.findByOrderById(pageable);
//    }


    @GetMapping({"assets"})
    public Page<Asset> getAllAssets(final Pageable pageable) {
        return this.assetRepository.findByOrderByIdAsc(pageable);
    }

    @GetMapping({"asset/assets"})
    public Page<Asset> getAssets(final Pageable pageable) {
        return this.assetService.getAssets(pageable);
    }

    @GetMapping({"all-assets"})
    List<Asset> getAssets() {
        return this.assetRepository.findAll();
    }

    @GetMapping({"all-assets/{id}"})
    public Optional<Asset> getAssetsById(@PathVariable("id") final Long id) {
        return this.assetRepository.findById(id);
    }

    @GetMapping("assetss/{state}")
    public ResponseEntity<Page<Asset>> getAssetsByState(@PathVariable String state, Pageable pageable) {
       return assetServices.getAssetsByState(state, pageable);
    }

    @GetMapping({"asset/{id}"})
    public ResponseEntity<Asset> getAssetById(@PathVariable("id") final Long id) throws ResourceNotFoundException {
        final Asset asset = this.assetRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Asset not found for this id :: " + id));
        return ResponseEntity.ok().body(asset);
    }

    @PostMapping({"asset/serial/{serialNumber}"})
    public Asset getByAssetSerialNumber(@PathVariable("serialNumber") final String serialNumber) throws ResourceNotFoundException {
        final Asset asset = this.assetRepository.findBySerialNumber(serialNumber);
        if (asset == null) {
            throw new ResourceNotFoundException("Asset not found for this asset Tag " + serialNumber);
        }
        return this.assetRepository.findBySerialNumber(serialNumber);
    }

    @GetMapping({"asset/tag/{assetId}"})
    public Asset getByAssetTag(@PathVariable("assetId") final String assetId) throws ResourceNotFoundException {
        final Asset asset = this.assetRepository.findByAssetId(assetId);
        if (asset == null) {
            throw new ResourceNotFoundException("Asset not found for this asset Tag " + assetId);
        }
        return this.assetRepository.findByAssetId(assetId);
    }

    @PostMapping({"asset"})
    public ResponseEntity<?> createAsset(@Valid @RequestBody final Asset asset) throws AlreadyExistsException, MessagingException {
        final Asset assetsID = this.assetRepository.findByAssetId(asset.getAssetId());
        final Asset assetsSerial = this.assetRepository.findBySerialNumber(asset.getSerialNumber());
        if (assetsID != null) {
            throw new AlreadyExistsException(String.format("Asset with assetsID %s already exist", asset.getAssetId()));
        }
        if (assetsSerial != null) {
            throw new AlreadyExistsException(String.format("Asset with Serial Number %s already exist", asset.getSerialNumber()));
        }
        return new ResponseEntity<>(this.assetService.save(asset), HttpStatus.CREATED);
    }

    @PatchMapping({"asset/{id}"})
    public ResponseEntity<Asset> updateAsset(@PathVariable("id") final long id, @Valid @RequestBody final AssetDto assetDto) {
        log.debug("Update Asset with Id = {}", id);
        Optional<Asset> optionalAsset = this.assetRepository.findById(id);
        if (optionalAsset.isPresent()){
            Asset asset = optionalAsset.get();
            asset.setEmailAddress(assetDto.getEmailAddress());
            asset.setLocation(assetDto.getLocation());
            asset.setAssignee(assetDto.getAssignee());
            asset.setProject(assetDto.getProject());
            asset.setFacility(assetDto.getFacility());
            asset.setStatus(assetDto.getStatus());
            asset.setStates(assetDto.getStates());
            asset.setPhone(assetDto.getPhone());
            asset.setCondition(assetDto.getCondition());
            asset.setApprovedBy(assetDto.getApprovedBy());
            return new ResponseEntity<>(assetRepository.save(asset), HttpStatus.OK);
        }else {
           return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping({"asset/{id}"})
    public Map<String, Boolean> deleteAsset(@PathVariable("id") final Long id) throws ResourceNotFoundException, MessagingException {
        final Asset asset = this.assetRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Asset not found for this id :: " + id));
        this.assetRepository.delete(asset);
        final Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }


    @GetMapping("asset/export-to-pdf")
    public ResponseEntity<byte[]> exportToPDFs(@RequestParam(required = false) String state) {
        try {
            List<Asset> data;

            if (state != null) {
                data = assetServices.getAssetsByState(state);
            } else {
                data = assetServices.getAllAssets();
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate()); // Set landscape orientation
            PdfWriter.getInstance(document, outputStream);

            document.open();
            addDataToPDF(document, data);
            document.close();

            byte[] pdfBytes = outputStream.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            if (state != null) {
                headers.setContentDispositionFormData("attachment", "assets_" + state + ".pdf");
            } else {
                headers.setContentDispositionFormData("attachment", "assets_all.pdf");
            }

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("asset/export-to-pdf/{state}")
    public ResponseEntity<byte[]> exportToPDF(@PathVariable("state") String state) {
        try {
            List<Asset> data = assetServices.getAssetsByState(state);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate()); // Set landscape orientation
            PdfWriter.getInstance(document, outputStream);

            document.open();
            addDataToPDF(document, data);
            document.close();

            byte[] pdfBytes = outputStream.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "assets_" + state + ".pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private void addDataToPDF(Document document, List<Asset> data) throws DocumentException {
        PdfPTable table = new PdfPTable(20); // Number of columns
        table.setWidthPercentage(100); // Set table width to 100% of the page

        // Set table headers
        table.addCell(createCell("Description", true));
        table.addCell(createCell("Asset ID", true));
        table.addCell(createCell("Manufacturer", true));
        table.addCell(createCell("Model number", true));
        table.addCell(createCell("Serial number", true));
        table.addCell(createCell("PO Number", true));
        table.addCell(createCell("Date received", true));
        table.addCell(createCell("Purchase price ($)", true));
        table.addCell(createCell("Purchase price (N)", true));
        table.addCell(createCell("Funder", true));
        table.addCell(createCell("Project", true));
        table.addCell(createCell("Useful life span (Years)", true));
        table.addCell(createCell("Current age of asset", true));
        table.addCell(createCell("Condition", true));
        table.addCell(createCell("States", true));
        table.addCell(createCell("Facility", true));
        table.addCell(createCell("Location ", true));
        table.addCell(createCell("Assignee ", true));
        table.addCell(createCell("Status", true));
        table.addCell(createCell("Email Address", true));

        // Add data rows
        for (Asset obj : data) {
            table.addCell(createCell(obj.getDescription(), false));
            table.addCell(createCell(obj.getAssetId(), false));
            table.addCell(createCell(obj.getManufacturer(), false));
            table.addCell(createCell(obj.getModelNumber(), false));
            table.addCell(createCell(obj.getSerialNumber(), false));
            table.addCell(createCell(obj.getPoNumber(), false));
            table.addCell(createCell(obj.getDateReceived(), false));
            table.addCell(createCell(obj.getPurchasedPriceInDollars(), false));
            table.addCell(createCell(obj.getPurchasePrice(), false));
            table.addCell(createCell(obj.getFunder(), false));
            table.addCell(createCell(obj.getProject(), false));
            table.addCell(createCell(obj.getUsefulLifeSpan(), false));
            table.addCell(createCell(obj.getCurrentAgeOfAsset(), false));
            table.addCell(createCell(obj.getCondition(), false));
            table.addCell(createCell(obj.getStates(), false));
            table.addCell(createCell(obj.getFacility(), false));
            table.addCell(createCell(obj.getLocation(), false));
            table.addCell(createCell(obj.getAssignee(), false));
            table.addCell(createCell(obj.getStatus(), false));
            table.addCell(createCell(obj.getEmailAddress(), false));
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


    @GetMapping("asset/export-to-csv-asset")
    public ResponseEntity<byte[]> exportToCSV(HttpServletResponse response) {
        try {
            List<Asset> data = assetService.getAllAssets();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(outputStream);

            // Create CSVWriter object

            CSVWriter csvWriter = new CSVWriter(writer);

            // Write CSV headers
            String[] headers = {
                    "Description", "Asset ID", "Manufacturer", "Model Number", "Serial Number",
                    "PO Number", "Date Received", "Purchase Price ($)", "Purchase Price (N)", "Funder",
                    "Project", "Useful life span", "Current age of asset", "Condition", "States",
                    "Facility", "Location Or Assignee", "Status", "Email Address"
            };
            csvWriter.writeNext(headers);

            // Write data rows
            for (Asset obj : data) {
                String[] row = {
                        obj.getDescription(), obj.getAssetId(), obj.getManufacturer(), obj.getModelNumber(), obj.getSerialNumber(),
                        obj.getPoNumber(), obj.getDateReceived(), obj.getPurchasedPriceInDollars(), obj.getPurchasePrice(), obj.getFunder(),
                        obj.getProject(), obj.getUsefulLifeSpan(), obj.getCurrentAgeOfAsset(), obj.getCondition(), obj.getStates(),
                        obj.getFacility(), obj.getLocation(), obj.getStatus(), obj.getEmailAddress()
                };
                csvWriter.writeNext(row);
            }

            csvWriter.flush();
            byte[] csvBytes = outputStream.toByteArray();

            writer.flush();
            writer.close();

            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.TEXT_PLAIN);
            header.setContentDispositionFormData("attachment", "asset.csv");

            // Return the byte array as the response
            return new ResponseEntity<>(csvBytes, header, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("asset/export-to-csv/{state}")
    public ResponseEntity<byte[]> exportToCSVs(@PathVariable(required = false) String state) throws ResourceNotFoundException {
        try {
            List<Asset> data;

            if (state != null) {
                data = assetServices.getAssetsByState(state);
            } else {
                data = assetService.getAllAssets();
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(outputStream);

            // Create CSVWriter object
            CSVWriter csvWriter = new CSVWriter(writer);

            // Write CSV headers
            String[] headers = {
                    "Description", "Asset ID", "Manufacturer", "Model Number", "Serial Number",
                    "PO Number", "Date Received", "Purchase Price ($)", "Purchase Price (N)", "Funder",
                    "Project", "Useful life span", "Current age of asset", "Condition", "States",
                    "Facility", "Location Or Assignee", "Status", "Email Address"
            };
            csvWriter.writeNext(headers);

            // Write data rows
            for (Asset obj : data) {
                String[] row = {
                        obj.getDescription(), obj.getAssetId(), obj.getManufacturer(), obj.getModelNumber(), obj.getSerialNumber(),
                        obj.getPoNumber(), obj.getDateReceived(), obj.getPurchasedPriceInDollars(), obj.getPurchasePrice(), obj.getFunder(),
                        obj.getProject(), obj.getUsefulLifeSpan(), obj.getCurrentAgeOfAsset(), obj.getCondition(), obj.getStates(),
                        obj.getFacility(), obj.getLocation(), obj.getStatus(), obj.getEmailAddress()
                };
                csvWriter.writeNext(row);
            }

            csvWriter.flush();
            writer.flush();

            byte[] csvBytes = outputStream.toByteArray();

            // Close the streams
            writer.close();
            outputStream.close();

            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.TEXT_PLAIN);
            if (state != null) {
                header.setContentDispositionFormData("attachment", "assets_" + state + ".csv");
            } else {
                header.setContentDispositionFormData("attachment", "assets_all.csv");
            }

            // Return the byte array as the response
            return new ResponseEntity<>(csvBytes, header, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @GetMapping({"search/asset/{keyword}"})
//    public Page<Asset> searchAssets(final Pageable pageable, @PathVariable("keyword") final String keyword) {
//        return assetRepository.findAll(pageable, keyword);
//    }

    @GetMapping("asset/search")
    public ResponseEntity<Page<Asset>> searchAssets(@RequestParam("query") String query, Pageable pageable) {
        Page<Asset> searchedAssets = assetServices.searchAssets(query, pageable);
        if (searchedAssets.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(searchedAssets);
    }
}