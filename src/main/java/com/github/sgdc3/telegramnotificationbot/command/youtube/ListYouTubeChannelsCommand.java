package com.github.sgdc3.telegramnotificationbot.command.youtube;

import com.github.sgdc3.telegramnotificationbot.command.AbstractCommand;
import com.github.sgdc3.telegramnotificationbot.storage.GroupDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class ListYouTubeChannelsCommand extends AbstractCommand {

    @Autowired
    private GroupDataRepository groupDataRepository;

    ListYouTubeChannelsCommand() {
        super("listyoutubechannels", "Lists the current monitored channels", true);
    }

    @Override
    public void process(Message message, String[] arguments) {
        bot.reply(message, "Current YouTube channels:\n"
                + String.join("\n", groupDataRepository.findByIdOrNew(message.getChatId()).getYouTubeChannelIds()));
    }
}
