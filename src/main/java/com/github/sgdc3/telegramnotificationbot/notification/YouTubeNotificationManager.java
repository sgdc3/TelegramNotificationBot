package com.github.sgdc3.telegramnotificationbot.notification;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sgdc3.telegramnotificationbot.BotHandler;
import com.github.sgdc3.telegramnotificationbot.storage.GroupDataRepository;
import com.github.sgdc3.telegramnotificationbot.storage.YouTubeChannel;
import com.github.sgdc3.telegramnotificationbot.storage.YouTubeChannelRepository;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class YouTubeNotificationManager {

    @Autowired
    private GroupDataRepository groupDataRepository;
    @Autowired
    private YouTubeChannelRepository channelRepository;
    @Autowired
    private BotHandler botHandler;

    @Value("${bot.youtube-api-key}")
    private String apiKey;
    @Value("${bot.youtube-default-notification}")
    private String defaultNotification;
    @Value("${bot.youtube-update-interval}")
    private int updateInterval;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Scheduled(fixedRateString = "${bot.youtube-update-interval}")
    @Transactional
    public void update() {
        Map<String, YouTubeVideo> updates = fetchNotifications();
        groupDataRepository.findAll().forEach(groupData -> {
            groupData.getYouTubeChannels().forEach(channel -> {
                Optional.ofNullable(updates.get(channel.getId())).ifPresent(video -> {
                    String message = groupData.getYouTubeNotificationMessage().orElse(defaultNotification)
                            .replace("%title%", video.getTitle())
                            .replace("%description%", video.getDescription())
                            .replace("%url%", video.getUrl())
                            .replace("%channel%", video.getChannel());
                    if (groupData.getAutoPin().orElse(false)) {
                        botHandler.sendPinnedMessage(groupData.getId(), message);
                    } else {
                        botHandler.sendMessage(groupData.getId(), message);
                    }
                });
            });
        });
    }

    public Map<String, YouTubeVideo> fetchNotifications() {
        Map<String, YouTubeVideo> result = new HashMap<>();
        channelRepository.findAll().forEach(channel -> {
            fetchLastVideo(channel).ifPresent(video -> {
                // Ignore already detected video
                if (video.getId().equals(channel.getLastVideo())) {
                    return;
                }
                // Ignore old videos
                if (video.getPublishedAt().isBefore(Instant.now().minus(updateInterval * 2, ChronoUnit.MILLIS))) {
                    return;
                }
                channel.setLastVideo(video.getId());
                channelRepository.save(channel);
                result.put(channel.getId(), video);
            });
        });
        return result;
    }

    public Optional<YouTubeVideo> fetchLastVideo(YouTubeChannel channel) {
        try {
            HttpResponse<String> httpResponse = Unirest.get("https://www.googleapis.com/youtube/v3/search")
                    .queryString("key", apiKey)
                    .queryString("channelId", channel.getId())
                    .queryString("part", "snippet,id")
                    .queryString("order", "date")
                    .queryString("maxResults", 1)
                    .asString();
            if (httpResponse.getStatus() != 200) {
                log.warn("Response: " + httpResponse.getStatus() + " -> " + httpResponse.getStatusText());
                return Optional.empty();
            }
            JsonNode response = objectMapper.readTree(httpResponse.getBody());
            JsonNode items = response.get("items");
            if (items.size() == 0) {
                return Optional.empty();
            }
            JsonNode element = items.get(0);
            return Optional.of(YouTubeVideo.fromJson(element));
        } catch (UnirestException | IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
