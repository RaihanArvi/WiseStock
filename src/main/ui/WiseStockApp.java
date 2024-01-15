package ui;

import model.Portfolio;
import model.Stock;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

// WiseStock stock analyzer terminal application.
public class WiseStockApp {
    private static final String AUTOSAVE_PATH = "./data/saves/autosaves.json";
    private Portfolio portfolio;
    private Scanner input;

    private JsonWriter jsonWriter;
    private JsonWriter jsonAutosave;
    private JsonReader jsonReader;

    // EFFECTS: runs the WiseStock application.
    public WiseStockApp() {
        runWiseStock();
    }

    // MODIFIES: this
    // EFFECTS: processes user input.
    private void runWiseStock() {
        boolean programState = true;
        String keyStroke;

        initialization();

        while (programState) {
            displayMenu();
            keyStroke = input.next();
            keyStroke = keyStroke.toLowerCase();

            if (keyStroke.equals("q")) {
                fileAutosave();
                programState = false;
            } else {
                keyStrokeAction(keyStroke);
            }
        }
        System.out.println("Autosaving...");
        System.out.println("Exiting WiseStock...");

        printLog();
    }

    // MODIFIES: this
    // EFFECTS: initializes portfolio.
    private void initialization() {
        portfolio = new Portfolio("User", 000000);

        input = new Scanner(System.in);
        input.useDelimiter("\n");

        jsonAutosave = new JsonWriter(AUTOSAVE_PATH);
    }

    public static void main(String[] args) {
        new WiseStockApp();
    }

    // EFFECTS: displays the menu options for user.
    private void displayMenu() {
        System.out.println("\nWelcome to WiseStock, " + portfolio.getNameUser() + "!");
        System.out.println("");
        System.out.println(""
                + "\u001B[7ma \u001B[0m Add Stock                  \u001B[7mr \u001B[0m Remove Stock"
                + "\n"
                + "\u001B[7me \u001B[0m Edit Stock                 \u001B[7ml \u001B[0m List All Stocks"
                + "\n"
                + "\u001B[7mf \u001B[0m List Fair Valued Stocks    \u001B[7mo \u001B[0m List Overvalued Stocks"
                + "\n"
                + "\u001B[7ms \u001B[0m Calculate Single Dividend"
                + "  \u001B[7md \u001B[0m Calculate All Dividends"
                + "\n"
                + "\u001B[7mz \u001B[0m Save Portofolio            \u001B[7mx \u001B[0m Load Portofolio"
                + "\n"
                + "\u001B[7mt \u001B[0m Get Stock Info             \u001B[7mq \u001B[0m Quit");
        System.out.print("\nSelect action > ");
    }

    // MODIFIES: this
    // EFFECTS: processes user input keystroke
    @SuppressWarnings("methodlength")
    private void keyStrokeAction(String keyStroke) {
        switch (keyStroke) {
            case "a":
                userAddStock();
                break;
            case "r":
                userRemoveStock();
                break;
            case "e":
                userEditStock();
                break;
            case "l":
                userGetAllStocksInfo();
                break;
            case "f":
                userListFairValuedStocks();
                break;
            case "o":
                userListOvervaluedStocks();
                break;
            case "s":
                userCalculateSingleDividend();
                break;
            case "d":
                userCalculateAllDividends();
                break;
            case "t":
                userGetStockInfo();
                break;
            case "z":
                saveFile();
                break;
            case "x":
                loadFile();
                break;
            default:
                System.out.println("Incorrect command...");
        }
    }

    // MODIFIES: this
    // EFFECTS: adds new stock to portfolio
    private void userAddStock() {
        System.out.print("Enter stock ticker symbol: ");
        String ticker = input.next();

        System.out.print("Enter company name: ");
        String company = input.next();

        System.out.print("Enter market capital: $ ");
        double marketCap = input.nextDouble();

        System.out.print("Enter total amount of public shares: ");
        int shares = input.nextInt();

        System.out.print("Enter dividend per share: $ ");
        double divPerShare = input.nextDouble();

        System.out.print("Enter amount of owned shares: ");
        int ownedShares = input.nextInt();

        System.out.print("Enter current market price: ");
        double price = input.nextDouble();

        this.portfolio.addTicker(ticker, company, marketCap, shares, divPerShare, ownedShares, price);
    }

    // MODIFIES: this
    // EFFECTS: removes any stock from the portfolio
    private void userRemoveStock() {
        System.out.print("Enter ticker symbol to remove: ");
        String ticker = input.next();

        this.portfolio.removeTicker(ticker);
    }

    // MODIFIES: this
    // EFFECTS: edits any stock properties in the portfolio
    private void userEditStock() {
        System.out.print("Enter current stock ticker symbol to edit: ");
        String tickerCurrent = input.next();

        System.out.print("Enter new stock ticker symbol to edit: ");
        String tickerNew = input.next();

        System.out.print("Enter company name: ");
        String company = input.next();

        System.out.print("Enter market capital: $ ");
        double marketCap = input.nextDouble();

        System.out.print("Enter total amount of public shares: ");
        int shares = input.nextInt();

        System.out.print("Enter dividend per share: $ ");
        double divPerShare = input.nextDouble();

        System.out.print("Enter amount of owned shares: ");
        int ownedShares = input.nextInt();

        System.out.print("Enter current market price: ");
        double price = input.nextDouble();

        portfolio.editStock(tickerCurrent, tickerNew, company, marketCap, shares, divPerShare, ownedShares, price);
    }

