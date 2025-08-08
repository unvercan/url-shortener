package tr.unvercanunlu.url_shortener.model;

import java.time.Instant;

public record UrlEntry(
    String url,
    Instant createdAt,
    long expandCount
) {

}
