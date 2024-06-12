/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package query;

import index.BitmapIndexOperations;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import table.Table;
import table.TableOperations;

/**
 *
 * @author Jana
 */
public class QueryExecutor implements QueryExecutorOperations {

    private final HashMap<String, BitmapIndexOperations> indices;
    private final TableOperations mainTable;
    private HashMap<String, TableOperations> resultTables;

    public QueryExecutor(HashMap<String, BitmapIndexOperations> indices, TableOperations mainTable) {
        this.indices = indices;
        this.mainTable = mainTable;
    }

    @Override
    public QueryResult executeWithIndex(String aggFunc, String aggCol, List<WhereClause> whereClauses, List<String> operators) {
        resultTables = new HashMap<>();
        BitSet result = combineBitSets(whereClauses, operators);
        Object aggRes = calculateAgg(result, aggFunc, aggCol);
        List<TableOperations> tables = new ArrayList<>();
        // from hash map to list of result tables
        for (TableOperations value : resultTables.values()) {
            tables.add(value);
        }
        return new QueryResult(aggRes, tables);
    }

    @Override
    public QueryResult executeWithoutIndex(String aggFunc, String aggCol, List<WhereClause> whereClauses, List<String> operators) {
        IterativeResult it = null;
        for (int i = 0; i < mainTable.getRows(); i++) {
            List<Object> row = mainTable.getRow(i);
            // evaluated from left to right
            boolean satisfies = true;
            for (int j = 0; j < whereClauses.size(); j++) {
                int col = mainTable.getColForColName(whereClauses.get(j).getColumnName());
                // if satisfies condition
                if ((int) row.get(col) != whereClauses.get(j).getValue()) {
                    if (j == 0) {
                        satisfies = false;
                    } else {
                        if (operators.get(j - 1).equals("AND")) {
                            satisfies = false;
                        }
                    }
                } else {
                    if (j != 0 && operators.get(j - 1).equals("OR")) {
                        satisfies = true;
                    }
                }
            }
            if (satisfies) {
                // satisfies the condition, add it to aggregation
                int col = mainTable.getColForColName(aggCol);
                String type = mainTable.getTypes().get(col);
                Object res = row.get(col);
                if (it == null) {
                    if (type.equals("Integer")) {
                        it = new IterativeResultInt((int) res, aggFunc);
                    } else {
                        it = new IterativeResultDouble((double) res, aggFunc);
                    }
                } else {
                    it.calculateNextResult(res);
                }
            }
        }
        Object aggRes = null;
        if (it != null) {
            aggRes = it.getFinalResult();
        }
        return new QueryResult(aggRes, fillResultTablesForNonIndexed(whereClauses));
    }

    private BitSet combineBitSets(List<WhereClause> whereClauses, List<String> operators) {
        BitSet result = null;
        for (int i = 0; i < whereClauses.size(); i++) {
            // get the index for column and add a table if not exists to result tables
            BitmapIndexOperations index = indices.get(whereClauses.get(i).getColumnName());
            if (resultTables.containsKey(whereClauses.get(i).getColumnName())) {
                // the value of where clause - 1 is the number of row that should be added
                if (whereClauses.get(i).getValue() - 1 < index.getAddTable().getRows()) {
                    resultTables.get(whereClauses.get(i).getColumnName()).insertRow(index.getAddTable().getRow(whereClauses.get(i).getValue() - 1));
                }
            } else {
                resultTables.put(whereClauses.get(i).getColumnName(), new Table(index.getAddTable().getColumnNames(), index.getAddTable().getTypes()));
                if (whereClauses.get(i).getValue() - 1 < index.getAddTable().getRows()) {
                    resultTables.get(whereClauses.get(i).getColumnName()).insertRow(index.getAddTable().getRow(whereClauses.get(i).getValue() - 1));
                }
            }
            if (result == null) {
                result = index.getBitSet(whereClauses.get(i).getValue());
                if (result == null) {
                    // value doesn't exist
                    result = new BitSet();
                }
            } else {
                BitSet newResult = index.getBitSet(whereClauses.get(i).getValue());
                if (newResult == null) {
                    // value doesn't exist
                    newResult = new BitSet();
                }
                combine(result, newResult, operators.get(i - 1));
            }
        }
        return result;
    }

    private void combine(BitSet bs1, BitSet bs2, String operator) {
        switch (operator) {
            case "AND":
                bs1.and(bs2);
                break;
            case "OR":
                bs1.or(bs2);
        }
    }

    private Object calculateAgg(BitSet result, String aggFunc, String aggCol) {
        if (result == null) {
            return null;
        }
        int j = mainTable.getColForColName(aggCol);
        String type = mainTable.getTypes().get(j);
        int i = result.nextSetBit(0);
        if (i == -1) {
            // no rows
            return null;
        }
        Object res = mainTable.getValue(i, j);
        IterativeResult it = null;
        if (type.equals("Integer")) {
            it = new IterativeResultInt((int) res, aggFunc);
        } else {
            it = new IterativeResultDouble((double) res, aggFunc);
        }
        i = result.nextSetBit(i + 1);
        while (i != -1) {
            it.calculateNextResult(mainTable.getValue(i, j));
            if (i == result.size() - 1) {
                break;
            }
            i = result.nextSetBit(i + 1);
        }
        return it.getFinalResult();
    }

    private List<TableOperations> fillResultTablesForNonIndexed(List<WhereClause> whereClauses) {
        // add tables used in where clauses for non-indexed search
        resultTables = new HashMap<>();
        for (int i = 0; i < whereClauses.size(); i++) {
            // get the index for column and add a table if not exists to result tables
            // index is used just to get to the table
            BitmapIndexOperations index = indices.get(whereClauses.get(i).getColumnName());
            if (resultTables.containsKey(whereClauses.get(i).getColumnName())) {
                // the value of where clause - 1 is the number of row that should be added
                resultTables.get(whereClauses.get(i).getColumnName()).insertRow(index.getAddTable().getRow(whereClauses.get(i).getValue() - 1));
            } else {
                resultTables.put(whereClauses.get(i).getColumnName(), new Table(index.getAddTable().getColumnNames(), index.getAddTable().getTypes()));
                resultTables.get(whereClauses.get(i).getColumnName()).insertRow(index.getAddTable().getRow(whereClauses.get(i).getValue() - 1));
            }
        }
        List<TableOperations> tables = new ArrayList<>();
        // from hash map to list of result tables
        for (TableOperations value : resultTables.values()) {
            tables.add(value);
        }
        return tables;
    }
}
