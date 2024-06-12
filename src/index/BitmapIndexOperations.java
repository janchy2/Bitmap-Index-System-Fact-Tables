/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package index;

import java.util.BitSet;
import table.TableOperations;

/**
 *
 * @author Jana
 */
public interface BitmapIndexOperations {

    void display();

    TableOperations getAddTable();
    
    BitSet getBitSet(int value);

}
