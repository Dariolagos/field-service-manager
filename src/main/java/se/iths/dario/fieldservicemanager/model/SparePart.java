package se.iths.dario.fieldservicemanager.model;

public class SparePart implements Billable {
    private final String articleNumber;
    private final String name;
    private final double price;
    private int stockQuantity;

    public SparePart(String articleNumber, String name, double price, int stockQuantity) {
        this.articleNumber = articleNumber;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public void reduceStock(int amount) {
        if (amount > stockQuantity) {
            throw new IllegalStateException("Inte tillräckligt många i lager för " + name);
        }
        this.stockQuantity -= amount;
    }

    @Override
    public double getCost() {
        return price;
    }

    @Override
    public String getDescription() {
        return name + " (" + articleNumber + ")";
    }

    public String getArticleNumber() { return articleNumber; }
    public String getName() { return name; }
    public int getStockQuantity() { return stockQuantity; }
}