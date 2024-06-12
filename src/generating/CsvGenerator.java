/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generating;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 *
 * @author Jana
 */
public class CsvGenerator {

    public static void main(String[] args) {
        //generateFactTable();
        generateDimensionTable();
        // DELETE THE LAST NEW ROW!!!
    }

    public static void generateFactTable() {
        String fileName = "files/fact_table.csv";
        int totalRows = 10000;
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.append("\n");
            Random random = new Random();
            for (int i = 5; i <= totalRows; i++) {
                int dateKey = random.nextInt(500) + 1;
                int productKey = random.nextInt(1000) + 1;
                int storeKey = random.nextInt(200) + 1;
                int quantitySold = random.nextInt(20) + 1;
                double totalAmount = quantitySold * (random.nextDouble() * 50 + 10);

                writer.append(String.format("%d,%d,%d,%d,%d,%.2f\n", i, dateKey, productKey, storeKey, quantitySold, totalAmount));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateDimensionTable() {
        String fileName = "files/d3.csv";
        int totalRows = 500;
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.append("\n");
            Random random = new Random();
            for (int i = 3; i <= totalRows; i++) {
                //double price = random.nextDouble() * 50 + 1;

                writer.append(i + ",2002-11-14" + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
