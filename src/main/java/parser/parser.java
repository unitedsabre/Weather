package parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

public class parser {
    //Получение кода страницы
    private static Document getPage() throws IOException {
        String url = "https://www.gismeteo.ru/weather-reftinsky-12772/weekly/";
        Document page = Jsoup.parse(new URL(url), 30000);
        return page;
    }

    public static void main(String[] args) throws IOException {
        //Создание файла для записи результатов
        File file = new File("src/main/java/parser/weather.txt");
        FileWriter writer = new FileWriter(file);

        Document page = getPage();

        //Получение необходимых данных
        Element tableWth = page.select("div[class=widget-items]").first();
        Elements table = page.select("div[class=widget-items]");

        //Получение дня недели и числа
        Elements day = tableWth.select("div[class=widget-row widget-row-days-date]");
        for (Element d : day) {
            String dy = d.select("div[class=day]").text();
            String dt = d.select("div[class=date]").text();
            //System.out.println("День недели: " + dy);
            writer.write(dy + "\n");
            //System.out.println("Число: " + dt);
            writer.write(dt);
            writer.write("\n");
        }

        //Получение значения max и min температуры
        Elements TempTen = tableWth.select("div[data-row=temperature-air]");
        Elements temps = TempTen.select("div[class=values]");
        Elements maxt = temps.select("div[class=maxt]");
        Elements mint = temps.select("div[class=mint]");
        //System.out.print("Максимальная температура: " + " ");
        for (Element t : maxt) {
            String tMax = t.select("span[class=unit unit_temperature_c]").text();
            //System.out.print(tMax + " ");
            writer.write(tMax + " ");
        }
        //System.out.println();
        writer.write("\n");

        //System.out.print("Минимальная температура: " + " ");
        for (Element tm : mint) {
            String tMin = tm.select("span[class=unit unit_temperature_c]").text();
            //System.out.print(tMin + " ");
            writer.write(tMin + " ");
        }
        //System.out.println();
        writer.write("\n");

        //Получение значения скорости ветра
        Elements wind = table.select("div[data-row=wind-speed]");
        //System.out.print("Скорость ветра м,с:" + " ");
        for (Element windT : wind) {
            String winds = windT.select("span[class=wind-unit unit unit_wind_m_s]").text();
            //System.out.print(winds + " ");
            writer.write(winds);
        }
        writer.write("\n");

        //Получение значения направления ветра
        Elements windD = table.select("div[data-row=wind-direction]");
        for (Element wd : windD) {
            String windDirection = windD.select("div[class=direction]").text();
            //System.out.print(windDirection + " ");
            writer.write(windDirection);
        }
        //System.out.println();
        writer.write("\n");

        //Получение значения max и min давления
        //System.out.print("Максимальное давление: " + " ");
        Elements pressure = table.select("div[class=value style_size_m]");
        Elements davlMax = pressure.select("div[class=maxt]");
        Elements davlMin = pressure.select("div[class=mint]");
        for (Element pressureMax : davlMax) {
            String prMax = pressureMax.select("span[class=unit unit_pressure_mm_hg_atm]").text();
            //System.out.print(prMax + " ");
            writer.write(prMax + " ");
        }
        //System.out.println();
        writer.write("\n");

        //System.out.print("Минимальное давление: " + " ");
        for (Element pressureMin : davlMin) {
            String prMin = pressureMin.select("span[class=unit unit_pressure_mm_hg_atm]").text();
            //System.out.print(prMin + " ");
            writer.write(prMin + " ");
        }
        writer.close();
        System.out.println("Парсинг прошёл успешно");
    }
}