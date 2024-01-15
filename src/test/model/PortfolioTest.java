package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioTest {
    Portfolio portfolioTest;

    @BeforeEach
    void runBefore() {
        portfolioTest = new Portfolio("Ryan", 65377822);
    }

    @Test
    void testConstructor() {
        assertEquals("Ryan", portfolioTest.getNameUser());
        assertEquals(65377822, portfolioTest.getUserId());

        portfolioTest.setNameUser("Jokowi");
        portfolioTest.setIdentificationNumber(123456);

        assertEquals("Jokowi", portfolioTest.getNameUser());
        assertEquals(123456, portfolioTest.getUserId());
    }

    @Test
    void addTickerTestSuccessOneStock() {
        String tickerTest = "GOTO";
        int succeed =  portfolioTest.addTicker(tickerTest, "Gojek Tokopedia", 5000000,
                5000, 0, 1000, 50);
        List<Stock> stockList = portfolioTest.getAllStocksList();

        assertEquals(1, succeed);
        assertEquals(1, stockList.size());
        assertEquals(tickerTest, stockList.get(0).getTicker());
    }

    @Test
    void addTickerTestSuccessTwoStocks() {
        String tickerTestOne = "GOTO";
        String tickerTestTwo = "PGEO";

        int succeedOne =  portfolioTest.addTicker(tickerTestOne, "Gojek Tokopedia", 5000000,
                5000, 0, 1000, 50);
        int succeedTwo = portfolioTest.addTicker(tickerTestTwo, "Pertamina Geothermal", 2500000,
                10000, 1, 500, 25);

        List<Stock> stockList = portfolioTest.getAllStocksList();

        assertEquals(1, succeedOne);
        assertEquals(1, succeedTwo);

        assertEquals(2, stockList.size());

        assertEquals(tickerTestOne, stockList.get(0).getTicker());
        assertEquals(tickerTestTwo, stockList.get(1).getTicker());
    }

    @Test
    void addTickerTestTwoStocksDuplicates() {
        String tickerTestOne = "GOTO";
        String tickerTestTwo = "GOTO";

        int succeedOne =  portfolioTest.addTicker(tickerTestOne, "Gojek Tokopedia", 5000000,
                5000, 0, 1000, 50);
        int succeedTwo = portfolioTest.addTicker(tickerTestTwo,  "Gojek Tokopedia", 5000000,
                5000, 0, 1000, 50);

        List<Stock> stockList = portfolioTest.getAllStocksList();

        assertEquals(1, succeedOne);
        assertEquals(-1, succeedTwo);

        assertEquals(1, stockList.size());

        assertEquals(tickerTestOne, stockList.get(0).getTicker());
    }

    @Test
    void isDuplicateTest() {
        portfolioTest.addTicker("SHPE", "Shoppee Indonesia", 250000,
                5000, 0, 1000, 25);
        portfolioTest.addTicker("PTBA", "Bukit Assam Mining", 100000,
                1500, 0, 1000, 45);
        portfolioTest.addTicker("BBCA", "Central Asia Bank", 150000,
                2000, 0, 1000, 35);

        boolean noDuplicate = portfolioTest.isDuplicate("GOTO");
        boolean duplicateOne = portfolioTest.isDuplicate("SHPE");
        boolean duplicateTwo = portfolioTest.isDuplicate("BBCA");

        assertFalse(noDuplicate);
        assertTrue(duplicateOne);
        assertTrue(duplicateTwo);
    }

    @Test
    void removeTickerTest() {
        portfolioTest.addTicker("SHPE", "Shoppee Indonesia", 5000000,
                5000, 0, 1000, 50);
        portfolioTest.addTicker("PTBA", "Bukit Assam Mining", 5000000,
                5000, 0, 1000, 50);

        List<Stock> stockList = portfolioTest.getAllStocksList();
        assertEquals(2, stockList.size());

        portfolioTest.removeTicker("PTBA");
        assertEquals(1, stockList.size());

        portfolioTest.removeTicker("SHPE");
        assertEquals(0, stockList.size());
    }

    @Test
    void calculateSingleDividendTest() {
        portfolioTest.addTicker("SHPE", "Shoppee Indonesia", 100000,
                0, 0.5, 1000, 25);
        portfolioTest.addTicker("PTBA", "Bukit Asam Mining", 150000,
                0, 1, 250, 45);

        double dividend = portfolioTest.calculateSingleDividend("SHPE", 5);
        assertEquals(dividend, 2500);
    }

    @Test
    void calculateAllDividendsTest() {
        portfolioTest.addTicker("SHPE", "Shoppee Indonesia", 5000000,
                5000, 0.5, 1000, 25);
        portfolioTest.addTicker("PTBA", "Bukit Assam Mining", 5000000,
                5000, 1, 250, 45);
        portfolioTest.addTicker("BBCA", "Central Asia Bank", 5000000,
                5000, 0.25, 500, 50);

        double dividend = portfolioTest.calculateAllDividends(5);
        assertEquals(dividend, 4375);
    }

    @Test
    void getStockIndexTest() {
        portfolioTest.addTicker("SHPE", "Shoppee Indonesia", 5000000,
                5000, 0, 1000, 25);
        portfolioTest.addTicker("PTBA", "Bukit Assam Mining", 5000000,
                5000, 0, 1000, 45);
        portfolioTest.addTicker("BBCA", "Central Asia Bank", 5000000,
                5000, 0, 1000, 50);

        int indexOne = portfolioTest.getStockIndex("SHPE");
        int indexTwo = portfolioTest.getStockIndex("PTBA");
        int indexThree = portfolioTest.getStockIndex("BBCA");
        int indexFails = portfolioTest.getStockIndex("BSSR");

        assertEquals(0, indexOne);
        assertEquals(1, indexTwo);
        assertEquals(2, indexThree);
        assertEquals(-1, indexFails);
    }

    @Test
    void getTickerListTest() {
        portfolioTest.addTicker("SHPE", "Shoppee Indonesia", 0,
                50000, 0, 150, 25);
        portfolioTest.addTicker("PTBA", "Bukit Assam Mining", 0,
                50000, 0, 250, 45);
        portfolioTest.addTicker("BBCA", "Central Asia Bank", 0,
                10000, 0, 250, 50);

        List<String> actualTickersList = new ArrayList();
        actualTickersList.add("SHPE");
        actualTickersList.add("PTBA");
        actualTickersList.add("BBCA");

        List<String> ownedTickersList = portfolioTest.getAllTickerList();
        assertEquals(ownedTickersList, actualTickersList);
    }

    @Test
    void getOwnedSharesListTest() {
        portfolioTest.addTicker("SHPE", "Shoppee Indonesia", 100000,
                0, 0, 150, 25);
        portfolioTest.addTicker("PTBA", "Bukit Assam Mining", 200000,
                0, 0, 200, 45);
        portfolioTest.addTicker("BBCA", "Central Asia Bank", 150000,
                0, 0, 250, 50);

        List<Integer> actualSharesList = new ArrayList();
        actualSharesList.add(150);
        actualSharesList.add(200);
        actualSharesList.add(250);

        List<Integer> ownedSharesList = portfolioTest.getOwnedSharesList();
        assertEquals(ownedSharesList, actualSharesList);
    }

    @Test
    void getAllTickerListTest() {
        portfolioTest.addTicker("SHPE", "Shoppee Indonesia", 100000,
                0, 0, 150, 25);
        portfolioTest.addTicker("PTBA", "Bukit Assam Mining", 200000,
                0, 0, 200, 45);
        portfolioTest.addTicker("BBCA", "Central Asia Bank", 150000,
                0, 0, 250, 50);

        List<String> expectedList = new ArrayList();
        expectedList.add("SHPE");
        expectedList.add("PTBA");
        expectedList.add("BBCA");

        List<String> tickersList = portfolioTest.getAllTickerList();

        assertEquals(expectedList, tickersList);
    }

    @Test
    void getSpecificTickerListTest() {
        Stock stockOne = new Stock("UNTR", "United Tractors", 500000,
                1500, 0.5, 200, 30);
        Stock stockTwo = new Stock("PANI", "Pantai Indah Kapuk Dua", 300000,
                1000, 0.5, 100, 20);
        Stock stockThree = new Stock("ASII", "Astra Indonesia", 200000,
                1000, 0, 300, 10);

        List<Stock> stockList = new ArrayList();
        stockList.add(stockOne);
        stockList.add(stockTwo);
        stockList.add(stockThree);

        List<String> expected = new ArrayList();
        expected.add("UNTR");
        expected.add("PANI");
        expected.add("ASII");

        assertEquals(expected, portfolioTest.getSpecificTickerList(stockList));
    }

    @Test
    void editStockTest() {
        portfolioTest.addTicker("BBCA", "Central Asia Bank", 150000,
                2500, 0, 250, 50);

        portfolioTest.editStock("BBCA", "BMRI", "Mandiri Bank", 100000,
                3000, 1, 150, 25);

        Stock stockTest = portfolioTest.getAllStocksList().get(portfolioTest.getStockIndex("BMRI"));

        assertEquals(1, portfolioTest.getAllStocksList().size());
        assertEquals("BMRI", stockTest.getTicker());
        assertEquals("Mandiri Bank", stockTest.getName());
        assertEquals(100000, stockTest.getCapital());
    }

    @Test
    void listFairValuedStocksTest() {
        portfolioTest.addTicker("PTBA", "Bukit Asam Mining", 200000,
                2000, 0, 200, 150);
        portfolioTest.addTicker("BBCA", "Central Asia Bank", 100000,
                2000, 0, 250, 35); // fair valued (PBV < 1)
        portfolioTest.addTicker("UNTR", "United Tractors", 200000,
                5000, 0, 250, 40); // fair valued (PBV == 1)
        portfolioTest.addTicker("ASII", "Astra Indonesia", 100000,
                1000, 0, 250, 150);

        List<Stock> expectedList = new ArrayList();
        expectedList.add(portfolioTest.getAllStocksList().get(1));
        expectedList.add(portfolioTest.getAllStocksList().get(2));

        assertEquals(expectedList, portfolioTest.listFairValuedStocks());
    }

    @Test
    void listOvervaluedStocksTest() {
        portfolioTest.addTicker("PTBA", "Bukit Asam Mining", 200000,
                2000, 0, 200, 150); // overvalued (PBV > 1)
        portfolioTest.addTicker("BBCA", "Central Asia Bank", 100000,
                2000, 0, 250, 35);
        portfolioTest.addTicker("UNTR", "United Tractors", 200000,
                5000, 0, 250, 40);
        portfolioTest.addTicker("ASII", "Astra Indonesia", 100000,
                1000, 0, 250, 150); // overvalued (PBV > 1)

        List<Stock> expectedList = new ArrayList();
        expectedList.add(portfolioTest.getAllStocksList().get(0));
        expectedList.add(portfolioTest.getAllStocksList().get(3));

        assertEquals(expectedList, portfolioTest.listOvervaluedStocks());
    }

}