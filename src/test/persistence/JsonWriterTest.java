package persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Portfolio;
import model.Stock;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class JsonWriterTest {
    Portfolio portfolio;

    @BeforeEach
    void runBefore() {
        portfolio = new Portfolio("Soekarno", 654321);
    }

    @Test
    void testWriterInvalidFile() {
        try {
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriterEmptyPortfolio() {
        try {
            JsonWriter writer = new JsonWriter("./data/testJson/testWriterEmptyPortfolio.json");
            writer.open();
            writer.write(portfolio);
            writer.close();

            JsonReader reader = new JsonReader("./data/testJson/testWriterEmptyPortfolio.json");
            portfolio = reader.read();
            assertEquals("Soekarno", portfolio.getNameUser());
            assertEquals(654321, portfolio.getUserId());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterGeneralPortfolio() {
        try {
            portfolio.addTicker("BREN", "Baramulti Minerals", 5000, 10000, 1,
                    500, 55);
            portfolio.addTicker("PANI", "Pantai Indah Kapuk", 10000, 50000, 0.5,
                    250, 85);
            JsonWriter writer = new JsonWriter("./data/testJson/testWriterGeneralPortfolio.json");
            writer.open();
            writer.write(portfolio);
            writer.close();

            JsonReader reader = new JsonReader("./data/testJson/testWriterGeneralPortfolio.json");
            portfolio = reader.read();
            assertEquals("Soekarno", portfolio.getNameUser());
            assertEquals(654321, portfolio.getUserId());
            List<Stock> stocks = portfolio.getAllStocksList();
            assertEquals(2, stocks.size());

            assertEquals(5000, stocks.get(0).getCapital());
            assertEquals(10000, stocks.get(1).getCapital());

        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }
}
