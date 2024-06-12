/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package index;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import table.TableOperations;

/**
 *
 * @author Jana
 */
public class BitmapIndexMakerFromCsv implements BitmapIndexMakerFromFile {

    private final List<String> columnNames = new ArrayList<>();
    private BufferedReader br;
    private final TableOperations table;

    public BitmapIndexMakerFromCsv(TableOperations table) {
        this.table = table;
    }

    @Override
    public HashMap<String, BitmapIndexOperations> makeIndices(String fileName, List<TableOperations> addTables) {
        try {
            br = new BufferedReader(new FileReader("files/" + fileName));
            // read which columns are indexed
            readColNames();
            HashMap<String, BitmapIndexOperations> res = new HashMap<>();
            for (int i = 0; i < columnNames.size(); i++) {
                res.put(columnNames.get(i), new BitmapIndex(table, columnNames.get(i), addTables.get(i)));
            }
            return res;
        } catch (FileNotFoundException ex) {
            System.out.println("File not found!");
            return null;
        } catch (IOException ex) {
            System.out.println("Error while reading from file!");
            return null;
        }
    }

    private void readColNames() throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            columnNames.add(line);
        }
    }

}
