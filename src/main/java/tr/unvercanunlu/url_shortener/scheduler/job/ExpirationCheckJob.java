package tr.unvercanunlu.url_shortener.scheduler.job;

import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;
import tr.unvercanunlu.url_shortener.config.AppConfig;
import tr.unvercanunlu.url_shortener.database.InMemoryDatabase;
import tr.unvercanunlu.url_shortener.model.UrlEntry;
import tr.unvercanunlu.url_shortener.util.TTLHelper;

@Slf4j
public class ExpirationCheckJob implements Runnable {

  @Override
  public void run() {
    for (Entry<String, UrlEntry> pair : InMemoryDatabase.PAIRS.entrySet()) {
      String shortened = pair.getKey();
      UrlEntry entry = pair.getValue();

      // remove if expired
      if ((entry != null) && (entry.createdAt() != null) && AppConfig.TTL_ENABLED) {
        TTLHelper.removeIfExpired(shortened, entry);
      }
    }
  }


}
