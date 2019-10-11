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
public class TorsionResist {

    public static double torsionResistance(double b, double h, double fck, double fyk, double As) {
        //get equivalent hollow section thickness using t=A(area)/u(parameter)
        float Area = (float) (b * h);
        float Perimeter = (float) (b * 2 + h * 2);
        float t = Area / Perimeter;
        //calculate the equivalent sections area: Ak=(b-t)(h-t)
        float Ak = (float) ((b - t) * (h - t));
        //nu=(1-fck/250)*0.7
        float nu = (float) ((1 - fck / 250) * 0.7);
        //assume theta=26.56
        float theta = (float) (26.56f * Math.PI / 180.0f);
        //compute z: 
        double xod = 1.895 * fyk * As / (fck * b * (h - 45));
        //z=(1-0.416x/d)*d
        double z = (1 - 0.416 * xod) * (h - 45);
        //acw=1
        //Vrdmax= acw*b*z*nu*fcd/(cot+tan)
        float fcd = (float) (0.85 * fck / 1.5);
        //Trdmax=2*nu*fcd*Ak*t*sin*cos
        float Trdmax = (float) (2 * nu * fcd * Ak * t * Math.sin(theta) * Math.cos(theta)) / 1000000.0f;
//efficiency
        //get the intersection point between:
        //the line from (0,0) to (Ted,Ved)
        //the line from (0,Tmax) to (Vmax,0)

        return Trdmax;
    }

    public static double torsionEfficiency(double Ted, double b, double h, double fck, double fyk, double S, double Asv, double As) {
        double efficiency;
        double Trdsmax = torsionResistance(b, h, fck, fyk, As);
        efficiency = Math.abs(Ted) / Trdsmax;
        return efficiency;
    }

    public static double torsionEfficiency(Member3 m1, double Ted) {
        double efficiency;
        double Trdsmax = torsionResistance(m1.getB()*1000.0, m1.getH()*1000.0, m1.getFck(), m1.getFyk(),m1.getXsec().AsY);
        efficiency = Math.abs(Ted) / Trdsmax;
        return efficiency;
    }

    public static double torsionPenalty(Member3 m) {
        //cases
        double torsionPenalty = 0.0;
        double torsionEfficiency = 0.0;
        //no torsion exists on that member, then penalty =0.0
        if (Math.abs(m.maxMx) < 10.0) {
            torsionPenalty = 0.0;
            m.TorsionEff = 1.0;

        } else {
            torsionEfficiency = torsionEfficiency(m, Math.abs(m.maxMx));
            if (torsionEfficiency > 1.0) {
                //Ted> Trdmax penalty =2.0=> section has failed
                torsionPenalty = 1.5;
                m.TorsionEff = 0.0;
            } else {
                //Ted< Trdmax penalty =(1-efficiency) => has (100-eff%)left in capacity
                torsionPenalty = 1 - torsionEfficiency;
                m.TorsionEff = torsionEfficiency;
            }

        }
//        System.out.println("TorsionEff: "+torsionEfficiency);
//        System.out.println("Torsion:" + torsionPenalty);
        return torsionPenalty;
    }

}
