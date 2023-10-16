public class BrokenKeyboard {

    public static int calculateFullyTypedWords(String message, String brokenKeys) {
        int count = 0;
        String[] words = message.split(" ");
        char[] brokenKeysArr = brokenKeys.toCharArray();
        for (String word: words) {
            if (word.isBlank()) continue;

            boolean contains = false;

            for (char ch: brokenKeysArr)
                if (word.contains(String.valueOf(ch))) contains = true;


            if (!contains) count++;
        }

        return count;
    }

    public static void main(String[] args) {

        System.out.println(calculateFullyTypedWords("i love mjt", "qsf3o"));
        System.out.println(calculateFullyTypedWords("secret      message info      ", "sms"));
        System.out.println(calculateFullyTypedWords("dve po 2 4isto novi beli kecove", "o2sf"));
        System.out.println(calculateFullyTypedWords("     ", "asd"));
        System.out.println(calculateFullyTypedWords(" - 1 @ - 4", "s"));

    }

}
