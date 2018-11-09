package com.github.sgdc3.telegramnotificationbot.storage;

import org.springframework.data.repository.CrudRepository;


public interface YouTubeChannelRepository extends CrudRepository<YouTubeChannel, String> {

    default YouTubeChannel findByIdOrNew(String id) {
        return findById(id).orElseGet(() -> {
            YouTubeChannel channel = new YouTubeChannel(id);
            save(channel);
            return channel;
        });
    }
}
