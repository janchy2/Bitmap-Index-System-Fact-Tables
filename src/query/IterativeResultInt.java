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
public class IterativeResultInt extends IterativeResult {

    private int current;
    private int count;
    private final String function;

    public IterativeResultInt(int current, String function) {
        this.current = current;
        this.count = 1;
        this.function = function;
    }

    @Override
    void calculateNextResult(Object value) {
        switch (function) {
            case "SUM":
                current += (int) value;
                break;
            case "COUNT":
                count++;
                break;
            case "MIN":
                if ((int) value < current) {
                    current = (int) value;
                }
                break;
            case "MAX":
                if ((int) value > current) {
                    current = (int) value;
                }
                break;
            case "AVG":
                current += (int) value;
                count++;
                break;
        }
    }

    @Override
    Object getFinalResult() {
        switch (function) {
            case "SUM":
                return current;
            case "COUNT":
                return count;
            case "MIN":
                return current;
            case "MAX":
                return current;
            case "AVG":
                return (double) current / count;
            default:
                return null;
        }
    }

}
