/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import java.text.DecimalFormat;
import java.util.LinkedList;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author user
 */
public class biaxialMod {

    static double b, h;
    static Point2D[] mainR;
    static Point2D[] extraRXT;
    static Point2D[] extraRXB;
    static Point2D[] extraRYL;
    static Point2D[] extraRYR;

    static int mainDia, extraDiaXT, extraDiaXB, extraDiaYL, extraDiaYR;
    static int dx, dy; //center of rotation
    private static double a;
    static int mx, my;  // the most recently recorded mouse coordinates
    static double angle;
    static int indent = 10;
    static int minY, maxY, minX, maxX;
    static int NAdepth;
    static double Area;
    static double concArea;
    double AxForce, AxForceConC, CmForceStl, TsForceStl;
    static Point2D currentCentroid;
    static Point2D leftmost, rightmost, topmost, bottommost;
    static double fyd = 260.86;
    static double fcd = 11.33;
    static double eyd = (fyd / 200000);
    static int frameW = 700, panelW = 600;
    static int frameH = 350, panelH = 200;
    static double ecm = 0.0035;
    DecimalFormat strainFormatter = new DecimalFormat("0.000");
    DecimalFormat stressFormatter = new DecimalFormat("0.00");
    static Point2D center;
    static double totalSteelMoment, totalForce;
//PARAMETERS: b,h,fcd,fyd,ecm

