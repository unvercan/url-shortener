package tr.unvercanunlu.url_shortener.validation;

public interface Validator<T> {

  void validate(T data);

}
