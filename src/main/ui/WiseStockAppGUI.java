package ui;

import model.Portfolio;
import model.Stock;
import org.json.JSONException;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static javax.swing.JOptionPane.*;

// WiseStock stock analyzer GUI application.
public class WiseStockAppGUI extends JFrame {
    private static final String AUTOSAVE_PATH = "./data/saves/autosave.json";

    private Portfolio portfolio;

    // JSON Save & Load
    private JsonWriter jsonWriter;
    private JsonReader jsonReader;
    private JsonWriter jsonAutosave;

    private List<JTable> fhfhf;

    // Main & Top Panels
    private JPanel mainPanel;
    private JPanel topPanel;
    private JDialog dialog;

    // Bottom Panel
    private JPanel bottomPanel;
    private JButton fairValuedButton;
    private JButton overvaluedButton;
    private JButton calculateDividendsButton;

    // Left Panel
    private JPanel leftPanel;
    private JButton addStock;
    private JButton removeStock;
    private JScrollPane stockScrollList;
    private DefaultListModel<Stock> stockModel;
    private JList<Stock> stocksList;

    // Right Panel
    private JPanel rightPanel;
    private JButton editButton;
    private JButton calculateDividendButton;
    private JLabel infoLabel;

    // Menu Bar
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu aboutMenu;
    private JMenuItem saveButton;
    private JMenuItem loadButton;
    private JMenuItem quitButton;
    private JMenuItem aboutButton;

    // Add Stock Panel
    JTextField tickerSymbol;
    JTextField companyName;
    JTextField companyCapital;
    JTextField totalShares;
    JTextField divPerShare;
    JTextField ownedShares;
    JTextField marketPrice;

    // Edit Panel
    private JPanel stockEditPanel;
    private JLabel nameLabel;
    private JTextField nameField;
    private JButton doneEditing;
    private JButton cancelEdit;

    // Layout Manager
    GridBagConstraints gbc;

    // EFFECTS: creates new app window, set up all panels, display splash screen.
    public WiseStockAppGUI() {
        this.setTitle("WiseStock : Your Personal Portfolio Manager");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(windowAdapter);
        this.setSize(900, 650);
        this.setResizable(false);

        // Row Size
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);

        addMainPanel();
        setMenuBar();
        createTopPanel();
        createBottomPanel();
        createLeftPanel();
        createRightPane();
        //setupStockEditPanel();

        loadAutosave();

