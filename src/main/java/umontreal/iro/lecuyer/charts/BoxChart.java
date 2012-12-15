/*
 * Class:        BoxChart
 * Description:  
 * Environment:  Java
 * Software:     SSJ 
 * Copyright (C) 2001  Pierre L'Ecuyer and Université de Montréal
 * Organization: DIRO, Université de Montréal
 * @author       
 * @since

 * SSJ is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License (GPL) as published by the
 * Free Software Foundation, either version 3 of the License, or
 * any later version.

 * SSJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * A copy of the GNU General Public License is available at
 <a href="http://www.gnu.org/licenses">GPL licence site</a>.
 */
package umontreal.iro.lecuyer.charts;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;

import java.util.Locale;
import java.util.Formatter;
import javax.swing.JFrame;

/**
 * This class provides tools to create and manage box-and-whisker plots. Each {@link BoxChart} object is linked with a
 * {@link umontreal.iro.lecuyer.charts.BoxSeriesCollection} data set.
 *
 * <P>
 * A boxplot is a convenient way of viewing sets of numerical data through their summaries: the smallest observation,
 * first quartile ($Q_1 = x_{.25}$), median ($Q_2 = x_{.5}$), third quartile ($Q_3 = x_{.75}$), and largest observation.
 * Sometimes, the mean and the outliers are also plotted.
 *
 * <P>
 * In the charts created by this class, the box has its lower limit at $Q_1$ and its upper limit at $Q_3$. The median is
 * indicated by the line inside the box, while the mean is at the center of the filled circle inside the box. Define the
 * interquartile range as $Q_3 - Q_1$. Any data observation which is more than $1.5(Q_3 - Q_1)$ lower than the first
 * quartile or $1.5(Q_3 - Q_1)$ higher than the third quartile is considered an outlier. The smallest and the largest
 * values that are not outliers are connected to the box with a vertical line or "whisker" which is ended by a
 * horizontal line. Outliers are indicated by hollow circles outside the whiskers. Triangles indicate the existence of
 * very far outliers.
 */
public class BoxChart extends CategoryChart {

    protected void init(String title, String XLabel, String YLabel) {
        // create the chart...
        chart = ChartFactory.createBoxAndWhiskerChart(
                title, // chart title
                XLabel, // x axis label
                YLabel, // y axis label
                (DefaultBoxAndWhiskerCategoryDataset) dataset.getSeriesCollection(), // data
                true // include legend
                );

        ((CategoryPlot) chart.getPlot()).setRenderer(dataset.getRenderer());
        // Initialize axis variables
        initAxis();
    }

    protected void initAxis() {
        YAxis = new Axis((NumberAxis) ((CategoryPlot) chart.getPlot()).getRangeAxis(),
                Axis.ORIENTATION_VERTICAL);
        setAutoRange();
    }

    /**
     * Initializes a new {@link BoxChart} instance with an empty data set.
     */
    public BoxChart() {
        super();
        dataset = new BoxSeriesCollection();
        init(null, null, null);
    }

    /**
     * Initializes a new {@link BoxChart} instance with data {@code data}. Only <SPAN CLASS="textit">the first</SPAN>
     * {@code numPoints} of {@code data} will be considered for the plot.
     *
     * @param title chart title.
     * @param XLabel Label on $x$-axis.
     * @param YLabel Label on $y$-axis.
     * @param data A set of plotting data.
     * @param numPoints Number of points to plot
     */
    public BoxChart(String title, String XLabel, String YLabel,
            double[] data, int numPoints) {
        super();
        dataset = new BoxSeriesCollection(data, numPoints);
        init(title, XLabel, YLabel);
    }

    /**
     * Initializes a new {@link BoxChart} instance with data {@code data}.
     *
     * @param title chart title.
     * @param XLabel Label on $x$-axis.
     * @param YLabel Label on $y$-axis.
     * @param data series of point sets.
     */
    public BoxChart(String title, String XLabel, String YLabel,
            double[]... data) {
        super();
        dataset = new BoxSeriesCollection(data);
        init(title, XLabel, YLabel);
    }

    /**
     * Adds a data series into the series collection.
     *
     * @param data A set of plotting data.
     * @return Integer that represent the new point set's position in the JFreeChart <TT>BoxSeriesCollection</TT>
     * object.
     */
    public int add(double[] data) {
        return add(data, data.length);
    }

    /**
     * Adds a data series into the series collection. Only <SPAN CLASS="textit">the first</SPAN> {@code numPoints} of
     * {@code data} will be taken into account for the new series.
     *
     * @param data point set.
     * @param numPoints number of points to add.
     * @return Integer that represent the new point set's position in the JFreeChart <TT>BoxSeriesCollection</TT>
     * object.
     */
    public int add(double[] data, int numPoints) {
        int seriesIndex = getSeriesCollection().add(data, numPoints);
        initAxis();
        return seriesIndex;
    }

