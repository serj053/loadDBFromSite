package loadDBFromSite2;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ForkJoinPool;

public class Main {
    public static void main(String[] args) throws SQLException {
        DbWork2 dbWork2 = new DbWork2();
        ForkJoinPool fjp = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        ConcurrentSkipListSet<String> urlPool = new ConcurrentSkipListSet<>();
        //String url = "https://sky.pro";
        //String url = "https://lenta.ru";
        String url = "https://skillbox.ru";
        //String url = "https://urban-university.ru";//*
        //String url = "https://rw6ase.narod.ru/";//* 21 минута
        //String url = "https://uslugi.mosreg.ru/services/21849";
        //String url = "https://pythonstart.ru/osnovy/dvumernyy-massiv-v-python-osnovy-raboty";//*
        long start = System.currentTimeMillis();
        int counter = 10;// ограничительный счетчик потоков (загружаемых страниц)
        Mapping.constantPart = getConstantPart(url);
        Mapping task = new Mapping(dbWork2, urlPool, url, counter);
        fjp.invoke(task);
        long finish = System.currentTimeMillis() - start;
        int n = 0;
        for (String urls : urlPool) {
            System.out.println(urls);
            n++;
        }
        System.out.println("count - " + n + "   * full time - " + finish / 1000 + " секунд");

    }

    //выделяем постоянную часть имени url
    public static String getConstantPart(String url) {
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
