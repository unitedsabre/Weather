package WeatherTestBot.WeatherBot.service;

import WeatherTestBot.WeatherBot.config.BotConfig;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    final BotConfig config;

    static final String HELP_TEXT = "This bot is created to demonstrate weather";
    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/parser", "get a parsing weather"));
        listOfCommands.add(new BotCommand("/weather", "get a weather forecast"));
        listOfCommands.add(new BotCommand("/help", "get a explanation of the bot"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException ignored) {
        }
    }
    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    break;
                case "/parser":
                    try {
                        parser();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    sendMessage(chatId, "Parsing success");
                    break;
                case "/weather":
                    try {
                        sendFromFile(chatId);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                default: sendMessage(chatId, "Sorry");
            }
        }
    }
    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi, " + name + ", nice to see you!";
        sendMessage(chatId, answer);
    }

    public void sendFromFile(long chatId) throws IOException {
        String fName = "src/main/java/WeatherTestBot/WeatherBot/weather.txt";
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(fName));
        String line = br.readLine();
        while (line != null) {
            sb.append(line).append("\n");
            line = br.readLine();
        }
        String contents = sb.toString().trim();
        br.close();
        sendMessage(chatId, contents);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException ignored) {
        }
    }

    private void parser() throws IOException {
        String url = "https://www.gismeteo.ru/weather-pervouralsk-11325/weekly/";
        Document doc = Jsoup.parse(new URL(url), 30000);
        File file = new File("src/main/java/WeatherTestBot/WeatherBot/weather.txt");
        FileWriter writer = new FileWriter(file);

        //Получение необходимых данных
        Element tableWth = doc.select("div[class=widget-items]").first();
        Elements table = doc.select("div[class=widget-items]");

        //Получение дня недели и числа
        assert tableWth != null;
        Elements day = tableWth.select("div[class=widget-row widget-row-days-date]");
        for (Element d : day) {
            String dy = d.select("div[class=day]").text();
            String dt = d.select("div[class=date]").text();
            writer.write("Дата" + "\n");
            writer.write(dy + "\n");
            writer.write(dt);
            writer.write("\n" + "Максимальная и минимальная температура" + "\n");
        }

        //Получение значения max и min температуры
        Elements TempTen = tableWth.select("div[data-row=temperature-air]");
        Elements temps = TempTen.select("div[class=values]");
        Elements maxt = temps.select("div[class=maxt]");
        Elements mint = temps.select("div[class=mint]");
        for (Element t : maxt) {
            String tMax = t.select("span[class=unit unit_temperature_c]").text();
            writer.write(tMax + " ");
        }
        writer.write("\n");

        for (Element tm : mint) {
            String tMin = tm.select("span[class=unit unit_temperature_c]").text();
            writer.write(tMin + " ");
        }
        writer.write("\n" + "Скорость ветра и направление ветра" + "\n");

        //Получение значения скорости ветра
        Elements wind = table.select("div[data-row=wind-speed]");
        for (Element windT : wind) {
            String winds = windT.select("span[class=wind-unit unit unit_wind_m_s]").text();
            writer.write(winds);
        }
        writer.write("\n");

        //Получение значения направления ветра
        Elements windD = table.select("div[data-row=wind-direction]");
        for (Element ignored : windD) {
            String windDirection = windD.select("div[class=direction]").text();
            writer.write(windDirection);
        }
        writer.write("\n" + "Максимальное и минимальное давление воздуха" + "\n");

        //Получение значения max и min давления
        Elements pressureTen = tableWth.select("div[data-row=pressure]");
        Elements pressure = pressureTen.select("div[class=value style_size_m]");
        Elements davlMax = pressure.select("div[class=maxt]");
        Elements davlMin = pressure.select("div[class=mint]");
        for (Element pressureMax : davlMax) {
            String prMax = pressureMax.select("span[class=unit unit_pressure_mm_hg_atm]").text();
            writer.write(prMax + " ");
        }
        writer.write("\n");

        for (Element pressureMin : davlMin) {
            String prMin = pressureMin.select("span[class=unit unit_pressure_mm_hg_atm]").text();
            writer.write(prMin + " ");
        }
        writer.close();
    }
}