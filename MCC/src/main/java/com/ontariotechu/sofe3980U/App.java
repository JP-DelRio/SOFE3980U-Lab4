package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.*;

public class App {
    public static void main(String[] args) {
        String filePath = "model.csv";
        FileReader filereader;
        List<String[]> allData;

        try {
            filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();
            allData = csvReader.readAll();
        } catch (Exception e) {
            System.out.println("Error reading the CSV file");
            return;
        }

        int n = allData.size();

        //confusion matrix
        int[][] confusionMatrix = new int[5][5];

        double crossEntropy = 0.0;

        for (String[] row : allData) {
            int y_true = Integer.parseInt(row[0]); // actual class (1–5)

            float[] y_predicted = new float[5];

            //read probabilities
            for (int i = 0; i < 5; i++) {
                y_predicted[i] = Float.parseFloat(row[i + 1]);
            }

            //cross Entropy Calculation

            float prob = y_predicted[y_true - 1];

            //avoid log(0)
            if (prob > 0) {
                crossEntropy += Math.log(prob);
            }

            //Predicted class (argmax)
            int y_hat = 1;
            float maxProb = y_predicted[0];

            for (int i = 1; i < 5; i++) {
                if (y_predicted[i] > maxProb) {
                    maxProb = y_predicted[i];
                    y_hat = i + 1;
                }
            }

            //Update Confusion Matrix
            //Row = predicted, Column = actual
            confusionMatrix[y_hat - 1][y_true - 1]++;
        }

        //Final CE
        crossEntropy = -crossEntropy / n;

        //output
        System.out.println("CE =" + crossEntropy);

        System.out.println("Confusion matrix");
        System.out.println("                y=1      y=2     y=3     y=4     y=5");

        for (int i = 0; i < 5; i++) {
            System.out.print("        y^=" + (i + 1) + "    ");
            for (int j = 0; j < 5; j++) {
                System.out.print(confusionMatrix[i][j] + "     ");
            }
            System.out.println();
        }
    }
}
