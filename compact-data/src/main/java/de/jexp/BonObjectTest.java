package de.jexp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BonObjectTest {

    private LocalDateTime now = LocalDateTime.ofInstant(Instant.parse("2020-04-01T16:04:00Z"), ZoneId.of("UTC"));
    private BonObject bon;

    @BeforeEach
    void setUp() {
        Store store = new Store("123");
        bon = new BonObject(store, "804", now);
        bon.addItem(5, new Product("386", BigDecimal.valueOf(1000, 2)));
        bon.addItem(20, new Product("666", BigDecimal.valueOf(300, 2)));
    }

    @Test
    void getTime() {
        assertEquals(now, bon.getTime());
    }

    @Test
    void getStore() {
        assertEquals("123", bon.getStore());
    }

    @Test
    void getBon() {
        assertEquals("804", bon.getBon());
    }

    @Test
    void getTotal() {
        assertEquals(BigDecimal.valueOf(11000, 2), bon.getTotal());
    }
}