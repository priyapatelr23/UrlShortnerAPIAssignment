package com.cloudflare.urlshortner.CloudflareCodingExercise.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "url")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Url implements Serializable {
    @Id
    private String id;
    private String longUrl;
    @Indexed
    private String shortUrl;
    private List<ShortUrlAccessEvent> shortUrlAccessEvents;

    public int getCount()
    {
        LocalDateTime lastTwenyFourHoursAgo = LocalDateTime.now().minusHours(24);
        int count = 0;
        for (ShortUrlAccessEvent event : shortUrlAccessEvents) {
            if (event.getAccessTime().isAfter(lastTwenyFourHoursAgo)) {
                count++;
            }
        }
        return count;
    }

    public void removeOldAccessEventsAddNewEvent(ShortUrlAccessEvent newShortUrlAccessEvent)
    {
        if (shortUrlAccessEvents == null) {
            shortUrlAccessEvents = new ArrayList<>();
        }
        LocalDateTime lastTwenyFourHoursAgo = LocalDateTime.now().minusHours(24);
        shortUrlAccessEvents.removeIf(shortUrlAccessEvent -> shortUrlAccessEvent.getAccessTime().isBefore(lastTwenyFourHoursAgo));
        // Add the new shortUrlAccessEvent to the accessEvents list
        shortUrlAccessEvents.add(newShortUrlAccessEvent);
    }
}
