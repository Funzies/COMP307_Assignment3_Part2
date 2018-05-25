/**
 * Jack Huang 300343247
 * COMP307 Assignment 3
 * Building a Bayesian network for classification
 */
package code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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

        totalSpam = 1; //starts at one due to the dealing with zero occurrence method
        totalNonSpam = 1;
        buildTable(trainingData);
    }

    /**
     * Builds the counts array using the training data
     * @param data Training data
     */
    public void buildTable(File data) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(data));
            String line;
            while ((line = in.readLine()) != null) {
                String[] tokens = line.split("\\s++");

                //Getting rid of an empty element in the array
                List<Integer> values = new ArrayList<>();
                for (int i = 0; i <= tokens.length-1; i++) {
                    if (tokens[i].equals("1") || tokens[i].equals("0")){
                        values.add(Integer.parseInt(tokens[i]));
                    }
                }

                //incrementing cells in counts array based on data
                int _class = values.get(values.size()-1);
                for (int i = 0; i < values.size()-1; i++){ //skip class at end
                    int featureValue = values.get(i);
                    int featureRow = i*2 + featureValue; //even rows are when feature is false, odd rows are when true
                    counts[featureRow][_class]++;
                }

                if (_class == 0){ totalNonSpam++; }
                else { totalSpam++; }
            }
            System.out.print("Class = 0 \t\t\t\t\t Class = 1 \n");
            for (int i = 0; i < FEATURES; i++) {
                for (int j = 0; j < CLASSES; j++) {
                    String bool = "false";
                    if (i%2 == 1) { bool = "true"; }
                    System.out.print("Feature " + i/2 + " = " + bool + ": ");
                    System.out.print(counts[i][j] + "\t\t");
                }
                System.out.print("\n");
            }
            System.out.println("Total spam instances: "+totalSpam);
            System.out.println("Total Non-spam instances: "+totalNonSpam);

        } catch (IOException e) {
            System.out.println("File I/O error!");
        }
    }

    /**
     * Classifies the instances saved within the test data file.
     * Classification is either a 0 or 1 indicating non spam or spam respectively.
     */
    public void classify(){
        List<Integer> classifications = new ArrayList<>();

        try {
            BufferedReader in = new BufferedReader(new FileReader(testData));
            String line;
            while ((line = in.readLine()) != null) {
                String[] tokens = line.split("\\s++");

                //Getting rid of an empty element in the array
                List<Integer> values = new ArrayList<>();
                for (int i = 0; i <= tokens.length-1; i++) {
                    if (tokens[i].equals("1") || tokens[i].equals("0")){
                        values.add(Integer.parseInt(tokens[i]));
                    }
                }
                //these two variables are the numerators of the Bayes rule
                //they are initalised to P(nonspam) and P(spam) so all that is left is to times by P(featurei | (non)spam)
                float probSpam = (float) totalSpam/(totalSpam + totalNonSpam);
                float probNonSpam = (float) totalNonSpam/(totalSpam + totalNonSpam);

                for (int i = 0; i < values.size(); i++){
                    int featureValue = values.get(i);
                    float nonSpamDenominator = counts[i*2][0] + counts[i*2+1][0];
                    float spamDenominator = counts[i*2][1] + counts[i*2+1][1];

                    probNonSpam *= counts[i*2+featureValue][0]/nonSpamDenominator;
                    probSpam *= counts[i*2+featureValue][1]/spamDenominator;
                }
                if (probNonSpam >= probSpam){
                    classifications.add(0);
                }
                else { classifications.add(1); }

            }
            for (int i = 0; i < classifications.size(); i++) {
                System.out.println("Instance " + i + " was classified as: " + classifications.get(i));
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
                classifier.classify();
            }

            else { System.out.println("Invalid args"); }
    }
}