    public static LinkedList<Point2D> interactionCoordinates(Section sec, double b, double h, double angl, double fyd, double fcd, double ecm) {
        //center of section
        setSectionParameters(sec);
        dx = (int) (b * 0.45);
        dy = (int) (h * 0.5);
        angle = angl;
        LinkedList<Point2D> points = new LinkedList<>();
        Point2D centerOrig = new Point2D(0, 0);
        center = rotateAndTranslate(centerOrig, angle);
        //compute section coordinates(formSection)
        Point2D[] sectionCoord = formSection(b / 2.0, h / 2.0, angle, fyd, fcd);
        //get fringe vertices(setMinMaxValues)
        double[] MinMaxY = getMaxAndMinYCoordinates(sectionCoord);
        double[] MinMaxX = getMaxAndMinXCoordinates(sectionCoord);
        setMaxAndMinValues(MinMaxY, MinMaxX);
        NAdepth = minY + 30;
        double maxDepth = minY + (maxY - minY) * 1.25;
        final XYSeries fitVSgen = new XYSeries("first", false, false);
//        System.out.println("center:" +"("+center.x+","+center.y+")");
        Point2D maxTension = pureTension();
        points.add(maxTension);
        while (NAdepth <= maxDepth) {
            if (Math.abs(NAdepth - 350) < 0.01) {
//                System.out.println("sotp");
            }
            //concrete forces:
            //get coordinates for enclosed area(compArea)
            Point2D[] concRegion = CompressionRegion(sectionCoord, angle);
            //compute centroid(polyCentroid)
            Point2D centroid = polyCentroid(concRegion);
            //compute area (polyArea)
//            System.out.println("cx: " + centroid.x + " cy: " + centroid.y);
            double totalArea = polyArea(concRegion) * 2.0;
            //compute compressive force
            //           System.out.println("total area: " + totalArea);
            double concForce = totalArea * fcd * 2 / 1000.0;
            //get final moment(center-polyCentroid)*polyArea*11.33/1000
            double concMoment = concForce * Math.abs((center.y - centroid.y) * 2 / 1000.0);
            //steel forces:
            //calculate rebar coordinates(formSection)
            //calculate strain based on location of NAdepth(drawStrainInfo)
            //calculate stresses from strains computed(computeSteelStress)
            //calculate forces from stresses(drawSteelStress)
            totalForce = Math.abs(concForce);
            //           System.out.println("concMom: "+concMoment + "conc Force: " + concForce);
            totalSteelMoment = 0;
            for (Point2D mainR1 : mainR) {
                double mx = (int) (mainR1.x / 2.0 - (b / 4.0));
                double my = (int) (mainR1.y / 2.0 - (h / 4.0));
                Point2D rb = rotateAndTranslate(new Point2D(mx, my), angle);
                //           System.out.println("rb: ("+rb.x+","+rb.y+")");

                double strain = computeSteelStrain(rb);
                double force = computeSteelStress(rb, strain, mainDia, fyd);
            }
            for (Point2D extraRX1 : extraRXT) {
                double mx = (int) (extraRX1.x / 2.0 - (b / 4.0));
                double my = (int) (extraRX1.y / 2.0 - (h / 4.0));
                Point2D rb = rotateAndTranslate(new Point2D(mx, my), angle);
//            System.out.println("rb: ("+rb.x+","+rb.y+")");

                double strain = computeSteelStrain(rb);
                double force = computeSteelStress(rb, strain, extraDiaXT, fyd);

            }
            for (Point2D extraRX1 : extraRXB) {
                double mx = (int) (extraRX1.x / 2.0 - (b / 4.0));
                double my = (int) (extraRX1.y / 2.0 - (h / 4.0));
                Point2D rb = rotateAndTranslate(new Point2D(mx, my), angle);
//            System.out.println("rb: ("+rb.x+","+rb.y+")");

                double strain = computeSteelStrain(rb);
                double force = computeSteelStress(rb, strain, extraDiaXB, fyd);

            }
            for (Point2D extraRY1 : extraRYL) {
                double mx = (int) (extraRY1.x / 2.0 - (b / 4.0));
                double my = (int) (extraRY1.y / 2.0 - (h / 4.0));
                Point2D rb = rotateAndTranslate(new Point2D(mx, my), angle);
//            System.out.println("rb: ("+rb.x+","+rb.y+")");

                double strain = computeSteelStrain(rb);
                double force = computeSteelStress(rb, strain, extraDiaYL, fyd);

            }
            for (Point2D extraRY1 : extraRYR) {
                double mx = (int) (extraRY1.x / 2.0 - (b / 4.0));
                double my = (int) (extraRY1.y / 2.0 - (h / 4.0));
                Point2D rb = rotateAndTranslate(new Point2D(mx, my), angle);
//            System.out.println("rb: ("+rb.x+","+rb.y+")");

                double strain = computeSteelStrain(rb);
                double force = computeSteelStress(rb, strain, extraDiaYR, fyd);

            }

//            System.out.println("steel force: " + (totalForce + concForce));
            double totalMoment = totalSteelMoment + concMoment;
//            System.out.println("steel mom:"+totalSteelMoment);
//            System.out.println(NAdepth + " N: " + totalForce + " M: " + totalMoment);
            NAdepth = NAdepth + 30;
            points.add(new Point2D(totalMoment, totalForce));

        }
        //Total Compression
        Point2D maxCompression = pureCompression(b, h,fcd);
        points.add(maxCompression);
        for (Point2D point : points) {
//            System.out.println(" N: " + point.y + " M: " + point.x);
        }

        return points;
    }

