package com.cloudflare.urlshortner.CloudflareCodingExercise.service;

import com.cloudflare.urlshortner.CloudflareCodingExercise.model.ShortUrlAccessEvent;
import com.cloudflare.urlshortner.CloudflareCodingExercise.model.Url;
import com.cloudflare.urlshortner.CloudflareCodingExercise.model.UrlRequestDto;
import com.cloudflare.urlshortner.CloudflareCodingExercise.repository.UrlRepository;
import com.google.common.hash.Hashing;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class UrlServiceImpl implements UrlService {
    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private CacheManager cacheManager;

    @Override
    public Url generateShortUrl(UrlRequestDto urlRequestDto) {
        if(StringUtils.isNotEmpty(urlRequestDto.getLongUrl()))
        {
            // Step 1: Generate short encoded url
            String encodedShortUrl = encodeLongUrl(urlRequestDto.getLongUrl());
            // Step 2: Create Url object to store to DB
            Url url = new Url();
            url.setShortUrl(encodedShortUrl);
            url.setLongUrl(urlRequestDto.getLongUrl());
            // Step 3: Save the Url object in DB
            Url urlResponse = urlRepository.save(url);

            // Step 4: Return the url response if url is saved to DB
            if(urlResponse != null)
            {
                // Step 5: Save url mapping to cache
                saveUrlToCache(urlResponse.getShortUrl(), urlResponse.getLongUrl());
                return urlResponse;
            }
            // Step 6: Throw an exception if url not saved to DB
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong, please try at a later time");
        }
        //Step 7: Throw an exception if long url is empty
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request please try again");
    }

    private void saveUrlToCache(String shortUrl, String longUrl)
    {
        // Get the cache by name
        Cache cache = cacheManager.getCache("urlCacheMapping");
        // Put short url and longurl mapping in cache
        cache.put(shortUrl, longUrl);
    }

    @Override
    @Cacheable(value = "urlCacheMapping", key = "#shortUrl")
    public String getLongUrl(String shortUrl) {
        // Step 1: Query db to find URL object using short url if not found in cache
        String result = urlRepository.findLongUrlByShortUrl(shortUrl);
        if(result != null) {
            String longUrl = null;
            try {
                longUrl = extractLongUrlFromJson(result);
            } catch (JSONException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong, please try at a later time");
            }
            // Step 2: If long url not null return long url
            if (longUrl != null) {
                return longUrl;
            }
        }
        // Step 3: If no object found in the db with the given short url throw error
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request please try again");
    }

    public static String extractLongUrlFromJson(String jsonString) throws JSONException {
        jsonString = jsonString.replace("Optional[", "").replace("]", "");
        JSONObject jsonObject = new JSONObject(jsonString);
        return jsonObject.getString("longUrl");
    }
    @Override
    @Async
    public void addNewAccessEventForShortUrl(String url) {
        // Step 1: Get the latest Url object from the DB
        Url latestUrlObject = urlRepository.findByShortUrl(url);
        if(latestUrlObject != null) {
            // Step 2: Add the new shortUrl access event to the list and also remove events that are older than 24hr
            latestUrlObject.removeOldAccessEventsAddNewEvent(new ShortUrlAccessEvent(LocalDateTime.now()));
            // Step 3: Save to repository
            urlRepository.save(latestUrlObject);
        }
    }

    @Override
    public int getNumberOfTimesShortUrlIsAccessed(String shortUrl) {
        Url latestUrlObject = urlRepository.findByShortUrl(shortUrl);
        // If entry found in db return the count or else throw an exception
        if(latestUrlObject != null)
        {
            return latestUrlObject.getCount();
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request please try again");
    }

    private String encodeLongUrl(String longUrl)
    {
        // Encode the long url to generate the short url
        String shortEncodedUrl = "";
        LocalDateTime time = LocalDateTime.now();
        // Append current time to long url to ensure new encoded url is generated each time even for duplicate long urls
        longUrl = longUrl.concat(time.toString());
        shortEncodedUrl = Hashing.murmur3_32()
                .hashString(longUrl, StandardCharsets.UTF_8)
                .toString();
        return  shortEncodedUrl;
    }
}
