/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import SAM3D.Joint3;
import SAM3D.Load3;
import SAM3D.Member3;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXSlider;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import static UI.VandMPanel.internalActionCoordinates;
import static UI.VandMPanel.width;
import static UI.biaxialMod.interactionCoordinates;
import static UI.biaxialMod.setSectionParameters;

/**
 *
 * @author user
 */
public class SectionInfoFX extends JPanel implements MouseListener, MouseMotionListener {

    static double b, h;
    Point2D[] mainR;
    Point2D[] extraRXT;
    Point2D[] extraRXB;
    Point2D[] extraRYL;
    Point2D[] extraRYR;
    int mainDia, extraDiaXT, extraDiaXB, extraDiaYL, extraDiaYR;
    static int dx, dy; //center of rotation
    int mx, my;  // the most recently recorded mouse coordinates
    static double angle;
    int indent = 10;
    int minY, maxY, minX, maxX;
    int NAdepth;
    double Area;
    double concArea;
    double AxForce, AxForceConC, CmForceStl, TsForceStl;
    Point2D currentCentroid;
    Point2D center;
    Point2D leftmost, rightmost, topmost, bottommost;
    static double fyd = 260.86;
    static double fcd = 11.333;
    static double eyd = (fyd / 200000);
    static int frameW = 700, panelW = 600;
    static int frameH = 350, panelH = 200;
    static double ecm = 0.0035;
    public DecimalFormat strainFormatter = new DecimalFormat("0.000");
    public DecimalFormat stressFormatter = new DecimalFormat("0.00");
    static SectionInfoFX SectionPanel;
    static JPanel GraphPanel;
    static VandMPanel DiagramPanel;
    static JFXSlider hor_left;
    static ChartPanel cp;
    double totalSteelMoment;
    double concMoment;
    double resMoment;
    double maxDepth;
    static Section sect;
    /*
     color definitions
     */
    static Color pomegranateR = new Color(196, 57, 38);
    static Color emeraldG = new Color(102, 232, 160);
    static Color darkGreen = new Color(12, 113, 24);
    static Color belizeHoleB = new Color(33, 127, 188);
    static Color cloudsW = new Color(236, 240, 241);
    Color BACKGROUNDCOLOR = Color.WHITE;
    public float scale = 1.0f;
    public float scalemin = 0.05f;
    public float scalemax = 5f;

