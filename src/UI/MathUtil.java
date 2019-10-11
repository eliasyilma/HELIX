/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import static java.lang.Math.pow;
import org.apache.commons.math3.linear.RealMatrix;

/**
 *
 * @author user
 */
public class MathUtil {

    public void printMatrix(RealMatrix m) {
        for (int i = 0; i < m.getRowDimension(); i++) {
            for (int j = 0; j < m.getColumnDimension(); j++) {
                System.out.printf("%12.2f", m.getEntry(i, j));
            }
            System.out.println();
        }
    }

    public static boolean pointInsideTriangle(interTriangle tr, interPoint p) {
        boolean insideTriangle;
        interPoint a = tr.a;
        interPoint b = tr.b;
        interPoint c = tr.c;
        double s = a.y * c.x - a.x * c.y + (c.y - a.y) * p.x + (a.x - c.x) * p.y;
        double t = a.x * b.y - a.y * b.x + (a.y - b.y) * p.x + (b.x - a.x) * p.y;
        if ((s < 0) != (t < 0)) {
            return false;
        }
        double A = -a.y * c.x + a.y * (c.x - b.x) + a.x * (b.y - c.y) + b.x * c.y;
        if (A < 0.0) {
            s = -1.0 * s;
            t = -1.0 * t;
            A = -1.0 * A;
        }
        return s > 0 && t > 0 && (s + t) <= A;
    }

    public static double areaTriangle(double x1, double y1, double x2, double y2, double x3, double y3) {
        return Math.abs((x1 * (y2 - y3)) + x2 * (y3 - y1) + x3 * (y1 - y2));
    }

    public static boolean isInsideTriangle(interTriangle trigon, interPoint point) {
        return isInsideTriangle(trigon.a, trigon.b, trigon.c, point);
    }
    public static boolean isInsideTriangle(interPoint p1, interPoint p2, interPoint p3, interPoint p) {
        double x1 = p1.x;
        double y1 = p1.y;
        double x2 = p2.x;
        double y2 = p2.y;
        double x3 = p3.x;
        double y3 = p3.y;
        double xp = p.x;
        double yp = p.y;
        double A = areaTriangle(x1, y1, x2, y2, x3, y3);
        double A1 = areaTriangle(xp, yp, x2, y2, x3, y3);
        double A2 = areaTriangle(x1, y1, xp, yp, x3, y3);
        double A3 = areaTriangle(x1, y1, x2, y2, xp, yp);
        return (Math.abs(A - (A1 + A2 + A3)) < 0.00001);
    }

    // this is a duplicate
    public static int binary_to_decimal(byte[] bin) {
        int decimal = 0;
        int j = bin.length - 1;
        for (int i = 0; i < bin.length; i++) {
            decimal = (int) (decimal + (pow(2, j) * bin[i]));
            j--;
        }
        return decimal;
    }

    public static void main(String[] args) {
        interPoint p1 = new interPoint(0, 0);
        interPoint p2 = new interPoint(-5, 0);
        interPoint p3 = new interPoint(-2.5, -2.5);
        interPoint p = new interPoint(-2.6, -1.1);
        interTriangle test = new interTriangle(p1, p2, p3);
        System.out.println("inside: " + pointInsideTriangle(test, p));
        System.out.println("inside: " + isInsideTriangle(p1,p2,p3,p));
        p1 = new interPoint(0, 1377.74);
        p2 = new interPoint(-66.219, -178.241);
        p3 = new interPoint(84.86, -88.59);
        p = new interPoint(75, 0);
        System.out.println("inside: " + isInsideTriangle(p1,p2,p3,p));

    }
}
