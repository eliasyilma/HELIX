/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;



import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import static UI.SectionInfoFX.GraphPanel;
import static UI.SectionInfoFX.createChart;

/**
 *
 * @author user
 */
public class ResultDialog {

    ChartPanel fitness;
    ChartPanel penalty;
    ChartPanel cost;

    public static void initAndShowGUI(ChartPanel fit, ChartPanel pen, ChartPanel cost) {
// This method is invoked on the EDT thread
        JFrame ResultFrame = new JFrame("Simulation Results");
        ResultFrame.setSize(1210, 440);
        ResultFrame.setLayout(null);
        fit.setSize(400, 400);
        cost.setSize(400, 400);
        pen.setSize(400, 400);
        fit.setLocation(0, 0);
        cost.setLocation(400, 0);
        pen.setLocation(800, 0);
        ResultFrame.add(cost);
        ResultFrame.add(pen);
        ResultFrame.add(fit);
        ResultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ResultFrame.setVisible(true);

    }

    public static void main(String[] args) {
        final XYSeries fitVSgen = new XYSeries("fitness vs. gen");
        final XYSeries penVSgen = new XYSeries("penalty vs. gen");
        final XYSeries wgtVSgen = new XYSeries("weight vs. gen");

        DrawGraph fitg = null;
        final XYSeriesCollection datafit = new XYSeriesCollection();
        datafit.addSeries(fitVSgen);
        final XYSeriesCollection datapen = new XYSeriesCollection();
        datapen.addSeries(penVSgen);
        final XYSeriesCollection datawt = new XYSeriesCollection();
        datawt.addSeries(wgtVSgen);

//        JFreeChart fitC = createChart(datafit, "Fitness Vs. Generation", "Generation No.", "Fitness");
//        ChartPanel fitPanel = new ChartPanel(fitC);
//        JFreeChart penC = createChart(datafit, "Penalty Vs. Generation", "Generation No.", "Penalty");
//        ChartPanel penPanel = new ChartPanel(penC);
//        JFreeChart costC = createChart(datafit, "Cost Vs. Generation", "Generation No.", "Cost");
//        ChartPanel costPanel = new ChartPanel(costC);
//        ResultDialog.initAndShowGUI(fitPanel, penPanel, costPanel);
        
    }

}
