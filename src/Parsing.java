import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

//содержимое страниц
public class Parsing {
    String url;
    String constantPart;

    public Parsing(String url) {
        this.url = url;
        this.constantPart = constantPart(url);
    }

    static boolean isLink(String link, String constantPart) {
        String regex = "http[s]?://" + constantPart + "\\.ru[^#,\\s]*";
        return link.matches(regex);
        // return url.matches(template);
    }

    public static boolean isFile(String link) {
        return link.contains("jpg")
                || link.contains(".jpeg")
                || link.contains(".png")
                || link.contains(".gif")
                || link.contains(".webp")
                || link.contains(".pdf")
                || link.contains(".eps")
                || link.contains(".xlsx")
                || link.contains(".doc")
                || link.contains(".pptx")
                || link.contains(".docx")
                || link.contains("?_ga");

    }

    public Set<String> getList() {
        Set<String> list = new HashSet<>();

        Connection connection = Jsoup.connect(url).ignoreHttpErrors(true)
                .ignoreContentType(true).followRedirects(false);
        Document document = null;
        try {
            document = connection.get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Elements elements = document.select("body").select("a");
        for (Element a : elements) {

            String absUrl = a.absUrl("href");
            if (isLink(absUrl, constantPart) && !isFile(absUrl)) {
                list.add(absUrl);
            }
        }
        return list;
    }

    //выделяем постоянную часть имени url
    private String constantPart(String url) {
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


    public static void main(String[] args) throws IOException, SQLException, InterruptedException {
        String url = "https://lenta.ru";
        long start = System.currentTimeMillis();
        DbWork db = new DbWork();
        Parsing p = new Parsing(url);
        Set<String> list = p.getList();
        int n = 0;
        for (String urlChils : list) {
            System.out.println(urlChils);
            db.save("url, name, text", "'" + urlChils + "','Lenta', '"
                    + p.getText(urlChils) + "'");
            n++;
        }
        db.connection.close();
        System.out.println("n - " + n);
        System.out.println("time ms - " + (System.currentTimeMillis() - start));
    }

    public String getText(String url) throws IOException {
        if (isLink(url, "lenta") && !isFile(url)) {
            Document document = Jsoup.connect(url).ignoreHttpErrors(true).get();
            String text = document.body().text()
                    .replace("'", "\"")
                    .replace("\\", "");
            return text;
        }
        return "";
    }
}
