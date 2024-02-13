package com.cloudflare.urlshortner.CloudflareCodingExercise.controller;

import com.cloudflare.urlshortner.CloudflareCodingExercise.model.Url;
import com.cloudflare.urlshortner.CloudflareCodingExercise.model.UrlRequestDto;
import com.cloudflare.urlshortner.CloudflareCodingExercise.model.UrlResponseDto;
import com.cloudflare.urlshortner.CloudflareCodingExercise.service.UrlService;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class UrlShortnerControllerTest {
    @InjectMocks
    UrlShortnerController urlShortnerController;
    @Mock
    private UrlService urlService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateShortUrlValidRequest() {
        Url url = new Url();
        url.setShortUrl("absbdsbd");
        url.setLongUrl("https://www.google.com");
        UrlRequestDto urlRequestDto = new UrlRequestDto("https://www.google.com");
        when(urlService.generateShortUrl(urlRequestDto)).thenReturn(url);
        ResponseEntity<UrlResponseDto> responseEntity = urlShortnerController.createShortUrl(urlRequestDto);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseEntity.getBody().getShortUrl(), url.getShortUrl());
        verify(urlService, times(1)).generateShortUrl(urlRequestDto);
    }

    @Test
    public void testCreateShortUrlInvalidRequest() {
        UrlRequestDto urlRequestDto = new UrlRequestDto("");
        when(urlService.generateShortUrl(urlRequestDto)).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request please try again"));
        assertThrows(ResponseStatusException.class, () -> {
            urlShortnerController.createShortUrl(urlRequestDto);
        });

    }

    @Test
    public void testRedirectToLongUrlValidTest(){
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(urlShortnerController).build();
        Url url = new Url();
        url.setShortUrl("absbdsbd");
        url.setLongUrl("https://www.google.com");
        when(urlService.getLongUrl(url.getShortUrl())).thenReturn(url.getLongUrl());
        try {
            mockMvc.perform(get("/absbdsbd"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("https://www.google.com"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(urlService, times(1)).getLongUrl(url.getShortUrl());
    }

    @Test
    public void testRedirectToLongUrlTestInvalidRequest(){
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(urlShortnerController).build();
        assertThrows(ServletException.class, () -> {
            mockMvc.perform(get("/absbdsbd"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("https://www.google.com"));
        });
    }

    @Test
    public void testGetNumberOfTimesShortUrlIsAccessedValidRequest() throws IOException {
        Url url = new Url();
        url.setShortUrl("absbdsbd");
        url.setLongUrl("https://www.google.com");
        when(urlService.getNumberOfTimesShortUrlIsAccessed(url.getShortUrl())).thenReturn(1);
        ResponseEntity<?> responseEntity = urlShortnerController.getNumberOfTimesShortUrlIsAccessed(url.getShortUrl());
        assertEquals(responseEntity.getBody().toString(), "1");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        verify(urlService, times(1)).getNumberOfTimesShortUrlIsAccessed(url.getShortUrl());
    }

    @Test
    public void testGetNumberOfTimesShortUrlIsAccessedInValidRequest() throws IOException {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(urlShortnerController).build();
        assertThrows(ServletException.class, () -> {
            mockMvc.perform(get("/url/"));
        });
    }

}
