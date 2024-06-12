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
abstract class IterativeResult {

    abstract void calculateNextResult(Object value);

    abstract Object getFinalResult();
}
