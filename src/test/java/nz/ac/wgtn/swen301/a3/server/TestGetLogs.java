package nz.ac.wgtn.swen301.a3.server;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.ServletSecurityElement;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestGetLogs {


    @Test
    public void testInvalidRequestResponseCode1() throws IOException, ServletException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        // query parameter missing
        LogsServlet service = new LogsServlet();
        service.doGet(request,response);

        assertEquals(400,response.getStatus());
    }

    @Test
    public void testInvalidRequestResponseCode2() throws IOException, ServletException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("not a valid param name","42");
        MockHttpServletResponse response = new MockHttpServletResponse();
        // wrong query parameter

        LogsServlet service = new LogsServlet();
        service.doGet(request,response);

        assertEquals(400,response.getStatus());
    }

    @Test
    public void testValidRequestResponseCode() throws IOException, ServletException {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("level","off");
        request.setParameter("limit","10");
        MockHttpServletResponse response = new MockHttpServletResponse();

        LogsServlet service = new LogsServlet();
        service.doGet(request,response);

        assertEquals(200,response.getStatus());
    }

    @Test
    public void testValidLevels() throws IOException, ServletException {
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
        request.setParameter("level","off");
        request.setParameter("limit","10");
        MockHttpServletResponse response = new MockHttpServletResponse();

        LogsServlet service = new LogsServlet();
        service.doGet(request,response);

        assertTrue(response.getContentType().startsWith("application/json"));
    }
}
