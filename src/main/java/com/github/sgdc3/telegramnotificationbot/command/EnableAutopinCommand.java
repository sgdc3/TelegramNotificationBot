package com.github.sgdc3.telegramnotificationbot.command;

import com.github.sgdc3.telegramnotificationbot.storage.GroupData;
import com.github.sgdc3.telegramnotificationbot.storage.GroupDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class EnableAutopinCommand extends AbstractCommand {

    @Autowired
    private GroupDataRepository groupDataRepository;

    EnableAutopinCommand() {
        super("enableautopin", "Enables the automatic message pin feature", true);
    }

    @Override
    public void process(Message message, String[] arguments) {
        GroupData data = groupDataRepository.findByIdOrNew(message.getChatId());
        data.setAutoPin(true);
        groupDataRepository.save(data);
        bot.reply(message, "Auto-pin enabled!");
    }
}
