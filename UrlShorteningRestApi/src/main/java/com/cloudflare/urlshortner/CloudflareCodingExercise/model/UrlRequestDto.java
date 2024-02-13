package com.cloudflare.urlshortner.CloudflareCodingExercise.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UrlRequestDto {
    String longUrl;
}
