package fhi360.it.assetverify.asset.serviceImpl;

import fhi360.it.assetverify.asset.model.Asset;
import fhi360.it.assetverify.asset.repository.AssetRepository;
import fhi360.it.assetverify.asset.service.AssetService;
import fhi360.it.assetverify.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {
    private final AssetRepository assetRepository;

    @Override
    public boolean isAssetAlreadyPresent(final Asset asset) {
        boolean isAssetAlreadyExists = false;
        final Asset existingAsset = this.assetRepository.findByAssetId(asset.getAssetId());
        if (existingAsset != null) {
            isAssetAlreadyExists = true;
        }
        return isAssetAlreadyExists;
    }

    @Override
    public Asset save(@RequestBody final Asset asset) {
        return this.assetRepository.save(asset);
    }

    @Override
    public void delete(final int id) {
    }


    @Override
    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    @Override
    public Page<Asset> getAssets(Pageable pageable) {
        Page<Asset> result = assetRepository.findByOrderByIdAsc(pageable);

        // Process all pages
        while (result.hasNext()) {
            // Iterate over all assets in the current page
            for (Asset asset : result.getContent()) {
                String dateReceived = asset.getDateReceived();
                LocalDate date = parseDate(dateReceived);
                int age = calculateAgeOfAsset(date, LocalDate.now());
                // Update the current age of the asset
                asset.setCurrentAgeOfAsset(String.valueOf(age));
                // Save updated assets to the database
                assetRepository.saveAll(result.getContent());
            }
            // Move to the next page
            pageable = pageable.next();
            result = assetRepository.findByOrderByIdAsc(pageable);
        }

        return result;
    }


    public Page<Asset> searchAssets(String query, Pageable pageable) {
            return assetRepository.searchAsset(query, pageable);
    }

//    @Override
//    public Page<Asset> getAssets(Pageable pageable) {
//        // Initialize page number
//        int pageNumber = 0;
//        Page<Asset> result;
//
//        // Process all pages
//        do {
//            // Increment page number
//            pageNumber++;
//
//            // Fetch the page
//            pageable = PageRequest.of(pageNumber, pageable.getPageSize());
//            result = assetRepository.findByOrderByIdAsc(pageable);
//
//            // Process the current page
//            for (Asset asset : result.getContent()) {
//                String dateReceived = asset.getDateReceived();
//                LocalDate date = parseDate(dateReceived);
//                int age = calculateAgeOfAsset(date, LocalDate.now());
//                // Update the current age of the asset
//                asset.setCurrentAgeOfAsset(String.valueOf(age));
//            }
//            // Save updated assets to the database
//            assetRepository.saveAll(result);
//
//        } while (result.hasNext());
//
//        return (Page<Asset>) assetRepository.saveAll(result);
//    }


    private LocalDate parseDate(String dateReceived) {
        LocalDate date;
        if (dateReceived.matches("\\d/\\d/\\d{2}")) { // Check if date format is single digit for day and month
            date = LocalDate.parse(dateReceived, DateTimeFormatter.ofPattern("M/d/yy"));
        } else if (dateReceived.matches("\\d{2}/\\d/\\d{2}")) { // Check if date format has double-digit month and single-digit day
            date = LocalDate.parse(dateReceived, DateTimeFormatter.ofPattern("MM/d/yy"));
        } else if (dateReceived.matches("\\d/\\d{2}/\\d{2}")) { // Check if date format has single-digit month and double-digit day
            date = LocalDate.parse(dateReceived, DateTimeFormatter.ofPattern("M/dd/yy"));
        } else if (dateReceived.matches("\\d{2}/\\d{2}/\\d{4}")) { // Check if date format is MM/dd/yyyy
            date = LocalDate.parse(dateReceived, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } else if (dateReceived.matches("\\d/\\d{2}/\\d{4}")) { // Check if date format is single-digit month MM/dd/yyyy
            date = LocalDate.parse(dateReceived, DateTimeFormatter.ofPattern("M/dd/yyyy"));
        } else if (dateReceived.matches("\\d{2}/\\d/\\d{4}")) { // Check if date format is double-digit day MM/dd/yyyy
            date = LocalDate.parse(dateReceived, DateTimeFormatter.ofPattern("MM/d/yyyy"));
        } else { // Assume date format is MM/dd/yy by default
            date = LocalDate.parse(dateReceived, DateTimeFormatter.ofPattern("MM/dd/yy"));
        }

        return date;
    }

    private int calculateAgeOfAsset(LocalDate dateReceived, LocalDate currentDate) {
        return Period.between(dateReceived, currentDate).getYears();
    }

    public ResponseEntity<Page<Asset>> getAssetsByState(String state, Pageable pageable) {
        Page<Asset> assets = assetRepository.findByStates(state, pageable);
        if (assets.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(assets, HttpStatus.OK);
    }

    public List<Asset> getAssetsByState(String state) throws ResourceNotFoundException {
        List<Asset> assets = assetRepository.findByStates(state);
        if (assets.isEmpty()) {
            throw new ResourceNotFoundException("No assets found for the state: " + state);
        }
        return assets;
    }
}