        splashScreen();
        centreOnScreen();
        this.setVisible(true);
    }

    /* MODIFIES: this.
     * EFFECTS: creates a menu bar, with file and about tab, and their
     *          corresponding submenu.
     */
    private void setMenuBar() {
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        saveButton = new JMenuItem("Save Portfolio As...");
        saveButton.addActionListener(e -> saveDialog());
        loadButton = new JMenuItem("Load Portfolio...");
        loadButton.addActionListener(e -> loadDialog());
        quitButton = new JMenuItem("Quit WiseStock");
        quitButton.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));

        fileMenu.add(saveButton);
        fileMenu.add(loadButton);
        fileMenu.add(quitButton);

        aboutMenu = new JMenu("About");
        aboutButton = new JMenuItem("About WiseStock");
        aboutButton.addActionListener(e -> aboutDialog());

        aboutMenu.add(aboutButton);

        menuBar.add(fileMenu);
        menuBar.add(aboutMenu);

        this.setJMenuBar(menuBar);
    }

    // MODIFIES: this.
    // EFFECTS: creates the main panel and set the layout manager.
    private void addMainPanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        this.add(mainPanel);
    }

    // MODIFIES: this.
    // EFFECTS: creates top panel, set layout manager, set border, and attach
    //          to main panel.
    private void createTopPanel() {
        topPanel = new JPanel(new GridLayout(1, 2));
        topPanel.setBorder(BorderFactory.createEmptyBorder(50,50,10,50));

        gbc.gridy = 0;
        gbc.ipady = 20;
        gbc.gridheight = 1;

        mainPanel.add(topPanel, gbc);
    }

    // MODIFIES: this.
    // EFFECTS: creates bottom panel, set layout manager, set border, and attach
    //          to main panel. Add three buttons to bottom panel.
    private void createBottomPanel() {
        bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));
        bottomPanel.setForeground(Color.black);

        fairValuedButton = new JButton("List Fair Valued Stocks");
        fairValuedButton.addActionListener(e -> fairValuedStocksDialog());
        overvaluedButton = new JButton("List Overvalued Stocks");
        overvaluedButton.addActionListener(e -> overvaluedStocksDialog());
        calculateDividendsButton = new JButton("Calculate All Dividends");
        calculateDividendsButton.addActionListener(e -> calculateAllDividends());

        bottomPanel.add(fairValuedButton);
        bottomPanel.add(overvaluedButton);
        bottomPanel.add(calculateDividendsButton);

        gbc.gridy = 1;
        gbc.ipady = 0;
        gbc.gridheight = 1;

        mainPanel.add(bottomPanel, gbc);
    }

    // MODIFIES: this.
    // EFFECTS: creates left panel, add components, and add to top panel.
    private void createLeftPanel() {
        leftPanel = new JPanel();
        leftPanel.setForeground(Color.black);
        TitledBorder blackline = BorderFactory.createTitledBorder("Stock List");
        blackline.setTitleJustification(TitledBorder.CENTER);
        leftPanel.setBorder(blackline);

        // Buttons
        addStock = new JButton("Add Stock");
        addStock.addActionListener(e -> addStockDialog());
        removeStock = new JButton("Remove Stock");
        removeStock.addActionListener(e -> removeStock());

        // Add Buttons
        leftPanel.add(addStock);
        leftPanel.add(removeStock);

        // Stocks List
        displayStocksList(leftPanel);
        topPanel.add(leftPanel);
    }

    // MODIFIES: this.
    // EFFECTS: creates a selectable list where list of stocks in the portfolio
    //          are displayed.
    private void displayStocksList(JPanel panel) {
        stockModel = new DefaultListModel<>();
        stocksList = new JList(stockModel);
        stocksList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        stocksList.addListSelectionListener(e -> stockSelectionChangeAction(e));
        stocksList.setFixedCellWidth(300);
        stocksList.setFixedCellHeight(15);

        // Set custom cell renderer to display CustomObject instances
        stocksList.setCellRenderer(new StockObjectCellRenderer());
        stocksList.addListSelectionListener(e -> stockSelectionChangeAction(e));

        stockScrollList = new JScrollPane(stocksList);
        stockScrollList.setPreferredSize(new Dimension(300, 350));
        stockScrollList.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panel.add(stockScrollList);
    }

    // MODIFIES: this.
    // EFFECTS: creates right panel and add to top panel. Add components to the right panel.
    private void createRightPane() {
        rightPanel = new JPanel();
        rightPanel.setForeground(Color.black);
        TitledBorder blackline = BorderFactory.createTitledBorder("Stock Information");
        blackline.setTitleJustification(TitledBorder.CENTER);
        rightPanel.setBorder(blackline);

        // Buttons
        editButton = new JButton("Edit Stock");
        editButton.addActionListener(e -> setupStockEditPanel());
        calculateDividendButton = new JButton("Calculate Dividend");
        calculateDividendButton.addActionListener(e -> calculateDividend());

        infoLabel = new JLabel();
        infoLabel.setPreferredSize(new Dimension(300, 350));
        infoLabel.setVerticalAlignment(SwingConstants.TOP);

        rightPanel.add(editButton);
        rightPanel.add(calculateDividendButton);
        rightPanel.add(infoLabel);

        topPanel.add(rightPanel);
    }

    // MODIFIES: this.
    // EFFECTS: set up edit panel in the right panel.
    private void setupStockEditPanel() {
        Stock editedStock = getSelectedStock();
        dialog = new JDialog(this, "Edit Stock: " + editedStock.getTicker(), true);
        JPanel panel = new JPanel(new GridLayout(8, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        initializeEditTextFields(editedStock);
        initializeEditStockPanel(panel, editedStock);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        panel.add(cancelButton);

        dialog.add(panel);
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // MODIFIES: this.
    // EFFECTS: initialize text fields for edit panel.
    private void initializeEditTextFields(Stock stock) {
        tickerSymbol = new JTextField(10);
        tickerSymbol.setText(stock.getTicker());
        companyName = new JTextField(10);
        companyName.setText(stock.getName());
        companyCapital = new JTextField(10);
        companyCapital.setText(String.valueOf(stock.getCapital()));
        totalShares = new JTextField(10);
        totalShares.setText(String.valueOf(stock.getTotalShares()));
        divPerShare = new JTextField(10);
        divPerShare.setText(String.valueOf(stock.getDividendPerShare()));
        ownedShares = new JTextField(10);
        ownedShares.setText(String.valueOf(stock.getOwnedShares()));
        marketPrice = new JTextField(10);
        marketPrice.setText(String.valueOf(stock.getPrice()));
    }

    // MODIFIES: this.
    // EFFECTS: Adds labels and button to edit panel.
    private void initializeEditStockPanel(JPanel panel, Stock editedStock) {
        panel.add(new JLabel("Ticker Symbol:"));
        panel.add(tickerSymbol);
        panel.add(new JLabel("Company Name:"));
        panel.add(companyName);
        panel.add(new JLabel("Company Capital $ (Double):"));
        panel.add(companyCapital);
        panel.add(new JLabel("Total Shares (Integer):"));
        panel.add(totalShares);
        panel.add(new JLabel("Dividend Per Share $ (Double):"));
        panel.add(divPerShare);
        panel.add(new JLabel("Owned Shares (Integer):"));
        panel.add(ownedShares);
        panel.add(new JLabel("Market Price $ (Double):"));
        panel.add(marketPrice);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            editStock(editedStock);
            dialog.dispose(); // Close the dialog
        });

        panel.add(submitButton);
    }

    // MODIFIES: this, Stock.
    // EFFECTS: edit selected stock. If specific integer/double has no numbers, catch NumberFormatException.
    private void editStock(Stock stock) {
        try {
            double companyCapitalDouble = Double.parseDouble(companyCapital.getText());
            int totalSharesInteger = Integer.parseInt(totalShares.getText());
            double divPerShareDouble = Double.parseDouble(divPerShare.getText());
            int ownedSharesDouble = Integer.parseInt(ownedShares.getText());
            double marketPriceDouble = Double.parseDouble(marketPrice.getText());

            portfolio.editStock(stock.getTicker(), tickerSymbol.getText(), companyName.getText(), companyCapitalDouble,
                    totalSharesInteger, divPerShareDouble, ownedSharesDouble, marketPriceDouble);
            JOptionPane.showMessageDialog(this, "Successfully Edited "
                    + tickerSymbol.getText() + " to portfolio!");
            refreshStocksList();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: Please input corresponding "
                    + "integer/double field(s) in the correct data type!");
            addStockDialog();
            return;
        }
    }

    // MODIFIES: this.
    // EFFECTS: display a splash screen for a set amount of time when called.
    private void splashScreen() {
        JWindow splashScreen = new JWindow();
        splashScreen.setSize(660, 297);
        splashScreen.setLocationRelativeTo(null);
        splashScreen.add(new JLabel(new ImageIcon("./data/images/splashscreen.jpg")));
        splashScreen.setVisible(true);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            splashScreen.setVisible(false);
        }
    }

    // MODIFIES: this.
    // EFFECTS: display the list of fair valued stocks in the portfolio popup when called.
    private void fairValuedStocksDialog() {
        dialog = new JDialog(this, "Fair Valued Stocks", true);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        String openingText = "<html>Here are the fair valued stocks from your portfolio and their PBVs:<br></html>";
        panel.add(new JLabel(openingText));

        List<Stock> stockList = portfolio.listFairValuedStocks();
        List<String> tickerList = portfolio.getSpecificTickerList(stockList);

        int counter = 0;
        for (String ticker : tickerList) {
            String enumerate = Integer.toString(counter + 1);
            String pbv = Double.toString(stockList.get(counter).getPriceToBookValue());

            JLabel label = new JLabel("<html>" + enumerate + ". " + ticker + " = " + pbv + "<br></html>");
            panel.add(label);
            counter++;
        }

        JButton proceedButton = new JButton("Proceed");
        proceedButton.addActionListener(e -> dialog.dispose());

        panel.add(proceedButton);

        dialog.add(panel);
        dialog.setSize(400, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // MODIFIES: this.
    // EFFECTS: display the list of overvalued stocks in the portfolio popup when called.
    private void overvaluedStocksDialog() {
        dialog = new JDialog(this, "Overvalued Stocks", true);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        String openingText = "<html>Here are the overvalued stocks from your portfolio and their PBVs:<br></html>";
        panel.add(new JLabel(openingText));

        List<Stock> stockList = portfolio.listOvervaluedStocks();
        List<String> tickerList = portfolio.getSpecificTickerList(stockList);

        int counter = 0;
        for (String ticker : tickerList) {
            String enumerate = Integer.toString(counter + 1);
            String pbv = Double.toString(stockList.get(counter).getPriceToBookValue());

            JLabel label = new JLabel("<html>" + enumerate + ". " + ticker + " = " + pbv + "<br></html>");
            panel.add(label);
            counter++;
        }

        JButton proceedButton = new JButton("Proceed");
        proceedButton.addActionListener(e -> dialog.dispose());

        panel.add(proceedButton);

        dialog.add(panel);
        dialog.setSize(400, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // MODIFIES: this.
    // EFFECTS: center the main window when app launched.
    private void centreOnScreen() {
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        setLocation((width - getWidth()) / 2, (height - getHeight()) / 2);
    }

    // EFFECTS: returns the index of the selected stock from the JList.
    private Stock getSelectedStock() {
        return stockModel.get(stocksList.getSelectedIndex());
    }

    // MODIFIES: this.
    // EFFECTS: refresh the stocks list in the Jlist (selectable stocks list).
    private void refreshStocksList() {
        stockModel.clear();

        for (Stock stock : portfolio.getAllStocksList()) {
            stockModel.addElement(stock);
        }

        preventEmptyStocksList();
        preventStocksListDeselection();
        setStockInfoRightPanel();
    }

    // MODIFIES: this.
    // EFFECTS: reflects the change in the right panel info when changing stock selection in the left panel.
    private void stockSelectionChangeAction(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }

        preventStocksListDeselection();
        if (stocksList.getSelectedIndex() != -1) {
            setStockInfoRightPanel();
        }
    }

    // MODIFIES: this.
    // EFFECTS: display the information of the selected stock in the left panel to the right panel.
    private void setStockInfoRightPanel() {
        Stock stock = getSelectedStock();

        String info = "<html><pre>Ticker Symbol: " + stock.getTicker()
                + "\n\nCompany Name: " + stock.getName()
                + "\n\nMarket Capital $: " + stock.getCapital()
                + "\n\nTotal Shares: " + stock.getTotalShares()
                + "\n\nDividend Per Share $: " + stock.getDividendPerShare()
                + "\n\nOwned Shares: " + stock.getOwnedShares()
                + "\n\nMarket Price $: " + stock.getPrice()
                + "\n\n\n" + "  Analysis: "
                + "\n\nBook Value $: " + stock.getBookValue()
                + "\n\nPrice to Book Value (PBV): " + stock.getPriceToBookValue()
                + "\n\nDividend Yield %: " + stock.getDividendYield();

        infoLabel.setText(info);
    }

    // MODIFIES: this.
    // EFFECTS: add an example stock object when user deleted all stocks in the portfolio.
    private void preventEmptyStocksList() {
        Stock stockExample;

        if (stockModel.isEmpty()) {
            stockExample = new Stock("AAPL", "Apple Inc.", 3000000, 100000, 0.5,
                    100, 45);
            stockModel.addElement(stockExample);
        }
    }

    // EFFECTS: prevent user from deselecting the stock in the left panel.
    private void preventStocksListDeselection() {
        if (stocksList.getSelectedIndex() == -1) {
            stocksList.setSelectedIndex(0);
        }
    }

    // MODIFIES: this.
    // EFFECTS: automatically load autosave.json file in the ./data/saves/ folder when launching the app.
    private void loadAutosave() {
        String path = "./data/saves/autosave.json";
        jsonReader = new JsonReader(path);

        try {
            portfolio = jsonReader.read();
            refreshStocksList();
        } catch (IOException | JSONException e) {
            int option = showConfirmDialog(null,
                    "Cannot open portfolio!\n" + e.getMessage()
                            + "\nPress \"OK\" if you would like to create a new portfolio.",
                    "Autosave Corrupted", OK_CANCEL_OPTION, ERROR_MESSAGE
            );
            if (option == CANCEL_OPTION) {
                System.exit(0);
            } else {
                portfolio = new Portfolio("User", 123456);
                refreshStocksList();
            }
        }

    }

    // EFFECTS: make the class file runnable.
    public static void main(String[] args) {
        new WiseStockAppGUI();
    }

    // MODIFIES: this, Portfolio.
    // EFFECTS: create a popup the enables user to add new stock to the portfolio.
    private void addStockDialog() {
        dialog = new JDialog(this, "Add Stock", true);
        JPanel panel = new JPanel(new GridLayout(8, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        initializeAddStock(panel);

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            addStockPortfolio();
            dialog.dispose(); // Close the dialog
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        panel.add(submitButton);
        panel.add(cancelButton);

        dialog.add(panel);
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // MODIFIES: this.
    // EFFECTS: initialize fields for addStockDialog() and add those to the dialog panel.
    private void initializeAddStock(JPanel panel) {
        tickerSymbol = new JTextField(10);
        companyName = new JTextField(10);
        companyCapital = new JTextField(10);
        totalShares = new JTextField(10);
        divPerShare = new JTextField(10);
        ownedShares = new JTextField(10);
        marketPrice = new JTextField(10);

        panel.add(new JLabel("Ticker Symbol:"));
        panel.add(tickerSymbol);
        panel.add(new JLabel("Company Name:"));
        panel.add(companyName);
        panel.add(new JLabel("Company Capital $ (Double):"));
        panel.add(companyCapital);
        panel.add(new JLabel("Total Shares (Integer):"));
        panel.add(totalShares);
        panel.add(new JLabel("Dividend Per Share $ (Double):"));
        panel.add(divPerShare);
        panel.add(new JLabel("Owned Shares (Integer):"));
        panel.add(ownedShares);
        panel.add(new JLabel("Market Price $ (Double):"));
        panel.add(marketPrice);
    }

    // MODIFIES: this, Portfolio.
    // EFFECTS: add the stock to the portfolio. If specific integer/double has no numbers, catch NumberFormatException.
    private void addStockPortfolio() {
        try {
            double companyCapitalDouble = Double.parseDouble(companyCapital.getText());
            int totalSharesInteger = Integer.parseInt(totalShares.getText());
            double divPerShareDouble = Double.parseDouble(divPerShare.getText());
            int ownedSharesDouble = Integer.parseInt(ownedShares.getText());
            double marketPriceDouble = Double.parseDouble(marketPrice.getText());

            portfolio.addTicker(tickerSymbol.getText(), companyName.getText(), companyCapitalDouble,
                    totalSharesInteger, divPerShareDouble, ownedSharesDouble, marketPriceDouble);
            JOptionPane.showMessageDialog(this, "Successfully added "
                    + tickerSymbol.getText() + " to portfolio!");
            refreshStocksList();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: Please input corresponding "
                    + "integer/double field(s) in the correct data type!");
            addStockDialog();
            return;
        }
    }

    // MODIFIES: this, Portfolio.
    // EFFECTS: creates a popup that enables user to remove a stock from the portfolio.
    private void removeStock() {
        int result = showConfirmDialog(
                null, "Are you sure you want to remove selected stock?",
                "Confirm Removal", YES_NO_OPTION
        );

        if (result == NO_OPTION) {
            return;
        }

        Stock deletedStock = getSelectedStock();
        portfolio.removeTicker(deletedStock.getTicker());

        int removedIndex = stocksList.getSelectedIndex();
        stockModel.removeElementAt(removedIndex);
        stocksList.setSelectedIndex(removedIndex - 1);

        preventEmptyStocksList();
        preventStocksListDeselection();
    }

    // EFFECTS: display the about dialog when called.
    private void aboutDialog() {
        dialog = new JDialog(this, "About", true);
        JPanel panel = new JPanel(new GridLayout(2, 1));
        JLabel label = new JLabel("WiseStock v1.0 by Raihan Arvi");
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JButton closeButton = new JButton("Close");
        closeButton.setSize(10, 20);
        closeButton.addActionListener(e -> dialog.dispose());

        panel.add(label);
        panel.add(closeButton);

        dialog.add(panel);
        dialog.setSize(250, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // EFFECTS: enables the user to save the portfolio and set name and id to a json file using file explorer.
    private void saveDialog() {
        dialog = new JDialog(this, "Save Portfolio", true);
        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JTextField portfolioName = new JTextField(20);
        JTextField portfolioId = new JTextField(20);

        panel.add(new JLabel("Portfolio Name:"));
        panel.add(portfolioName);
        panel.add(new JLabel("Enter ID (Integer):"));
        panel.add(portfolioId);

        JButton okButton = new JButton("Save");
        okButton.addActionListener(e -> {
            saveButtonHelper(portfolioName, portfolioId);
            dialog.dispose(); // Close the dialog
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        panel.add(okButton);
        panel.add(cancelButton);

        dialog.add(panel);
        dialog.setSize(300, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // MODIFIES: this.
    // EFFECTS: process the saving process when clicking the save button.
    private void saveButtonHelper(JTextField portfolioName, JTextField portfolioId) {
        String name = portfolioName.getText();
        String id = portfolioId.getText();
        int intValue = Integer.parseInt(id);
        saveFile(name, intValue);
    }

    // MODIFIES: this.
    // EFFECTS: save the file to a json file with corresponding name and id number.
    private void saveFile(String name, int idNumber) {
        portfolio.setNameUser(name);
        portfolio.setIdentificationNumber(idNumber);

        JFileChooser fileChooser = new JFileChooser("./data/saves/");

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.endsWith(".json")) {
                filePath += ".json";
            }

            jsonWriter = new JsonWriter(filePath);
            try {
                jsonWriter.open();
                jsonWriter.write(portfolio);
                jsonWriter.close();
            } catch (IOException e) {
                showMessageDialog(null, "Save failed!\n" + e.getMessage(),
                        "Save Error", ERROR_MESSAGE);
            }
        }
    }

    // MODIFIES: this, Portfolio.
    // EFFECTS: creates a dialog to load a portfolio file from a choosen json file.
    private void loadDialog() {
        JFileChooser fileChooser = new JFileChooser("./data/saves/");
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON File (*.json)", "json"));

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            int option = showConfirmDialog(null,
                    "Loading will overwrite any data currently stored in the app.\nContinue to load?",
                    "Confirm Load", YES_NO_OPTION, WARNING_MESSAGE
            );

            if (option == NO_OPTION) {
                return;
            }

            try {
                jsonReader = new JsonReader(fileChooser.getSelectedFile().getAbsolutePath());
                portfolio = jsonReader.read();
                refreshStocksList();
                showMessageDialog(null, "File loaded successfully!",
                        "File Loaded", INFORMATION_MESSAGE);
            } catch (IOException e) {
                showMessageDialog(
                        null, "This file cannot be loaded!\n" + e.getMessage(),
                        "Load Error", ERROR_MESSAGE
                );
            }
        }
    }

    // EFFECTS: display a popup to enable user to calculate for single stock dividend.
    private void calculateDividend() {
        String inputYears = JOptionPane.showInputDialog(this, "Enter how many years:");
        if (inputYears == null) {
            return;
        }

        int years;
        try {
            years = Integer.parseInt(inputYears);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: Please input years in number");
            calculateDividend();
            return;
        }

        String selectedTicker = getSelectedStock().getTicker();
        double dividend = portfolio.calculateSingleDividend(selectedTicker, years);
        JOptionPane.showMessageDialog(this, "Your dividend in " + years
                + " years $ " + dividend);
    }

    // EFFECTS: display a popup that enables the user to calculate dividends for all stocks.
    private void calculateAllDividends() {
        String inputYears = JOptionPane.showInputDialog(this, "Enter how many years:");
        if (inputYears == null) {
            return;
        }

        int years;
        try {
            years = Integer.parseInt(inputYears);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: Please input years in number");
            calculateAllDividends();
            return;
        }

        double dividend = portfolio.calculateAllDividends(years);
        JOptionPane.showMessageDialog(this, "Your dividends in " + years
                + " years $ " + dividend);
    }

    // MODIFIES: this.
    // EFFECTS: enables JList to display the company name of the stock object.
    static class StockObjectCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {

            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Stock) {
                Stock stock = (Stock) value;
                setText(stock.getTicker() + " : " + stock.getName()); // Display the company name of the Stock object.
            }
            return this;
        }
    }

    private WindowAdapter windowAdapter = new WindowAdapter() {
        // EFFECTS: asks the user to confirm to close the app and autosaves the app state when closing.
        @Override
        public void windowClosing(WindowEvent e) {
            int choice = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to close this window?",
                    "Confirm Close", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                autosaveClosing();
                System.out.println(portfolio.takeLog());
                dispose(); // Close the window
            } else {
                setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            }
        }

        // MODIFIES: this.
        // EFFECTS: autosaves the app state at closing.
        private void autosaveClosing() {
            jsonAutosave = new JsonWriter(AUTOSAVE_PATH);

            try {
                portfolio.setNameUser("User");
                portfolio.setIdentificationNumber(123456);
                jsonAutosave.open();
                jsonAutosave.write(portfolio);
                jsonAutosave.close();
            } catch (FileNotFoundException e) {
                showMessageDialog(
                        null, "Autosave Failed!\n" + e.getMessage(),
                        "Autosave Error", ERROR_MESSAGE
                );
            }
        }
    };
}