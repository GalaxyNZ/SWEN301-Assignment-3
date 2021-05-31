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

public class StatsXLSServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/csv");

        ServletOutputStream out = response.getOutputStream();

        Map<String, HashMap<String, Integer>> fileBuilder = createMap();


        StringBuilder xls = new StringBuilder();
        xls.append("logger\tALL\tTRACE\tDEBUG\tINFO\tWARN\tERROR\tFATAL\tOFF\n");
        for (String logger : fileBuilder.keySet()) {
            xls.append(logger);
            for (String error : errorStates) {
                xls.append("\t").append(fileBuilder.get(logger).getOrDefault(error, 0));
            }
            xls.append("\n");
        }

        out.print(xls.toString());

    }
}
