package nz.ac.wgtn.swen301.a3.server;

import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
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

    public static ArrayList<String> errorStates = new ArrayList<>(List.of("ALL", "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF"));

    public StatsCSVServlet() {}

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/csv");

        ServletOutputStream out =resp.getOutputStream();

        Map <String, HashMap<String, Integer>> fileBuilder = createMap();

        StringBuilder csv = new StringBuilder();
        csv.append("logger\tALL\tTRACE\tDEBUG\tINFO\tWARN\tERROR\tFATAL\tOFF\n");
        for (String logger : fileBuilder.keySet()) {
            csv.append(logger);
            for (String error : errorStates) {
                csv.append("\t").append(fileBuilder.get(logger).getOrDefault(error, 0));
            }
            csv.append("\n");
        }

        out.print(csv.toString());

        resp.setStatus(HttpServletResponse.SC_OK);
    }

    public static Map<String, HashMap<String, Integer>> createMap() {
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

        return fileBuilder;
    }
}
