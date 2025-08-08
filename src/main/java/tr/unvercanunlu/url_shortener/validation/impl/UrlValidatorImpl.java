package tr.unvercanunlu.url_shortener.validation.impl;

import java.net.URI;
import java.net.URISyntaxException;
import lombok.extern.slf4j.Slf4j;
import tr.unvercanunlu.url_shortener.config.AppConfig;

@Slf4j
public class UrlValidatorImpl extends BaseTextValidator {

  @Override
  public void validate(String url) {
    if (isEmpty(url)) {
      String message = "URL missing!";

      log.error(message);

      throw new IllegalArgumentException(message);
    }

    parseUrl(url);
  }

  private void parseUrl(String url) {
    URI parsed;

    try {
      parsed = new URI(url);

    } catch (URISyntaxException e) {
      String message = "URL has invalid syntax: url=%s".formatted(url);

      log.error(message);

      throw new IllegalArgumentException(message, e);
    }

    String scheme = parsed.getScheme();
    if ((scheme == null) || !AppConfig.WEB_PROTOCOLS.contains(scheme.toLowerCase())) {
      String message = "Invalid web URL: url=%s".formatted(url);

      log.error(message);

      throw new IllegalArgumentException(message);
    }
  }

}
