package tr.unvercanunlu.url_shortener.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tr.unvercanunlu.url_shortener.config.AppConfig;
import tr.unvercanunlu.url_shortener.database.InMemoryDatabase;
import tr.unvercanunlu.url_shortener.model.UrlEntry;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TTLHelper {

  public static void removeIfExpired(String shortened, UrlEntry entry) {
    Instant now = Instant.now();
    Instant end = entry.createdAt().plus(AppConfig.TTL_DAYS, ChronoUnit.DAYS);

    if (end.isBefore(now)) {
      log.info("TTL expired for URL: url=%s shortened=%s".formatted(entry.url(), shortened));

      InMemoryDatabase.PAIRS.remove(shortened);
      InMemoryDatabase.REVERSE_PAIRS.remove(entry.url());
      log.info("Shortened for URL removed because TTL expired: url=%s shortened=%s".formatted(entry.url(), shortened));

      if (AppConfig.ARCHIVING_ENABLED) {
        InMemoryDatabase.EXPIRED_PAIRS.put(shortened, entry);
        log.info("TTL expired shortened for URL archived: url=%s shortened=%s".formatted(entry.url(), shortened));
      }
    }
  }

}
