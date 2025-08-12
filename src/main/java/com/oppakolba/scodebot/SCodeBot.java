package com.oppakolba.scodebot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;

@Component
public class SCodeBot implements SpringLongPollingBot {
    private UpdateConsumer updateConsumer;

    public SCodeBot (UpdateConsumer updateConsumer) {
        this.updateConsumer = updateConsumer;

    }


    @Override
    public String getBotToken() {
        return "8497113011:AAGGJjjeCxqkZ046Twu-asDs6YO0bNXW0FI";
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return updateConsumer;
    }

}