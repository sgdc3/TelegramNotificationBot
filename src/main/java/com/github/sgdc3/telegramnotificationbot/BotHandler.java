package com.github.sgdc3.telegramnotificationbot;

import com.github.sgdc3.telegramnotificationbot.command.DisableAutopinCommand;
import com.github.sgdc3.telegramnotificationbot.command.EnableAutopinCommand;
import com.github.sgdc3.telegramnotificationbot.command.twitch.AddTwitchChannelCommand;
import com.github.sgdc3.telegramnotificationbot.command.twitch.ListTwitchChannelsCommand;
import com.github.sgdc3.telegramnotificationbot.command.twitch.RemoveTwitchChannelCommand;
import com.github.sgdc3.telegramnotificationbot.command.twitch.SetTwitchNotificationCommand;
import com.github.sgdc3.telegramnotificationbot.command.youtube.AddYouTubeChannelCommand;
import com.github.sgdc3.telegramnotificationbot.command.youtube.ListYouTubeChannelsCommand;
import com.github.sgdc3.telegramnotificationbot.command.youtube.RemoveYouTubeChannelCommand;
import com.github.sgdc3.telegramnotificationbot.command.youtube.SetYouTubeNotificationCommand;
import com.github.sgdc3.telegramnotificationbot.notification.TwitchNotificationManager;
import com.github.sgdc3.telegramnotificationbot.notification.YouTubeNotificationManager;
import com.github.sgdc3.telegramnotificationbot.storage.GroupDataRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Slf4j
@Service
public class BotHandler extends TelegramLongPollingCommandBot {

    @Autowired
    private GroupDataRepository groupDataRepository;

    @Autowired
    private YouTubeNotificationManager youTubeNotificationManager;
    @Autowired
    private TwitchNotificationManager twitchNotificationManager;

    @Autowired
    private EnableAutopinCommand enableAutopinCommand;
    @Autowired
    private DisableAutopinCommand disableAutopinCommand;

    @Autowired
    private AddYouTubeChannelCommand addYTChannelCommand;
    @Autowired
    private RemoveYouTubeChannelCommand removeYTChannelCommand;
    @Autowired
    private ListYouTubeChannelsCommand listYTChannelsCommand;
    @Autowired
    private SetYouTubeNotificationCommand setYtNotificationCommand;

    @Autowired
    private AddTwitchChannelCommand addTwitchChannelCommand;
    @Autowired
    private RemoveTwitchChannelCommand removeTwitchChannelCommand;
    @Autowired
    private ListTwitchChannelsCommand listTwitchChannelsCommand;
    @Autowired
    private SetTwitchNotificationCommand setTwitchNotificationCommand;

    @Value("${bot.token}")
    @Getter
    private String botToken;
    @Getter
    private User botUser;

    @Autowired
    BotHandler(@Value("${bot.username}") final String username) {
        super(username);
    }

    @PostConstruct
    void postConstruct() {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        registerCommands();
        try {
            telegramBotsApi.registerBot(this);
            botUser = getBotUser();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        log.info("Ready!");
    }

    @Override
    protected boolean filter(Message message) {
        if (message.getDate() < System.currentTimeMillis() / 5000) {
            return true;
        }
        if (!message.isSuperGroupMessage()) {
            sendMessage(message.getChatId(), "This bot can be used only in supergroups, i'm sorry.");
            return true;
        }
        return false;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.getMessage() == null) {
            return;
        }
        final Message message = update.getMessage();
        Optional.ofNullable(message.getLeftChatMember()).ifPresent(user -> {
            if (!user.getId().equals(botUser.getId())) {
                return;
            }
            groupDataRepository.deleteById(message.getChatId());
        });
    }

    private void registerCommands() {
        registerAll(enableAutopinCommand, disableAutopinCommand, addYTChannelCommand, removeYTChannelCommand, listYTChannelsCommand,
                setYtNotificationCommand, addTwitchChannelCommand, removeTwitchChannelCommand, listTwitchChannelsCommand,
                setTwitchNotificationCommand);
    }

    public Boolean isAdministrator(User user, Chat chat) throws TelegramApiException {
        ChatMember chatMember = execute(new GetChatMember().setChatId(chat.getId()).setUserId(user.getId()));
        return chatMember.getStatus().equals("creator") || chatMember.getStatus().equals("administrator");
    }

    public Message sendMessage(Long chatId, String message) {
        try {
            return execute(new SendMessage().setChatId(chatId).setText(message));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public Message sendMessage(Chat chat, String message) {
        return sendMessage(chat.getId(), message);
    }

    public Message reply(Message message, String reply) {
        try {
            return execute(new SendMessage().setChatId(message.getChatId())
                    .setReplyToMessageId(message.getMessageId()).setText(reply));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public Message sendPinnedMessage(Long chatId, String message) {
        Message sent = sendMessage(chatId, message);
        try {
            execute(new PinChatMessage().setChatId(chatId).setMessageId(sent.getMessageId()).setDisableNotification(false));
            return sent;
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public Message sendPinnedMessage(Chat chat, String message) {
        return sendPinnedMessage(chat.getId(), message);
    }
}
