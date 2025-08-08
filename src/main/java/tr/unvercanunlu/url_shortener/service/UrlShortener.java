package tr.unvercanunlu.url_shortener.service;

import java.net.URISyntaxException;
import java.util.Optional;

public interface UrlShortener {

  String shorten(String longUrl) throws URISyntaxException;

  Optional<String> expand(String shortened);

}
