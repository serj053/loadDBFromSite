import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

public class ManyThreads extends RecursiveAction {
    public static Set<String> urlsPool = new HashSet<>();
    public String url;
    public String constantPart;

    public ManyThreads(String url, String constantPart) {
        this.url = url;
        this.constantPart = constantPart;
    }

    @Override
    protected void compute() {

        Set<ManyThreads> forkList = new HashSet<>();
        Parsing parsing = new Parsing(url);
        Set<String> urlsList = parsing.getList();//надо добавить в базу
        urlsPool.add(url);
        for (String urlsNextPage : urlsList) {
            if (!urlsPool.contains(urlsNextPage)) {
                urlsPool.add(urlsNextPage);
                ManyThreads mt = new ManyThreads(urlsNextPage, constantPart);
                mt.fork();
                forkList.add(mt);
            }
        }
        for (ManyThreads mt : forkList) {
            mt.join();
        }
    }
}
