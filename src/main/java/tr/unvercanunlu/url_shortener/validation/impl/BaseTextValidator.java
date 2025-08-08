package tr.unvercanunlu.url_shortener.validation.impl;

import tr.unvercanunlu.url_shortener.validation.Validator;

public abstract class BaseTextValidator implements Validator<String> {

  protected boolean isNull(String data) {
    return (data == null);
  }

  protected boolean isEmpty(String data) {
    return isNull(data) || data.isBlank();
  }

}
