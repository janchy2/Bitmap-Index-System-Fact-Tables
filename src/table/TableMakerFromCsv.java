/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package table;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Jana
 */
public class TableMakerFromCsv implements TableMakerFromFile {

    private BufferedReader br;
    private List<String> columnNames;
    private List<String> types;
    private final DateFormat df;

    public TableMakerFromCsv() {
        this.df = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    public TableOperations makeTable(String fileName) {
        try {
            br = new BufferedReader(new FileReader("files/" + fileName));
            readHeader(br);
            TableOperations table = new Table(columnNames, types);
            insertRows(table);
            return table;
        } catch (FileNotFoundException ex) {
            System.out.println("File not found!");
            return null;
        } catch (IOException ex) {
            System.out.println("Error while reading from file!");
            return null;
        }
    }

    private Object convert(String field, String type) {
        // acceptable types of columns
        try {
            switch (type) {
                case "Integer":
                    return Integer.parseInt(field);
                case "Double":
                    return Double.parseDouble(field);
                case "Date":
                    return df.parse(field);
                default:
                    return field;
            }
        } catch (Exception ex) {
            System.out.println("Values don't match the header!");
            return null;
        }
    }

    private void readHeader(BufferedReader br) throws IOException {
        String line;
        String regex = "(\\w+)\\((\\w+)\\)";
        types = new ArrayList<>();
        columnNames = new ArrayList<>();
        if ((line = br.readLine()) != null) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                columnNames.add(matcher.group(1));
                types.add(matcher.group(2));
            }
        } else {
            throw new IOException();
        }
    }

    private void insertRows(TableOperations table) throws IOException {
        String regex = "([^,]+)";
        String line;
        int row = 0, col = 0;
        while ((line = br.readLine()) != null) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                if (col >= columnNames.size()) {
                    // doesn't match the header, too many values
                    throw new IOException();
                }
                Object field = convert(matcher.group(), types.get(col));
                if (field == null) {
                    throw new IOException();
                }
                table.addValue(row, field);
                col++;
            }
            row++;
            col = 0;
        }
    }

}
