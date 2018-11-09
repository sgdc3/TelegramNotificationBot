package com.github.sgdc3.telegramnotificationbot.command.twitch;

import com.github.sgdc3.telegramnotificationbot.command.AbstractCommand;
import com.github.sgdc3.telegramnotificationbot.storage.GroupData;
import com.github.sgdc3.telegramnotificationbot.storage.GroupDataRepository;
import com.github.sgdc3.telegramnotificationbot.storage.TwitchChannel;
import com.github.sgdc3.telegramnotificationbot.storage.TwitchChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Set;

@Component
public class AddTwitchChannelCommand extends AbstractCommand {

    @Autowired
    private GroupDataRepository groupDataRepository;
    @Autowired
    private TwitchChannelRepository channelRepository;

    AddTwitchChannelCommand() {
        super("addtwitchchannel", "Adds a channel id to the current monitored list", true);
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
        Set<TwitchChannel> channels = data.getTwitchChannels();
        TwitchChannel channel = channelRepository.findByIdOrNew(channelId);
        if (channels.contains(channel)) {
            bot.reply(message, "This channel is already monitored.");
            return;
        }

        channels.add(channel);
        groupDataRepository.save(data);
        bot.reply(message, "Channel added successfully!");
    }
}
