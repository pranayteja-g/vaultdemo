
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sun.net.httpserver.*;

public class CredentialHandler implements HttpHandler {

    public static final List<Credential> credentials = new ArrayList<>(Arrays.asList(
            new Credential("db-user", "admin"),
            new Credential("db-pass", "secret123"),
            new Credential("api-key", "querty123")));

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (method.equals("GET")) {
            handleGet(exchange);
        } else if (method.equals("POST")) {
            handlePost(exchange);
        } else {
            exchange.sendResponseHeaders(405, -1); // method not allowed
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < credentials.size(); i++) {
            Credential c = credentials.get(i);
            json.append(String.format("{\"key\": \"%s\", \"value\": \"%s\"}", c.getKey(), c.getValue()));
            if (i != credentials.size() - 1)
                json.append(",");
        }
        json.append("]");
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, json.toString().getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(json.toString().getBytes());
        os.close();
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        // read request body
        InputStream is = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }
        String json = body.toString();
        String key = extractValue(json, "key");
        String value = extractValue(json, "value");

        if (key == null || value == null) {
            exchange.sendResponseHeaders(400, 0);
            exchange.getResponseBody().write("invalid json".getBytes());
            exchange.getResponseBody().close();
            return;
        }

        boolean exists = credentials.stream().anyMatch(c -> c.getKey().equalsIgnoreCase(key));

        if (exists) {
            exchange.sendResponseHeaders(409, 0); // conflict
            exchange.getResponseBody().write("Key already exists. use a different key".getBytes());
        } else {
            credentials.add(new Credential(key, value));
            exchange.sendResponseHeaders(201, 0);
            exchange.getResponseBody().write("Credential added".getBytes());
        }

        exchange.getResponseBody().close();
        reader.close();
        is.close();
    }

    private String extractValue(String json, String fieldName) {
        String pattern = "\"" + fieldName + "\":\\s*\"(.*?)\"";
        return json.matches(".*" + pattern + ".*") ? json.replaceAll(".*" + pattern + ".*", "$1") : null;
    }

}
