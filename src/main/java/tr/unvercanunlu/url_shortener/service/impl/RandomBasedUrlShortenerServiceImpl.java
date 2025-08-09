package tr.unvercanunlu.url_shortener.service.impl;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.extern.slf4j.Slf4j;
import tr.unvercanunlu.url_shortener.config.AppConfig;
import tr.unvercanunlu.url_shortener.database.InMemoryDatabase;
import tr.unvercanunlu.url_shortener.model.UrlEntry;
import tr.unvercanunlu.url_shortener.service.UrlShortenerService;
import tr.unvercanunlu.url_shortener.util.TTLHelper;
import tr.unvercanunlu.url_shortener.util.TextHelper;
import tr.unvercanunlu.url_shortener.validation.Validator;
import tr.unvercanunlu.url_shortener.validation.impl.ShortenedValidatorImpl;
import tr.unvercanunlu.url_shortener.validation.impl.UrlValidatorImpl;

@Slf4j
public class RandomBasedUrlShortenerServiceImpl implements UrlShortenerService {

  // synchronization lock
  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  // validators
  private final Validator<String> urlValidator = new UrlValidatorImpl();
  private final Validator<String> shortenedValidator = new ShortenedValidatorImpl();

  @Override
  public String shorten(String url) {
    // validation
    urlValidator.validate(url);
    log.info("URL validated: url=%s".formatted(url));

    // ensure atomicity
    lock.writeLock().lock();

    try {
      // check URL exists
      checkUrlAlreadyExists(url);
      log.info("URL doesn't exist: url=%s".formatted(url));

      // generate shortened
      String shortened = generateValidShortened(url);
      log.info("Shortened generated for URL: url=%s shortened=%s".formatted(url, shortened));

      // store shortened - URL pair
      storeShortenedUrlPair(shortened, url);
      log.info("Shortened stored for URL: url=%s shortened=%s".formatted(url, shortened));

      return shortened;

    } finally {
      // release lock
      lock.writeLock().unlock();
    }
  }

  @Override
  public Optional<String> expand(String shortened) {
    // validation
    shortenedValidator.validate(shortened);
    log.info("Shortened validated: shortened=%s".formatted(shortened));

    // ensure atomicity
    lock.writeLock().lock();

    try {
      // retrieve entry
      UrlEntry entry = InMemoryDatabase.PAIRS.get(shortened);

      // update metrics
      if (entry != null) {
        entry = updateMetric(shortened, entry);
        log.info("Metric of shortened for URL updated: url=%s shortened=%s expandCount=%d".formatted(entry.url(), shortened, entry.expandCount()));
      }

      // remove if expired
      if ((entry != null) && (entry.createdAt() != null) && AppConfig.TTL_ENABLED) {
        TTLHelper.removeIfExpired(shortened, entry);
      }

      Optional<String> url = Optional.ofNullable(entry).map(UrlEntry::url);
      log.info("URL retrieved by shortened: shortened=%s url=%s".formatted(shortened, url.orElse("null")));

      return url;

    } finally {
      // release lock
      lock.writeLock().unlock();
    }
  }

  @Override
  public long countExpand(String shortened) {
    // validation
    shortenedValidator.validate(shortened);
    log.info("Shortened validated: shortened=%s".formatted(shortened));

    long result = 0;

    if (InMemoryDatabase.PAIRS.containsKey(shortened)) {
      UrlEntry entry = InMemoryDatabase.PAIRS.get(shortened);
      result = entry.expandCount();
    } else if (AppConfig.ARCHIVING_ENABLED && InMemoryDatabase.EXPIRED_PAIRS.containsKey(shortened)) {
      UrlEntry expiredEntry = InMemoryDatabase.EXPIRED_PAIRS.get(shortened);
      result = expiredEntry.expandCount();
    }

    log.info("Expand count retrieved for shortened: shortened=%s count=%d".formatted(shortened, result));

    return result;
  }

  private void checkUrlAlreadyExists(String url) {
    // check exists already
    if (InMemoryDatabase.REVERSE_PAIRS.containsKey(url)) {
      String message = "URL already exists: url=%s".formatted(url);

      log.warn(message);

      throw new IllegalStateException(message);
    }
  }

  private String generateValidShortened(String url) {
    int tryCount = 0;
    String shortened;

    do {
      // check max try exceeded
      if (tryCount > AppConfig.SHORTEN_TRY_MAX) {
        String message = "Max tries to shorten url exceeded: url=%s try=%d".formatted(url, tryCount);

        log.error(message);

        throw new IllegalStateException(message);
      }

      // generate randomly
      shortened = TextHelper.randomTextGenerate(
          AppConfig.SHORT_URL_LENGTH,
          AppConfig.SHORT_URL_LENGTH,
          AppConfig.SHORT_URL_CONTAINS_DUPLICATE,
          AppConfig.SHORT_URL_CONTAINS_DIGIT,
          AppConfig.SHORT_URL_CONTAINS_UPPERCASE
      );

      tryCount++;

      // check duplicate
    } while (InMemoryDatabase.PAIRS.containsKey(shortened) || (AppConfig.ARCHIVING_ENABLED && InMemoryDatabase.EXPIRED_PAIRS.containsKey(shortened)));

    // warn attempts
    if (tryCount > 1) {
      log.warn("Took %d attempts to generate shortened for url: url=%s".formatted(tryCount, url));
    }

    return shortened;
  }

  private void storeShortenedUrlPair(String shortened, String url) {
    // handle ttl
    Instant now = null;
    if (AppConfig.TTL_ENABLED) {
      now = Instant.now();
    }

    // create entry
    UrlEntry entry = new UrlEntry(url, now, 0);

    // store URL entry
    InMemoryDatabase.PAIRS.put(shortened, entry);
    InMemoryDatabase.REVERSE_PAIRS.put(url, shortened);
  }

  private UrlEntry updateMetric(String shortened, UrlEntry entry) {
    // increase expand count
    UrlEntry updated = new UrlEntry(
        entry.url(),
        entry.createdAt(),
        (entry.expandCount() + 1)
    );

    // update
    InMemoryDatabase.PAIRS.put(shortened, updated);

    return updated;
  }

}