    public SectionInfoFX() {
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(new MouseAdapter() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double delta = 0.05f * e.getWheelRotation();
                scale -= delta;
                if (scale > scalemax) {
                    scale = scalemax;
                }
                if (scale < scalemin) {
                    scale = scalemin;
                }
                revalidate();
                repaint();
            }

        });

    }

    @Override
    public void paintComponent(Graphics g) {
        totalSteelMoment = 0;
        concMoment = 0;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int wid = (int) (b / 2.0);
        int hei = (int) (h / 2.0);
        g2.setColor(BACKGROUNDCOLOR);
        g2.fillRect(0, 0, (int) b / 2 + panelW, (int) h / 2 + panelH);
        g2.setStroke(new BasicStroke(2));
        g2.setFont(new Font("Century Gothic", Font.ITALIC, 12));
        g2.setColor(pomegranateR);
        g2.drawString("ANGLE: " + angle, 12, 22);
        g2.drawString("DEPTH: " + (NAdepth - minY) * 2, 12, 42);
        g2.setColor(pomegranateR);
        drawTitle(g2);
        drawSect(g2, wid, hei, angle, fyd);
        drawCenter(g2);
        maxDepth = minY + (maxY - minY) * 1.25;
        if (my < minY) {
            NAdepth = minY;
            drawNA(g2);
            drawStrainLine(g2, ecm);
            drawConcreteStressBlock(g2);
        } else if (my > minY && my < maxDepth) {
            NAdepth = my;
            drawNA(g2);
            drawStrainLine(g2, ecm);
            drawConcreteStressBlock(g2);

        } else if (my > maxDepth) {
            NAdepth = (int) maxDepth;
            drawNA(g2);
            drawStrainLine(g2, ecm);
            drawConcreteStressBlock(g2);

        }
        if (AxForce < 0) {
            g2.setColor(darkGreen);
        } else {
            g2.setColor(pomegranateR);
        }
        String axRounded = stressFormatter.format(AxForce);
        g2.drawString("N: " + axRounded + " KN", 12, 62);
        String momRounded = stressFormatter.format(concMoment + totalSteelMoment);
        g2.drawString("M: " + momRounded + " KN.m", 12, 82);
    }

    public void drawCenter(Graphics2D gr) {

        gr.drawLine((int) scaleCoordinates(center.x - 5), (int) scaleCoordinates(center.y), (int) scaleCoordinates(center.x + 5), (int) scaleCoordinates(center.y));
        gr.drawLine((int) scaleCoordinates(center.x), (int) scaleCoordinates(center.y - 5), (int) scaleCoordinates(center.x), (int) scaleCoordinates(center.y + 5));
        gr.drawString("(" + Math.ceil(center.x) + "," + Math.ceil(center.y) + ")", (int) scaleCoordinates(center.x - 10), (int) scaleCoordinates(center.y - 10));
        //       System.out.println("center: " + " X:" + center.x + " Y:" + center.y);
    }

    public void drawTitle(Graphics2D gr) {
        gr.drawString("SECTION DETAIL", scaleCoordinates((int) ((minX + maxX) * 0.5) - 30), scaleCoordinates(35));
        gr.drawString("ST. STRAIN", scaleCoordinates(maxX + 70), scaleCoordinates(35));
        gr.drawString("CONC. STRESS", scaleCoordinates(maxX + 140), scaleCoordinates(35));
        gr.drawString("ST. FORCES", scaleCoordinates(maxX + 230), scaleCoordinates(35));
        gr.drawString("ST. MOMENTS", scaleCoordinates(maxX + 300), scaleCoordinates(35));
    }

    public void drawSect(Graphics2D gr, int b, int h, double angle, double fyd) {

        int x1 = -b / 2, y1 = -h / 2;
        int x2 = b / 2, y2 = -h / 2;
        int x3 = b / 2, y3 = h / 2;
        int x4 = -b / 2, y4 = h / 2;
        Point2D v1 = new Point2D(x1, y1);
        Point2D v2 = new Point2D(x2, y2);
        Point2D v3 = new Point2D(x3, y3);
        Point2D v4 = new Point2D(x4, y4);

        Point2D ot1 = rotateAndTranslate(v1, 0.0);
        Point2D ot2 = rotateAndTranslate(v2, 0.0);
        Point2D ot3 = rotateAndTranslate(v3, 0.0);
        Point2D ot4 = rotateAndTranslate(v4, 0.0);

        Point2D vt1 = rotateAndTranslate(v1, angle);
        Point2D vt2 = rotateAndTranslate(v2, angle);
        Point2D vt3 = rotateAndTranslate(v3, angle);
        Point2D vt4 = rotateAndTranslate(v4, angle);

        Point2D[] transVertices = {vt1, vt2, vt3, vt4};
        drawPoly(gr, transVertices);
        double[] MinMaxY = getMaxAndMinYCoordinates(transVertices);
        double[] MinMaxX = getMaxAndMinXCoordinates(transVertices);
        setMaxAndMinValues(MinMaxY, MinMaxX);
        //check
        drawCompArea(gr, transVertices);
        CmForceStl = 0;
        TsForceStl = 0;
        for (int i = 0; i < mainR.length; i++) {
            Point2D mainR1 = mainR[i];
            int mx = (int) (mainR1.x / 2.0 - (b / 2.0));
            int my = (int) (mainR1.y / 2.0 - (h / 2.0));
            Point2D rb = rotateAndTranslate(new Point2D(mx, my), angle);
            if (NAdepth > rb.y) {
                gr.setColor(darkGreen);
            } else {
                gr.setColor(pomegranateR);
            }
            //       gr.drawString("d"+mainDia, (int) rb.x - 10, (int) rb.y-10);
            gr.fillOval((int) scaleCoordinates(rb.x - (mainDia / 4)), (int) scaleCoordinates(rb.y - (mainDia / 4)), (mainDia / 2), (mainDia / 2));
            double strain = drawStrainInfo(gr, rb);
            drawSteelStress(gr, rb, strain, mainDia, fyd);
        }
        for (Point2D extraRXT : extraRXT) {
            int mx = (int) (extraRXT.x / 2.0 - (b / 2.0));
            int my = (int) (extraRXT.y / 2.0 - (h / 2.0));
            Point2D rb = rotateAndTranslate(new Point2D(mx, my), angle);

            if (NAdepth > rb.y) {
                gr.setColor(darkGreen);
            } else {
                gr.setColor(pomegranateR);
            }
            //          gr.drawString("d"+extraDiaX, (int) rb.x - 10, (int) rb.y-10);
            gr.fillOval((int) scaleCoordinates(rb.x - (extraDiaXT / 4)), (int) scaleCoordinates(rb.y - (extraDiaXT / 4)), (extraDiaXT / 2), (extraDiaXT / 2));
            double strain = drawStrainInfo(gr, rb);
            drawSteelStress(gr, rb, strain, extraDiaXT, fyd);
        }
        for (Point2D extraRXB : extraRXB) {
            int mx = (int) (extraRXB.x / 2.0 - (b / 2.0));
            int my = (int) (extraRXB.y / 2.0 - (h / 2.0));
            Point2D rb = rotateAndTranslate(new Point2D(mx, my), angle);

            if (NAdepth > rb.y) {
                gr.setColor(darkGreen);
            } else {
                gr.setColor(pomegranateR);
            }
            //          gr.drawString("d"+extraDiaX, (int) rb.x - 10, (int) rb.y-10);
            gr.fillOval((int) scaleCoordinates(rb.x - (extraDiaXB / 4)), (int) scaleCoordinates(rb.y - (extraDiaXB / 4)), (extraDiaXB / 2), (extraDiaXB / 2));
            double strain = drawStrainInfo(gr, rb);
            drawSteelStress(gr, rb, strain, extraDiaXB, fyd);
        }

        for (Point2D extraRYL : extraRYL) {
            int mx = (int) (extraRYL.x / 2.0 - (b / 2.0));
            int my = (int) (extraRYL.y / 2.0 - (h / 2.0));
            Point2D rb = rotateAndTranslate(new Point2D(mx, my), angle);

            if (NAdepth > rb.y) {
                gr.setColor(darkGreen);
            } else {
                gr.setColor(pomegranateR);
            }
            //            gr.drawString("d"+extraDiaY, (int) rb.x - 10, (int) rb.y-10);
            gr.fillOval((int) scaleCoordinates(rb.x - (extraDiaYL / 4)), (int) scaleCoordinates(rb.y - (extraDiaYL / 4)), (extraDiaYL / 2), (extraDiaYL / 2));
            double strain = drawStrainInfo(gr, rb);
            drawSteelStress(gr, rb, strain, extraDiaYL, fyd);

        }

        for (Point2D extraRYR : extraRYR) {
            int mx = (int) (extraRYR.x / 2.0 - (b / 2.0));
            int my = (int) (extraRYR.y / 2.0 - (h / 2.0));
            Point2D rb = rotateAndTranslate(new Point2D(mx, my), angle);

            if (NAdepth > rb.y) {
                gr.setColor(darkGreen);
            } else {
                gr.setColor(pomegranateR);
            }
            //            gr.drawString("d"+extraDiaY, (int) rb.x - 10, (int) rb.y-10);
            gr.fillOval((int) scaleCoordinates(rb.x - (extraDiaYR / 4)), (int) scaleCoordinates(rb.y - (extraDiaYR / 4)), (extraDiaYR / 2), (extraDiaYR / 2));
            double strain = drawStrainInfo(gr, rb);
            drawSteelStress(gr, rb, strain, extraDiaYR, fyd);

        }

        gr.setColor(pomegranateR);
        computeForcesAndMoments();
    }

    public void drawCompArea(Graphics2D gr, Point2D[] vertices) {
        double c1x, c1y, c2x, c2y, d1x, d1y, d2x, d2y, e1x, e1y, e2x, e2y;
        Point2D c1, d1, e1;
        Point2D c2, d2, e2;

        double NA = (NAdepth - minY) * 0.8;
        double newNA = minY + NA;
        double angleA;
        if (angle > 0.5) {
            angleA = angle;
        } else {
            angleA = 0;
        }

        //triangle: if both leftmost and rightmost corners are above the NADepth line
        if (leftmost.y > newNA && rightmost.y > newNA) {
            double x1 = NA / Math.tan(angleA * Math.PI / 180);
            double x2 = NA / Math.tan((90 - angleA) * Math.PI / 180);
            int[] xcoord = new int[3];
            int[] ycoord = new int[3];
            xcoord[0] = (int) topmost.x;
            xcoord[1] = (int) (topmost.x - x1);
            xcoord[2] = (int) (topmost.x + x2);

            ycoord[0] = (int) topmost.y;
            ycoord[1] = (int) newNA;
            ycoord[2] = (int) newNA;
            gr.setColor(emeraldG);
            gr.fillPolygon(scaleCoordinates(xcoord), scaleCoordinates(ycoord), 3);
            gr.setColor(darkGreen);
            gr.drawPolygon(scaleCoordinates(xcoord), scaleCoordinates(ycoord), 3);
            Point2D[] polyVertices = {new Point2D(xcoord[0], ycoord[0]), new Point2D(xcoord[1], ycoord[1]), new Point2D(xcoord[2], ycoord[2])};
            drawCentroid(gr, polyVertices);
            concArea = polyArea(polyVertices);
        } //quadrilateral: if either point is below NADepth line(@ CASES FOR LEFT AND RIGHT MOST POINTS)
        else if (leftmost.y <= newNA && rightmost.y >= newNA) {
            double x2 = NA / Math.tan((90 - angleA) * Math.PI / 180);
            double x3 = (newNA - leftmost.y) / Math.tan((90 - angleA) * Math.PI / 180);
            int[] xcoord = new int[4];
            int[] ycoord = new int[4];
            xcoord[0] = (int) topmost.x;
            xcoord[1] = (int) leftmost.x;
            xcoord[2] = (int) (leftmost.x + x3);
            xcoord[3] = (int) (topmost.x + x2);

            ycoord[0] = (int) topmost.y;
            ycoord[1] = (int) leftmost.y;
            ycoord[2] = (int) newNA;
            ycoord[3] = (int) newNA;
            gr.setColor(emeraldG);
            gr.fillPolygon(scaleCoordinates(xcoord), scaleCoordinates(ycoord), 4);
            gr.setColor(darkGreen);
            gr.drawPolygon(scaleCoordinates(xcoord), scaleCoordinates(ycoord), 4);
            Point2D[] polyVertices = {new Point2D(xcoord[0], ycoord[0]), new Point2D(xcoord[1], ycoord[1]), new Point2D(xcoord[2], ycoord[2]), new Point2D(xcoord[3], ycoord[3])};
            drawCentroid(gr, polyVertices);
            concArea = polyArea(polyVertices);

        } else if (leftmost.y >= newNA && rightmost.y <= newNA) {
            double x1 = NA / Math.tan(angleA * Math.PI / 180);
            double x3 = (newNA - rightmost.y) / Math.tan(angleA * Math.PI / 180);
            int[] xcoord = new int[4];
            int[] ycoord = new int[4];
            xcoord[0] = (int) topmost.x;
            xcoord[1] = (int) (topmost.x - x1);
            xcoord[2] = (int) (rightmost.x - x3);
            xcoord[3] = (int) rightmost.x;

            ycoord[0] = (int) topmost.y;
            ycoord[1] = (int) newNA;
            ycoord[2] = (int) newNA;
            ycoord[3] = (int) rightmost.y;
            gr.setColor(emeraldG);
            gr.fillPolygon(scaleCoordinates(xcoord), scaleCoordinates(ycoord), 4);
            gr.setColor(darkGreen);
            gr.drawPolygon(scaleCoordinates(xcoord), scaleCoordinates(ycoord), 4);
            Point2D[] polyVertices = {new Point2D(xcoord[0], ycoord[0]), new Point2D(xcoord[1], ycoord[1]), new Point2D(xcoord[2], ycoord[2]), new Point2D(xcoord[3], ycoord[3])};
            drawCentroid(gr, polyVertices);
            concArea = polyArea(polyVertices);

        } //pentagon: if both points are below NADepth line
        else if (leftmost.y < newNA && rightmost.y < newNA) {
            double x3 = (newNA - leftmost.y) / Math.tan((90 - angleA) * Math.PI / 180);
            double x4 = (newNA - rightmost.y) / Math.tan(angleA * Math.PI / 180);

            if (angle < 0.5) {
                int[] xcoord = new int[4];
                int[] ycoord = new int[4];
                xcoord[0] = (int) rightmost.x;
                xcoord[1] = (int) leftmost.x;
                xcoord[2] = (int) leftmost.x;
                xcoord[3] = (int) rightmost.x;

                ycoord[0] = (int) topmost.y;
                ycoord[1] = (int) topmost.y;
                ycoord[2] = (int) newNA;
                ycoord[3] = (int) newNA;
                gr.setColor(emeraldG);
                gr.fillPolygon(scaleCoordinates(xcoord), scaleCoordinates(ycoord), 4);
                gr.setColor(darkGreen);
                gr.drawPolygon(scaleCoordinates(xcoord), scaleCoordinates(ycoord), 4);
                Point2D[] polyVertices = {new Point2D(xcoord[0], ycoord[0]), new Point2D(xcoord[1], ycoord[1]), new Point2D(xcoord[2], ycoord[2]), new Point2D(xcoord[3], ycoord[3])};
                drawCentroid(gr, polyVertices);
                concArea = polyArea(polyVertices);

            } else {
                int[] xcoord = new int[5];
                int[] ycoord = new int[5];
                xcoord[0] = (int) topmost.x;
                xcoord[1] = (int) leftmost.x;
                xcoord[2] = (int) (leftmost.x + x3);
                xcoord[3] = (int) (rightmost.x - x4);
                xcoord[4] = (int) rightmost.x;

                ycoord[0] = (int) topmost.y;
                ycoord[1] = (int) leftmost.y;
                ycoord[2] = (int) newNA;
                ycoord[3] = (int) newNA;
                ycoord[4] = (int) rightmost.y;
                gr.setColor(emeraldG);
                gr.fillPolygon(scaleCoordinates(xcoord), scaleCoordinates(ycoord), 5);
                gr.setColor(darkGreen);
                gr.drawPolygon(scaleCoordinates(xcoord), scaleCoordinates(ycoord), 5);
                Point2D[] polyVertices = {new Point2D(xcoord[0], ycoord[0]), new Point2D(xcoord[1], ycoord[1]), new Point2D(xcoord[2], ycoord[2]), new Point2D(xcoord[3], ycoord[3]), new Point2D(xcoord[4], ycoord[4])};
                drawCentroid(gr, polyVertices);
                concArea = polyArea(polyVertices);

            }
        }
    }

    public Point2D scaleCoordinates(Point2D coord) {
        return new Point2D(coord.x * scale, coord.y * scale);
    }

    public double[] scaleCoordinates(double[] coord) {
        double[] sCoord = new double[coord.length];
        for (int i = 0; i < coord.length; i++) {
            sCoord[i] = coord[i] * scale;
        }
        return coord;
    }

    public int[] scaleCoordinates(int[] coord) {
        int[] sCoord = new int[coord.length];
        for (int i = 0; i < coord.length; i++) {
            sCoord[i] = (int) (coord[i] * scale);
        }
        return sCoord;
    }

    public double scaleCoordinates(double coord) {
        return coord * scale;
    }

    public int scaleCoordinates(int coord) {
        return (int) (coord * scale);
    }

    public void drawCentroid(Graphics2D gr, Point2D[] polyVertices) {
        Point2D centerOrig = new Point2D(0, 0);
        center = rotateAndTranslate(centerOrig, angle);
        Point2D centroid = polyCentroid(polyVertices);
        currentCentroid = centroid;
        gr.drawLine((int) scaleCoordinates(centroid.x - 5), (int) scaleCoordinates(centroid.y), (int) scaleCoordinates(centroid.x + 5), (int) scaleCoordinates(centroid.y));
        gr.drawLine((int) scaleCoordinates(centroid.x), (int) scaleCoordinates(centroid.y - 5), (int) scaleCoordinates(centroid.x), (int) scaleCoordinates(centroid.y + 5));
        gr.drawString("(" + Math.ceil(centroid.x) + "," + Math.ceil(centroid.y) + ")", (int) scaleCoordinates(centroid.x - 10), (int) scaleCoordinates(centroid.y - 10));
    }

    public void drawConcForce(Graphics2D gr) {
        double force = concArea * 4 * 11.33 / 1000;
        AxForceConC = force;
        drawArrowLine(gr, (int) scaleCoordinates(maxX + 140 + 39), (int) scaleCoordinates(currentCentroid.y), (int) scaleCoordinates(maxX + 140 + 9), (int) scaleCoordinates(currentCentroid.y), true, false);
        drawArrowLine(gr, (int) scaleCoordinates(maxX + 230 + 120 + 39), (int) scaleCoordinates(currentCentroid.y), (int) scaleCoordinates(maxX + 230 + 120 + 9), (int) scaleCoordinates(currentCentroid.y), true, false);
        concMoment = Math.abs(force) * Math.abs(center.y - currentCentroid.y) * 2 / 1000.0;
        String forceVal = stressFormatter.format(force);
        String momentVal = stressFormatter.format(concMoment);
        gr.drawString(forceVal + " KN", scaleCoordinates(maxX + 140 + 15), (int) scaleCoordinates(currentCentroid.y - 5));
        gr.drawString(momentVal + " KN.m", scaleCoordinates(maxX + 230 + 120 + 39), (int) scaleCoordinates(currentCentroid.y - 5));
    }

    public void computeForcesAndMoments() {
        AxForce = AxForceConC + CmForceStl + TsForceStl;
    }

    public double[] getMaxAndMinYCoordinates(Point2D[] vertices) {
        double maxY = vertices[0].y;
        double minY = vertices[0].y;
        bottommost = vertices[0];
        topmost = vertices[0];
        for (int i = 1; i < vertices.length; i++) {
            if (maxY > vertices[i].y) {

            } else {
                maxY = vertices[i].y;
                bottommost = vertices[i];
            }
            if (minY < vertices[i].y) {

            } else {
                minY = vertices[i].y;
                topmost = vertices[i];
            }
        }
        double[] ycoord = {minY, maxY};
        return ycoord;
    }

    public static double polyArea(Point2D[] points) {

        double area = 0;         // Accumulates area in the loop
        int numPoints = points.length;
        int j = numPoints - 1;  // The last vertex is the 'previous' one to the first
        for (int i = 0; i < numPoints; i++) {
            area = area + (points[j].x + points[i].x) * (points[j].y - points[i].y);
            j = i;  //j is previous vertex to i
        }
        return Math.abs(area / 2);
    }

    public double[] getMaxAndMinXCoordinates(Point2D[] vertices) {
        double maxX = vertices[0].x;
        double minX = vertices[0].x;
        rightmost = vertices[0];
        leftmost = vertices[0];
        for (int i = 1; i < vertices.length; i++) {
            if (maxX > vertices[i].x) {

            } else {
                maxX = vertices[i].x;
                rightmost = vertices[i];
            }
            if (minX < vertices[i].x) {

            } else {
                minX = vertices[i].x;
                leftmost = vertices[i];
            }
        }
        double[] xcoord = {minX, maxX};
        return xcoord;
    }

    public void drawStrainLine(Graphics2D gr, double ecm) {
        //draw 0-strain line 
        int ecmpxls = (int) (ecm * 10000);
        gr.drawLine(scaleCoordinates(maxX + 70), scaleCoordinates(minY), scaleCoordinates(maxX + 70), scaleCoordinates(maxY));
//        gr.drawLine(400, minY, 400 + ecmpxls, minY);
//        gr.drawLine(400, maxY, 400 - bottomStrainPxls, maxY);
//        gr.drawLine(400 + ecmpxls, minY, 400 - bottomStrainPxls, maxY);
    }

    public void drawConcreteStressBlock(Graphics2D gr) {
        int fcdpxl = (int) 11.33 * 4;
        double NALength = NAdepth - minY;
        //draw 0-stress line 
        gr.drawLine(scaleCoordinates(maxX + 140), scaleCoordinates(minY), scaleCoordinates(maxX + 140), scaleCoordinates(maxY));

        //draw stress block
        gr.setColor(emeraldG);
        gr.fillRect(scaleCoordinates(maxX + 140), scaleCoordinates(minY), (int) scaleCoordinates(fcdpxl), (int) scaleCoordinates((NALength * 0.8)));
        gr.setColor(darkGreen);
        gr.drawRect(scaleCoordinates(maxX + 140), scaleCoordinates(minY), (int) scaleCoordinates(fcdpxl), (int) scaleCoordinates((NALength * 0.8)));
        drawConcForce(gr);
        gr.setColor(pomegranateR);

    }

    public double drawStrainInfo(Graphics2D gr, Point2D rebarCoord) {
        //setup parameters
        double NALength = (NAdepth - minY) * 2;
        double totalLength = (maxDepth - minY) * 2;
        double strain;
        int ecmpxls = (int) (ecm * 10000);
        if (rebarCoord.y > NAdepth) {
            double bottomStrain = (((totalLength - NALength) / (NALength)) * ecm);
            strain = (-((rebarCoord.y - NAdepth) * 2) / (totalLength - NALength)) * bottomStrain;
            if (Math.abs(strain) > eyd) {
                strain = -eyd;
            }
        } else {
            strain = (((NAdepth - rebarCoord.y) * 2) / NALength * ecm);
            if (Math.abs(strain) > eyd) {
                strain = eyd;
            }
        }
        int strainpxls = (int) (strain * 20000);
        gr.drawLine(scaleCoordinates(maxX + 70), (int) scaleCoordinates(rebarCoord.y), scaleCoordinates(maxX + 70 + strainpxls), (int) scaleCoordinates(rebarCoord.y));
        String strVal = strainFormatter.format(strain * 100);
        gr.drawString("" + strVal + "%", (int) scaleCoordinates(rebarCoord.x - 10), (int) scaleCoordinates(rebarCoord.y - 10));
        gr.drawString("" + strVal + "%", scaleCoordinates(maxX + 70 + 10), (int) scaleCoordinates(rebarCoord.y));
        return strain;
    }

    public void drawSteelStress(Graphics2D gr, Point2D rebarCoord, double strain, double diam, double fyd) {
        double NALength = NAdepth - minY;
        //draw 0-force line 
        gr.setColor(pomegranateR);
        gr.drawLine(scaleCoordinates(maxX + 230), scaleCoordinates(minY), scaleCoordinates(maxX + 230), scaleCoordinates(maxY));
//        gr.drawLine(700, minY, 700, maxY);
        int strainpxls = (int) (strain * 20000);
        //if in tension
        if (strain < 0) {
            gr.setColor(pomegranateR);
            drawArrowLine(gr, (int) scaleCoordinates(maxX + 230), (int) scaleCoordinates(rebarCoord.y), (int) scaleCoordinates((maxX + 230 + strainpxls)), (int) scaleCoordinates(rebarCoord.y), true, false);
            double stress;
            if (Math.abs(strain) > eyd) {
                stress = -fyd;
            } else {
                stress = strain * 200000;
            }
            double rebarArea = diam * diam * Math.PI / 4.0;
            double force = stress * rebarArea / 1000;
            double momentArm = (rebarCoord.y - center.y) * 2;
            double moment = 0;
            if (momentArm < 0) {
                moment = -Math.abs(force) * Math.abs(momentArm) / 1000.0;
            } else {
                moment = Math.abs(force) * Math.abs(momentArm) / 1000.0;
            }
            String forceVal = stressFormatter.format(force);
            String momentVal = stressFormatter.format(moment);
            //           drawArrowLine(gr, (int) 700, (int) rebarCoord.y, (int) (700 + strainpxls), (int) rebarCoord.y, true, false);
            gr.drawString(forceVal + " KN", scaleCoordinates(maxX + 230 + 40), (int) scaleCoordinates(rebarCoord.y));
            gr.drawString(momentVal + " KN.m", scaleCoordinates(maxX + 230 + 120), (int) scaleCoordinates(rebarCoord.y));
            //           gr.drawString(forceVal + " KN", scaleCoordinates(640), (int) scaleCoordinates(rebarCoord.y));
            gr.drawString(forceVal + " KN", (int) scaleCoordinates(rebarCoord.x - 10), (int) scaleCoordinates(rebarCoord.y - 25));
            gr.drawString(momentArm + " mm", (int) scaleCoordinates(rebarCoord.x - 10), (int) scaleCoordinates(rebarCoord.y - 39));

            CmForceStl += force;
            totalSteelMoment += moment;

        } else {
            gr.setColor(darkGreen);
            drawArrowLine(gr, scaleCoordinates(maxX + 230 + 9 + strainpxls), (int) scaleCoordinates(rebarCoord.y), (int) scaleCoordinates(maxX + 230 + 9), (int) scaleCoordinates(rebarCoord.y), true, false);
            double stress;
            if (Math.abs(strain) > eyd) {
                stress = fyd;
            } else {
                stress = strain * 200000;
            }
            double rebarArea = diam * diam * Math.PI / 4.0;
            double force = stress * rebarArea / 1000;
            double momentArm = (rebarCoord.y - center.y) * 2;
            double moment = 0;
            if (momentArm < 0) {
                moment = Math.abs(force) * Math.abs(momentArm) / 1000.0;
            } else {
                moment = -Math.abs(force) * Math.abs(momentArm) / 1000.0;
            }
            String forceVal = stressFormatter.format(force);
            String momentVal = stressFormatter.format(moment);
//            drawArrowLine(gr, (int) 700, (int) rebarCoord.y, (int) (700 + strainpxls), (int) rebarCoord.y, true, false);
            //           System.out.println("strain:" + strain + " sforce: " + force + " smoment: " + moment + " moment arm: " + momentArm);
            gr.drawString(forceVal + " KN", scaleCoordinates(maxX + 230 + 40), (int) scaleCoordinates(rebarCoord.y));
            gr.drawString(momentVal + " KN.m", scaleCoordinates(maxX + 230 + 120), (int) scaleCoordinates(rebarCoord.y));
            gr.drawString(forceVal + " KN", (int) scaleCoordinates(rebarCoord.x - 10), (int) scaleCoordinates(rebarCoord.y - 25));
            gr.drawString(momentArm + " mm", (int) scaleCoordinates(rebarCoord.x - 10), (int) scaleCoordinates(rebarCoord.y - 39));
            TsForceStl += force;
            totalSteelMoment += moment;

        }
    }

    public static void drawArrowLine(Graphics2D g, int x1, int y1, int x2, int y2, boolean x_or_y, boolean arrowHeadPos) {
        //x_or_y: HORIZONTAL==TRUE,VERTICAL==FALSE
        int[] trixo = new int[3];
        int[] triyo = new int[3];
        int xp = x1;
        int yp = y1;
        int xs = x2;
        int ys = y2;
        if (arrowHeadPos == true) {
        } else {
            xp = x2;
            yp = y2;
            xs = x1;
            ys = y1;

        }

        if (x_or_y) {
            if (arrowHeadPos) {
                trixo[0] = xp + 9;
            } else {
                trixo[0] = xp - 9;
            }
            trixo[1] = xp;
            trixo[2] = xp;
            triyo[0] = yp;
            triyo[1] = yp - 9;
            triyo[2] = yp + 9;
        } else {
            trixo[0] = xp;
            trixo[1] = xp - 9;
            trixo[2] = xp + 9;
            if (arrowHeadPos) {
                triyo[0] = yp + 9;
            } else {
                triyo[0] = yp - 9;
            }
            triyo[1] = yp;
            triyo[2] = yp;
        }
        g.drawLine(xp, yp, xs, ys);
        g.fillPolygon(trixo, triyo, 3);
    }

    public Point2D rotateCoordinates(Point2D p, double theta) {
        int x1p = ((int) (p.x * Math.cos(theta * Math.PI / 180))) - ((int) (p.y * Math.sin(theta * Math.PI / 180)));
        int y1p = ((int) (p.x * Math.sin(theta * Math.PI / 180))) + ((int) (p.y * Math.cos(theta * Math.PI / 180)));
        Point2D transf = new Point2D(x1p + dx, dy - y1p);
        return transf;
    }

    public Point2D translateCoordinates(Point2D p, double dx, double dy) {
        Point2D transf = new Point2D(p.x + dx, dy - p.y);
        return transf;
    }

    public Point2D rotateAndTranslate(Point2D p, double theta) {
        int x1p = ((int) (p.x * Math.cos(theta * Math.PI / 180))) - ((int) (p.y * Math.sin(theta * Math.PI / 180)));
        int y1p = ((int) (p.x * Math.sin(theta * Math.PI / 180))) + ((int) (p.y * Math.cos(theta * Math.PI / 180)));
        Point2D transf = new Point2D((x1p + dx), (dy - y1p));
        return transf;
    }

    public void drawPoly(Graphics gr, Point2D[] vert) {
        int[] sectX = new int[4];
        int[] sectY = new int[4];
        sectX[0] = (int) (vert[0].x * scale);
        sectX[1] = (int) (vert[1].x * scale);
        sectX[2] = (int) (vert[2].x * scale);
        sectX[3] = (int) (vert[3].x * scale);

        sectY[0] = (int) (vert[0].y * scale);
        sectY[1] = (int) (vert[1].y * scale);
        sectY[2] = (int) (vert[2].y * scale);
        sectY[3] = (int) (vert[3].y * scale);
        gr.drawPolygon(sectX, sectY, 4);
    }

    public static Point2D polyCentroid(Point2D[] vertices) {
        Point2D centroid = new Point2D(0, 0);
        double signedArea = 0.0;
        double x0 = 0.0; // Current vertex X
        double y0 = 0.0; // Current vertex Y
        double x1 = 0.0; // Next vertex X
        double y1 = 0.0; // Next vertex Y
        double a = 0.0;  // Partial signed area
        int vertexCount = vertices.length;
        // For all vertices except last
        int i = 0;
        for (i = 0; i < vertexCount - 1; ++i) {
            x0 = vertices[i].x;
            y0 = vertices[i].y;
            x1 = vertices[i + 1].x;
            y1 = vertices[i + 1].y;
            a = x0 * y1 - x1 * y0;
            signedArea += a;
            centroid.x += (x0 + x1) * a;
            centroid.y += (y0 + y1) * a;
        }

        // Do last vertex separately to avoid performing an expensive
        // modulus operation in each iteration.
        x0 = vertices[i].x;
        y0 = vertices[i].y;
        x1 = vertices[0].x;
        y1 = vertices[0].y;
        a = x0 * y1 - x1 * y0;
        signedArea += a;
        centroid.x += (x0 + x1) * a;
        centroid.y += (y0 + y1) * a;

        signedArea *= 0.5;
        centroid.x /= (6.0 * signedArea);
        centroid.y /= (6.0 * signedArea);

        return centroid;
    }

    public void setMaxAndMinValues(double[] ycoord, double[] xcoord) {
        minY = (int) ycoord[0];
        maxY = (int) ycoord[1];
        minX = (int) xcoord[0];
        maxX = (int) xcoord[1];
    }

    public void drawNA(Graphics2D gr) {
        gr.drawString("NA", scaleCoordinates(10), scaleCoordinates(NAdepth - 3));
        gr.drawString("NA", scaleCoordinates(690), scaleCoordinates(NAdepth - 3));
        gr.drawLine(scaleCoordinates(10), scaleCoordinates(NAdepth), scaleCoordinates(700), scaleCoordinates(NAdepth));
    }

    public void drawSection(Section sec) {
        b = sec.Breadth;
        h = sec.Height;
        mainR = sec.reBar.mainCoor;
        extraRXT = sec.reBar.extraCoorXT;
        extraRXB = sec.reBar.extraCoorXB;
        extraRYL = sec.reBar.extraCoorYL;
        extraRYR = sec.reBar.extraCoorYR;

        mainDia = (int) sec.reBar.main_dia;
        extraDiaXT = (int) sec.reBar.extra_x_diaT;
        extraDiaYL = (int) sec.reBar.extra_y_diaL;
        extraDiaXB = (int) sec.reBar.extra_x_diaB;
        extraDiaYR = (int) sec.reBar.extra_y_diaR;
        NAdepth = 100;
    }

    private static void initFX(JFXPanel fxPanel1) {
// This method is invoked on the JavaFX thread
        Scene scene1 = createScene1();
        Scene scene2 = createScene2();
        fxPanel1.setScene(scene1);
    }

    public static void initAndShowGUI(Section sectio, double fydSect, double fcdSect) {
// This method is invoked on the EDT thread
        JFrame SectionFrame = new JFrame("Section Profile");
        final JFXPanel fxPanel = new JFXPanel();
//        final JFXPanel fxPanel2 = new JFXPanel();
        sect = sectio;
        JSlider angleChange = new JSlider(JSlider.HORIZONTAL);
        SectionPanel = new SectionInfoFX();
        GraphPanel = new JPanel();
        DiagramPanel = new VandMPanel();
        double b = sectio.Breadth;
        double h = sectio.Height;
        fyd=fydSect/1.15;
        eyd=fyd/200000;
        fcd=fcdSect;
        double mx = 200, my = 200;
        angle = Math.atan(mx / my) * 180.0 / Math.PI;
        dx = (int) (b * 0.4);
        dy = (int) (h * 0.5);
        System.out.println("angle: " + angle);
        Section sect = sectio;
        Border Lborder = new LineBorder(darkGreen, 2);
        SectionPanel.setBorder(Lborder);
        SectionPanel.drawSection(sect);
        SectionFrame.setLayout(null);
        SectionFrame.setLocation(20, 20);
        SectionFrame.setSize((int) (b / 2 + frameW + b / 2 + panelW * 0.4), (int) h / 2 + frameH);
        SectionPanel.setSize((int) (b / 2 + panelW), (int) h / 2 + panelH);
        SectionPanel.setLocation(50, 25);
        GraphPanel.setSize((int) (h / 2 + panelH), (int) h / 2 + panelH);
        GraphPanel.setLocation((int) (b / 2 + panelW + 70), 25);
        GraphPanel.setBackground(Color.WHITE);
        SectionFrame.add(SectionPanel);
        SectionFrame.add(GraphPanel);
        fxPanel.setLocation(50, (int) h / 2 + panelH + 30);
        fxPanel.setSize((int) b / 2 + panelW + 15, 90);
//        fxPanel2.setLocation((int) (70 + b / 2 + panelW), (int) h / 2 + panelH + 30);
//        fxPanel2.setSize((int) 130, 190);
        SectionFrame.getContentPane().setBackground(Color.WHITE);
        SectionFrame.add(fxPanel);
//        SectionFrame.add(fxPanel2);
        SectionPanel.setBackground(Color.white);
        SectionFrame.add(angleChange);
        SectionFrame.setVisible(true);
        SectionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        System.out.println("Main re:" + sect.reBar.main_num + "d" + sect.reBar.main_dia);
//        System.out.println("ExtraX re:" + sect.reBar.extra_x_num + "d" + sect.reBar.extra_x_dia);
//        System.out.println("ExtraY re:" + sect.reBar.extra_y_num + "d" + sect.reBar.extra_y_dia);
//        final XYSeries fitVSgen = new XYSeries("first", false, false);
//        XYDataset xyDataset = new XYSeriesCollection(fitVSgen);
//        JFreeChart chart = createChart(xyDataset, "Biaxial Interaction", "MOMENT, KN.m.", "FORCE, KN");
//        ChartPanel cp = new ChartPanel(chart) {
//
//            @Override
//            public Dimension getPreferredSize() {
//                return new Dimension((int) (b / 2 + panelW * 0.6), (int) h / 2 + panelH);
//            }
//        };
//        GraphPanel.add(cp);
        SectionFrame.setVisible(true);
//        SectionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);
            }
        });
    }

    public static final JFreeChart createChart(final XYDataset dataset, String Title, String XLabel, String YLabel,Color color) {

        // create the chart...
        final JFreeChart chart = ChartFactory.createXYLineChart(
                Title, // chart title
                XLabel, // x axis label
                YLabel, // y axis label
                dataset, // data
                PlotOrientation.VERTICAL,
                true, // include legend
                true, // tooltips
                false // urls
        );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);

