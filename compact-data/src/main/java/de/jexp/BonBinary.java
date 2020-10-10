package de.jexp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.AbstractList;
import java.util.List;

public class BonBinary implements Bon {
    final static int TIME=4, STORE=2, BON = 2, CUSTOMER=2, PRODUCT=2, QUANTITY = 1, PRICE=3;
    final static int TIME_OFF = 0, STORE_OFF = TIME_OFF + TIME, BON_OFF = STORE_OFF+STORE, CUSTOMER_OFF = BON_OFF + BON;
    final static int  PRODUCT_OFF = 0, QUANTITY_OFF = PRODUCT_OFF + PRODUCT, PRICE_OFF = QUANTITY_OFF + QUANTITY;
    private static final int SIZE = TIME+STORE+BON+CUSTOMER;
    private static final int ITEM_SIZE = PRODUCT+ QUANTITY +PRICE;
    private final int count;
    private final Data data;

    /*
    time 26M -> 25 bit , ms / 1000 / 60 -> 4B
    time YYYY-2000..2100 (6) MM 0..11 (4), dd 0..31 (5), hh 0..23 (5), mm 0..60 (6)
    store 0..10000 - 14 b -> 2B
    bon 0..10000 -> 14b -> 2B
    customer 0..50000 -> 16b -> 2B
    product 0..5000 -> 13b -> 2B
    quantity 0..250 -> 8 -> 1B
    price -> 0..250000 -> 18 -> 3B
    */
    public BonBinary(LocalDateTime time, String store, String bon, int count) {
        data = new Data(SIZE + count * ITEM_SIZE);
        this.count = count;
        long value = time.toEpochSecond(ZoneOffset.UTC) / 60;
        data.write(value, TIME_OFF,TIME);
        data.write(Long.parseLong(store), STORE_OFF,STORE);
        data.write(Long.parseLong(bon), BON_OFF,BON);
    }
    public BonBinary(LocalDateTime time, long store, long bon, int count) {
        data = new Data(SIZE + count * ITEM_SIZE);
        this.count = count;
        long value = time.toEpochSecond(ZoneOffset.UTC) / 60;
        data.write(value, TIME_OFF,TIME);
        data.write(store, STORE_OFF,STORE);
        data.write(bon, BON_OFF,BON);
    }

    @Override
    public LocalDateTime getTime() {
        long value = data.read(TIME_OFF, TIME);
        return LocalDateTime.ofEpochSecond(value *60,0,ZoneOffset.UTC);
    }

    @Override
    public String getStore() {
        return String.valueOf(data.read(STORE_OFF,STORE));
    }

    @Override
    public String getBon() {
        return String.valueOf(data.read(BON_OFF,BON));
    }

    @Override
    public BigDecimal getTotal() {
        long result = 0;
        for (int item=0;item<count;item++) {
            result += getTotal(item);
        }
        return BigDecimal.valueOf(result, 2);
    }

    private long getTotal(int item) {
        int off = SIZE + item * ITEM_SIZE;
        return data.read(off + QUANTITY_OFF, QUANTITY) *
                 data.read(off + PRICE_OFF, PRICE);
    }

    public void addItem(int item, int quantity, String product, BigDecimal price) {
        int off = SIZE + item * ITEM_SIZE;
        data.write(quantity, off + QUANTITY_OFF, QUANTITY);
        data.write(Long.parseLong(product),off + PRODUCT_OFF, PRODUCT);
        data.write(price.unscaledValue().longValue(),off + PRICE_OFF, PRICE);
    }

    public void addProductTotal(long[] totals) {
        for (int item=0;item<count;item++) {
            int off = SIZE + item * ITEM_SIZE;
            totals[(int) data.read( off + PRODUCT_OFF, PRODUCT)]
                    += data.read( off + QUANTITY_OFF, QUANTITY) *
                    data.read(off + PRICE_OFF, PRICE);
        }
    }

    @Override
    public List<BonItem> getItems() {
        return new AbstractList<>() {
            @Override
            public BonItem get(int item) {
                int off = SIZE + item * ITEM_SIZE;
                return new BonItem() {
                    @Override
                    public String getProduct() {
                        return String.valueOf(data.read(off + PRODUCT_OFF, PRODUCT));
                    }

                    @Override
                    public BigDecimal getTotal() {
                        return BigDecimal.valueOf(BonBinary.this.getTotal(item),2);
                    }

                    @Override
                    public int getQuantity() {
                        return (int) data.read(off + QUANTITY_OFF, QUANTITY);
                    }
                };
            }

            @Override
            public int size() {
                return count;
            }
        };
    }
}
