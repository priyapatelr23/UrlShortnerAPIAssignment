package com.cloudflare.urlshortner.CloudflareCodingExercise.service;

import com.cloudflare.urlshortner.CloudflareCodingExercise.model.Url;
import com.cloudflare.urlshortner.CloudflareCodingExercise.model.UrlRequestDto;

public interface UrlService {
    public Url generateShortUrl(UrlRequestDto urlRequestDto);

    String getLongUrl(String shortUrl);

    void addNewAccessEventForShortUrl(String url);

    int getNumberOfTimesShortUrlIsAccessed(String shortUrl);
}
