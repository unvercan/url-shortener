package tr.unvercanunlu.url_shortener.service.impl;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import tr.unvercanunlu.url_shortener.config.AppConfig;
import tr.unvercanunlu.url_shortener.service.UrlShortener;
import tr.unvercanunlu.url_shortener.util.TextUtil;

public class RandomBasedUrlShortenerImpl implements UrlShortener {

  private final ConcurrentMap<String, String> pairs = new ConcurrentHashMap<>();
  private final ConcurrentMap<String, String> reversePairs = new ConcurrentHashMap<>();

  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  @Override
  public String shorten(String longUrl) {
    // validation
    if ((longUrl == null) || longUrl.isEmpty()) {
      throw new IllegalArgumentException("Long URL missing!");
    }

    // ensure atomicity
    lock.writeLock().lock();

    try {
      // check exists already
      if (reversePairs.containsKey(longUrl)) {
        throw new IllegalStateException("Long URL already exists: url=%s".formatted(longUrl));
      }

      // generate shortened
      int tryCount = 0;
      String shortened;
      do {
        if (tryCount > AppConfig.SHORTEN_TRY_MAX) {
          throw new IllegalStateException("Max tries to shorten url exceeded: try=%d".formatted(tryCount));
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

      pairs.put(shortened, longUrl);
      reversePairs.put(longUrl, shortened);

      return shortened;

    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public Optional<String> expand(String shortened) {
    // validation
    if ((shortened == null) || shortened.isEmpty()) {
      throw new IllegalArgumentException("Shortened URL missing!");
    }

    // retrieve
    return Optional.ofNullable(
        pairs.get(shortened)
    );
  }

}
