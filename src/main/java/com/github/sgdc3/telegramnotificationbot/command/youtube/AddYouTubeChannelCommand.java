package com.github.sgdc3.telegramnotificationbot.command.youtube;

import com.github.sgdc3.telegramnotificationbot.command.AbstractCommand;
import com.github.sgdc3.telegramnotificationbot.storage.GroupData;
import com.github.sgdc3.telegramnotificationbot.storage.GroupDataRepository;
import com.github.sgdc3.telegramnotificationbot.storage.YouTubeChannel;
import com.github.sgdc3.telegramnotificationbot.storage.YouTubeChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Set;

@Component
public class AddYouTubeChannelCommand extends AbstractCommand {

    @Autowired
    private GroupDataRepository groupDataRepository;
    @Autowired
    private YouTubeChannelRepository channelRepository;

    AddYouTubeChannelCommand() {
        super("addyoutubechannel", "Adds a channel id to the current monitored list", true);
    }

    @Override
    public void process(Message message, String[] arguments) {
        if (arguments.length != 1) {
            bot.reply(message, "Invalid arguments!");
            return;
        }
        String channelId = arguments[0];

        GroupData data = groupDataRepository.findByIdOrNew(message.getChatId());
        Set<YouTubeChannel> channels = data.getYouTubeChannels();
        YouTubeChannel channel = channelRepository.findByIdOrNew(channelId);
        if (channels.contains(channel)) {
            bot.reply(message, "This channel is already monitored.");
            return;
        }

        channels.add(channel);
        groupDataRepository.save(data);
        bot.reply(message, "Channel added successfully!");
    }
}
