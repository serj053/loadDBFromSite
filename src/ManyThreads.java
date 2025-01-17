import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.RecursiveAction;
import java.util.logging.Logger;

public class ManyThreads extends RecursiveAction {
    public static ConcurrentSkipListSet<String> urlsPool = new ConcurrentSkipListSet<>();
    public static DbWork dbWork;
    public String url;
    public String constantPart;
    private int counter = 22;
    int n;

    public ManyThreads(String url, String constantPart, DbWork db, int n) throws SQLException {
        this.url = url;
        this.constantPart = constantPart;// постоянная часть URL адреса
        this.n = n;// счетчик
        dbWork = db;//внешняя ссылка на базу данных
    }

    @Override
    protected void compute() {
//        System.out.println("Current Thread - " + Thread.currentThread().getName()
//               + " counter - " + counter + " n - " + n);
        Set<ManyThreads> forkList = new HashSet<>();//накопитель потоков
        Parsing parsing = new Parsing(url);
        Set<String> urlsList = parsing.getList();//надо добавить в базу
        urlsPool.add(url);//локальное хранилище
        for (String urlsNextPage : urlsList) {
            if (!urlsPool.contains(urlsNextPage)) {
                n++;
                urlsPool.add(urlsNextPage);
                Document document;
                try {
                    Thread.sleep(150);
                    document = Jsoup.connect(urlsNextPage)
                            .ignoreHttpErrors(true)
                            .ignoreContentType(true)
                            .followRedirects(false)
                            .get();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                String name = document.title()
                        .replace("'", "\"")
                        .replace("\\", "");
                String nextPage = urlsNextPage
                        .replace("'", "\"")
                        .replace("\\", "");;
                String text = null;
                try {
                    dbWork.save("url, name, text", "'"
                            + nextPage + "', '" + name + "', 'text'");
                } catch (SQLException | InterruptedException e) {
                    Logger.getLogger(ManyThreads.class.getName()).info("** "+name);
                    throw new RuntimeException(e);
                }
                ManyThreads mt = null;
                try {
                    mt = new ManyThreads(urlsNextPage, constantPart, dbWork, n);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                mt.fork();
                forkList.add(mt);
            }
  //                      if (n > counter) return;
        }
        for (ManyThreads mt : forkList) {
            mt.join();
        }
    }
}