//        final StandardLegend legend = (StandardLegend) chart.getLegend();
//              legend.setDisplaySeriesShapes(true);
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
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesPaint(0, color);
        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        final NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setAutoRange(true);
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        domainAxis.setAutoRange(true);
        // OPTIONAL CUSTOMISATION COMPLETED.
        return chart;
    }

    private static Scene createScene2() {
        Group root = new Group();
        Scene scene = new Scene(root);
        VBox vb = new VBox();
        HBox hb = new HBox();
        vb.setSpacing(12);
        hb.setSpacing(60);
        JFXRadioButton Ax = new JFXRadioButton("AXIAL FORCE");
        JFXRadioButton Vy = new JFXRadioButton("SHEAR 2-2");
        JFXRadioButton Vz = new JFXRadioButton("SHEAR 3-3");
        JFXRadioButton Tx = new JFXRadioButton("TORSION");
        JFXRadioButton My = new JFXRadioButton("MOMENT 2-2");
        JFXRadioButton Mz = new JFXRadioButton("MOMENT 3-3");
        Ax.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, (e) -> {
            Ax.setSelected(true);
            Vy.setSelected(false);
            Vz.setSelected(false);
            Tx.setSelected(false);
            My.setSelected(false);
            Mz.setSelected(false);
            System.out.println(Ax.isSelected() + " " + Vy.isSelected() + " " + Vz.isSelected() + " " + Tx.isSelected() + " " + My.isSelected() + " " + Mz.isSelected());
            DiagramPanel.currDiagType = 0;
            DiagramPanel.repaint();
        });
        Vy.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, (e) -> {
            Ax.setSelected(false);
            Vy.setSelected(true);
            Vz.setSelected(false);
            Tx.setSelected(false);
            My.setSelected(false);
            Mz.setSelected(false);
            DiagramPanel.currDiagType = 1;
            DiagramPanel.repaint();

            System.out.println(Ax.isSelected() + " " + Vy.isSelected() + " " + Vz.isSelected() + " " + Tx.isSelected() + " " + My.isSelected() + " " + Mz.isSelected());

        });
        Vz.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, (e) -> {
            Ax.setSelected(false);
            Vy.setSelected(false);
            Vz.setSelected(true);
            Tx.setSelected(false);
            My.setSelected(false);
            Mz.setSelected(false);
            DiagramPanel.currDiagType = 2;
            DiagramPanel.repaint();

            System.out.println(Ax.isSelected() + " " + Vy.isSelected() + " " + Vz.isSelected() + " " + Tx.isSelected() + " " + My.isSelected() + " " + Mz.isSelected());

        });
        Tx.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, (e) -> {
            Ax.setSelected(false);
            Vy.setSelected(false);
            Vz.setSelected(false);
            Tx.setSelected(true);
            My.setSelected(false);
            Mz.setSelected(false);
            DiagramPanel.currDiagType = 3;
            DiagramPanel.repaint();

            System.out.println(Ax.isSelected() + " " + Vy.isSelected() + " " + Vz.isSelected() + " " + Tx.isSelected() + " " + My.isSelected() + " " + Mz.isSelected());

        });
        My.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, (e) -> {
            Ax.setSelected(false);
            Vy.setSelected(false);
            Vz.setSelected(false);
            Tx.setSelected(false);
            My.setSelected(true);
            Mz.setSelected(false);
            DiagramPanel.currDiagType = 4;
            DiagramPanel.repaint();

            System.out.println(Ax.isSelected() + " " + Vy.isSelected() + " " + Vz.isSelected() + " " + Tx.isSelected() + " " + My.isSelected() + " " + Mz.isSelected());

        });
        Mz.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, (e) -> {
            Ax.setSelected(false);
            Vy.setSelected(false);
            Vz.setSelected(false);
            Tx.setSelected(false);
            My.setSelected(false);
            Mz.setSelected(true);
            DiagramPanel.currDiagType = 5;
            DiagramPanel.repaint();

            System.out.println(Ax.isSelected() + " " + Vy.isSelected() + " " + Vz.isSelected() + " " + Tx.isSelected() + " " + My.isSelected() + " " + Mz.isSelected());

        });
        vb.getChildren().addAll(Ax, Vy, Vz, Tx, My, Mz);
        root.getChildren().add(vb);
        return (scene);
    }

    private static Scene createScene1() {
        Group root = new Group();
        Scene scene = new Scene(root);
        VBox vb = new VBox();
        vb.setSpacing(8);
        vb.setPadding(new Insets(10, 10, 0, 0));
        hor_left = new JFXSlider();
        hor_left.setMinWidth(800);
        hor_left.setMax(90);
        hor_left.setMin(0);

        hor_left.setMajorTickUnit(45);
        hor_left.setMinorTickCount(5);
//  //      hor_left.setSnapToTicks(true);
        hor_left.setShowTickMarks(true);
        hor_left.setShowTickLabels(true);
        hor_left.setIndicatorPosition(JFXSlider.IndicatorPosition.RIGHT);
        hor_left.addEventHandler(javafx.scene.input.MouseEvent.DRAG_DETECTED, (e) -> {
            final XYSeries fitVSgen = new XYSeries("Interaction boundary", false, false);
            angle = (int) hor_left.getValue();
 //           Section sect = new Section(17, 300, b, h);
            setSectionParameters(sect);

            LinkedList<Point2D> coord = interactionCoordinates(sect, b, h, angle, fyd, fcd, ecm);
            for (int i = 0; i < coord.size(); i++) {
                fitVSgen.add(coord.get(i).x, coord.get(i).y);
            }
            XYDataset xyDataset = new XYSeriesCollection(fitVSgen);
            GraphPanel.removeAll();
            GraphPanel.revalidate(); // This removes the old chart
            JFreeChart chart = createChart(xyDataset, "Biaxial Interaction", "MOMENT, KN.m.", "FORCE, KN",darkGreen);
            ChartPanel chartPanel = new ChartPanel(chart);
            GraphPanel.setLayout(new BorderLayout());
            GraphPanel.add(chartPanel);
            GraphPanel.repaint();

            SectionPanel.repaint();
        });
        hor_left.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, (e) -> {
            angle = (int) hor_left.getValue();
            final XYSeries fitVSgen = new XYSeries("Interaction boundary", false, false);
            angle = (int) hor_left.getValue();
//            Section sect = new Section(17, b, h);
            setSectionParameters(sect);

//            System.out.println("case1");
            LinkedList<Point2D> coord = interactionCoordinates(sect, b, h, angle, fyd, fcd, ecm);
            for (int i = 0; i < coord.size(); i++) {
                fitVSgen.add(coord.get(i).x, coord.get(i).y);
            }
            XYDataset xyDataset = new XYSeriesCollection(fitVSgen);
            GraphPanel.removeAll();
            GraphPanel.revalidate(); // This removes the old chart
            JFreeChart chart = createChart(xyDataset, "Biaxial Interaction", "MOMENT, KN.m.", "FORCE, KN",darkGreen);
            ChartPanel chartPanel = new ChartPanel(chart);
            GraphPanel.setLayout(new BorderLayout());
            GraphPanel.add(chartPanel);
            //               GraphPanel.repaint();

            SectionPanel.repaint();
        });
        hor_left.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_DRAGGED, (e) -> {
            angle = (int) hor_left.getValue();
            final XYSeries fitVSgen = new XYSeries("Interaction boundary", false, false);
            angle = (int) hor_left.getValue();
            setSectionParameters(sect);

//            System.out.println("case1");
            LinkedList<Point2D> coord = interactionCoordinates(sect, b, h, angle, fyd, fcd, ecm);
            for (int i = 0; i < coord.size(); i++) {
                fitVSgen.add(coord.get(i).x, coord.get(i).y);
            }
            XYDataset xyDataset = new XYSeriesCollection(fitVSgen);
            GraphPanel.removeAll();
            GraphPanel.revalidate(); // This removes the old chart
            JFreeChart chart = createChart(xyDataset, "Biaxial Interaction", "MOMENT, KN.m.", "FORCE, KN",darkGreen);
            ChartPanel chartPanel = new ChartPanel(chart);
            GraphPanel.setLayout(new BorderLayout());
            GraphPanel.add(chartPanel);
            //               GraphPanel.repaint();

            SectionPanel.repaint();
        });
        hor_left.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, (e) -> {
            angle = (int) hor_left.getValue();
            final XYSeries fitVSgen = new XYSeries("Interaction boundary", false, false);
            angle = (int) hor_left.getValue();
            setSectionParameters(sect);

            //           System.out.println("case1");
            LinkedList<Point2D> coord = interactionCoordinates(sect, b, h, angle, fyd, fcd, ecm);
            for (int i = 0; i < coord.size(); i++) {
                fitVSgen.add(coord.get(i).x, coord.get(i).y);
            }
            XYDataset xyDataset = new XYSeriesCollection(fitVSgen);
            GraphPanel.removeAll();
            GraphPanel.revalidate(); // This removes the old chart
            JFreeChart chart = createChart(xyDataset, "Biaxial Interaction", "MOMENT, KN.m.", "FORCE, KN",darkGreen);
            ChartPanel chartPanel = new ChartPanel(chart);
            GraphPanel.setLayout(new BorderLayout());
            GraphPanel.add(chartPanel);
//                GraphPanel.repaint();
            SectionPanel.repaint();
        });

        vb.getChildren().addAll(hor_left);
        root.getChildren().add(vb);
        return (scene);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
 //           Section x = new Section(2, 6,3,7,20, 200, 400, 500);

            @Override
            public void run() {
  //              initAndShowGUI(x);
            }
        });
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        if (my < minY) {
            NAdepth = minY;
            repaint();
        } else if (my > minY && my < maxDepth) {
            NAdepth = my;
            repaint();

        } else {
            NAdepth = maxY;
            repaint();

        }

        System.out.println("miny" + minY + "maxy" + maxY + "my" + my);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mx = e.getX();
        my = e.getY();
        e.consume();
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        mx = me.getX();
        my = me.getY();

        if (my < minY) {
            NAdepth = minY;
            repaint();
        } else if (my >= minY && my <= maxDepth) {
            NAdepth = my;
            repaint();
        } else {
            NAdepth = (int) maxDepth;
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent me) {
    }
}
