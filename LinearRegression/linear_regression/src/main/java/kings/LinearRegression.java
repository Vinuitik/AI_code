package kings;

import java.util.Hashtable;

import org.ejml.simple.SimpleMatrix;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.about.SystemProperties;

public class LinearRegression { 
    public LinearRegression() {}

    public static double[] linearReg(XYSeries data) {
        double[] res = new double[2]; // Array to hold intercept (b) and slope (m)
        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double sumX2 = 0;
        int n = data.getItemCount();

        // Compute the necessary sums
        for (int i = 0; i < n; ++i) {
            XYDataItem item = data.getDataItem(i);
            double x = item.getXValue();
            double y = item.getYValue();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        // Construct the matrix for coefficients (b and m)
        SimpleMatrix matrix = new SimpleMatrix(2, 2);
        matrix.set(0, 0, n);         // n
        matrix.set(0, 1, sumX);      // ∑x
        matrix.set(1, 0, sumX);      // ∑x
        matrix.set(1, 1, sumX2);     // ∑x²

        // Construct the vector for the results
        SimpleMatrix vector = new SimpleMatrix(2, 1);
        vector.set(0, 0, sumY);      // ∑y
        vector.set(1, 0, sumXY);     // ∑xy

        // Calculate the coefficients (b and m)
        if (matrix.determinant() != 0) {
            SimpleMatrix inverse = matrix.invert(); // Invert the coefficient matrix
            SimpleMatrix result = inverse.mult(vector); // Multiply by the results vector
            
            double denominator = n * sumX2 - sumX * sumX;
            double slope = (n * sumXY - sumX * sumY) / denominator; // Slope
            double intercept = (sumY * sumX2 - sumX * sumXY) / denominator; // Intercept

            res[0] = intercept; // b (intercept)
            res[1] = slope; // m (slope)
        } else {
            System.out.println("Matrix is singular and cannot be inverted.");
        }

        return res; // Return the calculated intercept and slope
    }
    public static double[] gradientDescent(XYSeries data) {
        double[] res = new double[2];
        res[0] = Math.random();
        res[1] = Math.random();

        int n = data.getItemCount();

        for(int i = 0;i<100;++i){
            for(int j = 0;j<n;++j){
                XYDataItem item = data.getDataItem(j);
                double x = item.getXValue();
                double y = item.getYValue();

                res[0] = res[0] + 0.01*(Math.sqrt(i+1)/(i+1)) * ( (y - res[0]*x - res[1]) )*x;
                res[1] = res[1] + 0.01*(Math.sqrt(i+1)/(i+1)) * (y - res[0]*x - res[1]);

            }
            System.out.println("Run completed"+i+" "+res[0]+" "+res[1]);
        }
        return res;
    }


    public static double[] geneticAlgo(XYSeries data) {
        double[] res = new double[2]; // Store the best result (w0, w1)
        res[0] = Math.random();
        res[1] = Math.random();

        final int GENERATIONS = 1000;
        final int POPULATION_SIZE = GENERATIONS;
        final int BREEDING_SIZE = 250;
        final double difference = 0.001;
        
        double[][] chromosome = new double[POPULATION_SIZE][2];
        Hashtable<Integer, Double> costs = new Hashtable<>();

        // Initialize chromosomes randomly
        for (int i = 0; i < POPULATION_SIZE; ++i) {
            chromosome[i][0] = Math.random();
            chromosome[i][1] = Math.random();
        }

        boolean converged = false;
        for (int i = 0; i < GENERATIONS && !converged; i+=2) {
            
            // Calculate costs for each chromosome
            calcCosts(data, chromosome, costs);

            // Sort chromosomes based on their costs
            sortChromosomes(chromosome, costs);

            if(Math.abs(SSE(data, res) - SSE(data,chromosome[0]))<difference){
                converged = true;
            }

            if(SSE(data, res)>SSE(data, chromosome[0])){
                res = chromosome[0];
            }

            

            for(int j = 0;j<BREEDING_SIZE;++j){
                //double[] bred1 ={ chromosome[j][0] + (-1 + 2 * Math.random()) * 0.01 ,chromosome[j+1][1] + 0.1*(-1 + 2 * Math.random())};
                //double[] bred2 ={ chromosome[j+1][0] + (-1 + 2 * Math.random()) * 0.01 ,chromosome[j][1] + 0.1*(-1 + 2 * Math.random())};

                double[] bred1 ={ chromosome[j][0] + (-1 + 2 * Math.random()) * 0.01 ,chromosome[j][1] + 0.1*(-1 + 2 * Math.random())};
                double[] bred2 ={ chromosome[j+1][0] + (-1 + 2 * Math.random()) * 0.01 ,chromosome[j+1][1] + 0.1*(-1 + 2 * Math.random())};


                chromosome[BREEDING_SIZE + j*2] = bred1;
                chromosome[BREEDING_SIZE +j*2 +1] = bred2;
            }
            for(int j = BREEDING_SIZE*3;j<POPULATION_SIZE;++j){
                chromosome[j][0] = Math.random();
                chromosome[j][1] = Math.random();
            }
            System.out.println(SSE(data, chromosome[0]));
            System.out.println(SSE(data, chromosome[1]));
            System.out.println(SSE(data, chromosome[2]));
            System.out.println(SSE(data, chromosome[3]));
            System.out.println();
        }

        return res;
    }

    // Helper function to calculate costs (negative SSE) for each chromosome
    private static void calcCosts(XYSeries data, double[][] chromosome, Hashtable<Integer, Double> costs) {
        for (int j = 0; j < chromosome.length; ++j) {
            costs.put(j, -1.0 * SSE(data, chromosome[j]));
        }
    }

    // Helper function to sort chromosomes based on their costs
    private static void sortChromosomes(double[][] chromosome, Hashtable<Integer, Double> costs) {
        for (int j = 1; j < chromosome.length; ++j) {
            boolean sorted = false;
            for (int k = j; k > 0 && !sorted; --k) {
                if (costs.get(k) <= costs.get(k - 1)) {
                    sorted = true;
                } else {
                    // Swap costs
                    double tempCost = costs.get(k);
                    costs.put(k, costs.get(k - 1));
                    costs.put(k - 1, tempCost);

                    // Swap chromosomes
                    double[] tempChromosome = chromosome[k];
                    chromosome[k] = chromosome[k - 1];
                    chromosome[k - 1] = tempChromosome;
                }
            }
        }
    }

    // Function to calculate Sum of Squared Errors (SSE) for a given chromosome
    public static double SSE(XYSeries data, double[] weight) {
        double res = 0;
        int n = data.getItemCount();
        for (int i = 0; i < n; ++i) {
            XYDataItem dataItem = data.getDataItem(i);
            double x = dataItem.getXValue();
            double y = dataItem.getYValue();
            res += Math.pow(y - (x * weight[1] + weight[0]), 2);
        }
        return res;
    }

}
