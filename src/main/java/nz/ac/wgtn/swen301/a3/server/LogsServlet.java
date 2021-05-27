package nz.ac.wgtn.swen301.a3.server;

import com.fasterxml.jackson.databind.JsonNode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class LogsServlet extends HttpServlet {

    Map<String, Integer> priority = new HashMap<>(Map.of("off", 1, "fatal", 2, "error", 3, "warn", 4, "info", 5, "debug", 6, "trace", 7, "all", 8));

    public LogsServlet() {

    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String level = req.getParameter("level");
        if (level == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        int limit;
        try {
            limit = Integer.parseInt(req.getParameter("limit"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        resp.setContentType("application/json");

        ArrayList<JsonNode> nodes = new ArrayList<>();

        for (JsonNode j : Persistency.DB) {
            if (priority.get(j.get("level").toString().toLowerCase()) <= priority.get(level)) {
                nodes.add(j);
            }
        }

        SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        nodes.sort((d1, d2) -> {
            try {
                return date.parse(
                        d2.get("timestamp").asText()).compareTo(
                        date.parse(d1.get("timestamp").asText()));
            } catch (Exception e) {
                return 0;
            }
        });

        while (nodes.size() > limit) {
            nodes.remove(nodes.size() - 1);
        }

        /*PrintWriter out = resp.getWriter();
        String names = Arrays.stream(nodes.toArray())
                .filter(name -> name.startsWith(n))
                .collect(Collectors.joining(" "));
        out.println(names);
        out.close();
        */
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
    }
}
