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
    private XYSeriesCollection dataset;
    private LinkedList<XYSeries> clusterSeriesList = new LinkedList<>();

    private JButton drawButton;
    private JButton gradientDescentButton;
    private JButton kMeansButton;
    private JButton toggleVisibilityButton; // New button for toggling dataset visibility
    private boolean isDatasetVisible = true;

    public InteractivePointPlotter() {
        series = new XYSeries("Data Points");
        regressionLine = new XYSeries("Regression Line");
        dataset = new XYSeriesCollection(series);
        dataset.addSeries(regressionLine);

        setTitle("Interactive Point Plotter");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

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

        setChartRanges(chart);
        ChartPanel chartPanel = createChartPanel(chart);

        drawButton = createButton("Draw Regression Line", Color.RED, e -> updateRegressionLine());
        gradientDescentButton = createButton("Gradient Descent Line", Color.BLUE, e -> performGeneticAlgorithm());
        kMeansButton = createButton("KMeans Clustering", Color.GREEN, e -> performKMeansClustering());
        toggleVisibilityButton = createButton("Toggle Dataset Visibility", Color.GRAY, e -> toggleDatasetVisibility());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(drawButton);
        buttonPanel.add(gradientDescentButton);
        buttonPanel.add(kMeansButton);
        buttonPanel.add(toggleVisibilityButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(chartPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        getContentPane().add(mainPanel);
    }

    private ChartPanel createChartPanel(JFreeChart chart) {
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);

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
        return chartPanel;
    }

    private JButton createButton(String text, Color backgroundColor, java.awt.event.ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.addActionListener(actionListener);
        return button;
    }

    private void setChartRanges(JFreeChart chart) {
        XYPlot plot = chart.getXYPlot();
        plot.getDomainAxis().setRange(-10, 10);
        plot.getRangeAxis().setRange(-10, 10);
    }

    private void updateRegressionLine() {
        regressionLine.clear();
        if (series.getItemCount() < 2) {
            JOptionPane.showMessageDialog(this, "Not enough points to perform regression.");
            return;
        }

        double[] results = LinearRegression.linearReg(series);
        double slope = results[1];
        double intercept = results[0];
        plotLine(regressionLine, slope, intercept);
    }

    private void performGradientDescent() {
        regressionLine.clear();
        if (series.getItemCount() < 2) {
            JOptionPane.showMessageDialog(this, "Not enough points to perform gradient descent.");
            return;
        }

        double[] results = LinearRegression.gradientDescent(series);
        double slope = results[0];
        double intercept = results[1];
        plotLine(regressionLine, slope, intercept);
    }

    private void performGeneticAlgorithm() {
        regressionLine.clear();
        if (series.getItemCount() < 2) {
            JOptionPane.showMessageDialog(this, "Not enough points to perform gradient descent.");
            return;
        }

        double[] results = LinearRegression.geneticAlgo(series);
        double slope = results[1];
        double intercept = results[0];
        plotLine(regressionLine, slope, intercept);
    }

    private void plotLine(XYSeries lineSeries, double slope, double intercept) {
        double minX = series.getMinX();
        double maxX = series.getMaxX();
        int numPoints = 100;

        for (int i = 0; i <= numPoints; i++) {
            double x = minX + (maxX - minX) * i / numPoints;
            double y = slope * x + intercept;
            lineSeries.add(x, y);
        }
        repaint();
    }

    private void performKMeansClustering() {

        regressionLine.clear();

        String input = JOptionPane.showInputDialog(this, "Enter number of clusters:");
        int k;
        try {
            k = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number of clusters.");
            return;
        }

        if (k <= 0) {
            JOptionPane.showMessageDialog(this, "Number of clusters must be positive.");
            return;
        }

        clearPreviousClusters();
        LinkedList<XYSeries> clusters = KMeans.perform(k, series);

        for (int i = 0; i < clusters.size(); i++) {
            XYSeries clusterSeries = clusters.get(i);
            clusterSeries.setKey("Cluster #" + (i + 1));
            clusterSeriesList.add(clusterSeries);
            dataset.addSeries(clusterSeries);
        }
        repaint();
    }

    private void clearPreviousClusters() {
        for (XYSeries cluster : clusterSeriesList) {
            dataset.removeSeries(cluster);
        }
        clusterSeriesList.clear();
    }

    private void toggleDatasetVisibility() {
        if (isDatasetVisible) {
            dataset.removeSeries(series);
        } else {
            dataset.addSeries(series);
        }
        isDatasetVisible = !isDatasetVisible;
        repaint();
    }
}
