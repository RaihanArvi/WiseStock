package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.List;

// representing a stock portfolio having owner name, id, and list of stocks.
public class Portfolio implements Writable {
    private String nameUser;
    private int identificationNumber;
    private List<Stock> allStocks;

    private String logString;

    /* REQUIRES: name has a non-zero length, id > 0.
     * EFFECTS: creates new Portfolio object with name, id,
     *          and an empty list of stocks allStocks.
     */
    public Portfolio(String name, int id) {
        this.nameUser = name;
        this.identificationNumber = id;
        allStocks = new ArrayList();

        logString = "";
        EventLog.getInstance().logEvent(new Event("New Portfolio Created!"));
    }

    /* REQUIRES: ticker has non-zero length.
     *           capital, shares, ownedShares, and price are > 0. dividend >= 0.
     * MODIFIES: this, Stock.
     * EFFECTS: creates a new Stock object with given arguments, analyze the stock's fundamentals,
     *          add that stock object to allStocks. each stock has unique ticker,
     *          fails to add to allStocks otherwise. return integer 1 if succeeded adding
     *          new stock and -1 if fails.
     */
    public int addTicker(String ticker, String name, double capital, int shares, double dividend,
                         int ownedShares, double price) {
        int succeed;

        if (!isDuplicate(ticker)) {
            Stock stock = new Stock(ticker, name, capital, shares, dividend, ownedShares, price);
            stock.analyzeValue();
            allStocks.add(stock);
            succeed = 1;

            EventLog.getInstance().logEvent(new Event("Added Stock: " + ticker));
        } else {
            succeed = -1;
            EventLog.getInstance().logEvent(new Event("Failed to Add Stock: Duplicate"));
        }
        return succeed;
    }

    // REQUIRES: ticker has a non-zero length.
    // EFFECTS: returns false if one of the Stock in allStocks have the same ticker symbol as
    //          given ticker.
    public boolean isDuplicate(String ticker) {
        boolean duplicate = false;

        for (Stock stock : allStocks) {
            if (ticker.equals(stock.getTicker())) {
                duplicate = true;
                break;
            }
        }
        return duplicate;
    }

    // REQUIRES: ticker has non-zero length
    // MODIFIES: this
    // EFFECTS: remove a Stock with given ticker symbol from allStocks.
    public void removeTicker(String ticker) {
        int index = getStockIndex(ticker);
        allStocks.remove(index);

        EventLog.getInstance().logEvent(new Event("Removed Stock: " + ticker));
    }

    // REQUIRES: years > 0. getStockIndex(ticker) != -1.
    // EFFECTS: returns dividend from a given stock ticker symbol and
    //          number of investing years.
    public double calculateSingleDividend(String ticker, int years) {
        int stockIndex = getStockIndex(ticker);
        Stock stock = allStocks.get(stockIndex);
        double dividendAmount = stock.getDividendPerShare() * stock.getOwnedShares() * years;

        EventLog.getInstance().logEvent(new Event("Dividend for " + ticker + " Calculated."));
        return dividendAmount;
    }

    // REQUIRES: years > 0.
    // EFFECTS: returns total dividends from all Stocks in allStocks for any given years.
    public double calculateAllDividends(int years) {
        double divPerShare;
        int ownedShares;

        double dividendsAmount = 0;

        for (Stock stock : allStocks) {
            divPerShare = stock.getDividendPerShare();
            ownedShares = stock.getOwnedShares();

            dividendsAmount += divPerShare * ownedShares * years;
        }

        EventLog.getInstance().logEvent(new Event("All Stocks Dividend Calculated."));
        return dividendsAmount;
    }

    // EFFECTS: returns index of Stock in allStocks from a given ticker.
    //          returns -1 if not found.
    public int getStockIndex(String ticker) {
        int counter = 0;
        int index = -1;

        for (Stock stock : allStocks) {
            if (stock.getTicker().equals(ticker)) {
                index = counter;
            } else {
                counter++;
            }
        }
        return index;
    }

