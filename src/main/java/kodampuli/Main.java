package kodampuli;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

    MetricsServer metricsServer = new MetricsServer();
    try {
      metricsServer.start();
    } catch (IOException ioException) {
      metricsServer.stop();
    }
  }
}
