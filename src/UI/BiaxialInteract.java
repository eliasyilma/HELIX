/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;


import java.awt.Point;
import java.io.IOException;
import java.util.LinkedList;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author CASE
 */
public class BiaxialInteract {

    public static double Ned;
    public static double Mz;
    public static double My;
    public Section sect;
    public static double ecm = 0.0035;
    public static double E = 200000000.0;
    public static double thetax;
    public static final double a0 = 0;
    public static final double a90 = Math.PI / 2.0;
    public static final double a180 = Math.PI;
    public static final double a270 = Math.PI * 3.0 / 2.0;

    public void computeFcArea() {

    }

    public void computeFcCentroid() {

    }

    public static void computeNAAngle() {
        if (Math.abs(My) < 0.0001) {
            thetax = 0.0;
        } else if (Math.abs(Mz) < 0.0001 && My > 0.0) {
            thetax = a90;
        } else if (Math.abs(Mz) < 0.0001 && My < 0.0) {
            thetax = a270;
        } else {
            thetax = Math.atan(My / Mz);
        }
    }

    public double computeStrainHeight() {
        double b = sect.Breadth;
        double h = sect.Height;
        double strainHeight;
        double hypotenuse = Math.sqrt(h * h + b * b);
        double thetabh = Math.atan(h / b);
        double thetai;
        if ((Math.PI / 2.0 - thetax) > thetabh) {
            thetai = (Math.PI / 2.0 - thetax) - thetabh;
        } else {
            thetai = thetabh - (Math.PI / 2.0 - thetax);
        }

        strainHeight = hypotenuse * Math.cos(thetai);
        return strainHeight;
    }

    public static double reinforcementStrain(double distToNA, double NADepth) {
        double es;
        es = distToNA * ecm / NADepth;
        return es;
    }

    public static double reinforcementStress(double es, double fyd) {
        double eyd = fyd * 1000.0 / E;
        double fs;
        if (es > eyd) {
            fs = fyd;
        } else if (es < -eyd) {
            fs = -fyd;
        } else {
            fs = E * es / 1000.0;
        }
        return fs;
    }

    public static int reinRelPosition(Point2D p1, Point2D p2, Point2D rebar) {
        double m = (p2.y - p1.y) / (p2.x - p1.x);
        double b = p1.y - p1.x * m;
        double yval = m * rebar.x + b;
        if (rebar.y > yval) {
            return 1;
        } else {
            return -1;
        }
    }

    public static double NA_Dist(Point2D p1, Point2D p2, Point2D pp) {
        double m1 = (p2.y - p1.y) / (p2.x - p1.x);
        double m2 = -1 / m1;
        double b2 = pp.y - (pp.x * m2);
        double b1 = p1.y - p1.x * m1;
        double xi = (b1 - b2) / (m2 - m1);
        double yi = xi * m1 + b1;
        double dist = Math.sqrt((pp.x - xi) * (pp.x - xi) + (pp.y - yi) * (pp.y - yi));
        return dist;
    }

