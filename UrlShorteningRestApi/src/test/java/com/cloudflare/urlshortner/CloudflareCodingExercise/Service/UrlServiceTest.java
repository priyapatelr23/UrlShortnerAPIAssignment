package com.cloudflare.urlshortner.CloudflareCodingExercise.Service;

import com.cloudflare.urlshortner.CloudflareCodingExercise.model.Url;
import com.cloudflare.urlshortner.CloudflareCodingExercise.model.UrlRequestDto;
import com.cloudflare.urlshortner.CloudflareCodingExercise.repository.UrlRepository;
import com.cloudflare.urlshortner.CloudflareCodingExercise.service.UrlServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UrlServiceTest {
    @MockBean
    private CacheManager cacheManager;
    @MockBean
    private UrlRepository urlRepository;

    @Autowired
    private UrlServiceImpl urlService;

    @Test
    public void testGenerateShortUrlMethod()
    {
        Url url = new Url();
        url.setShortUrl("absbdsbd");
        url.setLongUrl("https://www.google.com");
        Cache cacheMock = mock(Cache.class);
        when(urlRepository.save(any(Url.class))).thenReturn(url);
        when(cacheManager.getCache("urlCacheMapping")).thenReturn(cacheMock);
        urlService.generateShortUrl(new UrlRequestDto(url.getLongUrl()));
        verify(urlRepository, times(1)).save(any(Url.class));
        verify(cacheMock, times(1)).put(url.getShortUrl(), url.getLongUrl());
    }

    @Test
    public void testGenerateShortUrlMethodSaveToDbFailedThrowsError()
    {
        Url url = new Url();
        url.setLongUrl("https://www.google.com");
        when(urlRepository.save(any(Url.class))).thenReturn(null);
        assertThrows(ResponseStatusException.class, () -> {
            urlService.generateShortUrl(new UrlRequestDto(url.getLongUrl()));
        });
    }

    @Test
    public void testGenerateShortUrlMethodInvalidRequest()
    {
        Url url = new Url();
        url.setLongUrl("");
        assertThrows(ResponseStatusException.class, () -> {
            urlService.generateShortUrl(new UrlRequestDto(url.getLongUrl()));
        });
    }

    @Test
    public void testGetLongUrl()
    {
        Url url = new Url();
        url.setShortUrl("absbdsbd");
        url.setLongUrl("https://www.google.com");
        Cache cacheMock = mock(Cache.class);
        when(urlRepository.findLongUrlByShortUrl(url.getShortUrl())).thenReturn("{\"longUrl\": \"https://www.google.com\"}");
        when(cacheManager.getCache("urlCacheMapping")).thenReturn(cacheMock);
        assertNull(cacheMock.get(url.getShortUrl()));
        // 1st invocation
        urlService.getLongUrl(url.getShortUrl());
        verify(urlRepository, times(1)).findLongUrlByShortUrl(url.getShortUrl());
        verify(cacheMock, times(1)).put(url.getShortUrl(), url.getLongUrl());
        // 2nd invoaction
        when(cacheMock.get(url.getShortUrl())).thenReturn(new SimpleValueWrapper((url.getLongUrl())));
        urlService.getLongUrl(url.getShortUrl());
        verify(urlRepository, times(1)).findLongUrlByShortUrl(url.getShortUrl());
        assertNotNull(cacheMock.get(url.getShortUrl()));

    }

    @Test
    public void testGetLongUrlIfUrlNullThenThrowAnException() {
        Url url = new Url();
        url.setShortUrl("absbdsbd");
        url.setLongUrl("https://www.google.com");
        Cache cacheMock = mock(Cache.class);
        when(cacheManager.getCache("urlCacheMapping")).thenReturn(cacheMock);
        when(urlRepository.findLongUrlByShortUrl(url.getShortUrl())).thenReturn(null);
        assertThrows(ResponseStatusException.class, () -> {
            urlService.getLongUrl(url.getShortUrl());
        });
    }
}
