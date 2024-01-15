package persistence;

import model.Portfolio;
import model.Stock;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonReaderTest {

    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("./data/testJson/noSuchFile.json");
        try {
            Portfolio portfolio = reader.read();
            fail("IOException expected");

        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderEmptyPortfolio() {
        JsonReader reader = new JsonReader("./data/testJson/testEmptyPortfolio.json");
        try {
            Portfolio portfolio = reader.read();
            assertEquals("Bill", portfolio.getNameUser());
            assertEquals(555555, portfolio.getUserId());
            assertEquals(0, portfolio.getAllStocksList().size());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderGeneralPortfolio() {
        JsonReader reader = new JsonReader("./data/testJson/testPortfolio.json");
        try {
            Portfolio portfolio = reader.read();
            assertEquals("Jokowi", portfolio.getNameUser());
            assertEquals(123456, portfolio.getUserId());
            List<Stock> stocks = portfolio.getAllStocksList();
            double totalDividendsFiveYears = portfolio.calculateAllDividends(5);
            assertEquals(4, stocks.size());
            assertEquals(7500, totalDividendsFiveYears);
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }
}