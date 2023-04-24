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
        listOfCommands.add(new BotCommand("/weather", "get a weather forecast"));
        listOfCommands.add(new BotCommand("/help", "get a explanation of the bot"));
        listOfCommands.add(new BotCommand("/parser", "get a parsing weather"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
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
                    //Получение кода страницы
                    String url = "https://www.gismeteo.ru/weather-pervouralsk-11325/weekly/";
                    Document doc = null;
                    try {
                        doc = Jsoup.parse(new URL(url), 30000);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    File file = new File("src/main/java/parser/weather.txt");
                    FileWriter writer = null;
                    try {
                        writer = new FileWriter(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    //Получение необходимых данных
                    Element tableWth = doc.select("div[class=widget-items]").first();
                    Elements table = doc.select("div[class=widget-items]");

                    //Получение дня недели и числа
                    Elements day = tableWth.select("div[class=widget-row widget-row-days-date]");
                    for (Element d : day) {
                        String dy = d.select("div[class=day]").text();
                        String dt = d.select("div[class=date]").text();
                        try {
                            writer.write(dy + "\n");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            writer.write(dt);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            writer.write("\n");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    //Получение значения max и min температуры
                    Elements TempTen = tableWth.select("div[data-row=temperature-air]");
                    Elements temps = TempTen.select("div[class=values]");
                    Elements maxt = temps.select("div[class=maxt]");
                    Elements mint = temps.select("div[class=mint]");
                    for (Element t : maxt) {
                        String tMax = t.select("span[class=unit unit_temperature_c]").text();

                        try {
                            writer.write(tMax + " ");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    //System.out.println();
                    try {
                        writer.write("\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    //System.out.print("Минимальная температура: " + " ");
                    for (Element tm : mint) {
                        String tMin = tm.select("span[class=unit unit_temperature_c]").text();
                        //System.out.print(tMin + " ");
                        try {
                            writer.write(tMin + " ");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    //System.out.println();
                    try {
                        writer.write("\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    //Получение значения скорости ветра
                    Elements wind = table.select("div[data-row=wind-speed]");
                    //System.out.print("Скорость ветра м,с:" + " ");
                    for (Element windT : wind) {
                        String winds = windT.select("span[class=wind-unit unit unit_wind_m_s]").text();
                        //System.out.print(winds + " ");
                        try {
                            writer.write(winds);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    try {
                        writer.write("\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    //Получение значения направления ветра
                    Elements windD = table.select("div[data-row=wind-direction]");
                    for (Element wd : windD) {
                        String windDirection = windD.select("div[class=direction]").text();
                        //System.out.print(windDirection + " ");
                        try {
                            writer.write(windDirection);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    //System.out.println();
                    try {
                        writer.write("\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    //Получение значения max и min давления
                    //System.out.print("Максимальное давление: " + " ");
                    Elements pressureTen = tableWth.select("div[data-row=pressure]");
                    Elements pressure = pressureTen.select("div[class=value style_size_m]");
                    Elements davlMax = pressure.select("div[class=maxt]");
                    Elements davlMin = pressure.select("div[class=mint]");
                    for (Element pressureMax : davlMax) {
                        String prMax = pressureMax.select("span[class=unit unit_pressure_mm_hg_atm]").text();
                        //System.out.print(prMax + " ");
                        try {
                            writer.write(prMax + " ");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    //System.out.println();
                    try {
                        writer.write("\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    //System.out.print("Минимальное давление: " + " ");
                    for (Element pressureMin : davlMin) {
                        String prMin = pressureMin.select("span[class=unit unit_pressure_mm_hg_atm]").text();
                        //System.out.print(prMin + " ");
                        try {
                            writer.write(prMin + " ");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    try {
                        writer.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    //System.out.println("Парсинг прошёл успешно");
                    sendMessage(chatId, "Parsing success");
                    break;

                case "/weather":
                    try {
                        sendFromFile(chatId, messageText);
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

    public void sendFromFile(long chatId, String fileName) throws IOException {
        String fName = "src/main/java/parser/weather.txt";
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
        } catch (TelegramApiException e) {

        }
    }
}