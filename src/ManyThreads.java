import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

public class ManyThreads extends RecursiveAction {
    public static Set<String> urlsPool = new HashSet<>();
    public String url;
    public String constantPart;
    private  int counter = 22;
    int n ;

    public ManyThreads(String url, String constantPart, int n) {
        this.url = url;
        this.constantPart = constantPart;
        this.n = n;
    }

    @Override
    protected void compute() {
        System.out.println("Current Thread - " + Thread.currentThread().getName()
               + " counter - " + counter + " n - " + n);
        Set<ManyThreads> forkList = new HashSet<>();
        Parsing parsing = new Parsing(url);
        Set<String> urlsList = parsing.getList();//надо добавить в базу
        urlsPool.add(url);

        for (String urlsNextPage : urlsList) {
            if (!urlsPool.contains(urlsNextPage)) {
                n++;
                urlsPool.add(urlsNextPage);
                ManyThreads mt = new ManyThreads(urlsNextPage, constantPart, n);
                mt.fork();
                forkList.add(mt);
            }
            if (n > counter) return;
        }
        for (ManyThreads mt : forkList) {
            mt.join();
        }
    }
}
