/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package UI;

/**
 *
 * @author user
 */
public class Point3D {
   public double x, y, z;
   public int pxlx, pxly, pxlz;
   public Point3D( double X, double Y, double Z ) {
      x = X;  y = Y;  z = Z;
   }
   
   public String Print(){
       return "X= "+x+" Y= "+y+" Z= "+z;
   }
}
