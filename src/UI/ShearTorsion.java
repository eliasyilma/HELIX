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
public class ShearTorsion {

    //get size, fck,fyk,As
    public static double ShearTorsionInteract(double b, double h, double fck, double fyk, double As, double Ted, double Ved) {
        double efficiency;
        //get equivalent hollow section thickness using t=A(area)/u(parameter)
        float Area = (float) (b * h);
        float Parameter = (float) (b * 2 + h * 2);
        float t = Area / Parameter;
        //calculate the equivalent sections area: Ak=(b-t)(h-t)
        float Ak = (float) ((b - t) * (h - t));
        //nu=(1-fck/250)*0.7
        float nu = (float) ((1 - fck / 250) * 0.7);
        //assume theta=26.56
        float theta = (float) (26.56f * Math.PI / 180.0f);
        //compute z: 
        //z=(1-0.416x/d)*d
        double z = 0.9 * (h - 40);
        //acw=1
        //Vrdmax= acw*b*z*nu*fcd/(cot+tan)
        float fcd = (float) (0.85 * fck / 1.5);
        float Vrdmax = (float) (b * z * nu * fcd / (Math.tan(theta) + (1 / Math.tan(theta)))) / 1000.0f;
//        System.out.println("z: "+z+" nu: "+nu+" Ak: "+Ak+" t: "+t);
//        System.out.println("Vrdmax: " + Vrdmax);
        //Trdmax=2*nu*fcd*Ak*t*sin*cos
        float Trdmax = (float) (2 * nu * fcd * Ak * t * Math.sin(theta) * Math.cos(theta)) / 1000000.0f;
//        System.out.println("Trdmax: " + Trdmax);
//efficiency
        //get the intersection point between:
        //the line from (0,0) to (Ted,Ved)
        //the line from (0,Tmax) to (Vmax,0)
        //0.1 is added to denominators to avoid division by 0.
        double Vinter = Trdmax / ((Trdmax / Vrdmax) + (Ted / (Ved + 0.1)));
        double Tinter = Vinter * Ted / (Ved + 0.1);
        double dInter = Math.sqrt(Vinter * Vinter + Tinter * Tinter);
        double dEd = Math.sqrt(Ted * Ted + Ved * Ved);
        efficiency = dEd / dInter;
        return efficiency;
    }

    public static double ShearTorsionInteract(Member3 mem) {
        double effY = ShearTorsionInteract(mem.getB() * 1000.0, mem.getH() * 1000.0, mem.getFck(), mem.getFyk(), mem.getXsec().AsY, Math.abs(mem.maxMx), Math.abs(mem.maxMy));
        double effZ = ShearTorsionInteract(mem.getH() * 1000.0, mem.getB() * 1000.0, mem.getFck(), mem.getFyk(), mem.getXsec().AsZ, Math.abs(mem.maxMx), Math.abs(mem.maxMz));
        System.out.println("shearTorsionY: " + effY);
        System.out.println("shearTorsionZ: " + effZ);
        return effY + effZ;
    }

    public static double ShearTorsionPenalty(Member3 mem) {
        double penY, penZ;
        if (Math.abs(mem.maxMx) < 5.0) {
            penY = 0;
            penZ = 0;
        } else {
            double effY = ShearTorsionInteract(mem.getB() * 1000.0, mem.getH() * 1000.0, mem.getFck(), mem.getFyk(), mem.getXsec().AsY, Math.abs(mem.maxMx), Math.abs(mem.maxMy));
            double effZ = ShearTorsionInteract(mem.getH() * 1000.0, mem.getB() * 1000.0, mem.getFck(), mem.getFyk(), mem.getXsec().AsZ, Math.abs(mem.maxMx), Math.abs(mem.maxMz));
            mem.STEff = effZ;
            if (effY > 1.0) {
                penY = 1.5;
                mem.STEff = 1.00;
            } else {
                mem.STEff = effY;
                penY = 1 - effY;
            }
            if (effZ > 1.0) {
                mem.STEff = (mem.STEff + 1.0) / 2;
                penZ = 1.5;
            } else {
                mem.STEff = (mem.STEff + effZ) / 2;
                penZ = 1 - effZ;
            }
        }
        //       System.out.println("STY:"+penY+"STZ"+penZ);
        return (penY + penZ) * 0.5;
    }

    public static void main(String[] args) {
//        check should be done for both directions in shear
        double eff = ShearTorsionInteract(300, 500, 30, 460, 1500, 20, 340);
        System.out.println(eff);
    }
}
