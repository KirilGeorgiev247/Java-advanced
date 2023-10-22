package bg.sofia.uni.fmi.mjt.trading.stock;

import bg.sofia.uni.fmi.mjt.trading.Utils;

import java.time.LocalDateTime;

public class AmazonStockPurchase implements StockPurchase {

    private int quantity = 0;
    private LocalDateTime purchaseTimestamp = null;

    private double purchasePricePerUnit = 0;

    public AmazonStockPurchase(int quantity, LocalDateTime purchaseTimestamp, double purchasePricePerUnit) {
        this.quantity = quantity;
        this.purchaseTimestamp = purchaseTimestamp;
        this.purchasePricePerUnit = purchasePricePerUnit;
    }
    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public LocalDateTime getPurchaseTimestamp() {
        return purchaseTimestamp;
    }

    @Override
    public double getPurchasePricePerUnit() {
        return Utils.RoundHighUp(purchasePricePerUnit); // round ok
    }

    @Override
    public double getTotalPurchasePrice() {
        return Utils.RoundHighUp(quantity * getPurchasePricePerUnit()); // round ok
    }

    @Override
    public String getStockTicker() {
        return "AMZ";
    }
}
