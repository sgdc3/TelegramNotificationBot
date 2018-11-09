package com.github.sgdc3.telegramnotificationbot.command.twitch;

import com.github.sgdc3.telegramnotificationbot.command.AbstractCommand;
import com.github.sgdc3.telegramnotificationbot.storage.GroupDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.stream.Collectors;

@Component
public class ListTwitchChannelsCommand extends AbstractCommand {

    @Autowired
    private GroupDataRepository groupDataRepository;

    ListTwitchChannelsCommand() {
        super("listtwitchchannels", "Lists the current monitored channels", true);
    }

    @Override
    public void process(Message message, String[] arguments) {
        bot.reply(message, "Current Twitch channels:\n"
                + String.join("\n", groupDataRepository.findByIdOrNew(message.getChatId())
                .getTwitchChannelIds().stream().map(Object::toString).collect(Collectors.toSet())));
    }
}
