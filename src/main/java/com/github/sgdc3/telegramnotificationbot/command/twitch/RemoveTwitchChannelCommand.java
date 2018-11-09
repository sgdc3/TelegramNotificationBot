package com.github.sgdc3.telegramnotificationbot.command.twitch;

import com.github.sgdc3.telegramnotificationbot.command.AbstractCommand;
import com.github.sgdc3.telegramnotificationbot.storage.GroupData;
import com.github.sgdc3.telegramnotificationbot.storage.GroupDataRepository;
import com.github.sgdc3.telegramnotificationbot.storage.TwitchChannel;
import com.github.sgdc3.telegramnotificationbot.storage.TwitchChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

@Component
public class RemoveTwitchChannelCommand extends AbstractCommand {

    @Autowired
    private GroupDataRepository groupDataRepository;
    @Autowired
    private TwitchChannelRepository channelRepository;

    RemoveTwitchChannelCommand() {
        super("removetwitchchannel", "Removes a channel id from the current monitored list", true);
    }

    @Override
    public void process(Message message, String[] arguments) {
        if (arguments.length != 1) {
            bot.reply(message, "Invalid arguments!");
            return;
        }
        long channelId;
        try {
            channelId = Long.parseLong(arguments[0]);
        } catch (NumberFormatException e) {
            bot.reply(message, "Invalid channel id!");
            return;
        }

        GroupData data = groupDataRepository.findByIdOrNew(message.getChatId());
        if (!data.getTwitchChannelIds().contains(channelId)) {
            bot.reply(message, "This channel isn't monitored.");
            return;
        }

        Optional<TwitchChannel> removedChannel = data.removeTwitchChannelById(channelId);
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
