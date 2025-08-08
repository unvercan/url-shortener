package tr.unvercanunlu.url_shortener.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppConfig {

  // short url
  public static final int SHORT_URL_LENGTH = 10;
  public static final boolean SHORT_URL_CONTAINS_DUPLICATE = true;
  public static final boolean SHORT_URL_CONTAINS_UPPERCASE = true;
  public static final boolean SHORT_URL_CONTAINS_DIGIT = true;

  // shorten
  public static final int SHORTEN_TRY_MAX = 100;

}
