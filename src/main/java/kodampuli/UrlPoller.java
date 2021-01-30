package kodampuli;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
* Instrumented Class to poll the external urls in the background
* every 1.5s. The metrics are fed to a HM registry for the HTTP
* Server to consume.
 */
public class UrlPoller implements Runnable{

    private Map<String, Long> emitMap = new HashMap<>();
    private boolean isRunning = true;

    public Map<String, Long> getEmitMap() {
        return emitMap;
    }

    final String SAMPLE_EXT_URL_UP = "sample_external_url_up";
    final String SAMPLE_EXT_URL_RESPONSE_MS = "sample_external_url_response_ms";

    String baseHttp200 = "https://httpstat.us/200";
    String baseHttp503 = "https://httpstat.us/503";

    @Override
    public void run() {
        while (isRunning) {
            try {
                hitUrl(baseHttp200);
                hitUrl(baseHttp503);
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopThread(){
        isRunning = false;
    }

    private void hitUrl(String url){
        long code = 0; long elapsedTime;
        try {
            URL urlObj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(3000);

            Date start = new Date();
            con.connect();
            code = con.getResponseCode();
            elapsedTime = (new Date()).getTime() - start.getTime();
            con.disconnect();

            //Populate the HM registry with metric stats
            emitMap.put(SAMPLE_EXT_URL_UP + "{url=\"" + url + " \"}",
                    (long) (code == 200 ? 1 : 0));
            emitMap.put(SAMPLE_EXT_URL_RESPONSE_MS + "{url=\"" + url + " \"}",
                    elapsedTime);

        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
