/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import query.QueryExecutorOperations;
import query.QueryResult;
import query.WhereClause;
import table.TableOperations;

/**
 *
 * @author Jana
 */
public class AggregateFunctionGUI extends JFrame {

    private JComboBox<String> aggregateFunctionComboBox;
    private JComboBox<String> nonIndexedColumnComboBox;
    private JComboBox<String> whereComboBox;
    private JSpinner numberSpinner;
    private JTextArea conditionsTextArea;
    private final ArrayList<String> nonIndexedColumns;
    private final ArrayList<String> indexedColumns;
    private JRadioButton andRadioButton;
    private final JRadioButton orRadioButton;
    private final ButtonGroup conditionGroup;
    private ArrayList<WhereClause> whereClauses = new ArrayList<>();
    private ArrayList<String> operators = new ArrayList<>();
    private final QueryExecutorOperations qe;
    private int numConditions;
    private String aggFunction;
    private JRadioButton indexedRadioButton;
    private final JRadioButton nonIndexedRadioButton;
    private final ButtonGroup conditionGroup1;
    private QueryResult res;
    private long start;
    private double end;

    public AggregateFunctionGUI(ArrayList<String> nonIndexedColumns, ArrayList<String> indexedColumns, QueryExecutorOperations qe) {
        setTitle("Aggregate function calculator");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        this.nonIndexedColumns = nonIndexedColumns;
        this.indexedColumns = indexedColumns;
        this.qe = qe;

        aggregateFunctionComboBox = new JComboBox<>(new String[]{"SUM", "AVG", "MIN", "MAX", "COUNT"});
        nonIndexedColumnComboBox = new JComboBox<>(nonIndexedColumns.toArray(new String[0]));
        whereComboBox = new JComboBox<>(indexedColumns.toArray(new String[0]));
        numberSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1)); // for foreign keys
        conditionsTextArea = new JTextArea(5, 20);
        conditionsTextArea.setEditable(false);

        andRadioButton = new JRadioButton("AND", true);
        orRadioButton = new JRadioButton("OR");
        conditionGroup = new ButtonGroup();
        conditionGroup.add(andRadioButton);
        conditionGroup.add(orRadioButton);

        indexedRadioButton = new JRadioButton("Indexed Search", true);
        nonIndexedRadioButton = new JRadioButton("Non-Indexed Seacrh");
        conditionGroup1 = new ButtonGroup();
        conditionGroup1.add(indexedRadioButton);
        conditionGroup1.add(nonIndexedRadioButton);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Aggregate Function
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Aggregate Function:"), gbc);

        gbc.gridx = 1;
        inputPanel.add(aggregateFunctionComboBox, gbc);

        // Column for Aggregation
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Column for Aggregation:"), gbc);

        gbc.gridx = 1;
        inputPanel.add(nonIndexedColumnComboBox, gbc);

        // Where
        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Where:"), gbc);

        gbc.gridx = 1;
        inputPanel.add(whereComboBox, gbc);

        // Equals
        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("Equals:"), gbc);

        gbc.gridx = 1;
        inputPanel.add(numberSpinner, gbc);

        // AND/OR Radio Buttons
        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(andRadioButton, gbc);

        gbc.gridx = 1;
        inputPanel.add(orRadioButton, gbc);

        // Indexed/Non-Indexed Radio Buttons
        gbc.gridx = 0;
        gbc.gridy = 5;
        inputPanel.add(indexedRadioButton, gbc);

        gbc.gridx = 1;
        inputPanel.add(nonIndexedRadioButton, gbc);

        JButton addConditionButton = new JButton("Add Where Clause");
        addConditionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // add to where clauses
                whereClauses.add(new WhereClause((String) whereComboBox.getSelectedItem(), (int) numberSpinner.getValue()));
                // add to text area
                String condition = whereComboBox.getSelectedItem() + " = " + numberSpinner.getValue();
                if (numConditions > 0) {
                    condition = (andRadioButton.isSelected() ? "AND" : "OR") + "\n" + condition;
                    // add to operators
                    operators.add(andRadioButton.isSelected() ? "AND" : "OR");
                }
                numConditions++;
                conditionsTextArea.append(condition + "\n");
            }
        });

        JButton executeButton = new JButton("Execute Query");
        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aggFunction = (String) aggregateFunctionComboBox.getSelectedItem() + "(" + (String) nonIndexedColumnComboBox.getSelectedItem() + ")";
                // execute with or without index in a new thread
                Thread queryThread;
                if (indexedRadioButton.isSelected()) {
                    queryThread = new Thread() {
                        @Override
                        public void run() {
                            start = System.nanoTime();
                            res = qe.executeWithIndex((String) aggregateFunctionComboBox.getSelectedItem(), (String) nonIndexedColumnComboBox.getSelectedItem(), whereClauses, operators);
                            end = (double) (System.nanoTime() - start) / 1000000;
                        }
                    };
                } else {
                    queryThread = new Thread() {
                        @Override
                        public void run() {
                            start = System.nanoTime();
                            res = qe.executeWithoutIndex((String) aggregateFunctionComboBox.getSelectedItem(), (String) nonIndexedColumnComboBox.getSelectedItem(), whereClauses, operators);
                            end = (double) (System.nanoTime() - start) / 1000000;
                        }
                    };
                }
                queryThread.start();
                // wait for query thread
                try {
                    queryThread.join();
                } catch (InterruptedException ex) {
                }
                displayResults(res, end);
                // prepare for next query
                conditionsTextArea.setText("");
                numConditions = 0;
                whereClauses.clear();
                operators.clear();
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(addConditionButton);
        bottomPanel.add(executeButton);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(conditionsTextArea), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void displayResults(QueryResult res, double executionTime) {
        // display query results in new window
        JFrame resultsFrame = new JFrame("Query Results");
        resultsFrame.setSize(600, 400);
        resultsFrame.setLayout(new BorderLayout());

        JTextArea resultsTextArea = new JTextArea();
        resultsTextArea.setEditable(false);
        resultsTextArea.append("Aggregate Function Result: " + aggFunction + " = " + res.getAggResult() + "\n");
        resultsTextArea.append("Execution Time: " + executionTime + " ms\n\n");

        // Display the additional tables for used foreign keys in where clauses
        for (TableOperations table : res.getResultTables()) {
            resultsTextArea.append("\n\n");
            resultsTextArea.append("------------------------------------------------------------------------------------------\n");
            for (String columnName : table.getColumnNames()) {
                resultsTextArea.append(columnName + "\t");
            }
            resultsTextArea.append("\n\n");
            for (int i = 0; i < table.getRows(); i++) {
                for (int j = 0; j < table.getCols(); j++) {
                    resultsTextArea.append(table.getValue(i, j).toString() + "\t");
                }
                resultsTextArea.append("\n");
            }
        }

        resultsFrame.add(new JScrollPane(resultsTextArea), BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> resultsFrame.dispose());
        resultsFrame.add(closeButton, BorderLayout.SOUTH);

        resultsFrame.setVisible(true);
    }

}
