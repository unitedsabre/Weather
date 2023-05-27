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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
        //Указания сайта и указания файла в который будет записанны полученные данных
        String url = "https://www.gismeteo.ru/weather-pervouralsk-11325/weekly/";
        Document doc = Jsoup.parse(new URL(url), 30000);
        File file = new File("src/main/java/WeatherTestBot/WeatherBot/weather.txt");
        FileWriter writer = new FileWriter(file);
        //Создание массива для последующей записи в него полученных значений
        String[][] weather = new String[7][6];
        int i = -1;

        //Получение необходимых данных
        Element tableWth = doc.select("div[class=widget-items]").first();

        //Получение даты и дня недели
        Elements day = tableWth.select("div[class=widget-row widget-row-days-date]");
        Elements dd = day.select("div[class=date]");
        i = -1;
        for (Element d : dd) {
            i++;
            String tmp = d.text();
            weather[i][0] = tmp;
        }

        dd = day.select("div[class=day]");
        i = -1;
        for (Element d : dd) {
            i++;
            String tmp = d.text();
            weather[i][0] += " " + tmp;
        }

        //Получение значения max и min температуры
        Elements TempTen = tableWth.select("div[data-row=temperature-air]");
        Elements temps = TempTen.select("div[class=values]");
        Elements maxt = temps.select("div[class=maxt]");
        Elements mint = temps.select("div[class=mint]");
        i = -1;
        for (Element t : maxt) {
            i++;
            String tMax = t.select("span[class=unit unit_temperature_c]").text();
            weather[i][1] = tMax;
        }

        i = -1;
        for (Element tm : mint) {
            i++;
            String tMin = tm.select("span[class=unit unit_temperature_c]").text();
            weather[i][1] += " / "+ tMin;
        }

        //Получение значения осадков
        Elements widget = tableWth.select("div[class=widget-row widget-row-icon]");
        Elements wD = widget.select("div[class=weather-icon tooltip]");
        i = -1;
        for (Element w : wD) {
            i++;
            String wt = w.attributes().get("data-text");
            weather[i][2] = wt;
        }

        //Получение значения скорости ветра
        Elements wind = tableWth.select("div[data-row=wind-speed]");
        Elements wid = wind.select("div[class=row-item]");
        i = -1;
        for (Element windT : wid) {
            i++;
            String winds = windT.select("span[class=wind-unit unit unit_wind_m_s]").text();
            weather[i][3] = winds;
        }

        //Получение значения направления ветра
        Elements windD = tableWth.select("div[data-row=wind-direction]");
        Elements winD = windD.select("div[class=row-item]");
        i = -1;
        for (Element windR : winD) {
            i++;
            String windDirection = windR.select("div[class=direction]").text();
            weather[i][4] = windDirection;
        }

        //Получение значения max и min давления
        Elements pressureTen = tableWth.select("div[data-row=pressure]");
        Elements pressure = pressureTen.select("div[class=value style_size_m]");
        Elements davlMax = pressure.select("div[class=maxt]");
        Elements davlMin = pressure.select("div[class=mint]");
        i = -1;
        for (Element pressureMax : davlMax) {
            i++;
            String prMax = pressureMax.select("span[class=unit unit_pressure_mm_hg_atm]").text();
            weather[i][5] = prMax;
        }

        i = -1;
        for (Element pressureMin : davlMin) {
            i++;
            String prMin = pressureMin.select("span[class=unit unit_pressure_mm_hg_atm]").text();
            weather[i][5] += " / " + prMin;
        }

        //Запись всех полученных значений в файл
        for (i = 0; i < 7; i++) {
            writer.write("Дата: " + weather[i][0] + "\n");
            writer.write("Температура, °C: " + weather[i][1] + "\n");
            writer.write("Осадки: " + weather[i][2] + "\n");
            writer.write("Скорость ветра, м/с: " + weather[i][3] + "\n");
            writer.write("Направление ветра: " + weather[i][4] + "\n");
            writer.write("Давление, мм рт. ст: " + weather[i][5] + "\n");
            writer.write("\n");
        }
        writer.close();
    }
}