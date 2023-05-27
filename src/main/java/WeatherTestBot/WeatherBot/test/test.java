package WeatherTestBot.WeatherBot.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

public class test {
    public static void main(String[] args) throws IOException {
    String url = "https://www.gismeteo.ru/weather-pervouralsk-11325/weekly/";
    Document doc = Jsoup.parse(new URL(url), 30000);
    File file = new File("src/main/java/WeatherTestBot/WeatherBot/test/test.txt");
    FileWriter wr = new FileWriter(file);

    //Получение необходимых данных
    Element tableWth = doc.select("div[class=widget-items]").first();
    Elements table = doc.select("div[class=widget-items]");

    String[][] weather = new String[8][7];
    int i = -1;

        Elements day = tableWth.select("div[class=widget-row widget-row-days-date]");
        for (Element d : day) {
            i++;
            String dy = d.select("div[class=day]").text();
            String dt = d.select("div[class=date]").text();
            weather[i][0] = dt + ", " + dy;
        }

        Elements widget = tableWth.select("div[class=widget-row widget-row-icon]");
        Elements wD = widget.select("div[class=weather-icon tooltip]");
        for (Element w : wD) {
            i++;
            String wt = w.attributes().get("data-text");
            weather[i][0] = wt + ", ";
        }

        //Получение значения max и min температуры
        Elements TempTen = tableWth.select("div[data-row=temperature-air]");
        Elements temps = TempTen.select("div[class=values]");
        Elements maxt = temps.select("div[class=maxt]");
        Elements mint = temps.select("div[class=mint]");
        for (Element t : maxt) {
            i++;
            String tMax = t.select("span[class=unit unit_temperature_c]").text();
            weather[i][0] = tMax + ", ";
        }
        for (Element tm : mint) {
            i++;
            String tMin = tm.select("span[class=unit unit_temperature_c]").text();
            weather[i][0] = tMin + ", ";

        }
        //Получение значения скорости ветра
        Elements wind = table.select("div[data-row=wind-speed]");
        for (Element windT : wind) {
            i++;
            String winds = windT.select("span[class=wind-unit unit unit_wind_m_s]").text();
            weather[i][0] = winds + ", ";

        }
        //Получение значения направления ветра
        Elements windD = table.select("div[data-row=wind-direction]");
        for (Element ignored : windD) {
            i++;
            String windDirection = windD.select("div[class=direction]").text();
            weather[i][0] = windDirection + ", ";
        }


        //Получение значения max и min давления
        Elements pressureTen = tableWth.select("div[data-row=pressure]");
        Elements pressure = pressureTen.select("div[class=value style_size_m]");
        Elements davlMax = pressure.select("div[class=maxt]");
        Elements davlMin = pressure.select("div[class=mint]");
        for (Element pressureMax : davlMax) {
            i++;
            String prMax = pressureMax.select("span[class=unit unit_pressure_mm_hg_atm]").text();
            weather[i][0] = prMax + ", ";
        }


        for (Element pressureMin : davlMin) {
            i++;
            String prMin = pressureMin.select("span[class=unit unit_pressure_mm_hg_atm]").text();
            weather[i][0] = prMin + ", ";
        }
        wr.close();
        System.out.println(weather);
}
}
