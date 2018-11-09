package com.github.sgdc3.telegramnotificationbot.command.youtube;

import com.github.sgdc3.telegramnotificationbot.command.AbstractCommand;
import com.github.sgdc3.telegramnotificationbot.storage.GroupData;
import com.github.sgdc3.telegramnotificationbot.storage.GroupDataRepository;
import com.github.sgdc3.telegramnotificationbot.storage.YouTubeChannel;
import com.github.sgdc3.telegramnotificationbot.storage.YouTubeChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

@Component
public class RemoveYouTubeChannelCommand extends AbstractCommand {

    @Autowired
    private GroupDataRepository groupDataRepository;
    @Autowired
    private YouTubeChannelRepository channelRepository;

    RemoveYouTubeChannelCommand() {
        super("removeyoutubechannel", "Removes a channel id from the current monitored list", true);
    }

    @Override
    public void process(Message message, String[] arguments) {
        if (arguments.length != 1) {
            bot.reply(message, "Invalid arguments!");
            return;
        }
        String channelId = arguments[0];

        GroupData data = groupDataRepository.findByIdOrNew(message.getChatId());
        if (!data.getYouTubeChannelIds().contains(channelId)) {
            bot.reply(message, "This channel isn't monitored.");
            return;
        }

        Optional<YouTubeChannel> removedChannel = data.removeYouTubeChannelById(channelId);
        groupDataRepository.save(data);
        removedChannel.ifPresent(channel -> {
            // Cleanup
            if (channel.getGroups().isEmpty()) {
                channelRepository.delete(channel);
            }
        });
        bot.reply(message, "Channel removed successfully!");
    }
}
