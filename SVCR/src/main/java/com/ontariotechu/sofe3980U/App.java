package com.ontariotechu.sofe3980U;

import java.io.FileReader; 
import java.util.List;
import com.opencsv.*;

/**
 * Evaluate Single Variable Continuous Regression
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        String[] modelFiles = {"model_1.csv", "model_2.csv", "model_3.csv"};
        double[][] metrics = new double[3][3]; // [model][0=MSE, 1=MAE, 2=MARE]

        for (int i = 0; i < 3; i++) {
            String filePath = modelFiles[i];
            FileReader filereader;
            List<String[]> allData;
            try {
                filereader = new FileReader(filePath); 
                CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build(); 
                allData = csvReader.readAll();
            }
            catch (Exception e) {
                System.out.println("Error reading the CSV file: " + filePath);
                return;
            }
            
            double sumSquaredError = 0.0;
            double sumAbsError = 0.0;
            double sumAbsRelError = 0.0;
            int n = 0;
            final double EPS = 1e-10; // very small positive number to avoid division by zero
            
            for (String[] row : allData) { 
                double y_true = Double.parseDouble(row[0]);
                double y_predicted = Double.parseDouble(row[1]);
                double diff = y_true - y_predicted;
                sumSquaredError += diff * diff;
                sumAbsError += Math.abs(diff);
                sumAbsRelError += Math.abs(diff) / (Math.abs(y_true) + EPS);
                n++;
            } 
            
            metrics[i][0] = sumSquaredError / n; // MSE
            metrics[i][1] = sumAbsError / n;     // MAE
            metrics[i][2] = sumAbsRelError / n;  // MARE (as fractional relative error)
        }
        
        // Print results for all models
        for (int i = 0; i < 3; i++) {
            System.out.println("for " + modelFiles[i]);
            System.out.println("        MSE =" + metrics[i][0]);
            System.out.println("        MAE =" + metrics[i][1]);
            System.out.println("        MARE =" + metrics[i][2]);
        }
        
        // Determine and print the best model according to each metric
        // MSE
        int bestMSEIdx = 0;
        double minMSE = metrics[0][0];
        for (int i = 1; i < 3; i++) {
            if (metrics[i][0] < minMSE) {
                minMSE = metrics[i][0];
                bestMSEIdx = i;
            }
        }
        System.out.println("According to MSE, The best model is " + modelFiles[bestMSEIdx]);
        
        // MAE
        int bestMAEIdx = 0;
        double minMAE = metrics[0][1];
        for (int i = 1; i < 3; i++) {
            if (metrics[i][1] < minMAE) {
                minMAE = metrics[i][1];
                bestMAEIdx = i;
            }
        }
        System.out.println("According to MAE, The best model is " + modelFiles[bestMAEIdx]);
        
        // MARE
        int bestMAREIdx = 0;
        double minMARE = metrics[0][2];
        for (int i = 1; i < 3; i++) {
            if (metrics[i][2] < minMARE) {
                minMARE = metrics[i][2];
                bestMAREIdx = i;
            }
        }
        System.out.println("According to MARE, The best model is " + modelFiles[bestMAREIdx]);
    }
}
