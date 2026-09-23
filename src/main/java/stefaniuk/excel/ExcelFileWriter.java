package stefaniuk.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * class used to write Excel file (report)
 */
public class ExcelFileWriter {

    private final Path templatePath = Paths.get("src/main/resources/files/template.xlsx");
    private final Path reportPath = Paths.get("src/main/resources/files/report.xlsx");
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private ReportDataProvider dataProvider;

    public ExcelFileWriter(ReportDataProvider dataProvider){
        this.dataProvider = dataProvider;
    }

    // since each site address has to be on a separate line, rows on target cells move down as i add rows
    // this variable helps to track by how many rows the target cell went down
    private byte insertedRowsCount = 0;

    public File getReportFile(){
        initializeWorkbook();
        initializeSheet();

        // filling in the actual report data
        fillInTopRight();
        fillInPersonalInformation();
        writeReportDescription();
        fillInPriceColumn();
        fillInUnitColumn();
        fillInAmountColumn();

        writeToFile();
        return reportPath.toFile();
    }



    /**
     * initializes the class level WorkBook object representing the report.xlsx file, with basic template
     * @throws IllegalStateException if template.xlsx not found in resources/files
     */
    private void initializeWorkbook() {
        if (!Files.exists(templatePath)) {
            throw new IllegalStateException("ExcelFileWriter -> initializeWorkbook(): Excel template file is missing");
        }

        // InputStream reads raw bytes of template off the disk and hands them to Java
        // workbook takes those bytes and rebuilds the Excel sheets, cells, formulas, and styles as Java objects in RAM
        // all changes on workbook will be stored in RAM
        try (InputStream in = Files.newInputStream(templatePath)) {
            this.workbook = new XSSFWorkbook(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize workbook from template", e);
        }
    }


    /**
     * initializes the class level Sheet object representing the sheet containing template
     * @throws IllegalStateException when the sheet does not exist in workbook
     */
    private void initializeSheet () {
        if (workbook == null) {
            initializeWorkbook();
        }
        this.sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            throw new IllegalStateException("ExcelFileWriter -> initializeSheet(): template sheet is missing in Excel template file");
        }
    }


    /**
     * Writes the in-memory workbook content to the report file on disk.
     * converts the modified workbook structure and sends the data out by OutputStream
     */
    private void writeToFile(){
        // OutputStream opens a write connection to report.xlsx on disk
        // workbook.write(out) tells the workbook to collect the bytes in memory and push them through the OutputStream
        try (OutputStream out = Files.newOutputStream(reportPath)) {
            this.workbook.write(out);

            System.out.println("writing to the file");
        } catch (IOException e) {
            throw new RuntimeException("Failed to write report file", e);
        }
    }


    /**
     * fills in the top right section containing the serial number of report, date covered in report and HST number
     */
    private void fillInTopRight () {
        // write the serial number
        writeToCell(2, 12, dataProvider.generateSerialNumber());

        // write the period dates
        writeToCell(3, 10, dataProvider.getReportPeriod());

        // write the hst number
        writeToCell(4, 10, dataProvider.getHstNumber());

    }


    /**
     * fills in section containing the full name and the home address
     */
    private void fillInPersonalInformation(){
        // write the full name
        writeToCell(6, 9, dataProvider.getFullName());

        // write the home address
        writeToCell(7, 9, dataProvider.getStreetAddress());
        writeToCell(8, 9, dataProvider.getCityProvincePostalCode());
    }

    /**
     * fills in description column in report containing info about
     * site addresses, hours worked, and dates
     */
    private void writeReportDescription(){
        byte columnIndex = 5;
        writeSiteAddresses();

        // write the first week date (start and end dates of first week
        writeToCell(16 + insertedRowsCount, columnIndex, "\t" + dataProvider.getFirstWeekDate());

        // write the second week
        writeToCell(18 + insertedRowsCount, columnIndex, "\t" + dataProvider.getSecondWeekDate());

        fillInHours();

    }


    /**
     * fills in cells that contain number of hours worked for each week separately and total
     * along with cells that contain descriptions ( like "My hours" )
     */
    private void fillInHours(){
        byte textColumnIndex = 5; // column where you put text like "My hours"
        byte numberColumnIndex = 8; // column where you put the number of hours
        // hours for first week
        writeToCell(17 + insertedRowsCount, textColumnIndex, "hours \t\t"); // label
        writeToCell(17 + insertedRowsCount, numberColumnIndex, dataProvider.getFirstWeekHours()); // number

        // hours for second week label
        writeToCell(19 + insertedRowsCount, textColumnIndex, "hours \t\t"); // label
        writeToCell(19 + insertedRowsCount, numberColumnIndex, dataProvider.getSecondWeekHours()); // number

        // write the total hours label
        writeToCell(20 + insertedRowsCount, textColumnIndex, "My hours \t\t"); // label
        writeToCell(20 + insertedRowsCount, numberColumnIndex, dataProvider.getTotalHours()); // number


    }


    /**
     * fills in the price column containing the hourly rates
     */
    private void fillInPriceColumn(){
        byte columnIndex = 9;
        // write rate for first week
        writeToCell(17 + insertedRowsCount,columnIndex, dataProvider.getHourlyRate());

        // write rate for second week
        writeToCell(19 + insertedRowsCount,columnIndex, dataProvider.getHourlyRate());
    }


    /**
     * fills in the unit column
     */

    private void fillInUnitColumn(){
        byte columnIndex = 11;
        // write rate for first week
        writeToCell(17 + insertedRowsCount,columnIndex, dataProvider.getFirstWeekHours());

        // write rate for second week
        writeToCell(19 + insertedRowsCount,columnIndex, dataProvider.getSecondWeekHours());
    }


