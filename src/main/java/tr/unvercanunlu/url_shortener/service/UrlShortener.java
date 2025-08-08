package tr.unvercanunlu.url_shortener.service;

import java.util.Optional;

public interface UrlShortener {

  String shorten(String url);

  Optional<String> expand(String shortened);

}
