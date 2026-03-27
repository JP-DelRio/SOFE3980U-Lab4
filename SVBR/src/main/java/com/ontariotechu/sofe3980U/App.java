package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.*;


 //Evaluate Binary Classification Models using BCE, Confusion Matrix,
 //Accuracy, Precision, Recall, F1-score, and AUC-ROC
 
public class App {

    public static void main(String[] args) {
        String[] models = {"model_1.csv", "model_2.csv", "model_3.csv"};

        double bestBCE = Double.MAX_VALUE;
        double bestAcc = -1.0;
        double bestPrec = -1.0;
        double bestRec = -1.0;
        double bestF1 = -1.0;
        double bestAUC = -1.0;

        String bestBCEModel = "";
        String bestAccModel = "";
        String bestPrecModel = "";
        String bestRecModel = "";
        String bestF1Model = "";
        String bestAUCModel = "";

        for (String filePath : models) {
            System.out.println("for " + filePath);

            List<String[]> allData;
            try (FileReader filereader = new FileReader(filePath)) {
                CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
                allData = csvReader.readAll();
            } catch (Exception e) {
                System.out.println("Error reading the CSV file: " + filePath);
                continue;
            }

            int n = allData.size();
            int nPositive = 0;
            int nNegative = 0;

            int tp = 0, tn = 0, fp = 0, fn = 0;   // for threshold = 0.5
            double bceSum = 0.0;

            double[] yTrue = new double[n];
            double[] yPred = new double[n];

            int idx = 0;
            for (String[] row : allData) {
                int yt = Integer.parseInt(row[0]);
                double yp = Double.parseDouble(row[1]);

                yTrue[idx] = yt;
                yPred[idx] = yp;

                if (yt == 1) nPositive++;
                else nNegative++;

                // Binary Cross Entropy
                bceSum += -(yt * Math.log(yp) + (1 - yt) * Math.log(1 - yp));


                // Confusion matrix at threshold 0.5
                int yPredBin = (yp >= 0.5) ? 1 : 0;
                if (yt == 1 && yPredBin == 1) tp++;
                else if (yt == 1 && yPredBin == 0) fn++;
                else if (yt == 0 && yPredBin == 1) fp++;
                else if (yt == 0 && yPredBin == 0) tn++;

                idx++;
            }

            double bce = bceSum / n;

            // Metrics at threshold = 0.5
            double accuracy = (double) (tp + tn) / n;
            double precision = (tp + fp == 0) ? 0.0 : (double) tp / (tp + fp);
            double recall = (tp + fn == 0) ? 0.0 : (double) tp / (tp + fn);
            double f1 = (precision + recall == 0) ? 0.0 : 2 * precision * recall / (precision + recall);

            // AUC-ROC with 101 points (i=0 to 100)
            double auc = calculateAUCROC(yTrue, yPred, nPositive, nNegative);

            // Print output
            System.out.printf("        BCE =%.7f%n", bce);
            System.out.println("        Confusion matrix");
            System.out.println("                        y=1      y=0");
            System.out.printf("                y^=1    %d      %d%n", tp, fp);
            System.out.printf("                y^=0    %d      %d%n", fn, tn);
            System.out.printf("        Accuracy =%.4f%n", accuracy);
            System.out.printf("        Precision =%.8f%n", precision);
            System.out.printf("        Recall =%.8f%n", recall);
            System.out.printf("        f1 score =%.8f%n", f1);
            System.out.printf("        auc roc =%.8f%n%n", auc);

            //track best model
            if (bce < bestBCE) {
                bestBCE = bce;
                bestBCEModel = filePath;
            }
            if (accuracy > bestAcc) {
                bestAcc = accuracy;
                bestAccModel = filePath;
            }
            if (precision > bestPrec) {
                bestPrec = precision;
                bestPrecModel = filePath;
            }
            if (recall > bestRec) {
                bestRec = recall;
                bestRecModel = filePath;
            }
            if (f1 > bestF1) {
                bestF1 = f1;
                bestF1Model = filePath;
            }
            if (auc > bestAUC) {
                bestAUC = auc;
                bestAUCModel = filePath;
            }
        }

        // Final report
        System.out.println("According to BCE, The best model is " + bestBCEModel);
        System.out.println("According to Accuracy, The best model is " + bestAccModel);
        System.out.println("According to Precision, The best model is " + bestPrecModel);
        System.out.println("According to Recall, The best model is " + bestRecModel);
        System.out.println("According to F1 score, The best model is " + bestF1Model);
        System.out.println("According to AUC ROC, The best model is " + bestAUCModel);
    }

    
     //Calculates AUC-ROC using the procedure described:
     //101 thresholds (0.00 to 1.00), TPR and FPR at each threshold,
     //then trapezoidal rule.
     
    private static double calculateAUCROC(double[] yTrue, double[] yPred, int nPositive, int nNegative) {
        if (nPositive == 0 || nNegative == 0) return 0.5;

        double auc = 0.0;
        double prevTPR = 0.0;
        double prevFPR = 0.0;

        for (int i = 0; i <= 100; i++) {
            double th = i / 100.0;

            int tpCount = 0;
            int fpCount = 0;

            for (int j = 0; j < yTrue.length; j++) {
                if (yPred[j] >= th) {
                    if (yTrue[j] == 1) tpCount++;
                    else fpCount++;
                }
            }

            double tpr = (double) tpCount / nPositive;
            double fpr = (double) fpCount / nNegative;

            // Trapezoidal rule, add area between previous and current point
            if (i > 0) {
                auc += (prevTPR + tpr) * Math.abs(prevFPR - fpr) / 2.0;
            }

            prevTPR = tpr;
            prevFPR = fpr;
        }

        return auc;
    }
}
