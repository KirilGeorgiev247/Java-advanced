package bg.sofia.uni.fmi.mjt.trading.price;

import bg.sofia.uni.fmi.mjt.trading.Utils;

public class PriceChart implements PriceChartAPI {

    private double microsoftStockPrice = 0;
    private double googleStockPrice = 0;
    private double amazonStockPrice = 0;

    public PriceChart(double microsoftStockPrice, double googleStockPrice, double amazonStockPrice) {
        this.microsoftStockPrice = microsoftStockPrice; // round ok
        this.googleStockPrice = googleStockPrice; // round ok
        this.amazonStockPrice = amazonStockPrice; // round ok
    }
    @Override
    public double getCurrentPrice(String stockTicker) {
        if (stockTicker == null) {
            return 0.00;
        }

        return switch (stockTicker) {
            case "MSFT" -> Utils.RoundHighUp(microsoftStockPrice); // round ok
            case "AMZ" -> Utils.RoundHighUp(amazonStockPrice);
            case "GOOG" -> Utils.RoundHighUp(googleStockPrice);
            default -> 0.00;
        };
    }

    @Override
    public boolean changeStockPrice(String stockTicker, int percentChange) {

        if (percentChange <= 0)
            return false;

        if (stockTicker == null) {
            return false;
        }

        return switch (stockTicker) {
            case "MSFT" -> {
                microsoftStockPrice *= (1 + (percentChange/100.0)); // round ok
                yield true;
            }
            case "AMZ" -> {
                amazonStockPrice *= (1 + (percentChange/100.0)); // round ok
                yield true;
            }
            case "GOOG" -> {
                googleStockPrice *= (1 + (percentChange/100.0)); // round ok
                yield true;
            }

            default -> {
                yield false;
            }
        };
    }
}
