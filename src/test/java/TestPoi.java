import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestPoi {
    public static void main(String[] args) throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("学生列表");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("学号");
        cell = row.createCell(1);
        cell.setCellValue("姓名");
        cell = row.createCell(2);
        cell.setCellValue("性别");

        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);

        for(int i=1;i<=10;i++){
            row = sheet.createRow(i);
            cell = row.createCell(0);
            cell.setCellStyle(style);
            cell.setCellValue("2019055567"+i);
            cell = row.createCell(1);
            cell.setCellStyle(style);
            cell.setCellValue("name"+i);
            cell = row.createCell(2);
            cell.setCellStyle(style);
            if (i%2==0)
                cell.setCellValue("男");
            else
                cell.setCellValue("女");
        }

        FileOutputStream fos = new FileOutputStream("D:\\学生列表.xls");
        wb.write(fos);

        fos.close();
        wb.close();
    }
}
