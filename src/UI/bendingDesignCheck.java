/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import SAM3D.Member3;
import static UI.MathUtil.isInsideTriangle;
import UI.interPoint;
import UI.interTriangle;
import java.util.LinkedList;
import static UI.biaxialMod.interactionCoordinates;

/**
 *
 * @author user
 */
public class bendingDesignCheck {

    //biaxial interaction check
    public static interPoint maximumMoment(LinkedList<interPoint> coordinates) {
        interPoint maxM = coordinates.get(0);
        for (int i = 1; i < coordinates.size(); i++) {
            if (maxM.x < coordinates.get(i).x) {
                maxM = coordinates.get(i);
            }
        }
        return maxM;
    }

    public static double biaxialEfficiency(Section sec, double b, double h, double fyd, double fcd, double ax, double mx, double my) {
        double efficiency;
        double angle = Math.atan(mx / my) * 180.0 / Math.PI;
        double resultant = Math.sqrt(my * my + mx * mx);
        LinkedList<Point2D> interactionCoord = interactionCoordinates(sec, b, h, angle, fyd, fcd, 0.0035);
        LinkedList<interPoint> interPoints = new LinkedList<>();
        for (int i = 0; i < interactionCoord.size(); i++) {
            interPoint p = new interPoint(interactionCoord.get(i).x, interactionCoord.get(i).y);
            interPoints.add(p);
        }
        interPoint load = new interPoint(resultant, ax);
        interPoint maxM = maximumMoment(interPoints);
        efficiency = ComputeColumnEfficiency(interPoints, maxM, load);
        return efficiency;
    }

    public static double FlexuralResistance(Member3 m, int position) {
        double c1, c2, c3;
        double hp = 0.0;

        double E = m.getE();
        double Msd;
        double fyk = m.getFyk();
        double fcu = m.getFcu();
        double as1, as2;
        if (position == 0) {
            Msd = m.momentZCoords.get(0).y;
            as1 = m.getXsecNegLeft().AsY2;
            as2 = m.getXsecNegLeft().AsY1;
            hp = m.getXsecNegLeft().Hpn;
        } else if (position == 1) {
            Msd = m.minMz;
            as1 = m.getXsecPos().AsY1;
            as2 = m.getXsecPos().AsY2;
            hp = m.getXsecPos().Hpp;
        } else {
            Msd = m.momentZCoords.get(m.momentZCoords.size() - 1).y;
            as1 = m.getXsecNegRight().AsY2;
            as2 = m.getXsecNegRight().AsY1;
            hp = m.getXsecNegRight().Hpn;
        }

        double h = m.getH() * 1000.0;
        double b = m.getB() * 1000.0;
        double Mrd;
        double fyd = fyk / 1.15;
        double fcd = m.getFck() * 1.25 * 0.453333333;
        double Es = 200000;
        double x; //neutral depth
        double fs1, fs2;               //forces in tension and compression steel
        double eyd = fyd / Es, es1, es2;           //steel strains
        //POINT 0: pure tension
        es1 = 0.025;
        es2 = 0.025;
        fs1 = as1 * fyd;

        //POINT 1: pure flexure
        double n = 0.8 * fcd * b;
        double o = 0.0035 * Es * as2 - fyd * as1;
        double p = -hp * as2 * 0.0035 * Es;
        double x1 = (-o + Math.sqrt(o * o - 4 * n * p)) / (2 * n);
        double x2 = (-o - Math.sqrt(o * o - 4 * n * p)) / (2 * n);
        if (x1 > x2) {
            x = x1;

        } else {
            x = x2;
        }

        fs2 = 0.0035 * Es * (x - hp) / x;
        double cc = fcd * 0.8 * x * b;
        double cs = fs2 * as2;
        double ts = fyd * as1;
        Mrd = (cc * (0.5 * h - 0.4 * x) + cs * (0.5 * (h - hp)) + ts * (h - hp - 0.5 * h)) / 1000000;
        c1 = Math.abs(Msd / Mrd) - 1;
        if (c1 < 0) {
            c1 = Math.abs(Msd / Mrd);
        } else if (c1 > 0) {
            c1 = 1.5;
        }
//        System.out.println("Msd: "+Msd+" Mrd: "+Mrd);
        return c1;
    }

