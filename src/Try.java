public class Try {
    public static void main(String[] args) {
        String str = "https://skillbox.ru/stady";
        int start = str.indexOf("://");
        System.out.println("start " +  start);
        //перебор всех доменных имен
        String[] domainNames = {".com", ".ru", ".aero", ".info", ".biz", ".net", ".org", ".pro"};
        int end = 0;
        for (String name : domainNames) {
            if (str.contains(name)) {
                end = str.indexOf(name);
                System.out.println("end " + end);
            }
        }

        if (end != 0) {
            String sub = str.substring(start + 3, end);
            System.out.println(sub);
        }

    }
}