    /**
     * fills in the amount column containing the price earned per week and total amount
     */
    private void fillInAmountColumn(){
        byte dollarColumnIndex = 12;
        byte centColumnIndex = 13;
        int[] dollarsAndCents; // first element is dollars, second is cents

        // first week
        dollarsAndCents = dataProvider.getFirstWeekEarned();
        writeToCell(17 + insertedRowsCount, dollarColumnIndex, String.valueOf(dollarsAndCents[0]));
        writeToCell(17 + insertedRowsCount, centColumnIndex, String.valueOf(dollarsAndCents[1]));

        // second week
        dollarsAndCents = dataProvider.getSecondWeekEarned();
        writeToCell(19 + insertedRowsCount, dollarColumnIndex, String.valueOf(dollarsAndCents[0]));
        writeToCell(19 + insertedRowsCount, centColumnIndex, String.valueOf(dollarsAndCents[1]));

        // total of two weeks
        dollarsAndCents = dataProvider.getTotalEarned();
        writeToCell(21 + insertedRowsCount, dollarColumnIndex, String.valueOf(dollarsAndCents[0]));
        writeToCell(21 + insertedRowsCount, centColumnIndex, String.valueOf(dollarsAndCents[1]));

        // the HST amount
        dollarsAndCents = dataProvider.getHstAmount();
        writeToCell(22 + insertedRowsCount, dollarColumnIndex, String.valueOf(dollarsAndCents[0]));
        writeToCell(22 + insertedRowsCount, centColumnIndex, String.valueOf(dollarsAndCents[1]));

        // total including the HST
        dollarsAndCents = dataProvider.getTotalWithHst();
        writeToCell(24 + insertedRowsCount, dollarColumnIndex, String.valueOf(dollarsAndCents[0]));
        writeToCell(24 + insertedRowsCount, centColumnIndex, String.valueOf(dollarsAndCents[1]));
    }




    /**
     * fills in site addresses only. if there are more than two addresses,
     * creates new rows and pushes rows # 16 or more down
     */
    private void writeSiteAddresses() {
        List<String> siteAddresses = dataProvider.getSiteAddresses();

        // Tracks how many extra rows to push subsequent cells down
        // -1 accounts for the first site address line already present in the template
        // when there are two site addresses, count is (-1 + 2 = 1), so one row to add
        byte additionalSitesCount = (byte) (siteAddresses.size() - 1);
        if(additionalSitesCount > 0){
            insertedRowsCount = additionalSitesCount;
            createSiteAddressesRows(insertedRowsCount);
        }

        // write all sites in the cells
        byte rowIndex = 15;
        byte columnIndex = 5;


        for(int i = 0; i < siteAddresses.size(); i ++){
            if(i == 0){
                writeToCell(rowIndex + i, columnIndex, "The site(s): " + siteAddresses.get(i));
            }
            else {
                writeToCell(rowIndex + i, columnIndex, "\t" + siteAddresses.get(i));
            }
        }
    }


    /**
     * creates a given number of rows starting at row index 16 (adds rows after row index 16)
     * handles the formatting for rows as well
     * @param rowsToInsert byte object representing number of rows to add
     */
    private void createSiteAddressesRows(byte rowsToInsert) {
        int newRowIndex = 16; //Add the new row between row 16 and 17

        for(int i = 0; i < rowsToInsert; i ++){
            Row sourceRow = sheet.getRow(16);
            // Shifting rows from index 15 to the end of the sheet down by 1 row
            sheet.shiftRows(newRowIndex, sheet.getLastRowNum(), 1);
            Row newRow = sheet.createRow(newRowIndex);
            formatRow(sourceRow,newRow);
        }
    }


    /**
     * formats the new row in same way as the source row (copies format from source to new row)
     * @param sourceRow Row object containing the formated row as a source of format/style
     * @param newRow Row object to be formated
     */
    private void formatRow(Row sourceRow, Row newRow) {
        // format cells in the range 4 to 13 inclusive
        int startColIndex = 4; // including
        int endColIndex = 13; // including

        // set the height of the row
        short sourceHeight = sourceRow.getHeight();
        newRow.setHeight(sourceHeight);

        for(int i = startColIndex; i <= endColIndex; i ++){
            // style cells
            Cell sourceCell = sourceRow.getCell(i);
            CellStyle sourceStyle = sourceCell.getCellStyle();
            int sourceWidth = sheet.getColumnWidth(i);

            Cell targetCell = newRow.getCell(i);
            if(targetCell == null){
                targetCell = newRow.createCell(i);
            }
            targetCell.setCellStyle(sourceStyle);
            sheet.setColumnWidth(i, sourceWidth);
        }

        // merge cells with index 5 to 8 together
        // (rowFrom, rowTo, colFrom, colTo)
        sheet.addMergedRegion(new CellRangeAddress(newRow.getRowNum(), newRow.getRowNum(), 5, 8));
    }


    /**
     * inserts the provided value in the cell in certain row and column
     * DOES not edit or handle any formating
     * @param row int object representing the index (STARTING FROM 0) of the target row
     * @param column int object representing the index (STARTING FROM 0) of the target column
     * @param content String object representing the value to insert in the cell
     */
    private void writeToCell(int row, int column, String content){
        if(row < 0 || column < 0){
            // content can be empty as well
            return;
        }

        Row hstCellRow = sheet.getRow(row);
        if(hstCellRow == null){
            hstCellRow = sheet.createRow(row);
        }

        Cell hstCell = hstCellRow.getCell(column);
        if(hstCell == null){
            hstCell = hstCellRow.createCell(column);
        }

        hstCell.setCellValue(content);
    }
}
