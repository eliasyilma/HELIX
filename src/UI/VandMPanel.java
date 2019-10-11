/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import SAM3D.Joint3;
import SAM3D.Load3;
import SAM3D.Member3;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.DecimalFormat;
import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import static UI.SectionInfoFX.darkGreen;
import static UI.SectionInfoFX.pomegranateR;

/**
 *
 * @author user
 */
public class VandMPanel extends JPanel implements MouseListener, MouseMotionListener {
//mouse coordinates

    public static int mx, my;
    public static int insets = 30;
//size of panel
    public static int currDiagType = 1;
    public static int width, height;
    public static int maxX, minX = insets;
    public static DecimalFormat Formatter = new DecimalFormat("0.00");
    public static Member3 mem;
    public static int writingWidth = 150;
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());
        if (currDiagType == 0) {
            drawAFD(g2);
        } else if (currDiagType == 1) {
            drawVYD(g2);
        } else if (currDiagType == 2) {
            drawVZD(g2);
        } else if (currDiagType == 3) {
            drawTMD(g2);
        } else if (currDiagType == 4) {
            drawMYD(g2);
        } else if (currDiagType == 5) {
            drawMZD(g2);
        }
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(insets, insets, width, height);
        drawZeroLine(g2);
        drawSectionLine(g2, currDiagType);

    }

    public static double gradient(double y1, double y2,double xd){
        return ((y1-y2)/xd);
    }
    
    public static LinkedList<Point2D> getCriticalRegions(LinkedList<Point2D> coords,double length) {
        LinkedList<Point2D> crit = new LinkedList<>();
        double diff = length/ (coords.size() - 1.0);
        double grad = gradient(coords.get(1).y,coords.get(0).y, diff);
        double prev = grad;
        if (grad < 0 || (Math.abs(grad)<0.001)) {
            crit.add(coords.get(0));
        }
                    System.out.println();

        for (int i = 1; i < coords.size() - 2; i++) {
            grad = gradient(coords.get(i + 1).y , coords.get(i).y , diff);
            double gradn = gradient(coords.get(i + 2).y , coords.get(i+1).y , diff);            
            if((coords.get(i + 1).y<0) && (coords.get(i).y>0)&&(Math.abs(prev)<0.001) && (Math.abs(gradn)<0.001)){
                crit.add(coords.get(i+1));
            }else if((coords.get(i + 1).y>0) && (coords.get(i).y<0)&&(Math.abs(prev)<0.001) && (Math.abs(gradn)<0.001)){
                crit.add(coords.get(i+1));
            }

            else if (Math.abs(grad)<0.001) { // when gradients=0
                //do nothing
            } else if(prev * grad > 0) {
                //do nothing
            }else{
                //gradient has changed, thus there is a maximum or minimum point there
                crit.add(coords.get(i));
 //               System.out.println("x: "+coords.get(i).x+"   y: "+coords.get(i).y);
            }
            prev=grad;
        }
        if (grad > 0) {
            crit.add(coords.get(coords.size()-1));
        }
        return crit;
    }

    public static void drawZeroLine(Graphics2D g2d) {
        g2d.setColor(darkGreen);
        g2d.setFont(new Font("Century Gothic", Font.ITALIC, 13));
        g2d.drawLine(0 + insets, height / 2 + insets, width + insets, height / 2 + insets);
    }

    public static void drawSectionLine(Graphics2D g2d, int type) {
        g2d.setColor(pomegranateR);
        g2d.drawLine(mx, 0 + insets, mx, height - 2 + insets);
        double currentLoc = (mx - insets) / (width * 1.0) * mem.getLength();
        LinkedList<Load3> currentLoads = IncludeLoad(mem, currentLoc);
        double[] appliedActions = ComputeTotalAppliedAction(mem, currentLoads, currentLoc);
        double[] internalActions = ComputeInternalActions(mem, appliedActions);
        if (type == 0) {
            String val = Formatter.format(internalActions[0]);
            String loc = Formatter.format(currentLoc);
            g2d.drawString("Loc = " + loc + "m", mx + 3, 10 + insets);
            g2d.drawString("Ax = " + val + "KN", mx + 3, 28 + insets);
            g2d.drawString("Max: " + mem.maxAx + "KN", width + 35, height - 20);
            g2d.drawString("Min: " + mem.minAx + "KN", width + 35, height - 40);

        } else if (type == 1) {
            String val = Formatter.format(internalActions[1]);
            String loc = Formatter.format(currentLoc);
            g2d.drawString("Loc = " + loc + "m", mx + 3, 10 + insets);
            g2d.drawString("Vy = " + val + "KN", mx + 3, 28 + insets);
            g2d.drawString("Max: " + mem.maxVy + "KN", width + 35, height - 20);
            g2d.drawString("Min: " + mem.minVy + "KN", width + 35, height - 40);

        } else if (type == 2) {
            String val = Formatter.format(internalActions[2]);
            String loc = Formatter.format(currentLoc);
            g2d.drawString("Loc = " + loc + "m", mx + 3, 10 + insets);
            g2d.drawString("Vz = " + val + "KN", mx + 3, 28 + insets);
            g2d.drawString("Max: " + mem.maxVz + "KN", width + 35, height - 20);
            g2d.drawString("Min: " + mem.minVz + "KN", width + 35, height - 40);

        } else if (type == 3) {
            String val = Formatter.format(internalActions[3]);
            String loc = Formatter.format(currentLoc);
            g2d.drawString("Loc = " + loc + "m", mx + 3, 10 + insets);
            g2d.drawString("Tx = " + val + "KN.m.", mx, 28 + insets);
            g2d.drawString("Max: " + mem.maxMx, width + 35, height - 20);
            g2d.drawString("Min: " + mem.minMx + "KN.m.", width + 35, height - 40);

        } else if (type == 4) {
            String val = Formatter.format(internalActions[4]);
            String loc = Formatter.format(currentLoc);
            g2d.drawString("Loc = " + loc + "m", mx + 3, 10 + insets);
            g2d.drawString("My = " + val + "KN.m.", mx + 3, 28 + insets);
            g2d.drawString("Max: " + mem.maxMy + "KN.m.", width + 35, height - 20);
            g2d.drawString("Min: " + mem.minMy + "KN.m.", width + 35, height - 40);

        } else if (type == 5) {
            String val = Formatter.format(internalActions[5]);
            String loc = Formatter.format(currentLoc);
            g2d.drawString("Loc = " + loc + "m", mx + 3, 14 + insets);
            g2d.drawString("Mz = " + val + "KN.m.", mx + 3, 28 + insets);
            g2d.drawString("Max: " + mem.maxMz + "KN.m.", width + 35, height - 20);
            g2d.drawString("Min: " + mem.minMz + "KN.m.", width + 35, height - 40);

        }

    }

    public VandMPanel() {
        mx = minX + 10;
        my = 0;
        addMouseListener(this);
        addMouseMotionListener(this);

    }

    public static void drawAFD(Graphics2D g2) {
        for (int i = 1; i < mem.momentZCoords.size(); i++) {
            int zero = height / 2 + insets;
            Point2D start = mapToPanel(mem.axialCoords.get(i - 1), 0);
            Point2D end = mapToPanel(mem.axialCoords.get(i), 0);
            g2.setColor(SectionInfoFX.emeraldG);
            int[] x = {(int) start.x, (int) start.x, (int) end.x, (int) end.x};
            int[] y = {(int) zero, (int) start.y, (int) end.y, (int) zero};
            g2.fillPolygon(x, y, 4);
            g2.drawLine((int) start.x, (int) start.y, (int) end.x, (int) end.y);
            g2.setColor(SectionInfoFX.darkGreen);
        }
    }

    public static void drawVYD(Graphics2D g2) {
        for (int i = 1; i < mem.momentZCoords.size(); i++) {
            int zero = height / 2 + insets + 1;
            Point2D start = mapToPanel(mem.shearYCoords.get(i - 1), 1);
            Point2D end = mapToPanel(mem.shearYCoords.get(i), 1);
            g2.setColor(SectionInfoFX.emeraldG);
            int[] x = {(int) start.x, (int) start.x, (int) end.x, (int) end.x};
            int[] y = {(int) zero, (int) start.y, (int) end.y, (int) zero};
            g2.fillPolygon(x, y, 4);
            g2.drawLine((int) start.x, (int) start.y, (int) end.x, (int) end.y);
            g2.setColor(SectionInfoFX.darkGreen);

        }
    }

    public static void drawVZD(Graphics2D g2) {
        for (int i = 1; i < mem.momentZCoords.size(); i++) {
            int zero = height / 2 + insets + 1;

            Point2D start = mapToPanel(mem.shearZCoords.get(i - 1), 2);
            Point2D end = mapToPanel(mem.shearZCoords.get(i), 2);
            g2.setColor(SectionInfoFX.emeraldG);
            int[] x = {(int) start.x, (int) start.x, (int) end.x, (int) end.x};
            int[] y = {(int) zero, (int) start.y, (int) end.y, (int) zero};

            g2.fillPolygon(x, y, 4);
            g2.setColor(SectionInfoFX.darkGreen);

            g2.drawLine((int) start.x, (int) start.y, (int) end.x, (int) end.y);

        }
    }

    public static void drawTMD(Graphics2D g2) {
        for (int i = 1; i < mem.momentZCoords.size(); i++) {
            int zero = height / 2 + insets + 1;
            Point2D start = mapToPanel(mem.torsionCoords.get(i - 1), 3);
            Point2D end = mapToPanel(mem.torsionCoords.get(i), 3);
            g2.drawLine((int) start.x, (int) start.y, (int) end.x, (int) end.y);
            g2.setColor(SectionInfoFX.emeraldG);
            int[] x = {(int) start.x, (int) start.x, (int) end.x, (int) end.x};
            int[] y = {(int) zero, (int) start.y, (int) end.y, (int) zero};
            g2.fillPolygon(x, y, 4);
            g2.setColor(SectionInfoFX.darkGreen);
            g2.drawLine((int) start.x, (int) start.y, (int) end.x, (int) end.y);
        }
    }

    public static void drawMYD(Graphics2D g2) {
        for (int i = 1; i < mem.momentZCoords.size(); i++) {
            int zero = height / 2 + insets + 1;
            Point2D start = mapToPanel(mem.momentYCoords.get(i - 1), 4);
            Point2D end = mapToPanel(mem.momentYCoords.get(i), 4);
            g2.drawLine((int) start.x, (int) start.y, (int) end.x, (int) end.y);
            g2.setColor(SectionInfoFX.emeraldG);
            int[] x = {(int) start.x, (int) start.x, (int) end.x, (int) end.x};
            int[] y = {(int) zero, (int) start.y, (int) end.y, (int) zero};
            g2.fillPolygon(x, y, 4);
            g2.setColor(SectionInfoFX.darkGreen);
            g2.drawLine((int) start.x, (int) start.y, (int) end.x, (int) end.y);

        }
    }

    public static void drawMZD(Graphics2D g2) {
        for (int i = 1; i < mem.momentZCoords.size(); i++) {
            int zero = height / 2 + insets + 1;
            Point2D start = mapToPanel(mem.momentZCoords.get(i - 1), 5);
            Point2D end = mapToPanel(mem.momentZCoords.get(i), 5);
            g2.drawLine((int) start.x, (int) start.y, (int) end.x, (int) end.y);
            g2.setColor(SectionInfoFX.emeraldG);
            int[] x = {(int) start.x, (int) start.x, (int) end.x, (int) end.x};
            int[] y = {(int) zero, (int) start.y, (int) end.y, (int) zero};
            g2.fillPolygon(x, y, 4);
            g2.setColor(SectionInfoFX.darkGreen);
            g2.drawLine((int) start.x, (int) start.y, (int) end.x, (int) end.y);

        }
    }

    public static Point2D mapToPanel(Point2D pt, int type) {
        double xval = pt.x / mem.getLength() * width;
        double yval = 0;
        double Hmax;
        if (type == 0) {
            //axialForce
            Hmax = (mem.maxAx > Math.abs(mem.minAx)) ? Math.abs(mem.maxAx) : Math.abs(mem.minAx);
            if (Math.abs(Hmax) <= 0.0001) {
                Hmax = 1;
            }
            yval = pt.y / Hmax * (height / 2.0);
            yval = (yval > 0) ? (height / 2.0 - Math.abs(yval)) : (height / 2.0 + Math.abs(yval));
        }
        if (type == 1) {
            //shearForceY
            Hmax = (mem.maxVy > Math.abs(mem.minVy)) ? Math.abs(mem.maxVy) : Math.abs(mem.minVy);
            if (Math.abs(Hmax) <= 0.0001) {
                Hmax = 1;
            }
            yval = pt.y / Hmax * (height / 2.0);
            yval = (yval > 0) ? (height / 2.0 - Math.abs(yval)) : (height / 2.0 + Math.abs(yval));
        }
        if (type == 2) {
            //axialForce
            Hmax = (mem.maxVz > Math.abs(mem.minVz)) ? Math.abs(mem.maxVz) : Math.abs(mem.minVz);
            if (Math.abs(Hmax) <= 0.0001) {
                Hmax = 1;
            }
            yval = pt.y / Hmax * (height / 2.0);
            yval = (yval > 0) ? (height / 2.0 - Math.abs(yval)) : (height / 2.0 + Math.abs(yval));
        }
        if (type == 3) {
            //axialForce
            Hmax = (mem.maxMx > Math.abs(mem.minMx)) ? Math.abs(mem.maxMx) : Math.abs(mem.minMx);
            if (Math.abs(Hmax) <= 0.0001) {
                Hmax = 1;
            }
            yval = pt.y / Hmax * (height / 2.0);
            yval = (yval > 0) ? (height / 2.0 - Math.abs(yval)) : (height / 2.0 + Math.abs(yval));
        }
        if (type == 4) {
            //axialForce
            Hmax = (mem.maxMy > Math.abs(mem.minMy)) ? Math.abs(mem.maxMy) : Math.abs(mem.minMy);
            if (Math.abs(Hmax) <= 0.0001) {
                Hmax = 1;
            }
            yval = pt.y / Hmax * (height / 2.0);
            yval = (yval > 0) ? (height / 2.0 - Math.abs(yval)) : (height / 2.0 + Math.abs(yval));

        }
        if (type == 5) {
            //axialForce
            Hmax = (mem.maxMz > Math.abs(mem.minMz)) ? Math.abs(mem.maxMz) : Math.abs(mem.minMz);
            if (Math.abs(Hmax) <= 0.0001) {
                Hmax = 1;
            }
            yval = pt.y / Hmax * (height / 2.0);
            yval = (yval > 0) ? (height / 2.0 - Math.abs(yval)) : (height / 2.0 + Math.abs(yval));

        }

        Point2D mappedPt = new Point2D(xval + insets, yval + insets);
        return mappedPt;
    }
