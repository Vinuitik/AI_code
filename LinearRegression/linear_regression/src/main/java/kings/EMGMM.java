package kings;

import java.util.LinkedList;
import org.jfree.data.xy.XYSeries;

public class EMGMM {
    public static class Gaussian {
        XYSeries mean;          // Mean of the Gaussian
        double[][] covariance;  // 2x2 Covariance matrix
        double weight;          // Mixing weight of the Gaussian
        public Gaussian(XYSeries mean, double[][] covariance, double weight) {
            this.mean = mean;
            this.covariance = covariance;
            this.weight = weight;
        }
    }

    public static LinkedList<Gaussian> perform(int k, XYSeries data) {
        LinkedList<Gaussian> gaussians = new LinkedList<>();

        // Step 1: Initialize Gaussian components with random means, identity covariances, and equal weights
        double minX = data.getMinX();
        double minY = data.getMinY();
        double maxX = data.getMaxX();
        double maxY = data.getMaxY();
        for (int i = 0; i < k; ++i) {
            XYSeries mean = new XYSeries("Mean #" + (i + 1));
            mean.add(minX + Math.random() * (maxX - minX), minY + Math.random() * (maxY - minY));
            double[][] covariance = { {1.0, 0.0}, {0.0, 1.0} }; // Start with identity matrix
            double weight = 1.0 / k;
            gaussians.add(new Gaussian(mean, covariance, weight));
        }

        boolean converged = false;
        final double THRESHOLD = 1e-3;
        final int MAX_STEPS = 100;

        // Main loop for EM iterations
        for (int step = 0; step < MAX_STEPS && !converged; ++step) {
            double[][] responsibilities = expectationStep(data, gaussians);
            LinkedList<Gaussian> previousGaussians = new LinkedList<>(gaussians);
            maximizationStep(data, gaussians, responsibilities);
            converged = checkConvergence(previousGaussians, gaussians, THRESHOLD);
        }
        return gaussians;
    }

    private static double[][] expectationStep(XYSeries data, LinkedList<Gaussian> gaussians) {
        double[][] responsibilities = new double[data.getItemCount()][gaussians.size()];

        for (int i = 0; i < data.getItemCount(); ++i) {
            double x = data.getX(i).doubleValue();
            double y = data.getY(i).doubleValue();
            double sum = 0.0;

            // Compute responsibilities
            for (int j = 0; j < gaussians.size(); ++j) {
                Gaussian g = gaussians.get(j);
                double prob = gaussianProbability(x, y, g);
                responsibilities[i][j] = g.weight * prob;
                sum += responsibilities[i][j];
            }

            // Normalize responsibilities
            for (int j = 0; j < gaussians.size(); ++j) {
                responsibilities[i][j] /= sum;
            }
        }
        return responsibilities;
    }

    private static void maximizationStep(XYSeries data, LinkedList<Gaussian> gaussians, double[][] responsibilities) {
        int n = data.getItemCount();
        for (int j = 0; j < gaussians.size(); ++j) {
            double weightSum = 0;
            double meanX = 0;
            double meanY = 0;

            // Calculate new means
            for (int i = 0; i < n; ++i) {
                weightSum += responsibilities[i][j];
                meanX += responsibilities[i][j] * data.getX(i).doubleValue();
                meanY += responsibilities[i][j] * data.getY(i).doubleValue();
            }
            meanX /= weightSum;
            meanY /= weightSum;

            // Update Gaussian mean
            Gaussian g = gaussians.get(j);
            g.mean.clear();
            g.mean.add(meanX, meanY);

            // Calculate new covariance
            double[][] covariance = new double[2][2];
            for (int i = 0; i < n; ++i) {
                double dx = data.getX(i).doubleValue() - meanX;
                double dy = data.getY(i).doubleValue() - meanY;
                covariance[0][0] += responsibilities[i][j] * dx * dx;
                covariance[0][1] += responsibilities[i][j] * dx * dy;
                covariance[1][0] += responsibilities[i][j] * dy * dx;
                covariance[1][1] += responsibilities[i][j] * dy * dy;
            }
            covariance[0][0] /= weightSum;
            covariance[0][1] /= weightSum;
            covariance[1][0] /= weightSum;
            covariance[1][1] /= weightSum;
            g.covariance = covariance;

            // Update mixing weight
            g.weight = weightSum / n;
        }
    }

    private static boolean checkConvergence(LinkedList<Gaussian> previousGaussians, LinkedList<Gaussian> gaussians, double threshold) {
        for (int i = 0; i < gaussians.size(); ++i) {
            Gaussian g1 = previousGaussians.get(i);
            Gaussian g2 = gaussians.get(i);
            double dist = Math.pow(g1.mean.getX(0).doubleValue() - g2.mean.getX(0).doubleValue(), 2)
                        + Math.pow(g1.mean.getY(0).doubleValue() - g2.mean.getY(0).doubleValue(), 2);
            if (dist > threshold * threshold) {
                return false;
            }
        }
        return true;
    }

    private static double gaussianProbability(double x, double y, Gaussian g) {
        double dx = x - g.mean.getX(0).doubleValue();
        double dy = y - g.mean.getY(0).doubleValue();
        double det = g.covariance[0][0] * g.covariance[1][1] - g.covariance[0][1] * g.covariance[1][0];
        double normFactor = 1.0 / (2 * Math.PI * Math.sqrt(det));
        double invCov00 = g.covariance[1][1] / det;
        double invCov01 = -g.covariance[0][1] / det;
        double invCov10 = -g.covariance[1][0] / det;
        double invCov11 = g.covariance[0][0] / det;
        double exponent = -0.5 * (dx * (dx * invCov00 + dy * invCov01) + dy * (dx * invCov10 + dy * invCov11));
        return normFactor * Math.exp(exponent);
    }
}
