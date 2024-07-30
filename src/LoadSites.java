import java.sql.SQLException;
import java.util.concurrent.ForkJoinPool;

public class LoadSites {
    public static void main(String[] args) throws SQLException {
        long start = System.currentTimeMillis();
        ForkJoinPool fp = new ForkJoinPool();
        //String url = "https://lenta.ru";
        String url = "https://skillbox.ru";
        //String url = "https://urban-university.ru/profile/courses";
        //String url = "https://sky.pro/wiki/";
        DbWork db = new DbWork();
        int counter = 0;
        ManyThreads mt = new ManyThreads(url, constantPart(url), db, counter);
        fp.invoke(mt);
        for (String urlStr : ManyThreads.urlsPool) {
            System.out.println(urlStr);
        }
        System.out.println("Время - " + (System.currentTimeMillis() - start) / 1000);

    }
   static String constantPart(String url) {
        int start = url.indexOf("://");
        String[] domainNames = {".com", ".ru", ".aero", ".info", ".biz", ".net", ".org", ".pro"};
        int end = 0;
        for (String name : domainNames) {
            if (url.contains(name)) {
                end = url.indexOf(name);
            }
        }
        return url.substring(start + 3, end);
    }
}
