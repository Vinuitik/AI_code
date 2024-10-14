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

public class InteractivePointPlotter extends JFrame {
    private XYSeries series;
    private XYSeries regressionLine; // Series to store regression line points
    private JButton drawButton; // Button to draw regression line

    public InteractivePointPlotter() {
        series = new XYSeries("Data Points");
        regressionLine = new XYSeries("Regression Line");

        // Set up the JFrame
        setTitle("Interactive Point Plotter");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the chart with an empty dataset
        XYSeriesCollection dataset = new XYSeriesCollection(series);
        dataset.addSeries(regressionLine); // Add regression line series
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
        
        // Set fixed ranges for both axes
        setChartRanges(chart);

        // Mouse listener to add points
        chartPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Convert the click location to chart coordinates
                XYPlot plot = (XYPlot) chart.getPlot();
                Point2D point = chartPanel.translateScreenToJava2D(e.getPoint());
                double x = plot.getDomainAxis().java2DToValue(point.getX(), chartPanel.getScreenDataArea(), plot.getDomainAxisEdge());
                double y = plot.getRangeAxis().java2DToValue(point.getY(), chartPanel.getScreenDataArea(), plot.getRangeAxisEdge());

                // Add the point to the series
                series.add(x, y);
            }
        });

        // Create a button to draw the regression line
        drawButton = new JButton("Draw Regression Line");
        drawButton.setBackground(Color.RED);
        drawButton.setForeground(Color.WHITE);
        drawButton.addActionListener(e -> updateRegressionLine());

        // Add the button and chart panel to the frame
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        panel.add(drawButton, BorderLayout.SOUTH);
        getContentPane().add(panel);
    }

    // Method to set fixed ranges for both axes
    private void setChartRanges(JFreeChart chart) {
        XYPlot plot = chart.getXYPlot();
        
        // Set x-axis range from -10 to 10
        plot.getDomainAxis().setRange(-10, 10);
        
        // Set y-axis range from -10 to 10
        plot.getRangeAxis().setRange(-10, 10);
    }

    // Method to update and draw the regression line
    // Method to update and draw the regression line
private void updateRegressionLine() {
    // Clear previous regression line data
    regressionLine.clear();

    // Check if there are enough points to perform regression
    if (series.getItemCount() < 2) {
        System.out.println("Not enough points to perform regression.");
        return; // Skip the regression line update
    }

    // Calculate regression coefficients
    double[] results = LinearRegression.linearReg(series);
    double slope = results[1];    // Gradient
    double intercept = results[0]; // Intercept


    // Determine the range of the x-axis
    double minX = series.getMinX();
    double maxX = series.getMaxX();

    // Number of points for the regression line
    int numPoints = 100; // Increase or decrease this for smoother/less smooth line

    // Calculate and add points for the regression line
    for (int i = 0; i <= numPoints; i++) {
        double x = minX + (maxX - minX) * i / numPoints; // Evenly spaced x values
        double y = slope * x + intercept; // Calculate corresponding y value
        regressionLine.add(x, y); // Add point to regression line series
    }

    // Repaint the chart to show the updated regression line
    repaint();
}

}
