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
public class StatisticTime {
    public static void main(String[] args) throws IOException, InterruptedException, ParseException {
        //标识符：判断是否将时间写入文件
        String flagStr = "";

        /**
         * 拼接：循环的开始时间和结束时间
         */
        //获取当前时间
        Date dateNow2 = new Date();
        SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd ");
        //获取开始时间、结束时间拼接字符串前缀
        String dateStr4 = sdf4.format(dateNow2);
        //拼接开始时间字符串
        String beginTimeStr = dateStr4 + "06:00:00";
        //拼接结束时间字符串
        String endTimeStr = dateStr4 + "21:00:00";

        SimpleDateFormat sdf5 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取开始时间Long类型
        long beginTimeLong = sdf5.parse(beginTimeStr).getTime();
        //获取线束时间Long类型
        long endTimeLong = sdf5.parse(endTimeStr).getTime();
        //获取当前时间Long类型
        long currentTimeLong = sdf5.parse(sdf5.format(dateNow2)).getTime();

        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd E ");
        //写入文件里的内容的字符串前缀
        String dateStr3 = sdf3.format(dateNow2);

        while (currentTimeLong > beginTimeLong && currentTimeLong < endTimeLong) {

            Document doc;
            //标志位，如果执行出现异常，则进行重度
            boolean flag = true;
            while (flag){
                try {
                    //网络获取html文件
                    doc = Jsoup.connect("http://szxing-fwc.icitymobile.com/line/10003091").get();
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
            Element content = doc.getElementById("content");
            Elements links = content.getElementsByTag("a");

            /**
             * 循环填充站点、到站时间信息
             */
            for (int i = 0; i < links.size(); i++) {
                Element link = links.get(i);
                String linkText = link.text();
                String[] arr = linkText.split("\\s");
                if (arr[0].equals("塘北小区") && arr.length > 1) {
                    //拼接字符串，本次查询到的入站时间点
                    String writeString = dateStr3 + arr[1];
                    /**
                     * 进行入库操作，如果本次入站时间与上次入站时间不一致
                     */
                    if (!writeString.equals(flagStr)) {
                        flagStr = writeString;
                        File file = new File(".\\1.txt");
                        FileWriter fileWriterVar = new FileWriter(file.getName(), true);
                        BufferedWriter bufferWriterVar = new BufferedWriter(fileWriterVar);
                        bufferWriterVar.write(writeString + "\n");
                        bufferWriterVar.close();
                        System.out.println("Debug flagInt = " + flagStr);
                        break;
                    }
                } else if (arr[0].equals("塘北小区") && arr.length == 1) {
                    break;
                }
            }
            currentTimeLong = sdf5.parse(sdf5.format(new Date())).getTime();
            sleep(60000);
        }
    }
}