    // EFFECTS: returns a list of all ticker symbol in allStocks list.
    public List<String> getAllTickerList() {
        List<String> tickerList = new ArrayList();

        for (Stock stock : allStocks) {
            tickerList.add(stock.getTicker());
        }
        return tickerList;
    }

    // REQUIRES: stockList != null.
    // EFFECTS: returns a list of all ticker symbol in any list of Stocks.
    public List<String> getSpecificTickerList(List<Stock> stockList) {
        List<String> tickerList = new ArrayList();

        for (Stock stock : stockList) {
            tickerList.add(stock.getTicker());
        }
        return tickerList;
    }

    // EFFECTS: returns a list of owned shares of each Stock in allStocks.
    public List<Integer> getOwnedSharesList() {
        List<Integer> ownedSharesList = new ArrayList();
        for (Stock stock : allStocks) {
            ownedSharesList.add(stock.getOwnedShares());
        }
        return ownedSharesList;
    }

    // REQUIRES: tickerSymbol has non-zero length.
    //           capital, shares, ownedShares, and price are > 0. dividend >= 0.
    // MODIFIES: Stock.
    // EFFECTS:  edits any given tickerSymbol Stock fields from given arguments
    //           and reanalyze the stock's fundamentals.
    public void editStock(String tickerSymbol, String ticker, String name, double capital,
                          int shares, double dividend, int ownedShares, double price) {
        int index = getStockIndex(tickerSymbol);
        Stock stock = allStocks.get(index);

        stock.setTicker(ticker);
        stock.setName(name);
        stock.setCapital(capital);
        stock.setTotalShares(shares);
        stock.setDividendPerShare(dividend);
        stock.setOwnedShares(ownedShares);
        stock.setPrice(price);

        stock.analyzeValue();
        EventLog.getInstance().logEvent(new Event("Stock Information Edited: " + tickerSymbol));
    }

    // EFFECTS: returns Stocks that have getPriceToBookValue() <= 1.
    public List<Stock> listFairValuedStocks() {
        List<Stock> stockList = new ArrayList();
        double stockPBV;

        for (Stock stock : this.allStocks) {
            stockPBV = stock.getPriceToBookValue();

            if (stockPBV <= 1) {
                stockList.add(stock);
            }
        }
        EventLog.getInstance().logEvent(new Event("Fair Valued Stocks Listed"));
        return stockList;
    }

    // EFFECTS: returns Stocks that have getPriceToBookValue() > 1.
    public List<Stock> listOvervaluedStocks() {
        List<Stock> stockList = new ArrayList();
        double stockPBV;

        for (Stock stock : this.allStocks) {
            stockPBV = stock.getPriceToBookValue();

            if (stockPBV > 1) {
                stockList.add(stock);
            }
        }
        EventLog.getInstance().logEvent(new Event("Overvalued Stocks Listed"));
        return stockList;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", nameUser);
        json.put("id", identificationNumber);
        json.put("stocks", stocksToJson());
        return json;
    }

    // EFFECTS: returns every stocks in this portfolio as JSON array.
    private JSONArray stocksToJson() {
        JSONArray jsonArray = new JSONArray();

        for (Stock stock : allStocks) {
            jsonArray.put(stock.toJson());
        }

        return jsonArray;
    }

    // MODIFIES: this
    // EFFECTS: returns string of all logs when called.
    public String takeLog() {
        logString = logString + "\nProgram Logs:\n\n";

        for (Event next : EventLog.getInstance()) {
            logString = logString + next.toString();
            logString = logString + "\n";
        }
        return logString;
    }

    // Getter functions.
    public List<Stock> getAllStocksList() {
        return allStocks;
    }

    public String getNameUser() {
        return nameUser;
    }

    public int getUserId() {
        return identificationNumber;
    }

    // Setter functions.
    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
        EventLog.getInstance().logEvent(new Event("Portfolio Name Set: " + nameUser));
    }

    public void setIdentificationNumber(int id) {
        this.identificationNumber = id;
        EventLog.getInstance().logEvent(new Event("ID Number Set: " + id));
    }

}
