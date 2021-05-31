package nz.ac.wgtn.swen301.a3.server;

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

public class StatsServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        ServletOutputStream out = response.getOutputStream();

        Map<String, HashMap<String, Integer>> fileBuilder = createMap();

        StringBuilder html = new StringBuilder();
        html.append("<html>" +
                "<header>" +
                "<title> Logger Stats </title>" +
                "</header>" +
                "<body>" +
                "<table>" +
                "<tr>");
        html.append("<th>logger</th>" +
                "<th>ALL</th>" +
                "<th>TRACE</th>" +
                "<th>DEBUG</th>" +
                "<th>INFO</th>" +
                "<th>WARN</th>" +
                "<th>ERROR</th>" +
                "<th>FATAL</th>" +
                "<th>OFF</th>\n</tr>");
        for (String logger : fileBuilder.keySet()) {
            html.append("<tr><th>").append(logger).append("</th>\n");
            for (String error : errorStates) {
                html.append("<td>").append(fileBuilder.get(logger).getOrDefault(error, 0)).append("</td>\n");
            }
            html.append("</tr>\n");
        }
        html.append("</table>" +
                "</body>" +
                "</html>");

        out.print(html.toString());
    }

}
