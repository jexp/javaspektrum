package de.jexp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Store {
    String store;
    List<Bon> bons = new ArrayList<>(10000);

    public Store(String store) {
        this.store = store;
    }
    public void addBon(Bon bon) {
        bons.add(bon);
    }

    public String getStore() {
        return store;
    }
}
interface Constants {
    int STORES=10000;
    int BONS=10000;
    int PRODUCTS=10000;
    int QUANTITIES = 100;
    int PRICE_MIN = 25;
    int PRICE_MAX = 25000;

}
interface BonItem {
    String getProduct();
    BigDecimal getTotal();
    int getQuantity();
}

interface Bon {
    LocalDateTime getTime();
    String getStore();
    String getBon();
    BigDecimal getTotal();
}

class BonObject implements Bon {
    private final LocalDateTime time;
    private final Store store;
    private final String bon;
    private final List<BonItem> items = new ArrayList<>(25);

    @Override
    public LocalDateTime getTime() {
        return time;
    }

    @Override
    public String getStore() {
        return store.getStore();
    }
    @Override
    public String getBon() {
        return bon;
    }

    @Override
    public BigDecimal getTotal() {
        return items.stream().map(BonItem::getTotal).reduce( BigDecimal.ZERO, BigDecimal::add);
    }

    public BonObject(Store store, String bon, LocalDateTime time) {
        this.bon = bon;
        this.store = store;
        this.time = time;
    }
    public void addItem(int quantity, Product product) {
        this.items.add(new LineItem(quantity,product));
    }
}

class LineItem implements BonItem {
    private final int quantity;
    private final Product product;

    @Override
    public String getProduct() {
        return product.getProduct();
    }

    @Override
    public BigDecimal getTotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    public LineItem(int quantity, Product product) {
        this.quantity = quantity;
        this.product = product;
    }
}

class Product {
    private final String product;
    private final BigDecimal price;

    public Product(String product, BigDecimal price) {
        this.product = product;
        this.price = price;
    }

    public String getProduct() {
        return product;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
