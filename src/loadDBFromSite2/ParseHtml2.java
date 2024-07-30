package loadDBFromSite2;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ConcurrentSkipListSet;

import static java.lang.Thread.sleep;

public class ParseHtml2 {

    //парсинг кода страницы для получения ссылок на страницы
// внутри того же домена
//    public static void main(String[] args) {
//        /*
//        https://bookofthedead.pu710ev.com/?lang=ru&st=kFTF1179&s1=&s2=what_max_cas2022_ru_0103&s3=&s4=&s5=&pc=30&form_phone=%7Bform_phone%7D&form_email=%7Bform_email%7D&lrrPath=casino&trId=co6fla3ieqp81m7m7jfg&source=&startTime=1712126632911419463
//        */
//        ConcurrentSkipListSet list = ParseHtml.getLinks("https://skillbox.ru/");
//        list.forEach(System.out::println);
//        System.out.println(list.size());
//    }

    //Позволяет безопасно выполнять операции вставки, удаления
    // и доступа к множеству одновременно несколькими потоками.
    private  ConcurrentSkipListSet<String> links;

    //реализуем метод который проверяет является ли строка URL
    //адресом внутри домена сайта
    private  boolean isLink(String link, String constantPart) {
        //String regex = "http[s]?://[^#,\\s]*\\.?skillbox\\.ru[^#,\\s]*";//рабочий вариант - 59 минут 25585 строк(ссылок
        // String regex = "http[s]?://[^#,\\s]*\\.?playback\\.ru[^#,\\s]*";
        //String regex = "(?:https?):\\/\\/(\\w+:?\\w*)?(\\S+)(:\\d+)?(\\/|\\/([\\w#!:.?+=&%!\\-\\/]))?";//*
        //String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";//бесконечный цикл
        String regex = "http[s]?://" + constantPart + "\\.ru[^#,\\s]*";
        return link.matches(regex);
    }

    public  boolean isFile(String link) {
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

    //ConcurrentSkipListSet -это класс из библиотеки Java, представляющий собой
    // потокобезопасную реализацию структуры данных "список уникальных элементов
    // отсортированных по возрастанию"
    //собираем все ссылки со страницы
    public ConcurrentSkipListSet<String> getLinks(String url, String constantPart) {
        links = new ConcurrentSkipListSet<>();
        try {
            sleep(350);//выдерживать паузы между потоками обращения к этому статическому методу
            // (с помощью метода sleep() у потока), (чтобы сайт не заблокировал доступ приложения ?)
            Connection connection = Jsoup.connect(url)
                    .ignoreHttpErrors(true)//игнорировать ошибки HTTP, которые могут возникнуть
                    // при попытке получить данные по URL-адресу, то есть извлекать информацию из
                    // страницы даже в случае возникновения ошибки HTTP
                    //.timeout(100)//Метод timeout() библиотеки Jsoup время ожидания для подключения
                    // к серверу
                    .ignoreContentType(true)//игнорироватьтип содеожимого документа при анализе
                    .followRedirects(false);//избегать зацикливания при обработке перенаправлений
            Document document = connection.get();
            Elements elements = document.select("body").select("a");//здесь может быть пусто.
            for (Element element : elements) {
                String link = element.absUrl("href");
                if (isLink(link, constantPart) && !isFile(link)) {
                    links.add(link);
                }
            }
        } catch (InterruptedException e) {
            System.out.println(e + " " + url);
        } catch (SocketTimeoutException e) {
            System.out.println(e + " " + e);
        } catch (IOException e) {
            System.out.println(e + " " + url);
        }
        //если ссылок на странице нет то вернется пустой контейнер
        return links;
    }
}
/* ignoreContentType(true)
 * Игнорируйте тип содержимого документа при анализе ответа. По умолчанию это значение равно false,
 *  нераспознанный тип содержимого приведет к возникновению исключения IOException.
 *  (Это делается для предотвращения создания мусора при попытке анализа двоичного изображения в
 * формате JPEG, например.) Установите значение true, чтобы принудительно выполнить попытку
 * синтаксического анализа независимо от типа контента.
 *
 * */

