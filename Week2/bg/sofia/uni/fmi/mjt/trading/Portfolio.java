package bg.sofia.uni.fmi.mjt.trading;

import bg.sofia.uni.fmi.mjt.trading.stock.AmazonStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.GoogleStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.MicrosoftStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.StockPurchase;
import bg.sofia.uni.fmi.mjt.trading.price.PriceChartAPI;

import java.time.LocalDateTime;

public class Portfolio implements PortfolioAPI {

    private String owner = null;
    private PriceChartAPI priceChart = null;
    private double budget = 0;

    private int maxSize = 0;

    private int count = 0;

    private StockPurchase[] stockPurchases = null;

    private StockPurchase makePurchase(String stockTicker, int quantity) {
        if (count == maxSize)
            return null;

        if (quantity <= 0)
            return null;

        double purchasePrice = priceChart.getCurrentPrice(stockTicker) * quantity; // round ok

        if (purchasePrice > budget || budget <= 0.0)
            return null;

        StockPurchase currStock = switch(stockTicker) {
            case "MSFT" ->
                new MicrosoftStockPurchase(quantity, LocalDateTime.now(), priceChart.getCurrentPrice(stockTicker));
            case "AMZ" ->
                new AmazonStockPurchase(quantity, LocalDateTime.now(), priceChart.getCurrentPrice(stockTicker));
            default ->
                new GoogleStockPurchase(quantity, LocalDateTime.now(), priceChart.getCurrentPrice(stockTicker));
        };

        stockPurchases[count++] = currStock;

        priceChart.changeStockPrice(stockTicker, 5);

        budget = budget - purchasePrice; // round ok

        return currStock;
    }

    public Portfolio(String owner, PriceChartAPI priceChart, double budget, int maxSize) {
        this.owner = owner;
        this.priceChart = priceChart;
        this.budget = budget; // round ok
        this.maxSize = maxSize;
        stockPurchases = new StockPurchase[maxSize];
    }

    public Portfolio(String owner, PriceChartAPI priceChart, StockPurchase[] stockPurchases, double budget, int maxSize) {
        this.owner = owner;
        this.priceChart = priceChart;
        this.budget = budget; // round ok
        this.maxSize = maxSize;
        if (stockPurchases == null || stockPurchases.length == 0) {
            this.stockPurchases = new StockPurchase[maxSize];
        }
        else {
            this.stockPurchases = stockPurchases;
        }
    }
    @Override
    public StockPurchase buyStock(String stockTicker, int quantity) {
        if (stockTicker == null) {
            return null;
        }

        return switch (stockTicker) {
            case "MSFT" -> makePurchase(stockTicker, quantity);
            case "AMZ" -> makePurchase(stockTicker, quantity);
            case "GOOG" -> makePurchase(stockTicker, quantity);
            default -> null;
        };
    }

    @Override
    public StockPurchase[] getAllPurchases() {
        if (count == 0) {
            return null;
        }
        return stockPurchases;
    }

    @Override
    public StockPurchase[] getAllPurchases(LocalDateTime startTimestamp, LocalDateTime endTimestamp) {
        int currCount = 0;
        for (StockPurchase p: stockPurchases) {
            if (p != null && p.getPurchaseTimestamp().isAfter(startTimestamp) &&
                    p.getPurchaseTimestamp().isBefore((endTimestamp))) {
                currCount++;
            }
        }

        StockPurchase[] purchasesInTimeSpan = new StockPurchase[currCount];
        int index = 0;
        for (StockPurchase p: stockPurchases) {
            if (p != null && p.getPurchaseTimestamp().isAfter(startTimestamp) &&
                    p.getPurchaseTimestamp().isBefore((endTimestamp))) {
                purchasesInTimeSpan[index++] = p;
            }
        }

        return purchasesInTimeSpan;
    }

    @Override
    public double getNetWorth() {
        double netWorth = 0.0;

        if (count != 0) {
            for (StockPurchase p: stockPurchases) {
                if (p != null && p.getStockTicker() != null) {
                    netWorth += priceChart.getCurrentPrice(p.getStockTicker()) * p.getQuantity();
                }
            }
        }

        return Utils.RoundHighUp(netWorth); // round ok
    }

    @Override
    public double getRemainingBudget() {
        return Utils.RoundHighUp(budget); // round ok
    }

    @Override
    public String getOwner() {
        if (owner.isBlank() || owner.isEmpty())
            return null;
        return owner;
    }
}
