package kodampuli;

import com.sun.net.httpserver.HttpServer;
import io.prometheus.client.*;
import io.prometheus.client.exporter.HTTPServer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Date;

public class CheckUrlMain {

    public static void main(String[] args) {
        Gauge external_url_up = Gauge.build().namespace("sample").name("external_url_up")
                .help("tells if the external url is available").labelNames("url").register();
        Gauge external_url_response = Gauge.build().namespace("sample").name("external_url_up_response_ms")
                .help("tells the external url response time").labelNames("url").register();

        try {
            new HTTPServer("0.0.0.0", 8080, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            //Switch to port 80 when good to build docker image
            HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8081), 1000);

            server.createContext(
                    "/",
                    httpExchange -> {
                        //Histogram.Timer requestTimer = requestHistogram.startTimer();

                        String pathParam = httpExchange.getRequestURI().toString().substring(1);
                        int code;
                        long elapsedTime;

                        URL urlObj = new URL(pathParam);
                        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
                        con.setRequestMethod("GET");
                        //Set connection timeout
                        con.setConnectTimeout(3000);

                        Date start = new Date();
                        try {
                            con.connect();
                            code = con.getResponseCode();
                        }finally {
                            elapsedTime = (new Date()).getTime() - start.getTime();
                        }

                        httpExchange.sendResponseHeaders(code, 0);
                        httpExchange.getResponseBody().close();
                        httpExchange.close();
                        //con.disconnect();

                        external_url_response.labels(pathParam).set(elapsedTime);

                        if (code == 200) {
                            external_url_up.labels(pathParam).set(1);
                        } else{
                            external_url_up.labels(pathParam).set(0);
                        }
                    });

            server.start();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
