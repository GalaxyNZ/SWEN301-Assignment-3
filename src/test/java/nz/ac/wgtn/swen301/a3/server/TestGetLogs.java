package nz.ac.wgtn.swen301.a3.server;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static nz.ac.wgtn.swen301.a3.server.TestPostLogs.createNode;
import static nz.ac.wgtn.swen301.a3.server.TestPostLogs.createRandomNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestGetLogs {


    @Test
    public void testInvalidRequestResponseCode1() throws IOException, ServletException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        // query parameter missing
        LogsServlet service = new LogsServlet();
        service.doGet(request, response);

        assertEquals(400, response.getStatus());
    }

    @Test
    public void testInvalidRequestResponseCode2() throws IOException, ServletException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("not a valid param name", "42");
        MockHttpServletResponse response = new MockHttpServletResponse();
        // wrong query parameter

        LogsServlet service = new LogsServlet();
        service.doGet(request, response);

        assertEquals(400, response.getStatus());
    }

    @Test
    public void testValidRequestResponseCode() throws IOException, ServletException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("level", "off");
        request.setParameter("limit", "10");
        MockHttpServletResponse response = new MockHttpServletResponse();

        LogsServlet service = new LogsServlet();
        service.doGet(request, response);

        assertEquals(200, response.getStatus());
    }

    @Test
    public void testValidLevels() throws IOException, ServletException {
        MockHttpServletRequest request;
        MockHttpServletResponse response;
        for (String key : LogsServlet.priority.keySet()) {
            request = new MockHttpServletRequest();
            request.setParameter("level", key);
            request.setParameter("limit", "10");
            response = new MockHttpServletResponse();

            LogsServlet service = new LogsServlet();
            service.doGet(request, response);

            assertEquals(200, response.getStatus());
        }
    }

    @Test
    public void testLimit() throws IOException, ServletException {
        for (String key : LogsServlet.priority.keySet()) {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setParameter("level", key);
            request.setParameter("limit", "10");
            MockHttpServletResponse response = new MockHttpServletResponse();

            LogsServlet service = new LogsServlet();
            service.doGet(request, response);

            assertEquals(200, response.getStatus());
        }
    }

    @Test
    public void testValidContentType() throws IOException, ServletException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("level", "off");
        request.setParameter("limit", "10");
        MockHttpServletResponse response = new MockHttpServletResponse();

        LogsServlet service = new LogsServlet();
        service.doGet(request, response);

        assertEquals(response.getContentType(), "application/json");
    }


    @Test
    public void testLimitWithoutNumber() throws IOException, ServletException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("level", "off");
        request.setParameter("limit", "meow");
        MockHttpServletResponse response = new MockHttpServletResponse();
        // wrong query parameter

        LogsServlet service = new LogsServlet();
        service.doGet(request, response);

        assertEquals(400, response.getStatus());
    }


    @Test
    public void testGettingAllAbove() throws IOException, ServletException {

        MockHttpServletRequest request;
        MockHttpServletResponse response;
        for (int i = 1; i < 9; i++) {
            request = new MockHttpServletRequest();
            request.setParameter("LogEvent", createRandomNode(i).toPrettyString());
            response = new MockHttpServletResponse();

            LogsServlet service = new LogsServlet();
            service.doPost(request, response);

            assertEquals(201, response.getStatus());
        }

        request = new MockHttpServletRequest();
        request.addParameter("limit", "10");
        request.addParameter("level", "debug");
        response = new MockHttpServletResponse();

        LogsServlet service = new LogsServlet();
        service.doGet(request, response);

        String result = response.getContentAsString();

        assertTrue(result.contains("\"debug\""));
        assertTrue(result.contains("\"info\""));
        assertTrue(result.contains("\"warn\""));
        assertTrue(result.contains("\"error\""));
        assertTrue(result.contains("\"fatal\""));
        assertTrue(result.contains("\"off\""));
    }
}
