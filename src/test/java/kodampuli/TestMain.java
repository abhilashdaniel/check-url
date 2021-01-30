package kodampuli;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;

public class TestMain {

    MetricsServer metricsServer;
    HashMap<String, String> hmMetrics = new HashMap<>();
    String baseUrl200 = "sample_external_url_up{url=\"https://httpstat.us/200 \"}";
    String baseUrl503 = "sample_external_url_up{url=\"https://httpstat.us/503 \"}";
    String baseUrl200Response = "sample_external_url_response_ms{url=\"https://httpstat.us/200 \"}";
    String baseUrl503Response = "sample_external_url_response_ms{url=\"https://httpstat.us/503 \"}";

    @Before
    public void setUp() throws Exception {
        metricsServer = new MetricsServer();
        metricsServer.start();
        Thread.sleep(5000);
        getMetrics();
    }

    @After
    public void tearDown() throws Exception {
        Thread.sleep(5000);
        metricsServer.stop();
    }

    @Test
    public void testMetrics() throws IOException {
            assertTrue(hmMetrics.containsKey(baseUrl200) && hmMetrics.containsKey(baseUrl200Response) &&
                    hmMetrics.containsKey(baseUrl503) && hmMetrics.containsKey(baseUrl503Response));
    }

    public void getMetrics() throws IOException {
        URL obj = new URL("http://localhost:8080/metrics");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                String[] mSplit = inputLine.split(" ");
                String key = mSplit[0] + " " + mSplit[1];
                hmMetrics.put(key, mSplit[2]);
            }
            in.close();
        } else {
            fail();
        }
    }

}