    public static Point2D pureCompression(double b, double h,double fcd) {
        totalSteelMoment = 0;
        double maxCompVal = b * h * fcd / 1000.0;
        totalForce = maxCompVal;

        for (int i = 0; i < mainR.length; i++) {
            Point2D mainR1 = mainR[i];
            double mx = (int) (mainR1.x / 2.0 - (b / 4.0));
            double my = (int) (mainR1.y / 2.0 - (h / 4.0));
            Point2D rb = rotateAndTranslate(new Point2D(mx, my), angle);
            //           System.out.println("rb: ("+rb.x+","+rb.y+")");

            double strain = eyd;
            double force = computeSteelStress(rb, strain, mainDia, fyd);

        }
        for (Point2D extraRX1 : extraRXT) {
            double mx = (int) (extraRX1.x / 2.0 - (b / 4.0));
            double my = (int) (extraRX1.y / 2.0 - (h / 4.0));

            Point2D rb = rotateAndTranslate(new Point2D(mx, my), angle);
//            System.out.println("rb: ("+rb.x+","+rb.y+")");

            double strain = eyd;
            double force = computeSteelStress(rb, strain, extraDiaXT, fyd);

        }

        for (Point2D extraRX1 : extraRXB) {
            double mx = (int) (extraRX1.x / 2.0 - (b / 4.0));
            double my = (int) (extraRX1.y / 2.0 - (h / 4.0));

            Point2D rb = rotateAndTranslate(new Point2D(mx, my), angle);
//            System.out.println("rb: ("+rb.x+","+rb.y+")");
            double strain = eyd;
            double force = computeSteelStress(rb, strain, extraDiaXB, fyd);

        }

        for (Point2D extraRY1 : extraRYL) {
            double mx = (int) (extraRY1.x / 2.0 - (b / 4.0));
            double my = (int) (extraRY1.y / 2.0 - (h / 4.0));
            Point2D rb = rotateAndTranslate(new Point2D(mx, my), angle);
//            System.out.println("rb: ("+rb.x+","+rb.y+")");

            double strain = eyd;
            double force = computeSteelStress(rb, strain, extraDiaYL, fyd);

        }

        for (Point2D extraRY1 : extraRYR) {
            double mx = (int) (extraRY1.x / 2.0 - (b / 4.0));
            double my = (int) (extraRY1.y / 2.0 - (h / 4.0));
            Point2D rb = rotateAndTranslate(new Point2D(mx, my), angle);
//            System.out.println("rb: ("+rb.x+","+rb.y+")");

            double strain = eyd;
            double force = computeSteelStress(rb, strain, extraDiaYR, fyd);

        }
        double totalMoment = totalSteelMoment;
        return new Point2D(0, totalForce);

    }

    public static Point2D pureTension() {
        totalSteelMoment = 0;
        totalForce = 0;

        for (int i = 0; i < mainR.length; i++) {
            Point2D mainR1 = mainR[i];
            double mx = (int) (mainR1.x / 2.0 - (b / 4.0));
            double my = (int) (mainR1.y / 2.0 - (h / 4.0));
            Point2D rb = rotateAndTranslate(new Point2D(mx, my), angle);
            //           System.out.println("rb: ("+rb.x+","+rb.y+")");

            double strain = -eyd;
            double force = computeSteelStress(rb, strain, mainDia, fyd);

        }
        for (Point2D extraRX1 : extraRXT) {
            double mx = (int) (extraRX1.x / 2.0 - (b / 4.0));
            double my = (int) (extraRX1.y / 2.0 - (h / 4.0));
            Point2D rb = rotateAndTranslate(new Point2D(mx, my), angle);
//            System.out.println("rb: ("+rb.x+","+rb.y+")");

            double strain = -eyd;
            double force = computeSteelStress(rb, strain, extraDiaXT, fyd);

        }
        for (Point2D extraRX1 : extraRXB) {
            double mx = (int) (extraRX1.x / 2.0 - (b / 4.0));
            double my = (int) (extraRX1.y / 2.0 - (h / 4.0));
            Point2D rb = rotateAndTranslate(new Point2D(mx, my), angle);
//            System.out.println("rb: ("+rb.x+","+rb.y+")");

            double strain = -eyd;
            double force = computeSteelStress(rb, strain, extraDiaXB, fyd);

        }

        for (Point2D extraRY1 : extraRYL) {
            double mx = (int) (extraRY1.x / 2.0 - (b / 4.0));
            double my = (int) (extraRY1.y / 2.0 - (h / 4.0));
            Point2D rb = rotateAndTranslate(new Point2D(mx, my), angle);
//            System.out.println("rb: ("+rb.x+","+rb.y+")");

            double strain = -eyd;
            double force = computeSteelStress(rb, strain, extraDiaYL, fyd);

        }
        for (Point2D extraRY1 : extraRYR) {
            double mx = (int) (extraRY1.x / 2.0 - (b / 4.0));
            double my = (int) (extraRY1.y / 2.0 - (h / 4.0));
            Point2D rb = rotateAndTranslate(new Point2D(mx, my), angle);
//            System.out.println("rb: ("+rb.x+","+rb.y+")");

            double strain = -eyd;
            double force = computeSteelStress(rb, strain, extraDiaYR, fyd);

        }

        double totalMoment = totalSteelMoment;
        return new Point2D(0.01, totalForce);

    }

