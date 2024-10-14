package kings;

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
                XYDataItem item = data.getDataItem(i);
                double x = item.getXValue();
                double y = item.getYValue();

                res[0] = res[0] + (Math.sqrt(i)/i) * ( (y - res[0]*x-res[1]) )*x;
                res[1] = res[1] + (Math.sqrt(i)/i) * (y-res[0]*x-res[1]);

            }
        }
        return res;
    }
}
