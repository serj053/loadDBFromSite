public class Main {
    public static void main(String[] args) {
        String str = "'\\xF0\\x9F\\x92\\x99 \\xD0...' ";
        System.out.println(str);
        String str1 = str.replace("\\", "").replace("'","\"");
        System.out.println(str1);
    }
}