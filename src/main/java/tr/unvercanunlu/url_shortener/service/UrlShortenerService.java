package tr.unvercanunlu.url_shortener.service;

import java.util.Optional;

public interface UrlShortenerService {

  String shorten(String url);

  Optional<String> expand(String shortened);

  long countExpand(String shortened);

}
