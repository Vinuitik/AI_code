package kings;

import java.util.LinkedList;
import org.jfree.data.xy.XYSeries;

public class KMeans { // K-means Class
    public KMeans() {}

    public static LinkedList<XYSeries> perform(int k, XYSeries data) {
        LinkedList<XYSeries> clusters = new LinkedList<>();

        for (int i = 0; i < k; ++i) {
            clusters.add(new XYSeries("Cluster #" + (i + 1)));
        }
        XYSeries centroids = new XYSeries("Centroids");

        double minX = data.getMinX();
        double minY = data.getMinY();
        double maxX = data.getMaxX();
        double maxY = data.getMaxY();

        // Initialize centroids at random positions within the range
        for (int i = 0; i < k; ++i) {
            centroids.add(minX + Math.random() * (maxX - minX), minY + Math.random() * (maxY - minY));
        }

        boolean converged = false;
        final double THRESHOLD = 1e-3; // Small convergence threshold
        final int MAX_STEPS = 100;

        // Main loop for K-means iterations
        for (int step = 0; step < MAX_STEPS && !converged; ++step) {
            reassignClusters(data, clusters, centroids);

            XYSeries previousCentroids;
            try {
                previousCentroids = (XYSeries) centroids.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return null;
            }

            reassignCentroids(clusters, centroids);

            // Check convergence based on threshold
            converged = checkConvergence(previousCentroids, centroids, THRESHOLD);
        }
        return clusters;
    }

    private static boolean checkConvergence(XYSeries previousCentroids, XYSeries centroids, double threshold) {
        for (int i = 0; i < centroids.getItemCount(); i++) {
            double dist = calculateSquaredDistance(previousCentroids, i, centroids, i);
            if (dist > threshold * threshold) {
                return false;
            }
        }
        return true;
    }

    public static void reassignClusters(XYSeries data, LinkedList<XYSeries> clusters, XYSeries centroids) {
        for (XYSeries cluster : clusters) {
            cluster.clear();
        }

        // Assign each data point to the nearest centroid
        for (int i = 0; i < data.getItemCount(); ++i) {
            double minDist = calculateSquaredDistance(data, i, centroids, 0);
            int clusterIndex = 0;
            for (int j = 1; j < centroids.getItemCount(); ++j) {
                double dist = calculateSquaredDistance(data, i, centroids, j);
                if (dist < minDist) {
                    minDist = dist;
                    clusterIndex = j;
                }
            }
            clusters.get(clusterIndex).add(data.getDataItem(i));
        }
    }

    public static double calculateSquaredDistance(XYSeries data, int i, XYSeries centroids, int j) {
        double x1 = data.getX(i).doubleValue();
        double y1 = data.getY(i).doubleValue();
        double x2 = centroids.getX(j).doubleValue();
        double y2 = centroids.getY(j).doubleValue();

        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
    }

    public static void reassignCentroids(LinkedList<XYSeries> clusters, XYSeries centroids) {
        centroids.clear();

        for (XYSeries cluster : clusters) {
            double sumX = 0;
            double sumY = 0;
            int n = cluster.getItemCount();

            if (n > 0) {
                for (int j = 0; j < n; ++j) {
                    sumX += cluster.getX(j).doubleValue();
                    sumY += cluster.getY(j).doubleValue();
                }
                centroids.add(sumX / n, sumY / n);
            }
        }
    }
}
