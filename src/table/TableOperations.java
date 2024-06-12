/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package table;

import java.util.List;

/**
 *
 * @author Jana
 */
public interface TableOperations {

    public void addValue(int row, Object field);

    public Object getValue(int row, int col);

    public int getColForColName(String columnName);

    public int getRows();

    public int getCols();

    public void display();

    public List<String> getTypes();

    public List<String> getColumnNames();

    public void insertRow(List<Object> row);

    public List<Object> getRow(int i);
}
