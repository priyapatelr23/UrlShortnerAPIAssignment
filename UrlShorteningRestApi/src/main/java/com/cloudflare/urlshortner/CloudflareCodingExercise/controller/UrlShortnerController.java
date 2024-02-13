package com.cloudflare.urlshortner.CloudflareCodingExercise.controller;

import com.cloudflare.urlshortner.CloudflareCodingExercise.model.Url;
import com.cloudflare.urlshortner.CloudflareCodingExercise.model.UrlRequestDto;
import com.cloudflare.urlshortner.CloudflareCodingExercise.model.UrlResponseDto;
import com.cloudflare.urlshortner.CloudflareCodingExercise.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
public class UrlShortnerController {
    @Autowired
    private UrlService urlService;

    @PostMapping("/generate")
    public ResponseEntity<UrlResponseDto> createShortUrl(@RequestBody UrlRequestDto urlRequestDto)
    {
        Url urlObject = urlService.generateShortUrl(urlRequestDto);
        UrlResponseDto urlResponseDto = new UrlResponseDto();
        urlResponseDto.setShortUrl(urlObject.getShortUrl());
        return ResponseEntity.status(HttpStatus.OK).body(urlResponseDto);
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<?> redirectToLongUrl(@PathVariable String shortUrl, HttpServletResponse response) throws IOException {
        if(StringUtils.isNotEmpty(shortUrl))
        {
            String url = null;
            url = urlService.getLongUrl(shortUrl);
            response.sendRedirect(url);
            // Increment the access count of short url asynchronously
            urlService.addNewAccessEventForShortUrl(shortUrl);

        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request please try again");
    }

    @GetMapping("/url/{shortUrl}")
    public ResponseEntity<?> getNumberOfTimesShortUrlIsAccessed(@PathVariable String shortUrl) throws IOException {
        if(StringUtils.isNotEmpty(shortUrl))
        {
            return ResponseEntity.status(HttpStatus.OK).body(urlService.getNumberOfTimesShortUrlIsAccessed(shortUrl));
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request please try again");
    }

}
