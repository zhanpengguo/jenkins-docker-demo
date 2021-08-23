package com.example.demo;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhanpengguo
 * @date 2021-08-23 13:56
 */
public class ImagesUtil {

    /**
     * 获取CPU个数
     */
    private static int corePoolSize = Runtime.getRuntime().availableProcessors();
    /**
     * 创建线程池  调整队列数 拒绝服务
     */
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, corePoolSize + 1, 10L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000));

    public static void meizitu() throws IOException {
        System.out.println("系统CPU核数："+corePoolSize);
        /**
         * 正则 匹配页码
         */
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        String baseUrl = "https://www.tupianzj.com";
        Connection connect = Jsoup.connect(baseUrl+"/meinv/mm/meizitu/");
        Document document = connect.get();
        /**
         * 获取本栏目所有妹子套图节点
         */
        Elements elements = document.body().getElementsByClass("d1").select("li");
        for (Element img : elements) {
            Runnable task = () -> {
                try {
                    /**
                     * 获取套图地址
                     */
                    String href = img.child(0).attr("href");
                    Connection subConnect = Jsoup.connect(baseUrl+href)
                            . header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:49.0) Gecko/20100101 Firefox/49.0")
                            .timeout(8000);
                    Document subDocument = subConnect.get();
                    /**
                     * 获取套图标题用于创建目录
                     */
                    String title = subDocument.body().getElementsByTag("h1").eq(1).html();
                    System.out.println("开始下载："+title);
                    /**
                     * 获取套图图片数量
                     */
                    String txt =  subDocument.body().getElementsByClass("pages").select("li").eq(0).html();
                    Matcher m = p.matcher(txt);
                    if(StrUtil.isNotBlank(m.replaceAll("").trim())){
                        Integer pageNo =  Integer.parseInt(m.replaceAll("").trim());
                        for (int i=0;i<pageNo;i++){
                            String url = baseUrl+href;
                            if(i!=0){
                                int page = i+1;
                                url = url.replace(".html","_"+page+".html");
                            }
                            subConnect = Jsoup.connect(url);
                            subDocument = subConnect.get();
                            String src = subDocument.getElementById("bigpicimg").attr("src");
                            /**
                             * 下载妹子
                             */
                            HttpUtil.downloadFile(src, FileUtil.mkdir("/home/root/meizi/"+title+"/"));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            executor.execute(task);
        }
    }
}
