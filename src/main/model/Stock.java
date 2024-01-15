package model;

import org.json.JSONObject;
import persistence.Writable;

/* represents a single Stock with a ticker symbol, company name, market capital,
 * amount of publicly offered shares, dividend per share in $, amount of owned shares,
 * current price of stock in $, book value in $, price to book value (PBV) ratio,
 * and dividend yield.
 */
public class Stock implements Writable {
    private String ticker;
    private String name;
    private double capital;
    private int sharesTotal;
    private double dividendPerShare;
    private int ownedShares;
    private double price;

    private double bookValue;
    private double priceToBookValue;
    private double dividendYield;

    /* REQUIRES: ticker has non-empty length, capital, shares,
     *           ownedShares, and price are > 0. dividend >= 0.
     * EFFECTS: assigns given arguments as the Stock properties.
     */
    public Stock(String ticker, String name, double capital, int shares, double dividend,
                 int ownedShares, double price) {
        this.ticker = ticker;
        this.name = name;
        this.capital = capital;
        this.sharesTotal = shares;
        this.dividendPerShare = dividend;
        this.ownedShares = ownedShares;
        this.price = price;
    }

    // MODIFIES: this
    // EFFECTS: calculate the book value and PBV of the stock and assign to
    //          bookValue and priceToBookValue.
    public void analyzeValue() {
        this.bookValue = this.capital / this.sharesTotal;
        this.priceToBookValue = this.price / this.bookValue;
        this.dividendYield = this.dividendPerShare / this.price;
    }

    // EFFECTS: Put stock's info inside a JSON object.
    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("ticker", this.ticker);
        json.put("company", this.name);
        json.put("capital", this.capital);
        json.put("shares", this.sharesTotal);
        json.put("dividend", this.dividendPerShare);
        json.put("owned", this.ownedShares);
        json.put("price", this.price);

        return json;
    }

    // EFFECTS: returns the ticker symbol when toString() is called.
    @Override
    public String toString() {
        return this.ticker;
    }

    // Getter functions.
    public String getTicker() {
        return this.ticker;
    }

    public String getName() {
        return this.name;
    }

    public double getCapital() {
        return this.capital;
    }

    public int getTotalShares() {
        return this.sharesTotal;
    }

    public double getDividendPerShare() {
        return this.dividendPerShare;
    }

    public int getOwnedShares() {
        return this.ownedShares;
    }

    public double getPrice() {
        return this.price;
    }

    public double getBookValue() {
        return this.bookValue;
    }

    public double getPriceToBookValue() {
        return this.priceToBookValue;
    }

    public double getDividendYield() {
        return this.dividendYield;
    }

    // Setter functions.
    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCapital(double capital) {
        this.capital = capital;
    }

    public void setTotalShares(int shares) {
        this.sharesTotal = shares;
    }

    public void setDividendPerShare(double div) {
        this.dividendPerShare = div;
    }

    public void setOwnedShares(int shares) {
        this.ownedShares = shares;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
