package com.github.sgdc3.telegramnotificationbot.command;

import com.github.sgdc3.telegramnotificationbot.BotHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Getter
@Component
@Slf4j
public abstract class AbstractCommand implements IBotCommand {

    @Autowired
    protected BotHandler bot;

    private String commandIdentifier;
    private String description;
    private boolean adminOnly;

    protected AbstractCommand(String commandIdentifier, String description, boolean adminOnly) {
        this.commandIdentifier = commandIdentifier;
        this.description = description;
        this.adminOnly = adminOnly;
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        try {
            if (isAdminOnly() && !bot.isAdministrator(message.getFrom(), message.getChat())) {
                return;
            }
            process(message, arguments);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void process(Message message, String[] arguments) throws TelegramApiException;
}
