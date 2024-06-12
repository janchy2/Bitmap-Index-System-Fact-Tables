/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package table;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jana
 */
public class Table implements TableOperations {
    
    private final List<List<Object>> table;
    private final List<String> columnNames;
    private final List<String> types;
    
    public Table(List<String> columnNames, List<String> types) {
        table = new ArrayList<List<Object>>();
        this.columnNames = columnNames;
        this.types = types;
    }
    
    @Override
    public void addValue(int row, Object field) {
        if (table.size() <= row) {
            table.add(new ArrayList<Object>());
        }
        table.get(row).add(field);
    }
    
    @Override
    public Object getValue(int row, int col) {
        if (row >= getRows() || col >= getCols()) {
            return null;
        }
        return table.get(row).get(col);
    }
    
    @Override
    public int getRows() {
        return table.size();
    }
    
    @Override
    public int getCols() {
        return table.get(0).size();
    }
    
    @Override
    public void display() {
        System.out.println(columnNames);
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getCols(); j++) {
                System.out.print(table.get(i).get(j) + " ");
            }
            System.out.println();
        }
    }
    
    @Override
    public int getColForColName(String columnName) {
        return columnNames.indexOf(columnName);
    }
    
    @Override
    public List<String> getTypes() {
        return types;
    }
    
    @Override
    public List<String> getColumnNames() {
        return columnNames;
    }
    
    @Override
    public void insertRow(List<Object> row) {
        // it is the same row as in the other table, but it is not a problem
        // since we do not alter objects in a table
        table.add(row);
    }
    
    @Override
    public List<Object> getRow(int i) {
        return table.get(i);
    }
    
}
