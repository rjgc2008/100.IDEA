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
class Statistic extends Thread {

    //变量，记录网页的URL
    private String urlStr = "";
    //变量，记录存储文件的文件名
    private String fileNameStr = "";
    //变量，记录站点名称
    private String siteName = "";

    //标识符：用于判断是否将时间写入文件
    private String flagStr = "";
    //标识符：用于若网络获取文件出现异常，则进行重试
    private boolean flag = true;

    //获取当前日期时间
    private Date currentDate = new Date();
    private SimpleDateFormat ymdSDF = new SimpleDateFormat("yyyy-MM-dd ");
    private SimpleDateFormat ymdeSDF = new SimpleDateFormat("yyyy-MM-dd E ");
    private SimpleDateFormat ymdhmsSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //获取开始时间、结束时间拼接字符串前缀
    private String datePrefixStr = ymdSDF.format(currentDate);
    //拼接开始时间字符串
    private String startTimeStr = datePrefixStr + "06:00:00";
    //拼接结束时间字符串
    private String endTimeStr = datePrefixStr + "22:00:00";
    //写入文件里的内容的字符串前缀
    private String dateFilePrefixStr = ymdeSDF.format(currentDate);

    private long beginTimeLong = 0;
    private long endTimeLong = 0;
    private long currentTimeLong = 0;

    private Document doc = null;

    private Element contentElement = null;
    private Elements linksElements = null;
    private Element linkElement = null;
    private String[] arrArray = null;

    private File fileVar = null;
    private FileWriter fileWriterVar = null;
    private BufferedWriter bufferWriterVar = null;

    public Statistic(String urlStr, String fileNameStr, String siteName) {
        this.urlStr = urlStr;
        this.fileNameStr = fileNameStr;
        this.siteName = siteName;
    }

    @Override
    public void run() {
        try {
            //获取开始时间Long类型
            beginTimeLong = ymdhmsSDF.parse(startTimeStr).getTime();
            //获取线束时间Long类型
            endTimeLong = ymdhmsSDF.parse(endTimeStr).getTime();
            //获取当前时间Long类型
            currentTimeLong = ymdhmsSDF.parse(ymdhmsSDF.format(currentDate)).getTime();
        } catch (ParseException e) {
            System.out.println("ERROR: Exception");
        }

        while (currentTimeLong > beginTimeLong && currentTimeLong < endTimeLong) {
            //标志位置true，确保每次都查询获取一次html文件
            flag = true;
            try {
                while (flag) {
                    try {
                        //网络获取html文件
                        doc = Jsoup.connect(urlStr).get();
                        flag = false;
                    } catch (Exception e) {
                        System.out.println("debug: 运行异常");
                        //若出现异常后，停止30S，标志位不做任何处理，目的是30S后重新查询一次
                        sleep(30000);
                    }
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

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
                if (arrArray[0].equals(siteName) && arrArray.length == 2) {
                    //拼接字符串，本次查询到的入站时间点
                    String writeString = siteName + " " + dateFilePrefixStr + arrArray[1];
                    /**
                     * 进行写入文件操作，如果本次入站时间与上次入站时间不一致
                     */
                    if (!writeString.equals(flagStr)) {
                        //标志位：更新，确保不重复写入相同时间点的信息
                        flagStr = writeString;
                        try {
                            fileVar = new File(fileNameStr);
                            fileWriterVar = new FileWriter(fileVar.getName(), true);
                            bufferWriterVar = new BufferedWriter(fileWriterVar);
                            bufferWriterVar.write(writeString + "\n");
                            bufferWriterVar.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                } else if (arrArray[0].equals(siteName)) {
                    break;
                }
            }
            //更新当前时间变量，确保时间点在判断范围内
            try {
                currentTimeLong = ymdhmsSDF.parse(ymdhmsSDF.format(new Date())).getTime();
                sleep(60000);
            } catch (ParseException e) {
                e.printStackTrace();
            }catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}

public class StatisticTime {
    public static void main(String[] args) throws InterruptedException, ParseException, IOException {
        new Statistic("http://szxing-fwc.icitymobile.com/line/10003091", "557.txt", "塘北小区").start();
        new Statistic("http://szxing-fwc.icitymobile.com/line/10000585", "138.txt", "东振路东").start();
    }
}
