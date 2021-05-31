package nz.ac.wgtn.swen301.a3.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Map.entry;
import static nz.ac.wgtn.swen301.a3.server.TestPostLogs.createNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestStatsCSV {

    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @Test
    public void test_ValidContentType() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        StatsCSVServlet servlet = new StatsCSVServlet();
        servlet.doGet(request, response);
        assertEquals("text/csv", response.getContentType());

        Persistency.DB.clear();
    }

    @Test
    public void test_ValidRequestResponseCode() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        StatsCSVServlet servlet = new StatsCSVServlet();
        servlet.doGet(request, response);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        Persistency.DB.clear();
    }

    @Test
    public void test_ReturnedSingle() throws ServletException, IOException {
        Persistency.DB.clear();
        Persistency.DB.add(createNode(
                UUID.randomUUID().toString(),
                "error message",
                df.format(new Date()) + "",
                Thread.currentThread().toString() + "",
                "logger",
                "DEBUG",
                "details"
        ));
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        StatsCSVServlet servlet = new StatsCSVServlet();
        servlet.doGet(request, response);

        String result = response.getContentAsString();

        Map<String, Map<String, Integer>> table = getTable(result, 2);
        assertEquals(1, table.get("logger").get("DEBUG"));

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        Persistency.DB.clear();
    }

    @Test
    public void test_ReturnMultiple() throws ServletException, IOException {
        Persistency.DB.clear();
        Persistency.DB.add(createNode(
                UUID.randomUUID().toString(),
                "error message",
                df.format(new Date()) + "",
                Thread.currentThread().toString() + "",
                "logger1",
                "debug",
                "details"
        ));
        Persistency.DB.add(createNode(
                UUID.randomUUID().toString(),
                "error message",
                df.format(new Date()) + "",
                Thread.currentThread().toString() + "",
                "logger1",
                "trace",
                "details"
        ));
        Persistency.DB.add(createNode(
                UUID.randomUUID().toString(),
                "error message",
                df.format(new Date()) + "",
                Thread.currentThread().toString() + "",
                "logger1",
                "trace",
                "details"
        ));
        Persistency.DB.add(createNode(
                UUID.randomUUID().toString(),
                "error message",
                df.format(new Date()) + "",
                Thread.currentThread().toString() + "",
                "logger2",
                "fatal",
                "details"
        ));

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        StatsCSVServlet servlet = new StatsCSVServlet();
        servlet.doGet(request, response);

        String result = response.getContentAsString();

        Map<String, Map<String, Integer>> table = getTable(result, 3);

        assertEquals(1, table.get("logger1").get("DEBUG"));
        assertEquals(2, table.get("logger1").get("TRACE"));
        assertEquals(1, table.get("logger2").get("FATAL"));
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        Persistency.DB.clear();
    }

    @Test
    public void test_ReturnHeader() throws ServletException, IOException {
        Persistency.DB.clear();

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        StatsCSVServlet servlet = new StatsCSVServlet();
        servlet.doGet(request, response);

        String result = response.getContentAsString();

        getTable(result, 1);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        Persistency.DB.clear();
    }

    private Map<String, Map<String, Integer>> getTable(String result, int numRows) {
        Map<String, Map<String, Integer>> table = new HashMap<>();

        String[] rows = result.split("\\r?\\n");

        String[] headers = rows[0].split("\\t");
        assertEquals(numRows, rows.length);

        assertEquals("logger", headers[0]);
        assertEquals("ALL", headers[1]);
        assertEquals("TRACE", headers[2]);
        assertEquals("DEBUG", headers[3]);
        assertEquals("INFO", headers[4]);
        assertEquals("WARN", headers[5]);
        assertEquals("ERROR", headers[6]);
        assertEquals("FATAL", headers[7]);
        assertEquals("OFF", headers[8]);

        for (int row = 1; row < rows.length; row++) {
            String[] cols = rows[row].split("\\t");
            assertEquals(9, cols.length);
            if (!table.containsKey(rows[row])) {
                table.put(cols[0], new HashMap<>());
            }
            for (int col = 1; col < cols.length; col++) {
                table.get(cols[0]).put(headers[col], Integer.valueOf(cols[col]));
            }
        }

        return table;
    }
}
