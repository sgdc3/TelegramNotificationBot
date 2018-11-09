package com.github.sgdc3.telegramnotificationbot.notification;

import com.github.sgdc3.telegramnotificationbot.BotHandler;
import com.github.sgdc3.telegramnotificationbot.storage.GroupDataRepository;
import com.github.sgdc3.telegramnotificationbot.storage.TwitchChannel;
import com.github.sgdc3.telegramnotificationbot.storage.TwitchChannelRepository;
import lombok.extern.slf4j.Slf4j;
import me.philippheuer.twitch4j.TwitchClient;
import me.philippheuer.twitch4j.TwitchClientBuilder;
import me.philippheuer.twitch4j.model.Channel;
import me.philippheuer.twitch4j.model.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class TwitchNotificationManager {

    @Autowired
    private GroupDataRepository groupDataRepository;
    @Autowired
    private TwitchChannelRepository channelRepository;
    @Autowired
    private BotHandler botHandler;

    @Value("${bot.twitch-client-id}")
    private String clientId;
    @Value("${bot.twitch-client-secret}")
    private String clientSecret;
    @Value("${bot.twitch-default-notification}")
    private String defaultNotification;
    @Value("${bot.twitch-update-interval}")
    private int updateInterval;

    private TwitchClient client;

    @PostConstruct
    public void postConstruct() {
        client = TwitchClientBuilder.init()
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .build();
    }

    @Scheduled(fixedRateString = "${bot.twitch-update-interval}")
    @Transactional
    public void update() {
        Map<Long, TwitchStream> updates = fetchNotifications();
        groupDataRepository.findAll().forEach(groupData -> {
            groupData.getTwitchChannels().forEach(channel -> {
                Optional.ofNullable(updates.get(channel.getId())).ifPresent(stream -> {
                    String message = groupData.getTwitchNotificationMessage().orElse(defaultNotification)
                            .replace("%title%", stream.getTitle())
                            .replace("%game%", stream.getGame())
                            .replace("%url%", stream.getUrl())
                            .replace("%channel%", stream.getChannel())
                            .replace("%channelName%", stream.getChannelName());
                    if (groupData.getAutoPin().orElse(false)) {
                        botHandler.sendPinnedMessage(groupData.getId(), message);
                    } else {
                        botHandler.sendMessage(groupData.getId(), message);
                    }
                });
            });
        });
    }

    public Map<Long, TwitchStream> fetchNotifications() {
        Map<Long, TwitchStream> result = new HashMap<>();
        channelRepository.findAll().forEach(channel -> {
            fetchCurrentStream(channel).ifPresent(stream -> {
                // Ignore already detected video
                if (Objects.equals(stream.getId(), channel.getLastStream())) {
                    return;
                }
                channel.setLastStream(stream.getId());
                channelRepository.save(channel);
                result.put(channel.getId(), stream);
            });
        });
        return result;
    }

    public Optional<TwitchStream> fetchCurrentStream(TwitchChannel channel) {
        Channel streamer = client.getChannelEndpoint().getChannel(channel.getId());
        Stream stream = client.getStreamEndpoint().getByChannel(streamer);
        if(stream == null) {
            return Optional.empty();
        }
        return Optional.of(TwitchStream.fromStream(stream));
    }
}
