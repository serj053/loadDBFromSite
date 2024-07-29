import java.sql.SQLException;
import java.util.concurrent.ForkJoinPool;

public class LoadSites {
    public static void main(String[] args) throws SQLException {
        long start = System.currentTimeMillis();
        ForkJoinPool fp = new ForkJoinPool();
        String url = "https://lenta.ru";
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
        int end = url.indexOf(".ru");
        return url.substring(start +3, end);
    }
}
