package kodampuli;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;

/*
*   Server that produces metrics
 */
public class MetricsServer {

  private HttpServer server;
  private UrlPoller urlPoller = new UrlPoller();
  private Thread t = new Thread(urlPoller);

  public MetricsServer() {
      t.start();
  }

  public void start() throws IOException {
    // create HTTP server endpoint which will expose endpoint to /metrics
    server = HttpServer.create(new InetSocketAddress(8080), 0);
    server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(2));
    server.createContext("/metrics", this::handle);

    server.start();
  }

    public void handle(HttpExchange exchange) throws IOException {
        //Serve only if the path is metrics
        if ("/metrics".equals(exchange.getHttpContext().getPath())) {
            String requestMethod = exchange.getRequestMethod();
            if (requestMethod.equalsIgnoreCase("GET")) {
                Headers responseHeaders = exchange.getResponseHeaders();
                responseHeaders.set("Content-Type", "text/plain");
                exchange.sendResponseHeaders(200, 0);

                OutputStream responseBody = exchange.getResponseBody();
                for (Map.Entry<String, Long> entry : urlPoller.getEmitMap().entrySet()) {
                    String s = entry.getKey() + " " + entry.getValue() + "\n";
                    responseBody.write(s.getBytes());
                }
                responseBody.close();
            }
        }
    }

  public void stop() {
      urlPoller.stopThread();
      server.stop(0);
  }
}
