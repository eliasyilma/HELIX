/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package UI;

import SAM3D.Member3;

/**
 *
 * @author user
 */
public class VectorUtil3D {
    public static double closestX3D(double x3, double y3, Member3 member) {
    double x1 =member.getStart().getPxlx();
    double y1 =member.getStart().getPxly();
    double x2 =member.getEnd().getPxlx();
    double y2 =member.getEnd().getPxly();
    double x;

    double u = ((x3-x1)*(x2-x1) + (y3-y1)*(y2-y1))/((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
    if (u<0) {
      x = x1;
    }
    else if (u>1) {
      x = x2;
    }
    else {
      x = x1 + u*(x2-x1);
    }
    return x;
  }


  /** returns y coordinate on line perpendicular to point (if beyond line, then returns
      coordinates of end joint)
   */
  public static double closestY3D(double x3, double y3, Member3 member) {
    double x1 =member.getStart().getPxlx();
    double y1 =member.getStart().getPxly();
    double x2 =member.getEnd().getPxlx();
    double y2 =member.getEnd().getPxly();
    double y;

    double u = ((x3-x1)*(x2-x1) + (y3-y1)*(y2-y1))/((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
    if (u<0) {
      y = y1;
    }
    else if (u>1) {
      y = y2;
    }
    else {
      y = y1 + u*(y2-y1);
    }
    return y;
  }

public static double shortestDistancePointToLine3D(double x3, double y3, Member3 member) {
    double x = closestX3D(x3,y3,member);
    double y = closestY3D(x3,y3,member);

    double distance = Math.sqrt((x3-x)*(x3-x) + (y3-y)*(y3-y));
    return distance;
  }
}
