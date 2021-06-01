package nz.ac.wgtn.swen301.a3.server;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static nz.ac.wgtn.swen301.a3.server.StatsCSVServlet.createMap;
import static nz.ac.wgtn.swen301.a3.server.StatsCSVServlet.errorStates;

public class StatsXLSServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/xls");

        ServletOutputStream out = response.getOutputStream();

        Map<String, HashMap<String, Integer>> fileBuilder = createMap();


        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("log stats");


        int rowCount = 0, colCount = 0;
        Row row = sheet.createRow(rowCount++);
        Cell cell = row.createCell(colCount++);
        cell.setCellValue("logger");
        cell = row.createCell(colCount++);
        cell.setCellValue("ALL");
        cell = row.createCell(colCount++);
        cell.setCellValue("TRACE");
        cell = row.createCell(colCount++);
        cell.setCellValue("DEBUG");
        cell = row.createCell(colCount++);
        cell.setCellValue("INFO");
        cell = row.createCell(colCount++);
        cell.setCellValue("WARN");
        cell = row.createCell(colCount++);
        cell.setCellValue("ERROR");
        cell = row.createCell(colCount++);
        cell.setCellValue("FATAL");
        cell = row.createCell(colCount);
        cell.setCellValue("OFF");

        for (String logger : fileBuilder.keySet()) {
            colCount = 0;
            row = sheet.createRow(rowCount++);
            cell = row.createCell(colCount);
            cell.setCellValue(logger);
            for (String error : errorStates) {
                cell = row.createCell(colCount);
                cell.setCellValue(fileBuilder.get(logger).getOrDefault(error, 0));
            }
        }


        workbook.write(out);
        workbook.close();
        out.close();
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
