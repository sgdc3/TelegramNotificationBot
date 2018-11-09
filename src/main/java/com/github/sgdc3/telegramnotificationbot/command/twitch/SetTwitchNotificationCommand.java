package com.github.sgdc3.telegramnotificationbot.command.twitch;

import com.github.sgdc3.telegramnotificationbot.command.AbstractCommand;
import com.github.sgdc3.telegramnotificationbot.storage.GroupData;
import com.github.sgdc3.telegramnotificationbot.storage.GroupDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class SetTwitchNotificationCommand extends AbstractCommand {

    @Autowired
    private GroupDataRepository groupDataRepository;

    SetTwitchNotificationCommand() {
        super("settwitchnotification", "Sets the custom twitch notification format", true);
    }

    @Override
    public void process(Message message, String[] arguments) {
        GroupData data = groupDataRepository.findByIdOrNew(message.getChatId());
        if (arguments.length == 0) {
            data.setTwitchNotificationMessage(null);
            groupDataRepository.save(data);
            bot.reply(message, "Notification message has been reset to the default one!");
        } else {
            data.setTwitchNotificationMessage(String.join(" ", arguments));
            groupDataRepository.save(data);
            bot.reply(message, "Notification message set!");
        }
    }
}
