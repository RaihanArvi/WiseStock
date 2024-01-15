package persistence;

import model.Portfolio;
import org.json.JSONObject;

import java.io.*;

// Represents a writer that write Portfolio data representation into JSON file.
// Code influenced by the JsonSerializationDemo
// https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
public class JsonWriter {
    private static final int TAB = 4;
    private PrintWriter writer;
    private String path;

    // EFFECTS: construct a writer that write to destination file.
    public JsonWriter(String path) {
        this.path = path;
    }

    // MODIFIES: this.
    // EFFECTS: opens writer; throws FileNotFoundException if destination file cannot
    // be opened for writing.
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(new File(path));
    }

    // MODIFIES: this.
    // EFFECTS: writes JSON representation of portfolio to file.
    public void write(Portfolio portfolio) {
        JSONObject json = portfolio.toJson();
        saveToFile(json.toString(TAB));
    }

    // MODIFIES: this.
    // EFFECTS: closes the writer.
    public void close() {
        writer.close();
    }

    // MODIFIES: this.
    // EFFECTS: writes string to file.
    private void saveToFile(String json) {
        writer.print(json);
    }
}
