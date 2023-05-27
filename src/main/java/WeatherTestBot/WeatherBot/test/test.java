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
    private static Pattern pattern = Pattern.compile("\\d+");

    private static String getDate(String stringDate) throws Exception {
        Matcher matcher = pattern.matcher(stringDate);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new Exception("Can't");
    }
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

        //День
        Elements d1 = day.select("a[class=row-item item-day-6]");
        String dy1 = d1.select("div[class=day]").text();
        String dt1 = d1.select("div[class=date]").text();
        String dat1 = getDate(dt1);
        String dty1 = dat1 + " " + dy1;
        weather[0][0] = dty1;
        //System.out.println(dty1);

        Elements d2 = day.select("a[class=row-item item-day-7]");
        String dy2 = d2.select("div[class=day]").text();
        String dt2 = d2.select("div[class=date]").text();
        String dat2 = getDate(dt2);
        String dty2 = dat2 + " " + dy2;
        weather[1][0] = dty2;
        //System.out.println(dty2);

        Elements d3 = day.select("a[class=row-item item-day-1]");
        String dy3 = d3.select("div[class=day]").text();
        String dt3 = d3.select("div[class=date]").text();
        String dat3 = getDate(dt3);
        String dty3 = dat3 + " " + dy3;
        weather[2][0] = dty3;
        //System.out.println(dty3);

        Elements d4 = day.select("a[class=row-item item-day-2]");
        String dy4 = d4.select("div[class=day]").text();
        String dt4 = d4.select("div[class=date]").text();
        String dat4 = getDate(dt4);
        String dty4 = dat4 + " " + dy4;
        weather[3][0] = dty4;
        //System.out.println(dty4);

        Elements d5 = day.select("a[class=row-item item-day-3]");
        String dy5 = d5.select("div[class=day]").text();
        String dt5 = d5.select("div[class=date]").text();
        String dat5 = getDate(dt5);
        String dty5 = dat5 + " " + dy5;
        weather[4][0] = dty5;
        //System.out.println(dty5);

        Elements d6 = day.select("a[class=row-item item-day-4]");
        String dy6 = d6.select("div[class=day]").text();
        String dt6 = d6.select("div[class=date]").text();
        String dat6 = getDate(dt6);
        String dty6 = dat6 + " " + dy6;
        weather[5][0] = dty6;
        //.out.println(dty6);

        Elements d7 = day.select("a[class=row-item item-day-5]");
        String dy7 = d7.select("div[class=day]").text();
        String dt7 = d7.select("div[class=date]").text();
        String dat7 = getDate(dt7);
        String dty7 = dat7 + " " + dy7;
        weather[6][0] = dty7;
        //System.out.println(dty7);

        System.out.println(Arrays.deepToString(weather));


        //По элементно
        Elements widget = tableWth.select("div[class=widget-row widget-row-icon]");
        Elements wD = widget.select("div[class=weather-icon tooltip]");
        for (Element w : wD) {
        String wt = w.attributes().get("data-text");
            System.out.println(wt);
        //zap.write(wt + "\n");
        }
        //zap.write("Максимальная" + "\n");

        //Получение значения max и min температуры
        //По элементно
        Elements TempTen = tableWth.select("div[data-row=temperature-air]");
        Elements temps = TempTen.select("div[class=values]");
        Elements maxt = temps.select("div[class=maxt]");
        Elements mint = temps.select("div[class=mint]");
        for (Element t : maxt) {
            String tMax = t.select("span[class=unit unit_temperature_c]").text();
            System.out.println(tMax);
        //zap.write(tMax + " ");
        }
        //zap.write("\n" + "Минимальная" + "\n");

        for (Element tm : mint) {
            String tMin = tm.select("span[class=unit unit_temperature_c]").text();
            System.out.println(tMin);
        //zap.write(tMin + " ");
        }
        //writer.write("\n" + "Ветер" + "\n");
        //zap.write("\n" + "Скорость ветра, м/с" + "\n");



        //Получение значения скорости ветра
        //По элементно
        Elements wind = tableWth.select("div[data-row=wind-speed]");
        Elements wid = wind.select("div[class=row-item]");
        for (Element windT : wid) {
            String winds = windT.select("span[class=wind-unit unit unit_wind_m_s]").text();
            System.out.println(winds);
            //zap.write(winds);
        }
            //zap.write("\n");


        //Получение значения направления ветра
        //По элементно
        Elements windD = tableWth.select("div[data-row=wind-direction]");
        Elements winD = windD.select("div[class=row-item]");
        for (Element windR : winD) {
            String windDirection = windR.select("div[class=direction]").text();
            System.out.println(windDirection);
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
        for (Element pressureMax : davlMax) {
            String prMax = pressureMax.select("span[class=unit unit_pressure_mm_hg_atm]").text();
            System.out.println(prMax);
        //zap.write(prMax + " ");
        }
        //zap.write("\n" + "Минимальное" + "\n");

        for (Element pressureMin : davlMin) {
            String prMin = pressureMin.select("span[class=unit unit_pressure_mm_hg_atm]").text();
            System.out.println(prMin);
        //zap.write(prMin + " ");
        }
        //zap.close();
        //System.out.println(weather);

}
}