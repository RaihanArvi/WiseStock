package persistence;

import model.Portfolio;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

// Represents a reader that reads Portfolio from JSON data stored in a file.
// Code influenced by the JsonSerializationDemo
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file.
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads portfolio from file and returns it;
    // throws IOException if an error occurs reading data from file
    public Portfolio read() throws IOException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);

        return parsePortfolio(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it.
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }
        return contentBuilder.toString();
    }

    // EFFECTS: parses portfolio from JSON object and returns it.
    private Portfolio parsePortfolio(JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        int id = jsonObject.getInt("id");
        Portfolio portfolio = new Portfolio(name, id);

        addStocks(portfolio, jsonObject);
        return portfolio;
    }

    // MODIFIES: portfolio.
    // EFFECTS: parses stocks from JSON object and adds them to portfolio.
    private void addStocks(Portfolio portfolio, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("stocks");

        for (Object json : jsonArray) {
            JSONObject nextStock = (JSONObject) json;
            addStock(portfolio, nextStock);
        }
    }

    // MODIFIES: portfolio.
    // EFFECTS: parses stock from JSON object and adds it to portfolio.
    private void addStock(Portfolio portfolio, JSONObject jsonObject) {
        String ticker = jsonObject.getString("ticker");
        String name = jsonObject.getString("company");
        double capital = jsonObject.getDouble("capital");
        int sharesTotal = jsonObject.getInt("shares");
        double dividendPerShare = jsonObject.getDouble("dividend");
        int ownedShares = jsonObject.getInt("owned");
        double price = jsonObject.getDouble("price");

        portfolio.addTicker(ticker, name, capital, sharesTotal, dividendPerShare, ownedShares,
                                price);
    }
}