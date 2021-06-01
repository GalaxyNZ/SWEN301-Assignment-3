package nz.ac.wgtn.swen301.a3.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;

public class Client {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Invalid number of arguments");
            return;
        }

        String fileType = args[0];
        String fileName = args[1];

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request;
            if (fileType.toLowerCase().equals("excel")) {
                if (!fileName.toLowerCase().endsWith(".xlsx")) {
                    System.out.println("File must be xlsx ");
                    return;
                }

                request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8080/resthome4logs/statsxls"))
                        .setHeader("Content-Disposition", "attachment; filename=" + fileName).build();
                client.send(request, HttpResponse.BodyHandlers.ofFile(Paths.get(fileName)));
            } else if (fileType.toLowerCase().equals("csv")) {
                if (!fileName.toLowerCase().endsWith(".csv")) {
                    System.out.println("File must be a CSV");
                    return;
                }
                request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8080/resthome4logs/statscsv"))
                        .setHeader("Content-Disposition", "attachment; filename=" + fileName).build();
                client.send(request, HttpResponse.BodyHandlers.ofFile(Paths.get(fileName)));
            } else {
                System.out.println("FileType must be either CSV or XLSX");
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("Can't connect to server");
        }

        System.out.println("File created");
    }
}