    /**
     * @return The chart's dataset.
     */
    public BoxSeriesCollection getSeriesCollection() {
        return (BoxSeriesCollection) dataset;
    }

    /**
     * Links a new dataset to the current chart.
     *
     * @param dataset new dataset.
     */
    public void setSeriesCollection(BoxSeriesCollection dataset) {
        this.dataset = dataset;
    }

    /**
     * @param fill true if the boxes are filled
     */
    public void setFillBox(boolean fill) {
        ((BoxAndWhiskerRenderer) dataset.getRenderer()).setFillBox(fill);
    }

    /**
     * Displays chart on the screen using Swing. This method creates an application containing a chart panel displaying
     * the chart. The created frame is positioned on-screen, and displayed before it is returned. The circle represents
     * the mean, the dark line inside the box is the median, the box limits are the first and third quartiles, the lower
     * whisker (the lower line outside the box) is the first decile, and the upper whisker is the ninth decile. The
     * outliers, if any, are represented by empty circles, or arrows if outside the range bounds.
     *
     * @param width frame width.
     * @param height frame height.
     * @return frame containing the chart.
     */
    public JFrame view(int width, int height) {
        JFrame myFrame;
        if (chart.getTitle() != null) {
            myFrame = new JFrame("BoxChart from SSJ : " + chart.getTitle().getText());
        } else {
            myFrame = new JFrame("BoxChart from SSJ");
        }
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(width, height));
        myFrame.setContentPane(chartPanel);
        myFrame.pack();
        myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        myFrame.setLocationRelativeTo(null);
        myFrame.setVisible(true);
        return myFrame;
    }

    /**
     * NOT IMPLEMENTED.
     *
     * @param width Chart's width in centimeters.
     * @param height Chart's height in centimeters.
     * @return LaTeX source code.
     */
    public String toLatex(double width, double height) {
        throw new UnsupportedOperationException(" NOT implemented yet");
        /*
         double yunit;
         double[] save = new double[4];

         if(dataset.getSeriesCollection().getColumnCount() == 0)
         throw new IllegalArgumentException("Empty chart");

         //Calcul des parametres d'echelle et de decalage
         double YScale = computeYScale(YAxis.getTwinAxisPosition());


         yunit = height / ( (Math.max(YAxis.getAxis().getRange().getUpperBound(), YAxis.getTwinAxisPosition()) * YScale) - (Math.min(YAxis.getAxis().getRange().getLowerBound(), YAxis.getTwinAxisPosition()) * YScale) );
         //taille d'une unite en y et en cm dans l'objet "tikzpicture"

         Formatter formatter = new Formatter(Locale.US);

         // Entete du document
         formatter.format("\\documentclass[12pt]{article}%n%n");
         formatter.format("\\usepackage{tikz}%n\\usetikzlibrary{plotmarks}%n\\begin{document}%n%n");
         if(chart.getTitle() != null)
         formatter.format("%% PGF/TikZ picture from SSJ : %s%n", chart.getTitle().getText());
         else
         formatter.format("%% PGF/TikZ picture from SSJ %n");
         formatter.format("%%  YScale = %s, YShift = %s%n", YScale,  YAxis.getTwinAxisPosition());
         formatter.format("%%        and thisFileYValue = (originalSeriesYValue+YShift)*YScale%n%n");
         if (chart.getTitle() != null)
         formatter.format("\\begin{figure}%n");
         formatter.format("\\begin{center}%n");
         formatter.format("\\begin{tikzpicture}[y=%scm]%n", yunit);
         formatter.format("\\footnotesize%n");
         if(grid)
         formatter.format("\\draw[color=lightgray] (%s) grid[ystep=%s] (%s);%n",
         (Math.min(YAxis.getAxis().getRange().getLowerBound(), YAxis.getTwinAxisPosition())-YAxis.getTwinAxisPosition()) * YScale,
         ystepGrid*YScale,
         (Math.max(YAxis.getAxis().getRange().getUpperBound(), YAxis.getTwinAxisPosition())-YAxis.getTwinAxisPosition()) * YScale );
         formatter.format("%s", YAxis.toLatex(YScale) );

         formatter.format("%s", dataset.toLatex(YScale, YAxis.getTwinAxisPosition(),      YAxis.getAxis().getLowerBound(), YAxis.getAxis().getUpperBound()));

         formatter.format("\\end{tikzpicture}%n");
         formatter.format("\\end{center}%n");
         if (chart.getTitle() != null) {
         formatter.format("\\caption{");
         formatter.format(chart.getTitle().getText());
         formatter.format("}%n\\end{figure}%n");
         }
         formatter.format("\\end{document}%n");
         return formatter.toString();
         */
    }
}
