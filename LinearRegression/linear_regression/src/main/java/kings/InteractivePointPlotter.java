package kings;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.Random;

public class InteractivePointPlotter extends JFrame {
    private XYSeries series;
    private XYSeries regressionLine;
    private JButton drawButton;
    private JButton gradientDescentButton;
    private JButton kMeansButton; // New button for KMeans Clustering

    public InteractivePointPlotter() {
        series = new XYSeries("Data Points");
        regressionLine = new XYSeries("Regression Line");

        setTitle("Interactive Point Plotter");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        dataset.addSeries(regressionLine);
        JFreeChart chart = ChartFactory.createScatterPlot(
                "Click to Add Points",
                "X-Axis",
                "Y-Axis",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);
        setChartRanges(chart);

        chartPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                XYPlot plot = (XYPlot) chart.getPlot();
                Point2D point = chartPanel.translateScreenToJava2D(e.getPoint());
                double x = plot.getDomainAxis().java2DToValue(point.getX(), chartPanel.getScreenDataArea(), plot.getDomainAxisEdge());
                double y = plot.getRangeAxis().java2DToValue(point.getY(), chartPanel.getScreenDataArea(), plot.getRangeAxisEdge());
                series.add(x, y);
            }
        });

        drawButton = new JButton("Draw Regression Line");
        drawButton.setBackground(Color.RED);
        drawButton.setForeground(Color.WHITE);
        drawButton.addActionListener(e -> updateRegressionLine());

        gradientDescentButton = new JButton("Gradient Descent Line");
        gradientDescentButton.setBackground(Color.BLUE);
        gradientDescentButton.setForeground(Color.WHITE);
        gradientDescentButton.addActionListener(e -> performGradientDescent());

        kMeansButton = new JButton("KMeans Clustering");
        kMeansButton.setBackground(Color.GREEN);
        kMeansButton.setForeground(Color.WHITE);
        kMeansButton.addActionListener(e -> performKMeansClustering());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(drawButton);
        buttonPanel.add(gradientDescentButton);
        buttonPanel.add(kMeansButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(chartPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        getContentPane().add(mainPanel);
    }

    private void setChartRanges(JFreeChart chart) {
        XYPlot plot = chart.getXYPlot();
        plot.getDomainAxis().setRange(-10, 10);
        plot.getRangeAxis().setRange(-10, 10);
    }

    private void updateRegressionLine() {
        regressionLine.clear();
        if (series.getItemCount() < 2) {
            System.out.println("Not enough points to perform regression.");
            return;
        }

        double[] results = LinearRegression.linearReg(series);
        double slope = results[1];
        double intercept = results[0];

        double minX = series.getMinX();
        double maxX = series.getMaxX();
        int numPoints = 100;

        for (int i = 0; i <= numPoints; i++) {
            double x = minX + (maxX - minX) * i / numPoints;
            double y = slope * x + intercept;
            regressionLine.add(x, y);
        }
        repaint();
    }

    private void performGradientDescent() {
        regressionLine.clear();
        if (series.getItemCount() < 2) {
            System.out.println("Not enough points to perform gradient descent.");
            return;
        }

        double[] results = LinearRegression.gradientDescent(series);
        double slope = results[0];
        double intercept = results[1];

        double minX = series.getMinX();
        double maxX = series.getMaxX();
        int numPoints = 100;

        for (int i = 0; i <= numPoints; i++) {
            double x = minX + (maxX - minX) * i / numPoints;
            double y = slope * x + intercept;
            regressionLine.add(x, y);
        }
        repaint();
    }

    private void performKMeansClustering() {
        // Prompt the user to enter the number of clusters
        String input = JOptionPane.showInputDialog(this, "Enter number of clusters:");
        int k;
        try {
            k = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number of clusters.");
            return;
        }
    
        // Ensure the number of clusters is positive
        if (k <= 0) {
            JOptionPane.showMessageDialog(this, "Number of clusters must be positive.");
            return;
        }
    
        // Perform K-means clustering
        LinkedList<XYSeries> clusters = KMeans.perform(k, series);
    
        // Create a new dataset to hold the clusters
        XYSeriesCollection dataset = new XYSeriesCollection();
        Random random = new Random();
    
        // Generate distinct colors for each cluster
        Color[] colors = new Color[k];
        for (int i = 0; i < k; i++) {
            colors[i] = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)); // Random color for each cluster
        }
    
        for (int i = 0; i < clusters.size(); i++) {
            XYSeries cluster = clusters.get(i);
            cluster.setKey("Cluster #" + (i + 1));
            dataset.addSeries(cluster);
        }
    
        // Safely update the dataset in the plot and apply colors
        Component comp = getContentPane().getComponent(0);
        if (comp instanceof JPanel) {
            JPanel mainPanel = (JPanel) comp;
            for (Component innerComp : mainPanel.getComponents()) {
                if (innerComp instanceof ChartPanel) {
                    ChartPanel chartPanel = (ChartPanel) innerComp;
                    XYPlot plot = (XYPlot) chartPanel.getChart().getPlot();
                    plot.setDataset(dataset);
    
                    // Apply color to each cluster series renderer
                    for (int i = 0; i < clusters.size(); i++) {
                        plot.getRenderer().setSeriesPaint(i, colors[i]);
                    }
                    
                    repaint();
                    return;
                }
            }
        }
    
        JOptionPane.showMessageDialog(this, "Error: Chart panel not found.");
    }
    
    
}
