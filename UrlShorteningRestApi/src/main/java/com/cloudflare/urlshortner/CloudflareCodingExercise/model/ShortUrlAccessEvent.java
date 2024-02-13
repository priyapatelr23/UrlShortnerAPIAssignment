package com.cloudflare.urlshortner.CloudflareCodingExercise.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShortUrlAccessEvent {
    private LocalDateTime accessTime;
}
