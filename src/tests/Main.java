/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import gui.AggregateFunctionGUI;
import index.BitmapIndexMakerFromFile;
import index.BitmapIndexMakerFromCsv;
import index.BitmapIndexOperations;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.SwingUtilities;
import query.QueryExecutor;
import query.QueryExecutorOperations;
import table.TableMakerFromFile;
import table.TableMakerFromCsv;
import table.TableOperations;

/**
 *
 * @author Jana
 */
public class Main {

    public static void main(String[] args) {
        try {
            // input files added as args
            TableMakerFromFile tm = new TableMakerFromCsv();
            TableOperations table = tm.makeTable(args[0]);
            List<TableOperations> addTables = new ArrayList<>();
            for (int i = 1; i < args.length - 1; i++) {
                TableOperations d = tm.makeTable(args[i]);
                addTables.add(d);
            }
            BitmapIndexMakerFromFile bim = new BitmapIndexMakerFromCsv(table);
            HashMap<String, BitmapIndexOperations> indices = bim.makeIndices(args[args.length - 1], addTables);
            QueryExecutorOperations qe = new QueryExecutor(indices, table);
            ArrayList<String> nonIndexedColumns = new ArrayList<>();
            ArrayList<String> indexedColumns = new ArrayList<>();
            for (String col : indices.keySet()) {
                indexedColumns.add(col);
            }
            if (indexedColumns.isEmpty()) {
                throw new Exception();
            }
            for (String col : table.getColumnNames()) {
                if (table.getColumnNames().indexOf(col) != 0 && !indexedColumns.contains(col)) {
                    nonIndexedColumns.add(col);
                }
            }
            if (nonIndexedColumns.isEmpty()) {
                throw new Exception();
            }
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new AggregateFunctionGUI(nonIndexedColumns, indexedColumns, qe).setVisible(true);
                }
            });
        } catch (Exception e) {
            System.out.println("No indices or no non-indexed columns!");
        }
    }
}