    public static Point2D[] formSection(double b, double h, double angle, double fyd, double fcd) {

        double x1 = -b / 2, y1 = -h / 2;
        double x2 = b / 2, y2 = -h / 2;
        double x3 = b / 2, y3 = h / 2;
        double x4 = -b / 2, y4 = h / 2;
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
        return transVertices;
    }
    /*
     CONCRETE PARAMETER COMPUTATION METHODS
     */

    public static Point2D[] CompressionRegion(Point2D[] vertices, double angle) {
        Point2D[] finalVertices = null;
        double NA = (NAdepth - minY) * 0.8;
//        System.out.println("NA: " + NA);
        double newNA = minY + NA;
        double angleA;
        if (angle > 0.5) {
            angleA = angle;
        } else {
            angleA = 0.0;
        }

        //triangle: if both leftmost and rightmost corners are above the NADepth line
        if (leftmost.y > newNA && rightmost.y > newNA) {
//            System.out.println("case 1");
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
            Point2D[] polyVertices = {new Point2D(xcoord[0], ycoord[0]), new Point2D(xcoord[1], ycoord[1]), new Point2D(xcoord[2], ycoord[2])};
            finalVertices = polyVertices;
        } //quadrilateral: if either point is below NADepth line(@ CASES FOR LEFT AND RIGHT MOST POINTS)
        else if (leftmost.y <= newNA && rightmost.y >= newNA) {
            double x2 = NA / Math.tan((90 - angleA) * Math.PI / 180);
            double x3 = (newNA - leftmost.y) / Math.tan((90 - angleA) * Math.PI / 180);
//            System.out.println(" lx: " + leftmost.x + " ly: " + leftmost.y + "na" + newNA);
//            System.out.println("x3: " + x3);
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
            Point2D[] polyVertices = {new Point2D(xcoord[0], ycoord[0]), new Point2D(xcoord[1], ycoord[1]), new Point2D(xcoord[2], ycoord[2]), new Point2D(xcoord[3], ycoord[3])};
            finalVertices = polyVertices;

        } else if (leftmost.y >= newNA && rightmost.y <= newNA) {
            double x1 = NA / Math.tan(angleA * Math.PI / 180);
            double x3 = (newNA - rightmost.y) / Math.tan(angleA * Math.PI / 180);
//            System.out.println(" lx: " + leftmost.x + " ly: " + leftmost.y + "na" + newNA);
//            System.out.println("x3: " + x3);
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
            Point2D[] polyVertices = {new Point2D(xcoord[0], ycoord[0]), new Point2D(xcoord[1], ycoord[1]), new Point2D(xcoord[2], ycoord[2]), new Point2D(xcoord[3], ycoord[3])};
            finalVertices = polyVertices;

        } //pentagon: if both points are below NADepth line
        else if (leftmost.y < newNA && rightmost.y < newNA) {
            double x3 = (newNA - leftmost.y) / Math.tan((90 - angleA) * Math.PI / 180);
            double x4 = (newNA - rightmost.y) / Math.tan(angleA * Math.PI / 180);
//            System.out.println(" lx: " + leftmost.x + " ly: " + leftmost.y + "na" + newNA);
//            System.out.println("x3: " + x3);

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
                Point2D[] polyVertices = {new Point2D(xcoord[0], ycoord[0]), new Point2D(xcoord[1], ycoord[1]), new Point2D(xcoord[2], ycoord[2]), new Point2D(xcoord[3], ycoord[3])};
                finalVertices = polyVertices;

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
                Point2D[] polyVertices = {new Point2D(xcoord[0], ycoord[0]), new Point2D(xcoord[1], ycoord[1]), new Point2D(xcoord[2], ycoord[2]), new Point2D(xcoord[3], ycoord[3]), new Point2D(xcoord[4], ycoord[4])};
                finalVertices = polyVertices;

            }
        }
        return finalVertices;
    }

    public static void computeCentroid(Point2D[] polyVertices) {
        Point2D centroid = polyCentroid(polyVertices);
        currentCentroid = centroid;
    }

    public static double computeSteelStrain(Point2D rebarCoord) {
        //setup parameters
        double NALength = (NAdepth - minY) * 2;
        double totalLength = (maxY - minY) * 2;
        double strain;
        int ecmpxls = (int) (ecm * 10000);
//        int bottomStrainpxls=(int) (bottomStrain*10000);
        //       System.out.println("NA: "+NAdepth+" rebarY: "+rebarCoord.y);

        if (rebarCoord.y > NAdepth) {
            double bottomStrain = (((totalLength - NALength) / (NALength)) * ecm);
            strain = ((-((rebarCoord.y - NAdepth) * 2) / (totalLength - NALength)) * bottomStrain);
            if (Math.abs(strain) > eyd) {
                strain = -eyd;
            }

        } else {
            strain = (((NAdepth - rebarCoord.y) * 2) / NALength * ecm);
            if (Math.abs(strain) > eyd) {
                strain = eyd;
            }
        }
//        System.out.println("strain: " + strain + "  " + ecmpxls + "     ");
        return strain;
    }

    public static double computeSteelStress(Point2D rebarCoord, double strain, double diam, double fyd) {
        //draw 0-stress line 
        double force = 0;
        if (strain < 0) {
            double stress;
            if (Math.abs(strain) > eyd) {
                stress = -fyd;
            } else {
                stress = strain * 200000;
            }
            double rebarArea = diam * diam * Math.PI / 4.0;
            force = stress * rebarArea / 1000;
            double moment = 0;
            double momentArm = rebarCoord.y - center.y;

            if (momentArm < 0) {
                moment = -Math.abs(force) * Math.abs(momentArm) * 2 / 1000.0;
            } else {
                moment = Math.abs(force) * Math.abs(momentArm) * 2 / 1000.0;
            }
            totalSteelMoment += moment;
            totalForce += force;
//            System.out.println("strain:"+strain+" sforce: " + force+" smoment: "+moment+" moment arm: "+momentArm);
        } else {
            double stress;
            if (Math.abs(strain) > eyd) {
                stress = fyd;
            } else {
                stress = strain * 200000;
            }
            double rebarArea = diam * diam * Math.PI / 4.0;
            force = stress * rebarArea / 1000;
            double moment = 0;
            double momentArm = rebarCoord.y - center.y;

            if (momentArm < 0) {
                moment = Math.abs(force) * Math.abs(momentArm) * 2 / 1000.0;
            } else {
                moment = -Math.abs(force) * Math.abs(momentArm) * 2 / 1000.0;
            }
            totalSteelMoment += moment;
            totalForce += force;
//            System.out.println("sforce: " + force+" smoment: "+moment);

        }

        return force;
    }


    /*
     COORDINATE TRANSFORMATION FACILITIES
     */
    public static Point2D rotateCoordinates(Point2D p, double theta) {
        int x1p = ((int) (p.x * Math.cos(theta * Math.PI / 180))) - ((int) (p.y * Math.sin(theta * Math.PI / 180)));
        int y1p = ((int) (p.x * Math.sin(theta * Math.PI / 180))) + ((int) (p.y * Math.cos(theta * Math.PI / 180)));
        Point2D transf = new Point2D(x1p + dx, dy - y1p);
        return transf;
    }

    public static Point2D translateCoordinates(Point2D p, double dx, double dy) {
        Point2D transf = new Point2D(p.x + dx, dy - p.y);
        return transf;
    }

    public static Point2D rotateAndTranslate(Point2D p, double theta) {
        int x1p = ((int) (p.x * Math.cos(theta * Math.PI / 180))) - ((int) (p.y * Math.sin(theta * Math.PI / 180)));
        int y1p = ((int) (p.x * Math.sin(theta * Math.PI / 180))) + ((int) (p.y * Math.cos(theta * Math.PI / 180)));
        Point2D transf = new Point2D(x1p + dx, dy - y1p);
        return transf;
    }