    public static double biaxialEfficiency(Member3 mem, int typ) {
        double mleft = mem.momentZCoords.get(0).y;
        double mright = mem.momentZCoords.get(mem.momentZCoords.size() - 1).y;
        double mspan = mem.minMz;
//        System.out.println("mzl: " + mleft + "mzr: " + mright + "mzs: " + mspan);
        double eff = 0.0;
        if (!mem.isVertical()) {
            if (typ == 0) {
                eff = FlexuralResistance(mem, 0);
            } else if (typ == 1) {
                eff = FlexuralResistance(mem, 1);
            } else if (typ == 2) {
                eff = FlexuralResistance(mem, 2);
            }
//             if(eff>0.0 && eff<1.0){
//                 eff=1.0;
//             }

        } else {
            double effpos = 0.0;

            //if span moment is less than zero, no reinforcement is required there: penalize directly
            effpos = Math.abs(biaxialEfficiency(mem.getXsecPos(), mem.getB() * 1000.0, mem.getH() * 1000.0, mem.getFyk(), mem.getFck(), Math.abs(mem.maxAx), Math.abs(mem.maxMy), Math.abs(mem.maxMz)));
            double effNegL = Math.abs(biaxialEfficiency(mem.getXsecNegLeft(), mem.getB() * 1000.0, mem.getH() * 1000.0, mem.getFyk(), mem.getFck(), Math.abs(mem.maxAx), Math.abs(mem.maxMy), Math.abs(mem.maxMz)));
            double effNegR = Math.abs(biaxialEfficiency(mem.getXsecNegRight(), mem.getB() * 1000.0, mem.getH() * 1000.0, mem.getFyk(), mem.getFck(), Math.abs(mem.maxAx), Math.abs(mem.maxMy), Math.abs(mem.maxMz)));

            eff = Math.max(effpos, Math.max(effNegL, effNegR));

            if (eff < 1.0 && eff > 0.0) {
                if (eff > 0.6 && eff < 0.9) {
                    eff = 1 - Math.abs(eff - 0.75);
                } else if (eff < 0.6) {

                } else if (eff > 0.9) {
                    eff = 1 - eff;
                }
            }
            if (eff < 1.0 && eff > 0.0) {
                eff = 1.0;
            }

            Section bestSec = mem.getXsecNegLeft();
            if (bestSec.Height > mem.getXsecNegRight().Height) {
                bestSec = mem.getXsecNegRight();
            }
            if (bestSec.Height > mem.getXsecPos().Height) {
                bestSec = mem.getXsecPos();
            }
            float ratio = (float) (bestSec.Height / bestSec.Breadth);
            if (ratio < 0.9 || ratio > 1.15) {
                eff = 1.5;
            }
//            if(effNegL<effNegR){
//                bestSec=mem.getXsecNegRight();
//            }
//            if(effNegL<effpos){
//                bestSec=mem.getXsecPos();                
//            }
            mem.setSection(bestSec);
        }
        return eff;
    }

    public static double ComputeColumnEfficiency(LinkedList<interPoint> interactionCoordinates, interPoint maxM, interPoint load) {
        double percentageEfficiency;
        int pointLocationTriangleIndex;
        pointLocationTriangleIndex = CheckInteraction(interactionCoordinates, maxM, load.x, load.y);
        if (pointLocationTriangleIndex == 100) {
            percentageEfficiency = 1.5;
        } else {

            interPoint p3 = interactionCoordinates.get(pointLocationTriangleIndex);
            interPoint p4 = interactionCoordinates.get(pointLocationTriangleIndex + 1);
            interPoint p1 = new interPoint(0.0, maxM.y);
            interPoint p2 = load;
            float delx21 = (float) (p2.x - p1.x);
            float delx43 = (float) (p4.x - p3.x);
            if (Math.abs(delx21 - 0) < 0.001) {

                delx21 = (delx21 > 0) ? 0.01f : -0.01f;
            }
            if (Math.abs(delx43 - 0) < 0.001) {
                delx43 = (delx43 > 0) ? 0.01f : -0.01f;
            }
            double m1 = (p2.y - p1.y) / delx21;
            double m2 = (p4.y - p3.y) / delx43;
            double b1 = p1.y - p1.x * m1;
            double b2 = p3.y - p3.x * m2;
//            System.out.println("    " + m1 + "    " + m2 + "    " + b1 + "    " + b2);
            double y = (b2 * m1 - b1 * m2) / (m1 - m2);
            double x = (y - b1) / m1;
            double l1 = Math.sqrt((p1.x - x) * (p1.x - x) + (p1.y - y) * (p1.y - y));
            double l2 = Math.sqrt((p1.x - load.x) * (p1.x - load.x) + (p1.y - load.y) * (p1.y - load.y));
//            System.out.println("    " + m1 + "    " + m2 + "    " + b1 + "    " + b2 + "    " + l2 + l1);
            percentageEfficiency = l2 / l1;
        }
        //check interaction if out of the region return -1.0
        //else get the two points(other than the contour origin from the triangle that contains the point 

        return percentageEfficiency;
    }