//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public static LinkedList<Load3> IncludeLoad(Member3 m, Double location) {
        LinkedList<Load3> includedLoads = new LinkedList<>();
        for (int i = 0; i < m.getLoads().size(); i++) {
            Load3 l = m.getLoads().get(i);
            int type = l.getType();
            if (type == 0) {
                double pos = m.getLength() * l.getPosition();
                if ((location > pos) || Math.abs(location - pos) < 0.0001) {
                    includedLoads.add(l);
                }
            } else {
                includedLoads.add(l);
            }
        }
        return includedLoads;
    }

    public static double[] ComputeTotalAppliedAction(Member3 m, LinkedList<Load3> loads, double location) {
        double Fx = 0, Fy = 0, Fz = 0, Mx = 0, My = 0, Mz = 0;
        Mz += m.sFy * location;
        My += m.sFz * location;
        for (int i = 0; i < loads.size(); i++) {
            Load3 l = loads.get(i);
            double position = l.getPosition() * m.getLength();
            if (l.getType() == 0) {
                if (Math.abs(l.getX()) > 0) {
                    Fx += l.getX();
                }
                if (Math.abs(l.getY()) > 0) {
                    Fy += l.getY();
                    Mz += l.getY() * (location - position);

                }
                if (Math.abs(l.getZ()) > 0) {
                    Fz += l.getZ();
                    My += l.getZ() * (location - position);

                }
                if (Math.abs(l.getMX()) > 0) {
                    Mx += l.getMX();
                }
                if (Math.abs(l.getMY()) > 0) {
                    My += l.getMY();
                }
                if (Math.abs(l.getMZ()) > 0) {
                    Mz += l.getMZ();
                }
            } else {
                if (Math.abs(l.getX()) > 0) {
                    Fx += l.getX() * location;
                }
                if (Math.abs(l.getY()) > 0) {
                    Fy += l.getY() * location;
                    Mz += l.getY() * (location * location) * 0.5;

                }
                if (Math.abs(l.getZ()) > 0) {
                    Fz += l.getZ() * location;
                    My += l.getZ() * (location * location) * 0.5;
                }
            }
        }
        double[] appliedActions = {Fx, Fy, Fz, Mx, My, Mz};
        return appliedActions;
    }

    public static double[] ComputeInternalActions(Member3 m, double[] AppliedActions) {
        double[] internalActions = new double[6];
        internalActions[0] = -(m.sFx + AppliedActions[0]);
        internalActions[1] = -(m.sFy + AppliedActions[1]);
        internalActions[2] = -(m.sFz + AppliedActions[2]);
        internalActions[3] = -(m.sMx + AppliedActions[3]);
        internalActions[4] = -(m.sMy + AppliedActions[4]);
        internalActions[5] = -(m.sMz + AppliedActions[5]);
        return internalActions;
    }

    public static void internalActionCoordinates(Member3 m) {
        double length = m.getLength();
        double n = 30.0;
        double interval = length / n;
        double maxAx = m.sFx, maxVy = m.sFy, maxVz = m.sFz, maxTx = m.sMx, maxMy = m.sMy, maxMz = m.sMz;
        double minAx = m.sFx, minVy = m.sFy, minVz = m.sFz, minTx = m.sMx, minMy = m.sMy, minMz = m.sMz;
        m.axialCoords = new LinkedList<Point2D>();
        m.shearYCoords = new LinkedList<Point2D>();
        m.shearZCoords = new LinkedList<Point2D>();
        m.torsionCoords = new LinkedList<Point2D>();
        m.momentYCoords = new LinkedList<Point2D>();
        m.momentZCoords = new LinkedList<Point2D>();
        for (int i = 0; i <= n; i++) {
            double currentLoc = interval * i;
            LinkedList<Load3> currentLoads = IncludeLoad(m, currentLoc);
            double[] appliedActions = ComputeTotalAppliedAction(m, currentLoads, currentLoc);
            double[] internalActions = ComputeInternalActions(m, appliedActions);
            Point2D Ax = new Point2D(currentLoc, internalActions[0]);
            Point2D Vy = new Point2D(currentLoc, internalActions[1]);
            Point2D Vz = new Point2D(currentLoc, internalActions[2]);
            Point2D Tx = new Point2D(currentLoc, internalActions[3]);
            Point2D My = new Point2D(currentLoc, internalActions[4]);
            Point2D Mz = new Point2D(currentLoc, internalActions[5]);
//find maximum values
            maxAx = (maxAx < Ax.y) ? Ax.y : maxAx;
            maxVy = (maxVy < Vy.y) ? Vy.y : maxVy;
            maxVz = (maxVz < Vz.y) ? Vz.y : maxVz;
            maxTx = (maxTx < Tx.y) ? Tx.y : maxTx;
            maxMy = (maxMy < My.y) ? My.y : maxMy;
            maxMz = (maxMz < Mz.y) ? Mz.y : maxMz;
//find minimum values
            minAx = (minAx > Ax.y) ? Ax.y : minAx;
            minVy = (minVy > Vy.y) ? Vy.y : minVy;
            minVz = (minVz > Vz.y) ? Vz.y : minVz;
            minTx = (minTx > Tx.y) ? Tx.y : minTx;
            minMy = (minMy > My.y) ? My.y : minMy;
            minMz = (minMz > Mz.y) ? Mz.y : minMz;
            m.axialCoords.add(Ax);
            m.shearYCoords.add(Vy);
            m.shearZCoords.add(Vz);
            m.torsionCoords.add(Tx);
            m.momentYCoords.add(My);
            m.momentZCoords.add(Mz);
        }
        m.maxAx = maxAx;
        m.maxVy = maxVy;
        m.maxVz = maxVz;
        m.maxMx = maxTx;
        m.maxMy = maxMy;
        m.maxMz = maxMz;
        m.minAx = minAx;
        m.minVy = minVy;
        m.minVz = minVz;
        m.minMx = minTx;
        m.minMy = minMy;
        m.minMz = minMz;

    }

    public static void main(String[] args) {
        Joint3 j5 = new Joint3(5, -5, 5, 5, 5, 5);
        Joint3 j6 = new Joint3(6, 5, 5, 5, 5, 5);
        double W = 100;
        Load3 l1 = new Load3(0, 0.5, 0, -W, 0, 0, 0, 0);
//        Load3 l2 = new Load3(0, 0.5, 0, W / 10, 0, 0, 0, 0);

        Member3 t5 = new Member3(5, j5, j6);
        t5.addLoads(l1);
//        t5.addLoads(l2);

        t5.memLength();
        t5.sFx = -7.5;
        t5.sFy = 50;
        t5.sFz = 0;
        t5.sMx = 0;
        t5.sMy = 0;
        t5.sMz = -75;
        t5.eFx = 7.5;
        t5.eFy = 50;
        t5.eFz = 0;
        t5.eMx = 0;
        t5.eMy = 0;
        t5.eMz = 75;
        internalActionCoordinates(t5);
        getCriticalRegions(t5.axialCoords,t5.getLength());
        getCriticalRegions(t5.shearYCoords,t5.getLength());
        getCriticalRegions(t5.shearZCoords,t5.getLength());
        getCriticalRegions(t5.torsionCoords,t5.getLength());
        getCriticalRegions(t5.momentYCoords,t5.getLength());
        getCriticalRegions(t5.momentZCoords,t5.getLength());
        for (int i = 0; i < t5.axialCoords.size(); i++) {
 //           System.out.println("d: " + t5.axialCoords.get(i).x + "  " + t5.axialCoords.get(i).y + " " + t5.shearYCoords.get(i).y + " " + t5.shearZCoords.get(i).y + " " + t5.torsionCoords.get(i).y + " " + t5.momentYCoords.get(i).y + " " + t5.momentZCoords.get(i).y);
        }
        System.out.println("");
        System.out.println("max: " + t5.maxAx + "   " + t5.maxVy + "   " + t5.maxVz + "   " + t5.maxMx + "   " + t5.maxMy + "   " + t5.maxMz);
        System.out.println("min: " + t5.minAx + "   " + t5.minVy + "   " + t5.minVz + "   " + t5.minMx + "   " + t5.minMy + "   " + t5.minMz);

        mem = t5;
        width = 500;
        height = 150;
        maxX = width + insets;
        JFrame DFrame = new JFrame("Section Profile");
        DFrame.setLayout(null);
        DFrame.setSize(650, 270);
        VandMPanel panel = new VandMPanel();
        DFrame.add(panel);
        panel.setSize(width + insets * 4, height + insets * 2);
        panel.setLocation(10, 10);

        DFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DFrame.setVisible(true);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        mx = e.getX();
        my = e.getY();
        if (mx < minX) {
            mx = minX;
            repaint();

        } else if (mx >= minX && mx <= maxX) {

            repaint();
        } else {
            mx = maxX;
            repaint();

        }
        repaint();
        //       throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mousePressed(MouseEvent me) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased(MouseEvent me) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent me) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent me) {
        //       throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        mx = me.getX();
        my = me.getY();
        if (mx < minX) {
            mx = minX;
            repaint();

        } else if (mx >= minX && mx <= maxX) {

            repaint();
        } else {
            mx = maxX;
            repaint();

        }
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        //       throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
