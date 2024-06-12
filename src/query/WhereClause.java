/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package query;

/**
 *
 * @author Jana
 */
public class WhereClause {

    private final String columnName;
    private final int value;

    public WhereClause(String columnName, int value) {
        this.columnName = columnName;
        this.value = value;
    }

    public String getColumnName() {
        return columnName;
    }

    public int getValue() {
        return value;
    }

}
