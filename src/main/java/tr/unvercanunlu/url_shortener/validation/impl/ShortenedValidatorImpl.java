package tr.unvercanunlu.url_shortener.validation.impl;

import lombok.extern.slf4j.Slf4j;
import tr.unvercanunlu.url_shortener.config.AppConfig;

@Slf4j
public class ShortenedValidatorImpl extends BaseTextValidator {

  @Override
  public void validate(String shortened) {
    if (isEmpty(shortened)) {
      String message = "Shortened missing!";

      log.error(message);

      throw new IllegalArgumentException(message);
    }

    if (shortened.length() != AppConfig.SHORT_URL_LENGTH) {
      String message = "Shortened length invalid: shortened=%s".formatted(shortened);

      log.error(message);

      throw new IllegalArgumentException(message);
    }
  }

}
