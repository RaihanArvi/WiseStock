package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StockTest {
    Stock stockTest;

    @BeforeEach
    void runBefore() {
        stockTest = new Stock("UNTR", "United Tractors", 500000,
                5000, 15, 200, 150);
    }

    @Test
    void testConstructor() {
        assertEquals("UNTR", stockTest.getTicker());
        assertEquals("United Tractors", stockTest.getName());
        assertEquals(500000, stockTest.getCapital());
        assertEquals(5000, stockTest.getTotalShares());
        assertEquals(15, stockTest.getDividendPerShare());
        assertEquals(200, stockTest.getOwnedShares());
        assertEquals(150, stockTest.getPrice());
        assertEquals("UNTR", stockTest.toString());
    }

    @Test
    void analyzeValueTest() {
        stockTest.analyzeValue();

        assertEquals(100, stockTest.getBookValue());
        assertEquals(1.5, stockTest.getPriceToBookValue());
        assertEquals(0.1, stockTest.getDividendYield());
    }
}
