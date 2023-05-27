package WeatherTestBot.WeatherBot.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {
    public static void main(String[] args) throws Exception {
        String url = "https://www.gismeteo.ru/weather-pervouralsk-11325/weekly/";
        Document doc = Jsoup.parse(new URL(url), 30000);
        File file = new File("src/main/java/WeatherTestBot/WeatherBot/test/test.txt");
        FileWriter wr = new FileWriter(file);

        //Получение необходимых данных
        Element tableWth = doc.select("div[class=widget-items]").first();
        int i = -1;
        String[][] weather = new String[7][];
        //i++
        //weather[i][0] = dy;

        //Получение необходимых данных
        Elements day = tableWth.select("div[class=widget-row widget-row-days-date]");

        //День и число
        String dy1 = day.select("div[class=day]").text();
        String dt1 = day.select("div[class=date]").text();
        System.out.println(dy1);
        System.out.println(dt1);

        //Разбивание целой строчки на отдельные элементы
        String[] elements = dy1.split(" ");
        for (String element : elements) {
            System.out.println(element);
        }

        //Избавление от названия месяца в числах
        String regex = "\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(dt1);
        while (matcher.find()) {
            System.out.println(matcher.group());
        }



        //По элементно
        Elements widget = tableWth.select("div[class=widget-row widget-row-icon]");
        Elements wD = widget.select("div[class=weather-icon tooltip]");
        i = -1;
        for (Element w : wD) {
            i++;
            String wt = w.attributes().get("data-text");
            //System.out.println(wt);
            weather[i][0] = wt;
        //zap.write(wt + "\n");
        }
        //zap.write("Максимальная" + "\n");

        //Получение значения max и min температуры
        //По элементно
        Elements TempTen = tableWth.select("div[data-row=temperature-air]");
        Elements temps = TempTen.select("div[class=values]");
        Elements maxt = temps.select("div[class=maxt]");
        Elements mint = temps.select("div[class=mint]");
        i = -1;
        for (Element t : maxt) {
            i++;
            String tMax = t.select("span[class=unit unit_temperature_c]").text();
            weather[i][0] = tMax;
            //System.out.println(tMax);
        //zap.write(tMax + " ");
        }
        //zap.write("\n" + "Минимальная" + "\n");

        i = -1;
        for (Element tm : mint) {
            i++;
            String tMin = tm.select("span[class=unit unit_temperature_c]").text();
            //System.out.println(tMin);
            weather[i][0] = tMin;
        //zap.write(tMin + " ");
        }
        //writer.write("\n" + "Ветер" + "\n");
        //zap.write("\n" + "Скорость ветра, м/с" + "\n");



        //Получение значения скорости ветра
        //По элементно
        Elements wind = tableWth.select("div[data-row=wind-speed]");
        Elements wid = wind.select("div[class=row-item]");
        i = -1;
        for (Element windT : wid) {
            i++;
            String winds = windT.select("span[class=wind-unit unit unit_wind_m_s]").text();
            weather[i][0] = winds;
            //System.out.println(winds);
            //zap.write(winds);
        }
            //zap.write("\n");


        //Получение значения направления ветра
        //По элементно
        Elements windD = tableWth.select("div[data-row=wind-direction]");
        Elements winD = windD.select("div[class=row-item]");
        i = -1;
        for (Element windR : winD) {
            i++;
            String windDirection = windR.select("div[class=direction]").text();
            weather[i][0] = windDirection;
            //System.out.println(windDirection);
        //zap.write("Направление ветра" + "\n");
        //zap.write(windDirection);
        }
        //zap.write("\n" + "Давление, мм рт. ст." + "\n");
        //zap.write("Максимальное" + "\n");


        //Получение значения max и min давления
        //По элементно
        Elements pressureTen = tableWth.select("div[data-row=pressure]");
        Elements pressure = pressureTen.select("div[class=value style_size_m]");
        Elements davlMax = pressure.select("div[class=maxt]");
        Elements davlMin = pressure.select("div[class=mint]");
        i = -1;
        for (Element pressureMax : davlMax) {
            String prMax = pressureMax.select("span[class=unit unit_pressure_mm_hg_atm]").text();
            weather[i][0] = prMax;
            //System.out.println(prMax);
        //zap.write(prMax + " ");
        }
        //zap.write("\n" + "Минимальное" + "\n");

        for (Element pressureMin : davlMin) {
            String prMin = pressureMin.select("span[class=unit unit_pressure_mm_hg_atm]").text();
            weather[i][0] = prMin;
            //System.out.println(prMin);
        //zap.write(prMin + " ");
        }
        //zap.close();
        System.out.println(weather);

}
}