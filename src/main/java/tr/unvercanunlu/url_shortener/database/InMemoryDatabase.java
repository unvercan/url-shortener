package tr.unvercanunlu.url_shortener.database;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import tr.unvercanunlu.url_shortener.model.UrlEntry;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InMemoryDatabase {

  // bidirectional storage for fast access
  public static final ConcurrentMap<String, UrlEntry> PAIRS = new ConcurrentHashMap<>();
  public static final ConcurrentMap<String, String> REVERSE_PAIRS = new ConcurrentHashMap<>();

  // archive
  public static final ConcurrentMap<String, UrlEntry> EXPIRED_PAIRS = new ConcurrentHashMap<>();

}
