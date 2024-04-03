package fhi360.it.assetverify.util;

import fhi360.it.assetverify.asset.model.Asset;
import fhi360.it.assetverify.inventory.model.Inventory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelUtils {
    public static List<Asset> parseExcelAssetFile(final InputStream is) {
        try {
            final XSSFWorkbook workbook = new XSSFWorkbook(is);
            final XSSFSheet sheet = workbook.getSheetAt(0);
            final Iterator<Row> rows = sheet.iterator();
            final ArrayList<Asset> lstAsset = new ArrayList<>();
            int rowNumber = 1;
            while (rows.hasNext()) {
                final Row currentRow = rows.next();
                if (rowNumber == 1) {
                    ++rowNumber;
                } else {
                    final Iterator<Cell> cellsInRow = currentRow.iterator();
                    final DataFormatter formatter = new DataFormatter();
                    final Asset asset = new Asset();
                    int cellIndex = 1;
                    while (cellsInRow.hasNext()) {
                        final Cell currentCell = cellsInRow.next();
                        if (cellIndex == 1) {
                            asset.setDescription(formatter.formatCellValue(currentCell));
                        } else if (cellIndex == 2) {
                            asset.setAssetId(formatter.formatCellValue(currentCell));
                        } else if (cellIndex == 3) {
                            asset.setManufacturer(formatter.formatCellValue(currentCell));
                        } else if (cellIndex == 4) {
                            asset.setModelNumber(formatter.formatCellValue(currentCell));
                        } else if (cellIndex == 5) {
                            asset.setSerialNumber(formatter.formatCellValue(currentCell));
                        } else if (cellIndex == 6) {
                            asset.setPoNumber(formatter.formatCellValue(currentCell));
                        } else if (cellIndex == 7) {
                            asset.setDateReceived(formatter.formatCellValue(currentCell));
                        } else if (cellIndex == 8) {
                            asset.setPurchasedPriceInDollars(formatter.formatCellValue(currentCell));
                        } else if (cellIndex == 9) {
                            asset.setPurchasePrice(formatter.formatCellValue(currentCell));
                        } else if (cellIndex == 10) {
                            asset.setFunder(formatter.formatCellValue(currentCell));
                        } else if (cellIndex == 11) {
                            asset.setProject(formatter.formatCellValue(currentCell));
                        } else if (cellIndex == 12) {
                            asset.setUsefulLifeSpan(formatter.formatCellValue(currentCell));
                        } else if (cellIndex == 13) {
                            asset.setCurrentAgeOfAsset(formatter.formatCellValue(currentCell));
                        } else if (cellIndex == 14) {
                            asset.setCondition(formatter.formatCellValue(currentCell));
                        } else if (cellIndex == 15) {
                            asset.setStates(formatter.formatCellValue(currentCell));
                        } else if (cellIndex == 16) {
                            asset.setFacility(formatter.formatCellValue(currentCell));
                        } else if (cellIndex == 17) {
                            asset.setLocationAndAssignee(formatter.formatCellValue(currentCell));
                        } else if (cellIndex == 18) {
                            asset.setStatus(formatter.formatCellValue(currentCell));
                        }
                        ++cellIndex;
                    }
                    lstAsset.add(asset);
                }
            }
            workbook.close();
            return lstAsset;
        } catch (IOException e) {
            throw new RuntimeException("FAIL! -> message = " + e.getMessage());
        }
    }

    public static List<Inventory> parseExcelInventoryFile(final InputStream is) {
        try {
            final XSSFWorkbook workbook = new XSSFWorkbook(is);
            final XSSFSheet sheet = workbook.getSheetAt(0);
            final Iterator<Row> rows = sheet.iterator();
            final ArrayList<Inventory> lstInventory = new ArrayList<>();
            int rowNumber = 1;
            while (rows.hasNext()) {
                final Row currentRow = rows.next();
                if (rowNumber == 1) {
                    ++rowNumber;
                } else {
                    final Iterator<Cell> cellsInRow = currentRow.iterator();
                    final DataFormatter formatter = new DataFormatter();
                    final Inventory inventory = new Inventory();
                    int cellIndex = 1;
                    try {
                        while (cellsInRow.hasNext()) {
                            final Cell currentCell = cellsInRow.next();

                            if (cellIndex == 1) { // NameOfArticle
                                inventory.setStates(formatter.formatCellValue(currentCell));
                            } else if (cellIndex == 2) { // DateOfPurchase
                                inventory.setDescription(formatter.formatCellValue(currentCell));
                            } else if (cellIndex == 3) {
                               inventory.setDateReceived(formatter.formatCellValue(currentCell));
                            } else if (cellIndex == 4) { // source
                                inventory.setPoNumber(formatter.formatCellValue(currentCell));
                            } else if (cellIndex == 5) { // manufacturer
                               inventory.setVendor(formatter.formatCellValue(currentCell));
                            } else if (cellIndex == 6) { // manufacturer
                               inventory.setUnit(formatter.formatCellValue(currentCell));
                            } else if (cellIndex == 7) { // PurchaseOrderNumber
                                inventory.setStockState(formatter.formatCellValue(currentCell));
                            } else if (cellIndex == 8) { // OpeningBalance
                                inventory.setQuantityReceived(formatter.formatCellValue(currentCell));
                            } else if (cellIndex == 9) { // ClosingStock
                                inventory.setBalance(formatter.formatCellValue(currentCell));
//                            } else if (cellIndex == 10) { // StockOnHand
//                                inventory.setQuantityIssued(formatter.formatCellValue(currentCell));
                            }
                            ++cellIndex;
                        }
                        lstInventory.add(inventory);
                    } catch (Exception ex) {
                        // Log the row content for debugging
                        System.out.println("Error processing row {}: {}" + " " + rowNumber + " " + ex.getMessage());
                        throw ex; // Rethrow the exception after logging
                    }
                }
            }
            workbook.close();
            return lstInventory;
        } catch (IOException e) {
            throw new RuntimeException("FAIL! -> message = " + e.getMessage());
        }
    }
}