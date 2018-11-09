package com.github.sgdc3.telegramnotificationbot.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class YouTubeVideo {

    private final static String VIDEO_BASE_URL = "https://www.youtube.com/watch?v=";

    private String id;
    private String title;
    private String description;
    private Instant publishedAt;
    private String channel;

    public String getUrl() {
        return VIDEO_BASE_URL + getId();
    }

    public static YouTubeVideo fromJson(JsonNode node) {
        String videoId = node.get("id").get("videoId").asText();
        JsonNode snippet = node.get("snippet");
        String title = snippet.get("title").asText();
        String description = snippet.get("description").asText();
        String publishedAt = snippet.get("publishedAt").asText();
        String channelTitle = snippet.get("channelTitle").asText();
        return new YouTubeVideo(videoId, title, description, Instant.parse(publishedAt), channelTitle);
    }
}
