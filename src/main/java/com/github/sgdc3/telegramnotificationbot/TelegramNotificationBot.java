package com.github.sgdc3.telegramnotificationbot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
@EnableScheduling
@PropertySource(value = "classpath:application.properties", encoding = "UTF-8")
@Slf4j
public class TelegramNotificationBot {

    @Autowired
    private BotHandler botHandler;

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(TelegramNotificationBot.class, args);
    }
}
