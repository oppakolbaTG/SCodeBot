package com.oppakolba.scodebot;

import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;


@Component
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private static final String API_KEY = "sk-df56003917424e049f7dd5253697acf3";
    private static final String BASE_URL = "https://api.deepseek.com/v1";

    public UpdateConsumer() {
        this.telegramClient = new OkHttpTelegramClient("8497113011:AAGGJjjeCxqkZ046Twu-asDs6YO0bNXW0FI");
    }


    @SneakyThrows
    @Override
    public void consume(Update update) {
        String uMessage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        if("/start".equals(uMessage)){
            System.out.printf("Пришло сообщение: %s от: %s\n", uMessage, chatId);
            SendMessage mainMessage = SendMessage.builder().text(" Привет! \uD83D\uDE0A Я - умный чат-бот с искусственным интеллектом. \n" +
                    "        Задавайте мне любые вопросы, и я постараюсь дать полезный и развернутый ответ!\n" +
                    "        \n" +
                    "        • Могу объяснить сложные понятия простыми словами\n" +
                    "        • Помочь с учебой и работой\n" +
                    "        • Обсудить интересные темы\n" +
                    "        • И даже просто поболтать \uD83D\uDE09\n" +
                    "\n" +
                    "        Не стесняйтесь спрашивать о чем угодно! Каков ваш вопрос?").chatId(chatId).build();
            telegramClient.execute(mainMessage);
        }
        else {
            System.out.printf("Пришло сообщение: %s от: %s\n", uMessage, chatId);
            SendMessage loadmessage = SendMessage.builder().text("Подождите немного сообщение загружается").chatId(chatId).build();
            Message execitedlm = telegramClient.execute(loadmessage);
            int messid = execitedlm.getMessageId();
            System.out.println("Message id:" + messid);

            var body = String.format("""
                    {
                        "model": "deepseek-chat",
                        "messages": [
                            {
                                "role": "user",
                                "content": "%s"
                            }
                        ]
                    }
                    """, uMessage.replace("\"", "\\\""));

            var request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            var client = HttpClient.newHttpClient();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            var responseBody = response.body();

            System.out.println(responseBody);


            JSONObject json = new JSONObject(responseBody);
            String content = json.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
            SendMessage sendMessage = SendMessage.builder().text(content).chatId(chatId).build();
            telegramClient.execute(sendMessage);
            DeleteMessage deleteMessage = DeleteMessage.builder().chatId(chatId).messageId(messid).build();
            telegramClient.execute(deleteMessage);
        }
    }
}