    public static int CheckInteraction(LinkedList<interPoint> interactionCoordinates, interPoint maxM, Double Mz, Double Fy) {
        boolean insideRegion = false;
        //identify sixth point in interactionCoordinates and form contour origin point
        interPoint contourOrigin = new interPoint();
        interPoint designAction = new interPoint(Mz, Fy);
        contourOrigin.x = 0.0;
        contourOrigin.y = maxM.y;
        LinkedList<interTriangle> regions = new LinkedList<>();
        int pointLocation = 100;//in which region is the point located?
        //form and store all triangles
        //the triangles are formed in two phases:i.e. first all triangles above the maxM point
        //and then all points below maxM.
        int i = 1;
//        System.out.println("interCoord: "+interactionCoordinates.size());
        while (i < (interactionCoordinates.size() - 1) && (insideRegion == false || pointLocation == 100)) {
            if (interactionCoordinates.get(i).y > maxM.y) { //above maxM
                interTriangle trigon = new interTriangle(interactionCoordinates.get(i - 1), interactionCoordinates.get(i), contourOrigin);
                regions.add(trigon);
            } else {//below maxM
                interTriangle trigon = new interTriangle(contourOrigin, interactionCoordinates.get(i - 1), interactionCoordinates.get(i));
                regions.add(trigon);
            }

            if (isInsideTriangle(regions.get(i - 1), designAction)) {
                pointLocation = i;
                insideRegion = true;
            }
            i = i + 1;

        }
        //for each triangle,check whether Mr,Fy Lies inside of its region

        return pointLocation;
    }

    //maximum reinforcement
    public static double MaxReinforcement(Member3 m) {
        double as1 = m.getAs1();
        double as2 = m.getAs2();
        double h = m.getH();
        double b = m.getB();
        double As = as1 + as2;
        double Asmax = 0.04 * b * h - (as1 + as2);
        double c = (As / Asmax) - 1;
        if (c < 1) {
            c = 0;
        } else {
        }
        return c;
    }

    //minimum reinforcement
    public static double MinReinforcement(Member3 m) {
        double as1 = m.getAs1();
        double as2 = m.getAs2();
        double h = m.getH();
        double b = m.getB();
        double hp = m.getHp();
        double d = h - hp;
        double As = as1 + as2;
        double Asmin = Math.max((0.1 * m.maxAx / (m.fyk / 1.15)), (0.002 * 0.04 * b * h - As));
        double c = (Asmin / As) - 1;
        if (c < 1) {
            c = 0;
        } else {
        }
        return c;
    }

    public static double biaxialPenalty(Member3 m, int typ) {

        double biaxialPenalty = 0.0;
        double biaxialEfficiency = 0.0;
        //no biaxial force exists on that member, then penalty =0.0
        if ((Math.abs(m.maxMy) < 1.0) && (Math.abs(m.maxMz) < 1.0) && (Math.abs(m.minMz) < 1.0) && (Math.abs(m.minMz) < 1.0)) {
            biaxialPenalty = 0.0;
        } else {
            if (typ == 0) {
                biaxialEfficiency = biaxialEfficiency(m, 0);
            } else if (typ == 1) {
                biaxialEfficiency = biaxialEfficiency(m, 1);
            } else if (typ == 2) {
                biaxialEfficiency = biaxialEfficiency(m, 2);
            } else {
                biaxialEfficiency = biaxialEfficiency(m, 3);
            }

            if (biaxialEfficiency > 1.0) {
                //section has failed

                biaxialPenalty = 4.0;
            } else {
                // has (100-eff%)left in capacity
                biaxialPenalty = (1 - biaxialEfficiency) * 0.70;
            }

        }
        return biaxialPenalty;
    }

//    public static void main(String[] args) throws IOException {
//        double b = 500;
//        double h = 500;
//        double fcd = 11.33;
//        double fyd = 260.86;
//        double ecm = 0.0035;
//        double ax = 400;
//        double mx = 0, my = 20;
//        final XYSeries fitVSgen = new XYSeries("axial vs. res. moment", false, false);
//        final XYSeries loadings = new XYSeries("internal actions", false, false);
//        double mr = Math.sqrt(mx * mx + my * my);
//        loadings.add(mr, ax);
//        final XYSeriesCollection dataset = new XYSeriesCollection();
//        dataset.addSeries(loadings);
//        dataset.addSeries(fitVSgen);
//
//        System.out.println("mr: " + mr + " ax: " + ax);
//        angle = Math.atan(mx / my) * 180.0 / Math.PI;
////        dx = (int) (b * 0.5);
////        dy = (int) (h * 0.45);
//        System.out.println("angle: " + angle);
//        Section sect = new Section(3, 300, b, h);
//        setSectionParameters(sect);
//
//        System.out.println("Main re:" + sect.reBar.main_num + "d" + sect.reBar.main_dia);
//        System.out.println("ExtraX re:" + sect.reBar.extra_x_num + "d" + sect.reBar.extra_x_dia);
//        System.out.println("ExtraY re:" + sect.reBar.extra_y_num + "d" + sect.reBar.extra_y_dia);
//        LinkedList<Point2D> coord = interactionCoordinates(sect, b, h, angle, fyd, fcd, ecm);
//        double eff = biaxialEfficiency(sect, b, h, fyd, fcd, ax, mx, my);
//        System.out.println("efficiency: " + eff);
//        for (int i = 0; i < coord.size(); i++) {
//            fitVSgen.add(coord.get(i).x, coord.get(i).y);
//        }
//        final DrawGraph fitg = new DrawGraph("Biaxial Interaction", dataset, "M Vs. A", "moment", "axial F.");
//        fitg.pack();
//        RefineryUtilities.centerFrameOnScreen(fitg);
//        fitg.setVisible(true);
//
//    }

}
