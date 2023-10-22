package bg.sofia.uni.fmi.mjt.trading;
public class Utils {
    public static double RoundHighUp(double num) {
        return Math.round(num * 100.0) / 100.0;
    }
}
