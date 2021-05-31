package nz.ac.wgtn.swen301.a3.server;

import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsCSVServlet extends HttpServlet {

    public StatsCSVServlet() {}


    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map <String, HashMap<String, Integer>> fileBuilder = new HashMap<>();
        for (JsonNode node : Persistency.DB) {
            if (fileBuilder.containsKey(node.get("logger").textValue())) {
                if (fileBuilder.get(node.get("logger").textValue()).containsKey(node.get("level").textValue())) {
                    int value = fileBuilder.get(node.get("logger").textValue()).get(node.get("level").textValue());
                    fileBuilder.get(node.get("logger").textValue()).put(node.get("level").textValue(), value + 1);
                    continue;
                }
                fileBuilder.get(node.get("logger").textValue()).put(node.get("level").textValue(), 1);
                continue;
            }
            fileBuilder.put(node.get("logger").textValue(), new HashMap<>());
            fileBuilder.get(node.get("logger").textValue()).put(node.get("level").textValue(), 1);
        }

        ArrayList<String> errorStates = new ArrayList<>(List.of("ALL", "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF"));

        StringBuilder csv = new StringBuilder();
        csv.append("logger, ALL, TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF\n");
        for (String logger : fileBuilder.keySet()) {
            csv.append(logger);
            for (String error : errorStates) {
                csv.append(", ").append(fileBuilder.get(logger).getOrDefault(error, 0));
            }
            csv.append("\n");
        }

        try {
            FileWriter myWriter = new FileWriter("filename.txt");
            myWriter.write(csv.toString());
            myWriter.close();
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            e.printStackTrace();
        }

        resp.setStatus(HttpServletResponse.SC_OK);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
    }
}
