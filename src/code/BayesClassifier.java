package code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BayesClassifier {
    private final static int FEATURES = 24;
    private final static int CLASSES = 2;

    private File testData;

    private int[][] counts; //counts is saved as [features][class]
    private int totalSpam;
    private int totalNonSpam;

    public BayesClassifier(File trainingData, File testData) {
        this.testData = testData;
        counts = new int[FEATURES][CLASSES];

        //Initializing the whole table to 1's
        for (int i = 0; i < FEATURES; i++) {
            for (int j = 0; j < CLASSES; j++) {
                counts[i][j] = 1;
            }
        }
        //Building the counts table from training data
        buildTable(trainingData);
    }

    public void buildTable(File data) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(data));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                String[] tokens = line.split("\\s++");

                //Getting rid of an empty element in the array
                List<Integer> values = new ArrayList<>();
                for (int i = 0; i < tokens.length - 1; i++) {
                    if (tokens[i].equals("1") || tokens[i].equals("0")){
                        values.add(Integer.parseInt(tokens[i]));
                    }
                }

                //incrementing cells in counts based on data
                int _class = values.get(values.size()-1);
                for (int i = 0; i < values.size(); i++){ //skip class at end
                    int featureValue = values.get(i);
                    int featureRow = i*2 + featureValue; //even rows are false classes, odd rows are true classes
                    counts[featureRow][_class]++;
                }
            }
            System.out.print("\tClass = 0 \t\t\t\t Class = 1 \n");
            for (int i = 0; i < FEATURES; i++) {
                for (int j = 0; j < CLASSES; j++) {
                    String bool = "false";
                    if (i%2 == 1) { bool = "true"; }
                    System.out.print("\tFeature " + i/2 + " = " + bool + ": ");
                    System.out.print(counts[i][j] + "");
                }
                System.out.print("\n");
            }

        } catch (IOException e) {
            System.out.println("File I/O error!");
        }
    }




    public static void main(String[] args){

            if (args.length == 2) {
                File trainingDataFile = new File(args[0]);
                File testDataFile = new File(args[1]);
                BayesClassifier classifier = new BayesClassifier(trainingDataFile, testDataFile);
            }

            else { System.out.println("Invalid args"); }
    }
}
