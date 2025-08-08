package tr.unvercanunlu.url_shortener.service.impl;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.extern.slf4j.Slf4j;
import tr.unvercanunlu.url_shortener.config.AppConfig;
import tr.unvercanunlu.url_shortener.service.UrlShortener;
import tr.unvercanunlu.url_shortener.util.TextUtil;
import tr.unvercanunlu.url_shortener.validation.Validator;
import tr.unvercanunlu.url_shortener.validation.impl.ShortenedValidatorImpl;
import tr.unvercanunlu.url_shortener.validation.impl.UrlValidatorImpl;

@Slf4j
public class RandomBasedUrlShortenerImpl implements UrlShortener {

  // bidirectional storage for fast access
  private final ConcurrentMap<String, String> pairs = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, String> reversePairs = new ConcurrentHashMap<>();

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
      // check exists already
      if (reversePairs.containsKey(url)) {
        String message = "URL already exists: url=%s".formatted(url);

        log.warn(message);

        throw new IllegalStateException(message);
      }

      log.info("URL doesn't exist: url=%s".formatted(url));

      // generate shortened
      int tryCount = 0;
      String shortened;
      do {
        if (tryCount > AppConfig.SHORTEN_TRY_MAX) {
          String message = "Max tries to shorten url exceeded: url=%s try=%d".formatted(url, tryCount);

          log.error(message);

          throw new IllegalStateException(message);
        }

        shortened = TextUtil.randomTextGenerate(
            AppConfig.SHORT_URL_LENGTH,
            AppConfig.SHORT_URL_LENGTH,
            AppConfig.SHORT_URL_CONTAINS_DUPLICATE,
            AppConfig.SHORT_URL_CONTAINS_DIGIT,
            AppConfig.SHORT_URL_CONTAINS_UPPERCASE
        );

        tryCount++;

      } while (pairs.containsKey(shortened));

      log.info("Shortened generated for URL: url=%s shortened=%s".formatted(url, shortened));

      if (tryCount > 1) {
        log.warn("Took %d attempts to generate shortened for url: url=%s".formatted(tryCount, url));
      }

      // store
      pairs.put(shortened, url);
      reversePairs.put(url, shortened);

      log.info("Shortened stored for URL: url=%s shortened=%s".formatted(url, shortened));

      return shortened;

    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public Optional<String> expand(String shortened) {
    // validation
    shortenedValidator.validate(shortened);

    log.info("Shortened validated: shortened=%s".formatted(shortened));

    // retrieve
    Optional<String> expanded = Optional.ofNullable(pairs.get(shortened));

    log.info("URL retrieved by shortened: shortened=%s url=%s".formatted(shortened, expanded.orElse("null")));

    return expanded;
  }

}
