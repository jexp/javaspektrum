package de.jexp;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.jexp.Constants.PRODUCTS;

public class Computer {

    public Map<String, BigDecimal> computeSalesPerProduct(List<Store> stores) {
        Map<String, BigDecimal> totals =
                stores.stream()
                        .flatMap(s -> s.getBons().stream())
                        .flatMap(b -> b.getItems().stream())
                        .collect(Collectors.groupingBy(BonItem::getProduct,
                                Collectors.reducing(BigDecimal.ZERO, BonItem::getTotal, BigDecimal::add)));
        return totals;
    }
    public long[] computeSalesPerProductBinary(List<Store> stores) {
        long[] totals = new long[PRODUCTS];
        for (Store store : stores) {
            List<BonBinary> bons = (List) store.getBons();
            for (BonBinary bon : bons) {
                bon.addProductTotal(totals);
            }
        }
        return totals;
    }
}
