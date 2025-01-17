package loadDBFromSite2;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.RecursiveAction;

public class Mapping extends RecursiveAction {
    DbWork2 dbWork2;
    String url;
    ConcurrentSkipListSet<String> urlPool;//собираем все ссылки с текущей страницы
    public static String constantPart;
    private int counter;
    static private int currentCounter;

    public Mapping(DbWork2 dbWork2, ConcurrentSkipListSet<String> urlPool, String url, int counter) {
        this.dbWork2 = dbWork2;
        this.urlPool = urlPool;
        this.url = url;
        this.counter = counter;
    }

    @Override
    protected void compute() {
        ConcurrentSkipListSet<String> tempList;//временный список для переноса ссылок
        CopyOnWriteArrayList<Mapping> taskList = new CopyOnWriteArrayList<>();
        ParseHtml2 ph = new ParseHtml2();
        tempList = ph.getLinks(url, constantPart);//получаем все ссылки со страницы
        urlPool.add(url);

        for (String urlChildren : tempList) {
            if (currentCounter > counter) {
                return;
            }
            currentCounter++;
            if (!urlPool.contains(urlChildren)) {
                urlPool.add(urlChildren);//здесь можно использовать базу - записать текст
                /*********************************************************/
                Document document = null;
                try {
                    document = Jsoup.connect(urlChildren)
                            .timeout(100000)
                            .userAgent("Chrome/81.0.4044.138")
                            .ignoreHttpErrors(true)
                            .ignoreContentType(true)
                            .get();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String url = urlChildren
                        .replace("'", "\"")
                        .replace("\\", "");
                String name = document.title()
                        .replace("'", "\"")
                        .replace("\\", "");
                ;
                String text = document.body().text()
                        .replace("'", "\"")
                        .replace("\\", "");
                try {
                    dbWork2.save("url, name, text", "'" + url + "', '"
                            + name + "', '" + text + "'");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                /*****************************************************************************/
                Mapping task = new Mapping(dbWork2, urlPool, urlChildren, counter);
                task.fork();
                taskList.add(task);
            }
        }
        for (Mapping task : taskList) {
            task.join();//дожидаемся выполнения задачи и получаем результат (кода в объекте)
        }
        //     Logger.getLogger(Mapping.class.getName()).info("task size - "+taskList.size());
    }
}