    // EFFECTS: displays all the stock ticker symbols and the amount
    //          of owned shares in the portfolio
    private void userGetAllStocksInfo() {
        List<String> tickerList = portfolio.getAllTickerList();
        List<Integer> sharesList = portfolio.getOwnedSharesList();
        int counter = 0;

        System.out.println("Owned stocks and amount of shares:");

        for (String ticker : tickerList) {
            System.out.println(ticker + " = " + sharesList.get(counter) + " shares");
            counter++;
        }
    }

    // EFFECTS: displays all the undervalued and equal valued (PBV <= 1) stocks
    //          from the portfolio.
    private void userListFairValuedStocks() {
        List<Stock> stockList = portfolio.listFairValuedStocks();
        List<String> tickerList = portfolio.getSpecificTickerList(stockList);
        int counter = 0;

        System.out.println("Fair Valued Stocks and Their PBVs:");

        for (String ticker : tickerList) {
            System.out.println(ticker + " = " + stockList.get(counter).getPriceToBookValue());
            counter++;
        }
    }

    // EFFECTS: displays all the overvalued (PBV > 1) stocks
    //          from the portfolio.
    private void userListOvervaluedStocks() {
        List<Stock> stockList = portfolio.listOvervaluedStocks();
        List<String> tickerList = portfolio.getSpecificTickerList(stockList);
        int counter = 0;

        System.out.println("Overvalued Stocks and Their PBVs:");

        for (String ticker : tickerList) {
            System.out.println(ticker + " = " + stockList.get(counter).getPriceToBookValue());
            counter++;
        }
    }

    // EFFECTS: calculates total dividend of any given single stock for a given amount of investing years.
    private void userCalculateSingleDividend() {
        System.out.println("Enter ticker symbol: ");
        String ticker = input.next();
        System.out.println("Enter years: ");
        int years = input.nextInt();

        double dividend = this.portfolio.calculateSingleDividend(ticker, years);
        System.out.println("Dividend = " + dividend);
    }

    // EFFECTS: calculates total dividends of all stocks in portfolio for a given amount of investing years.
    private void userCalculateAllDividends() {
        System.out.println("\nCalculate dividend for all stocks.");

        System.out.println("Enter investing years: ");
        int years = input.nextInt();

        double dividendsTotal = this.portfolio.calculateAllDividends(years);
        System.out.println(dividendsTotal);
    }

    // EFFECTS: displays general information about any given stock.
    private void userGetStockInfo() {
        System.out.print("Enter stock ticker symbol to find info: ");
        String ticker = input.next();

        int index = portfolio.getStockIndex(ticker);
        Stock stock = portfolio.getAllStocksList().get(index);

        System.out.println("\nTicker Symbol: " + stock.getTicker());
        System.out.println("Company Name: " + stock.getName());
        System.out.println("Market Capitalization: " + stock.getCapital());
        System.out.println("Total Publicly Offered Shares: " + stock.getTotalShares());
        System.out.println("Dividend Per Share: " + stock.getDividendPerShare());
        System.out.println("Amount of Owned Shares: " + stock.getOwnedShares());
        System.out.println("Current Market Price: " + stock.getPrice());

        System.out.println("\nFundamental Analysis: ");
        System.out.println("Book Value: " + stock.getBookValue());
        System.out.println("Price to Book Value: " + stock.getPriceToBookValue());
        System.out.println("Dividend Yield: " + stock.getDividendYield());
    }

    // EFFECTS: autosave portfolio everytime user quits.
    public void fileAutosave() {
        try {
            jsonAutosave.open();
            jsonAutosave.write(portfolio);
            jsonAutosave.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: autosave failed.");
        }
    }

    // EFFECTS: save portfolio to a json file.
    public void saveFile() {
        System.out.print("Enter portfolio name: ");
        String name = input.next();
        portfolio.setNameUser(name);
        System.out.print("Enter portfolio ID: ");
        int idNumber = input.nextInt();
        portfolio.setIdentificationNumber(idNumber);
        System.out.print("Enter file name: ");
        String file = input.next();

        String path = "./data/" + file;

        jsonWriter = new JsonWriter(path);

        try {
            jsonWriter.open();
            jsonWriter.write(portfolio);
            jsonWriter.close();
            System.out.println("Saved " + portfolio.getNameUser() + " portfolio" + " to " + path);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + path);
        }
    }


    // MODIFIES: this
    // EFFECTS: load portfolio from json file.
    public void loadFile() {
        System.out.print("Enter JSON file name in ./data folder: ");
        String file = input.next();
        String path = "./data/" + file;

        jsonReader = new JsonReader(path);

        try {
            portfolio = jsonReader.read();
            System.out.println("Loaded " + portfolio.getNameUser() + " portfolio" + " from " + path);
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + path);
        }
    }

    // EFFECTS: print out the logs when called.
    private void printLog() {
        System.out.println(portfolio.takeLog());
    }

}
