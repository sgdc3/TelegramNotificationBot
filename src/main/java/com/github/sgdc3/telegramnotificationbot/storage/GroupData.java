package com.github.sgdc3.telegramnotificationbot.storage;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@RequiredArgsConstructor
@Entity
public class GroupData {

    @Id
    @Getter
    @NonNull
    private Long id;

    @OneToMany(fetch = FetchType.EAGER)
    @Getter
    private Set<YouTubeChannel> youTubeChannels = new HashSet<>();
    @OneToMany(fetch = FetchType.EAGER)
    @Getter
    private Set<TwitchChannel> twitchChannels = new HashSet<>();
    @Column
    @Setter
    private Boolean autoPin;
    @Column
    @Setter
    private String youTubeNotificationMessage;
    @Column
    @Setter
    private String twitchNotificationMessage;

    public Optional<Boolean> getAutoPin() {
        return Optional.ofNullable(autoPin);
    }

    public Optional<String> getYouTubeNotificationMessage() {
        return Optional.ofNullable(youTubeNotificationMessage);
    }

    public Optional<String> getTwitchNotificationMessage() {
        return Optional.ofNullable(twitchNotificationMessage);
    }

    public Set<String> getYouTubeChannelIds() {
        return youTubeChannels.stream().map(YouTubeChannel::getId).collect(Collectors.toSet());
    }

    public Set<Long> getTwitchChannelIds() {
        return twitchChannels.stream().map(TwitchChannel::getId).collect(Collectors.toSet());
    }

    public Optional<YouTubeChannel> removeYouTubeChannelById(String id) {
        for (YouTubeChannel channel : youTubeChannels) {
            if (channel.getId().equals(id)) {
                youTubeChannels.remove(channel);
                return Optional.of(channel);
            }
        }
        return Optional.empty();
    }

    public Optional<TwitchChannel> removeTwitchChannelById(Long id) {
        for (TwitchChannel channel : twitchChannels) {
            if (channel.getId().equals(id)) {
                twitchChannels.remove(channel);
                return Optional.of(channel);
            }
        }
        return Optional.empty();
    }
}