//------------------------------------------------------------------------------

    /*
     GEOMETRIC PARAMETER COMPUTATION FACILITIES
     */
    public static Point2D polyCentroid(Point2D[] vertices) {
        Point2D centroid = new Point2D(0, 0);
        double signedArea = 0.0;
        double x0; // Current vertex X
        double y0; // Current vertex Y
        double x1; // Next vertex X
        double y1; // Next vertex Y
        double a;  // Partial signed area
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

    public static void setMaxAndMinValues(double[] ycoord, double[] xcoord) {
        minY = (int) ycoord[0];
        maxY = (int) ycoord[1];
        minX = (int) xcoord[0];
        maxX = (int) xcoord[1];
    }

    public static double[] getMaxAndMinYCoordinates(Point2D[] vertices) {
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

    public static double[] getMaxAndMinXCoordinates(Point2D[] vertices) {
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

    public static void setSectionParameters(Section sec) {
        b = sec.Breadth;
        h = sec.Height;
        mainR = sec.reBar.mainCoor;
        extraRXT = sec.reBar.extraCoorXT;
        extraRXB = sec.reBar.extraCoorXB;
        extraRYL = sec.reBar.extraCoorYL;
        extraRYR = sec.reBar.extraCoorYR;
        mainDia = (int) sec.reBar.main_dia;
        extraDiaXT = (int) sec.reBar.extra_x_diaT;
        extraDiaXB = (int) sec.reBar.extra_x_diaB;
        extraDiaYL = (int) sec.reBar.extra_y_diaL;
        extraDiaYR = (int) sec.reBar.extra_y_diaR;
        NAdepth = 100;
    }

    private static void initAndShowGUI() {
// This method is invoked on the EDT thread
    }

//    public static void main(String[] args) throws IOException {
//        double b = 400;
//        double h = 500;
//        double mx = 500, my = 200;
//        final XYSeries fitVSgen = new XYSeries("first", false, false);
//
//        angle = Math.atan(mx / my) * 180.0 / Math.PI;
//        dx = (int) (b * 0.5);
//        dy = (int) (h * 0.45);
//        System.out.println("angle: " + angle);
//        Section sect = new Section(3, 300, b, h);
//        setSectionParameters(sect);
//
//        System.out.println("Main re:" + sect.reBar.main_num + "d" + sect.reBar.main_dia);
//        System.out.println("ExtraX re:" + sect.reBar.extra_x_num + "d" + sect.reBar.extra_x_dia);
//        System.out.println("ExtraY re:" + sect.reBar.extra_y_num + "d" + sect.reBar.extra_y_dia);
//        LinkedList<Point2D> coord = interactionCoordinates(sect, b, h, angle, fyd, fcd, ecm);
//        for (int i = 0; i < coord.size(); i++) {
//            fitVSgen.add(coord.get(i).x, coord.get(i).y);
//        }
////        final DrawGraph fitg = new DrawGraph("Biaxial Interaction", fitVSgen, "M Vs. A", "moment", "axial F.");
////        fitg.pack();
////        RefineryUtilities.centerFrameOnScreen(fitg);
////        fitg.setVisible(true);
//
//    }

}
