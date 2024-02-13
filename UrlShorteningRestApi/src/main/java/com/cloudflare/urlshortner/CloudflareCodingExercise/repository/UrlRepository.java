package com.cloudflare.urlshortner.CloudflareCodingExercise.repository;

import com.cloudflare.urlshortner.CloudflareCodingExercise.model.Url;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UrlRepository extends MongoRepository<Url, String> {
    Url findByShortUrl(String shortUrl);
    String findLongUrlByShortUrl(String shortUrl);
}
