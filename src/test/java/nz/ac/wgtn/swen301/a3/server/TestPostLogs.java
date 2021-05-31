package nz.ac.wgtn.swen301.a3.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestPostLogs {

    @Test
    public void test() throws ServletException, IOException {

        Persistency.DB.clear();
    }

    @Test
    public void testValidPost() throws ServletException, IOException {
        MockHttpServletRequest request;
        MockHttpServletResponse response;
        request = new MockHttpServletRequest();
        request.setParameter("LogEvent", createRandomNode().toPrettyString());
        response = new MockHttpServletResponse();

        LogsServlet service = new LogsServlet();
        service.doPost(request, response);

        assertEquals(201, response.getStatus());

        assertEquals("error message test", Persistency.DB.get(0).get("message").asText());
        Persistency.DB.clear();
    }

    @Test
    public void testInvalidPost() throws ServletException, IOException {
        MockHttpServletRequest request;
        MockHttpServletResponse response;
        request = new MockHttpServletRequest();
        request.setParameter("LogEvent", createNode(
                "blah",
                null,
                "12",
                "thread",
                "null",
                "off",
                "details"
                ).toString());
        response = new MockHttpServletResponse();

        LogsServlet service = new LogsServlet();
        service.doPost(request, response);

        assertEquals(400, response.getStatus());

        Persistency.DB.clear();
    }

    @Test
    public void testInvalidName() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setParameter("badname", createRandomNode().toPrettyString());

        LogsServlet service = new LogsServlet();
        service.doPost(request, response);

        assertEquals(400, response.getStatus());

        Persistency.DB.clear();
    }

    @Test
    public void testDuplicate() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setParameter("LogEvent", createRandomNode().toPrettyString());

        LogsServlet service = new LogsServlet();
        service.doPost(request, response);
        service.doPost(request, response);

        assertEquals(400, response.getStatus());

        Persistency.DB.clear();
    }

    @Test
    public void testAddingLevels() throws ServletException, IOException {
        MockHttpServletRequest request;
        MockHttpServletResponse response;
        for (int i = 1; i < 8; i++) {
            request = new MockHttpServletRequest();
            request.setParameter("LogEvent", createRandomNode(i).toPrettyString());
            response = new MockHttpServletResponse();

            LogsServlet service = new LogsServlet();
            service.doPost(request, response);

            assertEquals(201, response.getStatus());
        }
        assertEquals(7, Persistency.DB.size());
        Persistency.DB.clear();
    }

    public static ObjectNode createRandomNode() {
        ObjectMapper objMap = new ObjectMapper();
        ObjectNode objNode = objMap.createObjectNode();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        objNode.put("id", UUID.randomUUID().toString());
        objNode.put("message", "error message test");
        objNode.put("timestamp", df.format(new Date()) + "");
        objNode.put("thread", Thread.currentThread().toString() + "");
        objNode.put("logger", "logger example");
        objNode.put("level", getKey(LogsServlet.priority, (int) (Math.random() * 8)));
        objNode.put("errorDetails", "details example");

        return objNode;
    }

    public static ObjectNode createRandomNode(int level) {
        ObjectMapper objMap = new ObjectMapper();
        ObjectNode objNode = objMap.createObjectNode();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        objNode.put("id", UUID.randomUUID().toString());
        objNode.put("message", "error message test");
        objNode.put("timestamp", df.format(new Date()) + "");
        objNode.put("thread", Thread.currentThread().toString() + "");
        objNode.put("logger", "logger example");
        objNode.put("level", getKey(LogsServlet.priority, level));
        objNode.put("errorDetails", "details example");

        return objNode;
    }

    public static ObjectNode createNode(String id, String message, String timestamp, String thread, String logger, String level, String errorDetails) {
        ObjectMapper objMap = new ObjectMapper();
        ObjectNode objNode = objMap.createObjectNode();

        objNode.put("id", id);
        objNode.put("message", message);
        objNode.put("timestamp", timestamp);
        objNode.put("thread", thread);
        objNode.put("logger", logger);
        objNode.put("level", level);
        objNode.put("errorDetails", errorDetails);

        return objNode;
    }


    public static <K, V> K getKey(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

        /*
        {
          "id": "d290f1ee-6c54-4b01-90e6-d701748f0851",
          "message": "application started",
          "timestamp": "04-05-2021 10:12:00",
          "thread": "main",
          "logger": "com.example.Foo",
          "level": "DEBUG",
          "errorDetails": "string"
        }
       */
}
