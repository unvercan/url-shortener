# URL Shortener (Random-Based)
A thread-safe, in-memory URL shortener in Java.
Generates random short codes for valid URLs and stores them bidirectionally for fast lookup.

## Features
* Random short URL generation (configurable)
* Thread-safe with `ConcurrentHashMap` + `ReentrantReadWriteLock`
* Duplicate prevention and collision retry
* Input validation and clear error handling
* Detailed logging with SLF4J

## Configuration (`AppConfig.java`)
```java
SHORT_URL_LENGTH = 10
SHORT_URL_CONTAINS_UPPERCASE = true
SHORT_URL_CONTAINS_DIGIT = true
SHORT_URL_CONTAINS_DUPLICATE = true
SHORTEN_TRY_MAX = 100
WEB_PROTOCOLS = Set.of("http", "https")
```

## API
### `shorten(String url)`
Generates and stores a unique short code for the given URL.

### `expand(String shortened)`
Returns the original URL if it exists.

## Example
```java
UrlShortener service = new RandomBasedUrlShortenerImpl();
String shortUrl = service.shorten("https://example.com");
String original = service.expand(shortUrl).orElse("Not found");
```
