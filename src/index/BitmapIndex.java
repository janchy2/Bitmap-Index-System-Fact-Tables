/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package index;

import java.util.BitSet;
import java.util.HashMap;
import table.TableOperations;

/**
 *
 * @author Jana
 */
public class BitmapIndex implements BitmapIndexOperations {

    private final HashMap<Integer, BitSet> mappings;
    private final TableOperations addTable;
    private final String columnName;

    public BitmapIndex(TableOperations table, String columnName, TableOperations addTable) {
        this.addTable = addTable;
        this.columnName = columnName;
        mappings = new HashMap();
        int col = table.getColForColName(columnName);
        for (int i = 0; i < table.getRows(); i++) {
            int field = (int) table.getValue(i, col);
            if (mappings.containsKey(field)) {
                mappings.get(field).set(i);
            } else {
                mappings.put(field, new BitSet(table.getRows()));
                mappings.get(field).set(i);
            }
        }
    }

    @Override
    public void display() {
        System.out.println("Index for column: " + columnName);
        for (int key : mappings.keySet()) {
            System.out.println(key + ": " + mappings.get(key));
        }
    }

    @Override
    public TableOperations getAddTable() {
        return addTable;
    }

    @Override
    public BitSet getBitSet(int value) {
        return mappings.get(value);
    }
}
