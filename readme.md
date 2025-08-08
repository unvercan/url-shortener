# URL Shortener (Random-Based)
A simple, thread-safe, in-memory URL shortener in Java. It generates random short strings for long URLs and stores them bidirectionally for fast
lookup.

## Features
- Random short URL generation (configurable)
- Thread-safe with `ConcurrentHashMap` + `ReentrantReadWriteLock`
- Prevents duplicates (1 short → 1 long)
- Retry on collision
- Clean validation and error handling

## Config (`AppConfig.java`)
```java
SHORT_URL_LENGTH = 10
SHORT_URL_CONTAINS_UPPERCASE = true
SHORT_URL_CONTAINS_DIGIT = true
SHORT_URL_CONTAINS_DUPLICATE = true
SHORTEN_TRY_MAX = 100
````

## Key Methods
### `shorten(String longUrl)`
Generates a unique short code for a given long URL.

### `expand(String shortened)`
Returns the original long URL (if exists).

## Example
```java
UrlShortener service = new RandomBasedUrlShortenerImpl();
String shortUrl = service.shorten("https://example.com");
String original = service.expand(shortUrl).orElse("Not found");
```
