package com.github.sgdc3.telegramnotificationbot.storage;

import org.springframework.data.repository.CrudRepository;


public interface TwitchChannelRepository extends CrudRepository<TwitchChannel, Long> {

    default TwitchChannel findByIdOrNew(Long id) {
        return findById(id).orElseGet(() -> {
            TwitchChannel channel = new TwitchChannel(id);
            save(channel);
            return channel;
        });
    }
}
