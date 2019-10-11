/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package UI;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

/**
 *
 * @author user
 */
public class DrawGraph extends ApplicationFrame{
    File im=new File("C:/users/user/desktop/a.png");
     public DrawGraph(final String title, XYSeriesCollection dataset,String Title,String XLabel,String YLabel) throws IOException {
         
        super(title);
        final JFreeChart chart = createChart(dataset,Title,XLabel,YLabel);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
//                ChartUtilities.saveChartAsPNG(im, chart, 500, 270);

       

    }
     public final JFreeChart createChart(final XYDataset dataset,String Title,String XLabel,String YLabel) {
        
        // create the chart...
        final JFreeChart chart = ChartFactory.createXYLineChart(
            Title,      // chart title
            XLabel,                      // x axis label
            YLabel,                      // y axis label
            dataset,                  // data
            PlotOrientation.VERTICAL,
            true,                     // include legend
            true,                     // tooltips
            false                     // urls
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);

//        final StandardLegend legend = (StandardLegend) chart.getLegend();
  //      legend.setDisplaySeriesShapes(true);
        
        // get a reference to the plot for further customisation...
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
    //    plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        
        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, false);
        plot.setRenderer(renderer);

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        final NumberAxis domainAxis = (NumberAxis)plot.getDomainAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setAutoRange(true);
        domainAxis.setAutoRange(true);
        // OPTIONAL CUSTOMISATION COMPLETED.
                
        return chart;  
    }

}
