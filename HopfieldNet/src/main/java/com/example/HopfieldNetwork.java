package com.example;

public class HopfieldNetwork {
    private double[][] weights;
    private mongoDb db;
    int numberUnits;
    HopfieldNetwork(int rows, int cols){
        numberUnits = rows*cols;
        //weights = new double[numberUnits][numberUnits];
        db = new mongoDb();
    }
    public void train(Matrix matrix) {
        weights = null;
        double[][] currentWeights = db.getWeights(); // Get stored weights
        int cols = matrix.getColumns();
    
        if (currentWeights == null) {
            // Initialize weights if no stored weights are found
            currentWeights = new double[numberUnits][numberUnits];
        }
    
        // Update the weight matrix
        for (int i = 0; i < numberUnits; ++i) {
            for (int j = 0; j < numberUnits; ++j) {
                if (i == j) {
                    continue; // Skip diagonal weights
                }
                
                currentWeights[i][j] += matrix.get(i)* matrix.get(j);
            }
        }
    
        // Update or save the weights in the database
        db.saveOrUpdateMatrixInDB(currentWeights);
    }

    public double vote(int i, Matrix matrix) throws NoWeightsException{
        double res = 0;
        if(weights==null){
            weights = db.getWeights();
        }
        if(weights==null){
            throw new NoWeightsException("YOU HAVE NOT TRAINED ANY WEIGHTS"); 
        }
        //System.out.println("Called11");
        for(int j = 0;j<numberUnits;++j){
            res+=weights[i][j]*matrix.get(j);
        }
        return res;
    }

    
}
