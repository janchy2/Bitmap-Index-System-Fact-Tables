/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package index;

import java.util.HashMap;
import java.util.List;
import table.TableOperations;

/**
 *
 * @author Jana
 */
public interface BitmapIndexMakerFromFile {

    HashMap<String, BitmapIndexOperations> makeIndices(String fileName, List<TableOperations> addTables);
}
