import java.util.Arrays;

public class IPValidator {

    public static boolean checkForLeadingZero(char[] arr) {
        int counter = 0;
        for (char ch: arr) {
            if(Integer.parseInt(String.valueOf(ch)) == 0) counter++;
            else return false;

            if (counter > 1) return true;
        }

        return false;
    }

    public static boolean checkNumber(char[] arr) {
        if (arr.length > 3) return false;

        for (char ch: arr) {
            if (ch < '0' || ch > '9') return false;
        }

        if (Integer.parseInt(String.valueOf(arr)) > 255 || Integer.parseInt(String.valueOf(arr)) < 0) return false;

        return true;
    }
    public static boolean validateIPv4Address(String str) {
        String[] words = str.split("\\.");

        for (String word: words) {
            if (!checkNumber(word.toCharArray())) return false;
            if (checkForLeadingZero(word.toCharArray())) return false;
        }

        return true;
    }

    public static void main(String[] args) {
        System.out.println(validateIPv4Address("192.168.1.1"));
        System.out.println(validateIPv4Address("192.168.1.0"));
        System.out.println(validateIPv4Address("192.168.1.00"));
        System.out.println(validateIPv4Address("192.168@1.1"));
    }

}
