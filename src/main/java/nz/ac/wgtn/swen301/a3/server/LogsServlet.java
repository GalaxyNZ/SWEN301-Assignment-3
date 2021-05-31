package nz.ac.wgtn.swen301.a3.server;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class LogsServlet extends HttpServlet {

    public static Map<String, Integer> priority = new HashMap<>(Map.of("OFF", 1, "FATAL", 2, "ERROR", 3, "WARN", 4, "INFO", 5, "DEBUG", 6, "TRACE", 7, "ALL", 8));

    public LogsServlet() {

    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String level = req.getParameter("level");
        if (level == null) {
            System.out.println("level null");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        int limit;
        try {
            limit = Integer.parseInt(req.getParameter("limit"));
        } catch (NumberFormatException e) {

            System.out.println("limit not parsed");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        resp.setContentType("application/json");

        ArrayList<JsonNode> nodes = new ArrayList<>();

        for (JsonNode j : Persistency.DB) {
            if (priority.get(j.get("level").textValue().toUpperCase())
                    <= priority.get(level)) {
                nodes.add(j);
            }
        }

        SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        nodes.sort((d1, d2) -> {
            try {
                return date.parse(
                        d2.get("timestamp").textValue()).compareTo(
                        date.parse(d1.get("timestamp").asText()));
            } catch (ParseException e) {
                return 0;
            }
        });

        ObjectMapper om = new ObjectMapper();

        ArrayNode nodeArray = om.createArrayNode();

        for (int i = 0; i < limit && i < nodes.size(); i++) {
            nodeArray.add(nodes.get(i));
        }


        PrintWriter out = resp.getWriter();
        out.print(om.writerWithDefaultPrettyPrinter().writeValueAsString(nodeArray));
        out.close();

        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        ObjectMapper objMap = new ObjectMapper();
        ObjectNode objNode;
        // Hints stated not to use getParameter. I assumed this meant not to do getParameter for each of the options but instead to send the whole piece of data.
        try {
            objNode = objMap.readValue(req.getParameter("LogEvent"), ObjectNode.class);
        } catch (IllegalArgumentException e) {
            System.out.println("no LogEvent parameter");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            objNode.get("errorDetails");
            // Check objNode is not null
            if (objNode.get("id").textValue().equals("") ||
                    objNode.get("message").textValue().equals("") ||
                    objNode.get("timestamp").textValue().equals("") ||
                    objNode.get("thread").textValue().equals("") ||
                    objNode.get("logger").textValue().equals("") ||
                    objNode.get("level").textValue().equals("")
            ) {
                System.out.println("node field is empty" + objNode.toPrettyString());
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        } catch (NullPointerException e) {
            System.out.println("node field is null");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Check node isn't already in Persistency database or doesn't share an ID
        for (JsonNode node : Persistency.DB) {
            if (objNode.get("id").textValue().equals(node.get("id").textValue())) {
                System.out.println("Node already in database");
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }

        Persistency.DB.add(objNode);
        resp.setStatus(HttpServletResponse.SC_CREATED);

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

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Persistency.DB.clear();

        resp.setContentType("application/json");

        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
