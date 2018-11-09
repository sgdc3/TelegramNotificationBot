package com.github.sgdc3.telegramnotificationbot.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.philippheuer.twitch4j.model.Stream;

@Getter
@AllArgsConstructor
public class TwitchStream {

    private final static String CHANNEL_BASE_URL = "https://www.twitch.tv/";

    private long id;
    private String title;
    private String game;
    private String channel;
    private String channelName;

    public String getUrl() {
        return CHANNEL_BASE_URL + getChannel();
    }

    public static TwitchStream fromStream(Stream stream) {
        long id = stream.getId();
        String title = stream.getChannel().getStatus();
        String game = stream.getGame();
        String channel = stream.getChannel().getName();
        String channelName = stream.getChannel().getName();
        return new TwitchStream(id, title, game, channel, channelName);
    }
}
