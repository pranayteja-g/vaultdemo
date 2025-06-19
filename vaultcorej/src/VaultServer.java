

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class VaultServer {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.createContext("/credentials", new CredentialHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Vault Server running at http://localhost:8081/credentials");
    }
}
