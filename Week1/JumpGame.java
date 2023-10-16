import java.util.Arrays;

public class JumpGame {

    public static boolean canWin(int[] array) {
        int index = 0;

        for (int digit: array) {
            for (int i = digit; i > 0; i--) {
                if (array.length - i <= 1) return true;
                return canWin(Arrays.copyOfRange(array, i, array.length));
            }
        }

        return false;
    }

    public static void main(String[] args) {
        System.out.println(canWin(new int[]{2, 3, 1, 1, 0}));
        System.out.println(canWin(new int[]{3, 2, 1, 0, 0}));
        System.out.println(canWin(new int[]{2, 2, 0, 1, 0}));
    }
}
