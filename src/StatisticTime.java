/*
 *
 */

import static java.lang.Thread.sleep;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Tianyiyun
 */
class Statistic {
    public void StatisticTimeWay(String urlStr, String fileNameStr) throws IOException, InterruptedException, ParseException {

        //标识符：用于判断是否将时间写入文件
        String flagStr = "";
        //标识符：用于若网络获取文件出现异常，则进行重试
        boolean flag = true;

        //获取当前日期时间
        Date currentDate = new Date();
        SimpleDateFormat ymdSDF = new SimpleDateFormat("yyyy-MM-dd ");
        //获取开始时间、结束时间拼接字符串前缀
        String datePrefixStr = ymdSDF.format(currentDate);
        //拼接开始时间字符串
        String startTimeStr = datePrefixStr + "06:00:00";
        //拼接结束时间字符串
        String endTimeStr = datePrefixStr + "21:00:00";

        SimpleDateFormat ymdhmsSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取开始时间Long类型
        long beginTimeLong = ymdhmsSDF.parse(startTimeStr).getTime();
        //获取线束时间Long类型
        long endTimeLong = ymdhmsSDF.parse(endTimeStr).getTime();
        //获取当前时间Long类型
        long currentTimeLong = ymdhmsSDF.parse(ymdhmsSDF.format(currentDate)).getTime();

        SimpleDateFormat ymdeSDF = new SimpleDateFormat("yyyy-MM-dd E ");
        //写入文件里的内容的字符串前缀
        String dateFilePrefixStr = ymdeSDF.format(currentDate);
        Document doc = null;

        Element contentElement = null;
        Elements linksElements = null;
        Element linkElement = null;
        String[] arrArray = null;

        File fileVar = null;
        FileWriter fileWriterVar = null;
        BufferedWriter bufferWriterVar = null;

        while (currentTimeLong > beginTimeLong && currentTimeLong < endTimeLong) {
            while (flag) {
                try {
                    //网络获取html文件
//                    doc = Jsoup.connect("http://szxing-fwc.icitymobile.com/line/10003091").get();
                    doc = Jsoup.connect(urlStr).get();
                    flag = false;
                } catch (Exception e) {
                    System.out.println("debug: 运行异常");
                    sleep(30000);
                }
            }
//                调试语句
//            System.out.println("Debug: doc1" + doc.getElementById("content").getElementsByTag("a").get(10).text());
//            从本地获取文件
//            File input = new File("D:\\200WYJ\\999.tmp\\013.html\\input.html");
//            Document doc = Jsoup.parse(input, "UTF-8", "");
            contentElement = doc.getElementById("content");
            linksElements = contentElement.getElementsByTag("a");
            /**
             * 循环填充站点、到站时间信息
             */
            for (int i = 0, length = linksElements.size(); i < length; i++) {
                linkElement = linksElements.get(i);
                String linkText = linkElement.text();
                arrArray = linkText.split("\\s");
                if (arrArray[0].equals("塘北小区") && arrArray.length > 1) {
                    //拼接字符串，本次查询到的入站时间点
                    String writeString = dateFilePrefixStr + arrArray[1];
                    /**
                     * 进行入库操作，如果本次入站时间与上次入站时间不一致
                     */
                    if (!writeString.equals(flagStr)) {
                        //标志位：更新
                        flagStr = writeString;
                        fileVar = new File(fileNameStr);
                        fileWriterVar = new FileWriter(fileVar.getName(), true);
                        bufferWriterVar = new BufferedWriter(fileWriterVar);
                        bufferWriterVar.write(writeString + "\n");
                        bufferWriterVar.close();
                        System.out.println("Debug flagInt = " + flagStr);
                        break;
                    }
                } else if (arrArray[0].equals("塘北小区") && arrArray.length == 1) {
                    break;
                }
            }
            //更新当前时间变量
            currentTimeLong = ymdhmsSDF.parse(ymdhmsSDF.format(new Date())).getTime();
            System.out.println("Debug: print");
            sleep(60000);
        }
    }
}

public class StatisticTime {
    public static void main(String[] args) throws InterruptedException, ParseException, IOException {
        Statistic statisticObject = new Statistic();
        statisticObject.StatisticTimeWay("http://szxing-fwc.icitymobile.com/line/10003091", "557.txt");
    }
}
