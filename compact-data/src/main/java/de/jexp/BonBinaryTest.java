package de.jexp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class BonBinaryTest {

    private LocalDateTime now = LocalDateTime.ofInstant(Instant.parse("2020-04-01T16:04:00Z"),ZoneId.of("UTC"));
    private BonBinary bon;

    @BeforeEach

    void setUp() {
        bon = new BonBinary(now,"123","804",2);
        bon.addItem(0,5, "386", BigDecimal.valueOf(1000,2));
        bon.addItem(1,20, "666", BigDecimal.valueOf(300,2));
    }

    @Test
    void getTime() {
        assertEquals(now,bon.getTime());
    }

    @Test
    void getStore() {
        assertEquals("123",bon.getStore());
    }

    @Test
    void getBon() {
        assertEquals("804",bon.getBon());
    }

    @Test
    void getTotal() {
        assertEquals(BigDecimal.valueOf(11000,2), bon.getTotal());
    }
}