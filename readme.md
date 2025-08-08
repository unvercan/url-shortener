# URL Shortener (Random-Based)
A thread-safe, in-memory URL shortener in Java. Supports TTL, usage metrics, and optional archiving of expired links.

## Features
* Random short code generation (configurable)
* Thread-safe with `ConcurrentHashMap` + `ReentrantReadWriteLock`
* Expand count tracking per shortened URL
* TTL support (with optional archiving of expired entries)
* Clear validation and detailed logging

## Configuration (`AppConfig.java`)
```java
SHORT_URL_LENGTH = 10
SHORT_URL_CONTAINS_UPPERCASE = true
SHORT_URL_CONTAINS_DIGIT = true
SHORT_URL_CONTAINS_DUPLICATE = true
SHORTEN_TRY_MAX = 100
WEB_PROTOCOLS = Set.of("http","https")
TTL_ENABLED = true
TTL_DAYS = 30
ARCHIVING_ENABLED = true
```

## API
* `shorten(String url)` → generates and stores a unique short code
* `expand(String shortened)` → returns original URL (if exists & not expired)
* `countExpand(String shortened)` → returns how many times it was expanded

## Example
```java
UrlShortenerService service = new RandomBasedUrlShortenerServiceImpl();
String shortUrl = service.shorten("https://example.com");
String original = service.expand(shortUrl).orElse("Not found");
long count = service.countExpand(shortUrl);
```