    public static double Dist(Point2D p1, Point2D p2) {
        double x1 = p1.x, y1 = p1.y, x2 = p2.x, y2 = p2.y;
        double length = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        return length;
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

    public static Point2D polygCentroid(Point2D[] points) {
        double centroidX = 0, centroidY = 0;

        int numPoints = points.length;
        int j = numPoints - 1;  // The last vertex is the 'previous' one to the first
        double area = 0;         // Accumulates area in the loop

        for (int i = 0; i < numPoints; i++) {
            area = area + (points[j].x + points[i].x) * (points[j].y - points[i].y);
            centroidX = centroidX + (points[j].x + points[i].x) * (points[j].x * points[i].y - points[i].x * points[j].y);
            centroidY = centroidY + (points[j].y + points[i].y) * (points[j].x * points[i].y - points[i].x * points[j].y);
            j = i;  //j is previous vertex to i
        }
        Point2D cent = new Point2D(centroidX / (6 * area), centroidY / (6 * area));
        return cent;
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

    public static double[] MaxAndMinForces(Section sect, double fc, double fyd) {

        double Afc = sect.Breadth * sect.Height * fc;
        double As = 0;
//        for (int i = 0; i < sect.reBar.mainCoor.length; i++) {
//            As += sect.reBar.main_area * fyd;
//        }
//        for (int i = 0; i < sect.reBar.extraCoorX.length; i++) {
//            As += sect.reBar.extra_x_area * fyd;
//
//        }
//        for (int i = 0; i < sect.reBar.extraCoorY.length; i++) {
//            As += sect.reBar.extra_y_area * fyd;
//
//        }
        double Amax = (As + Afc) / 1000.0;
        double Amin = -As / 1000.0;
        double[] A = {Amin, Amax};
        return A;
    }

    public static double[] reinforcementForcesAndMoments(Section sect, Point2D p1, Point2D p2, Double NA_len, Double fyd, Point2D center) {
        double tsx = 0, csx = 0, fs = 0, cs = 0, mx = 0, my = 0;
        for (int i = 0; i < sect.reBar.mainCoor.length; i++) {
            //get the rebar
            Point2D rebar = sect.reBar.mainCoor[i];
            //compute the perpendicular distance between the rebar and the NA
            double dist = NA_Dist(p1, p2, rebar);
            double distX = rebar.x - center.x;
            double distY = rebar.y - center.y;
            //checks if the rebar is in the compression or tension regions
            int sign = reinRelPosition(p1, p2, rebar);
            //compute the strain
            double strain = sign * reinforcementStrain(dist, NA_len);
            //compute the stress
            double stress = reinforcementStress(strain, fyd);
            //compute the force developed
            double force = stress * sect.reBar.main_area;
//            System.out.println("dist:   " + dist + " strain: " + strain + " stress: " + stress + " force: " + force);
//            System.out.println("sign: " + sign);
            fs += force;
            mx += force * distY;
            my += force * distX;

        }
        for (int i = 0; i < sect.reBar.extraCoorX.length; i++) {
            //get the rebar
            Point2D rebar = sect.reBar.extraCoorX[i];
            //compute the perpendicular distance between the rebar and the NA
            double dist = NA_Dist(p1, p2, rebar);
            double distX = rebar.x - center.x;
            double distY = rebar.y - center.y;
            //checks if the rebar is in the compression or tension regions
            int sign = reinRelPosition(p1, p2, rebar);
            //compute the strain
            double strain = sign * reinforcementStrain(dist, NA_len);
            //compute the stress
            double stress = reinforcementStress(strain, fyd);
            //compute the force developed
            double force = stress * (sect.reBar.extra_x_areaB+sect.reBar.extra_x_areaT);
//            System.out.println("dist:   " + dist + " strain: " + strain + " stress: " + stress + " force: " + force);
//            System.out.println("sign: " + sign);
            fs += force;
            mx += force * distY;
            my += force * distX;

        }
        for (int i = 0; i < sect.reBar.extraCoorY.length; i++) {
            //get the rebar
            Point2D rebar = sect.reBar.extraCoorY[i];
            //compute the perpendicular distance between the rebar and the NA
            double dist = NA_Dist(p1, p2, rebar);
            double distX = rebar.x - center.x;
            double distY = rebar.y - center.y;
            //checks if the rebar is in the compression or tension regions
            int sign = reinRelPosition(p1, p2, rebar);
            //compute the strain
            double strain = sign * reinforcementStrain(dist, NA_len);
            //compute the stress
            double stress = reinforcementStress(strain, fyd);
            //compute the force developed
            double force = stress * (sect.reBar.extra_y_areaL+sect.reBar.extra_y_areaR);
//            System.out.println("dist:   " + dist + " strain: " + strain + " stress: " + stress + " force: " + force);
//            System.out.println("sign: " + sign);
            fs += force;
            mx += force * distY;
            my += force * distX;
        }

        double[] forcesAndMoments = {fs, mx, my};
        return forcesAndMoments;
    }

    public static LinkedList<Point2D> interactionCoordinatesMild(Section sect, double theta, double fyd, double fcd) {

        double b = sect.Breadth, h = sect.Height;
        Point2D center = new Point2D(b / 2, h / 2);
        double n = 20;
        double x = 0;
        double tant = Math.tan(theta);
        double maxX = b + h / tant;
        double step = maxX / n;
        Point2D[] vertices;
        Point2D[] vert = null;
        double fc, mfcx, mfcy;
        x = step;
        LinkedList<Point2D> coordinates = new LinkedList<>();
        double[] Amaxmin = MaxAndMinForces(sect, fcd, fyd);
        coordinates.add(new Point2D(0, Amaxmin[1]));
        System.out.println("h/tant" + (h / tant));
        while (x < b + Math.floor(h / tant)) {

            double y = tant * x;
            if (x < b) {
                vertices = new Point2D[5];
                vertices[0] = new Point2D(0, y);
                vertices[1] = new Point2D(x, 0);
                vertices[2] = new Point2D(b, 0);
                vertices[3] = new Point2D(b, h);
                vertices[4] = new Point2D(0, h);

            } else if (x > b && y < h) {
                vertices = new Point2D[4];
                vertices[0] = new Point2D(0, y);
                vertices[1] = new Point2D(b, (x - b) * (y / x));
                vertices[2] = new Point2D(b, h);
                vertices[3] = new Point2D(0, h);

            } else {
                vertices = new Point2D[3];
                vertices[0] = new Point2D((y - h) * x / y, h);
                vertices[1] = new Point2D(b, (x - b) * (y / x));
                vertices[2] = new Point2D(b, h);

            }
            vert = vertices;
            double area = polyArea(vertices);
            Point2D centroid = polyCentroid(vertices);
            //compute total compression force in concrete
            fc = area * fcd;
            double rx = centroid.x - center.x;
            double ry = centroid.y - center.y;

            mfcx = fc * ry;
            mfcy = fc * rx;

            //compute the distance b/n (b,h) and the NA for the purpose of getting the
            //rotated neutral depth magnitude
            double NA_len = NA_Dist(vertices[0], vertices[1], vertices[2]);
            double[] FandM = reinforcementForcesAndMoments(sect, vertices[0], vertices[1], NA_len, fyd, center);
            System.out.println("marm: " + Math.sqrt(rx * rx + ry * ry) + " area: " + area + " x: " + x + " y: " + y + " fcx: " + centroid.x + " fcy: " + centroid.y + " mfcx: " + mfcx + " mfcy: " + mfcy);

            double A = (FandM[0] + fc) / 1000.0;
            double Mx = (mfcx + FandM[1]) / 1000000.0;
            double My = (mfcy + FandM[2]) / 1000000.0;
            double M = Math.sqrt(Mx * Mx + My * My);
            coordinates.add(new Point2D(M, A));
            x += step;
        }
        coordinates.add(new Point2D(0.1, Amaxmin[0]));
        return coordinates;
    }

    public static LinkedList<Point2D> interactionCoordinatesSteep(Section sect, double theta, double fyd, double fcd) {

        double b = sect.Breadth, h = sect.Height;
        Point2D center = new Point2D(b / 2, h / 2);
        double n = 40;
        double tant = Math.tan(theta);
        double maxX = b + h / tant;
        double step = maxX / n;
        Point2D[] vertices;
        Point2D[] vert = null;
        double fc, mfcx, mfcy;
        double x = step;
        LinkedList<Point2D> coordinates = new LinkedList<>();
        double[] Amaxmin = MaxAndMinForces(sect, fcd, fyd);
        coordinates.add(new Point2D(0, Amaxmin[1]));

        while (x < b + h / tant) {
            double y = tant * x;
            if (y <= h) {
                vertices = new Point2D[5];
                vertices[0] = new Point2D(0, y);
                vertices[1] = new Point2D(x, 0);
                vertices[2] = new Point2D(b, 0);
                vertices[3] = new Point2D(b, h);
                vertices[4] = new Point2D(0, h);
                System.out.println("a");

            } else if (x < b && y > h) {
                vertices = new Point2D[4];
                vertices[0] = new Point2D((y - h) * x / y, h);
                vertices[1] = new Point2D(x, 0);
                vertices[2] = new Point2D(b, 0);
                vertices[3] = new Point2D(b, h);
                System.out.println("b");

            } else {
                vertices = new Point2D[3];
                vertices[0] = new Point2D((y - h) * x / y, h);
                vertices[1] = new Point2D(b, (x - b) * (y / x));
                vertices[2] = new Point2D(b, h);
                System.out.println("c");

            }
            vert = vertices;
            double area = polyArea(vertices);
            Point2D centroid = polyCentroid(vertices);
            //compute total compression force in concrete
            fc = area * fcd;
            double rx = centroid.x - center.x;
            double ry = centroid.y - center.y;

            mfcx = fc * ry;
            mfcy = fc * rx;

            //compute the distance b/n (b,h) and the NA for the purpose of getting the
            //rotated neutral depth magnitude
            double NA_len = NA_Dist(vertices[0], vertices[1], vertices[2]);
  //          System.out.println("dist from center:" + Math.sqrt(rx * rx + ry * ry));

            double[] FandM = reinforcementForcesAndMoments(sect, vertices[0], vertices[1], NA_len, fyd, center);

            double A = (FandM[0] + fc) / 1000.0;
            double Mx = (mfcx + FandM[1]) / 1000000.0;
            double My = (mfcy + FandM[2]) / 1000000.0;
            double M = Math.sqrt(Mx * Mx + My * My);
            coordinates.add(new Point2D(M, A));
            System.out.println("reAx: "+FandM[0]+" reMx: " + FandM[1]+" reMy: "+FandM[2]);
            System.out.println( " mfcx: " + mfcx + " mfcy: " + mfcy+ " A: "+A+" M: "+M);
            x += step;//"marm: " + Math.sqrt(rx * rx + ry * ry) + " area: " + fc + " rx: " + rx + " ry: " + ry  +
        }
        coordinates.add(new Point2D(0.1, Amaxmin[0]));
        return coordinates;

    }

    public static void main(String[] args) throws IOException {
        final XYSeries fitVSgen = new XYSeries("first", false, false);
        LinkedList<Point2D> points = new LinkedList<>();
        Section sect = new Section(14,300, 200, 400);
        double b = sect.Breadth, h = sect.Height;
        double n = 20;
        double fyd = 260.86;
        double fcd = 11.33;
        double mx = 400, my = 100;
        double rfc;
        Point2D center = new Point2D(b / 2.0, h / 2.0);
        double theta = Math.atan(mx / my);
        double thetabh = Math.atan(h / b);
        double x = 0, y = 0;
        double step = b / n;
        Point2D centroid;
        double area;
        double fc, mfcx, mfcy;
        System.out.println("eyd: " + fyd / E);
        System.out.println("[thetabh: " + thetabh * 180.0 / Math.PI + "]" + " [theta: " + theta * 180.0 / Math.PI + "]");
        if (thetabh > theta) {
            points = interactionCoordinatesMild(sect, theta, fyd, fcd);
        } else {
            points = interactionCoordinatesSteep(sect, theta, fyd, fcd);

        }
        for (int i = 0; i < points.size(); i++) {
            fitVSgen.add(points.get(i).x, points.get(i).y);
        }
//        final DrawGraph fitg = new DrawGraph("Biaxial Interaction", fitVSgen, "M Vs. A", "moment", "axial F.");
//        fitg.pack();
//        RefineryUtilities.centerFrameOnScreen(fitg);
//        fitg.setVisible(true);
        System.out.println("M");
        for (int k = 0; k < points.size(); k++) {
            System.out.println(points.get(k).x);
        }

        System.out.println("A");
        for (int k = 0; k < points.size(); k++) {
            System.out.println(points.get(k).y);
        }

    }
}
