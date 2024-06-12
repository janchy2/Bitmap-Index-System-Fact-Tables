/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package query;

import java.util.List;
import table.TableOperations;

/**
 *
 * @author Jana
 */
public class QueryResult {

    private final Object aggResult;
    private final List<TableOperations> resultTables;

    public QueryResult(Object aggResult, List<TableOperations> resultTables) {
        this.aggResult = aggResult;
        this.resultTables = resultTables;
    }

    public Object getAggResult() {
        return aggResult;
    }

    public List<TableOperations> getResultTables() {
        return resultTables;
    }

    public void display() {
        System.out.println("Result: ");
        System.out.println("Aggregation result: " + aggResult);
        for (TableOperations table : resultTables) {
            table.display();
        }
    }

}
