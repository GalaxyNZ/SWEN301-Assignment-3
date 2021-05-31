package nz.ac.wgtn.swen301.a3.server;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

import static nz.ac.wgtn.swen301.a3.server.TestPostLogs.createRandomNode;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDeleteLogs {

    @Test
    public void test_01() throws ServletException, IOException {
        MockHttpServletRequest request;
        MockHttpServletResponse response;

        for (int i = 0; i < 10; i++) {
            request = new MockHttpServletRequest();
            request.setParameter("LogEvent", createRandomNode().toPrettyString());
            response = new MockHttpServletResponse();

            LogsServlet service = new LogsServlet();
            service.doPost(request, response);

            assertEquals(201, response.getStatus());
        }

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        LogsServlet service = new LogsServlet();
        service.doDelete(request,response);

        assertEquals(200,response.getStatus());
        assertEquals(Persistency.DB.size(), 0);
    }


}
