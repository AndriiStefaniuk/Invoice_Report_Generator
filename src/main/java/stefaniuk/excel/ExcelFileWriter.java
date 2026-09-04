package stefaniuk.excel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * class used to write Excel file (report)
 */
public class ExcelFileWriter {

    private Path templatePath = Paths.get("files/template.xlsx");
    private Path reportPath = Paths.get("files/report.xlsx");
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private ReportDataProvider dataProvider;

    public ExcelFileWriter(ReportDataProvider dataProvider){
        this.dataProvider = dataProvider;
        createReportFile();
        initializeWorkbook();
        initializeSheet();
    }


    /**
     * initializes the class level WorkBook object representing the report.xlsx file, with basic template
     * @throws IllegalStateException if template.xlsx not found in resources/files
     */
    private void initializeWorkbook(){
        if(!Files.exists(templatePath)){
            throw new IllegalStateException("ExcelFileWriter -> initializeWorkbook(): Excel template file is missing");
        }

        File reportFile = new File(reportPath.toUri());

        try {
            this.workbook = new XSSFWorkbook(reportFile);
        } catch (IOException | InvalidFormatException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * initializes the class level Sheet object representing the sheet containing template
     */
    private void initializeSheet(){
        if(workbook == null){
            initializeWorkbook();
        }
        this.sheet = workbook.getSheetAt(0);
        if(sheet == null){
            throw new IllegalStateException("ExcelFileWriter -> initializeSheet(): template sheet is missing in Excel template file");
        }
    }


    /**
     * makes the copy of template in a new file, replacing any existing report files.
     * There are two files: template.xlsx and the report.xlsx which is a copy of template with filled in data
     */
    private void createReportFile(){
        try {
            Files.copy(templatePath, reportPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
