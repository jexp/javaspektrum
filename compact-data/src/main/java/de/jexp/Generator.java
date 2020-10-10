package de.jexp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.jexp.Constants.*;

public class Generator {
    public List<Store> generateObjects(int stores) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Product[] products = IntStream.range(0, PRODUCTS)
                .mapToObj(prodNo ->
                        new Product(String.valueOf(prodNo),
                                BigDecimal.valueOf(random.nextInt(PRICE_MIN, PRICE_MAX), 2)))
                .toArray(Product[]::new);
        LocalDateTime now = LocalDateTime.now();
        return IntStream.range(0, stores).mapToObj(
                i -> {
                    Store s = new Store(String.valueOf(i));
                    IntStream.range(0, BONS).forEach(bon -> {
                        BonObject b = new BonObject(s, String.valueOf(bon), now);
                        IntStream.range(0, random.nextInt(2, 100))
                                .forEach(item ->
                                        b.addItem(random.nextInt(QUANTITIES),
                                                products[random.nextInt(PRODUCTS)]));
                        s.addBon(b);
                    });
                    return s;
                }
        ).collect(Collectors.toList());
    }

    public List<Store> generateBinary(int stores) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Product[] products = IntStream.range(0, PRODUCTS)
                .mapToObj(prodNo ->
                        new Product(String.valueOf(prodNo),
                                BigDecimal.valueOf(random.nextInt(PRICE_MIN, PRICE_MAX), 2)))
                .toArray(Product[]::new);
        LocalDateTime now = LocalDateTime.now();
        return IntStream.range(0, stores).mapToObj(
                store -> {
                    Store s = new Store(String.valueOf(store));
                    IntStream.range(0, BONS).forEach(bon -> {
                        int items = random.nextInt(2, 100);
                        var b = new BonBinary(now,store, bon,items);
                        IntStream.range(0, items)
                                .forEach(item ->
                                {
                                    Product product = products[random.nextInt(PRODUCTS)];
                                    b.addItem(item,
                                            random.nextInt(QUANTITIES),
                                            product.getProduct(),
                                            product.getPrice()
                                            );
                                });
                        s.addBon(b);
                    });
                    return s;
                }
        ).collect(Collectors.toList());
    }
}
