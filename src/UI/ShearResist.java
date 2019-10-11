/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import SAM3D.Member3;
import static UI.TorsionResist.torsionEfficiency;

/**
 *
 * @author user
 */
public class ShearResist {

    public static double shearResistance(double b, double h, double fck, double fyk, double S, double Asv, double As) {
        double Vrds;
        double nu = 0.7 * (1 - fck / 250);
        double fcd = 0.85 * fck / 1.5, fyd = fyk / 1.15;
        double z = 0.85 * h * 1000;
        double theta = Math.asin(Math.sqrt(Asv * fyd / (1000.0 * b * S * nu * fcd)));
//        float Vrdmax = (float) ((b * z * nu * fcd * 1000.0f) / (Math.tan(theta) + (1 / Math.tan(theta))));
        Vrds = ((Asv / S) * z * fyd * (1 / Math.tan(theta))) / 1000.0;
        return Vrds;
    }

    public static double shearSpReqd(double Ved, double Ted, double b, double h, double fck, double fyk, double S, double Asv, double As) {
        double Vrds, Vrdmax, Trdmax;
        float Area = (float) (b * h);
        float Perimeter = (float) (b * 2 + h * 2);
        float t = Area / Perimeter;
        //calculate the equivalent sections area: Ak=(b-t)(h-t)
        float Ak = (float) ((b - t) * (h - t));
        //nu=(1-fck/250)*0.7
        float nu = (float) ((1 - fck / 250) * 0.6);
        double theta;
        double d = h - 45;
        Vrdmax = 0.124 * b * d * (1 - fck / 250) * fck;
        if (Vrdmax >= Ved * 1000.0) {
            theta = 22 * Math.PI / 180.0;
        } else {
            theta = 0.5 * Math.asin(Ved / (0.18 * b * d * fck * (1 - (fck / 250))));
            if (theta > 0.25 * Math.PI) {
                theta = 0.25 * Math.PI;
            }
        }
        Trdmax = 1.33 * nu * fck * t * Ak / (Math.tan(theta) + (1 / Math.tan(theta)));
        double Vratio = Ved * 1000.0 / (0.78 * d * fyk * (1 / Math.tan(theta)));
        double Tratio = Ted * 1000000.0 / (0.87 * 2 * Ak * fyk * (1 / Math.tan(theta)));
        double minratio = 0.08 * Math.sqrt(fck) * b / fyk + Tratio;
        double minSp = Asv / minratio;
        double total = Vratio + Tratio;
        double Spreqd = Asv / total;
        double Smax = Math.min(600, 0.75 * d);
        //if the required spacing is greater than, the max. allowable spacing 
        if (Spreqd > Smax) {
            // set the max. spacing value as the required spacing
            Spreqd = Smax;
        }
        //check for shear-torsion interaction
        double STInter = (Ved / Vrdmax) + (Ted / Trdmax);
        Spreqd = (((int) Spreqd) / 10) * 10;
//        System.out.println("VRDMAX: " + Vrdmax + "VED: " + Ved);
//        System.out.println("REQD: " + Spreqd + "PROV: " + S);
        //if provided spacing < required spacing ---> uneconomical , penalize
        //if provided spacing > required spacing ---> section will fail in shear, penalize
        double efficiency = S / Spreqd;
        if (STInter > 1.0 || S > minSp) {
            efficiency = 2.0;
        }
        return efficiency;
    }

    public static double shearPenaltyY(Member3 mem, double VedY, double Ted, int loc) {
        double efficiencyY;
//        efficiencyY = shearSpReqd(VedY, Ted, mem.getH() * 1000.0, mem.getB() * 1000.0, mem.getFck(), mem.getFyk(), mem.getXsec().shearS, mem.getXsec().Asv, mem.getXsec().AsY);
//        double VL = Math.abs(mem.shearYCoords.get(0).y);
//        double VR = Math.abs(mem.shearYCoords.get(mem.shearYCoords.size() - 1).y);
        if (loc == 0) {
            efficiencyY = shearSpReqd(VedY, Ted, mem.getH() * 1000.0, mem.getB() * 1000.0, mem.getFck(), mem.getFyk(), mem.getXsecNegLeft().shearS, mem.getXsecNegLeft().Asv, mem.getXsecNegLeft().AsY);
        } else if (loc == 1) {
            efficiencyY = shearSpReqd(VedY, Ted, mem.getH() * 1000.0, mem.getB() * 1000.0, mem.getFck(), mem.getFyk(), mem.getXsecPos().shearS, mem.getXsecPos().Asv, mem.getXsecPos().AsY);
        } else {
            efficiencyY = shearSpReqd(VedY, Ted, mem.getH() * 1000.0, mem.getB() * 1000.0, mem.getFck(), mem.getFyk(), mem.getXsecNegRight().shearS, mem.getXsecNegRight().Asv, mem.getXsecNegRight().AsY);
        }
        //       efficiencyY = (efficiencyYL + efficiencyYM + efficiencyYR) / 3.0;
        if (efficiencyY > 1.0) {
            mem.shearYEff = 0.0;
        } else {
            mem.shearYEff = efficiencyY;
        }
        double penaltyY = sPen(efficiencyY);
        return penaltyY;
    }

    public static double minShearSpacing(Section sect,double fck,double fyk) {
        double minratio = 0.08 * Math.sqrt(fck) * sect.Breadth / fyk;
        double minSp = sect.Asv / minratio;
        if(minSp>200) minSp=200;
        return ((double) (((int) minSp) / 10 * 10));
    }

    public static double shearPenaltyZ(Member3 mem, double VedZ, double Ted) {
        double efficiencyZ;
        //efficiencyZ = shearSpReqd(VedZ, Ted, mem.getB() * 1000.0, mem.getH() * 1000.0, mem.getFck(), mem.getFyk(), mem.getXsec().shearS, mem.getXsec().Asv, mem.getXsec().AsZ);
        double efficiencyZL = shearSpReqd(VedZ, Ted, mem.getH() * 1000.0, mem.getB() * 1000.0, mem.getFck(), mem.getFyk(), mem.getXsecNegLeft().shearS, mem.getXsecNegLeft().Asv, mem.getXsecNegLeft().AsY);
        double efficiencyZM = shearSpReqd(VedZ, Ted, mem.getH() * 1000.0, mem.getB() * 1000.0, mem.getFck(), mem.getFyk(), mem.getXsecPos().shearS, mem.getXsecPos().Asv, mem.getXsecPos().AsY);
        double efficiencyZR = shearSpReqd(VedZ, Ted, mem.getH() * 1000.0, mem.getB() * 1000.0, mem.getFck(), mem.getFyk(), mem.getXsecNegRight().shearS, mem.getXsecNegRight().Asv, mem.getXsecNegRight().AsY);

        efficiencyZ = (efficiencyZL + efficiencyZM + efficiencyZR) / 3.0;
        if (efficiencyZ > 1.0) {
            mem.shearZEff = 0.0;
        } else {
            mem.shearZEff = efficiencyZ;
        }
        double penaltyZ = sPen(efficiencyZ);
        return penaltyZ;
    }

    public static double columnShearPenalty(Member3 mem) {
        double SL = mem.getXsecNegLeft().shearS;
        double SM = mem.getXsecPos().shearS;
        double SR = mem.getXsecNegRight().shearS;
        double SPen = 0.0;
        double SMax = 0.0;
        //get the maximum
        //check the maximum

        if (SL < 240 && SL < 0.6 * (Math.min(mem.getXsecNegLeft().Breadth, mem.getXsecNegLeft().Height)) && SL < 12 * Math.min(mem.getXsecNegLeft().reBar.main_dia, mem.getXsecNegLeft().reBar.extra_x_diaT)) {
            if (SL > SM && SM > SR) {
                SMax = SL;
            }
        } else if (SM < 240 && SM < 0.6 * (Math.min(mem.getXsecNegLeft().Breadth, mem.getXsecNegLeft().Height)) && SM < Math.min(mem.getXsecNegLeft().reBar.main_dia, mem.getXsecNegLeft().reBar.extra_x_diaT)) {
            if (SM > SL && SM > SR) {
                SMax = SM;
            }
        } else if (SR < 240 && SR < 0.6 * (Math.min(mem.getXsecNegLeft().Breadth, mem.getXsecNegLeft().Height)) && SR < Math.min(mem.getXsecNegLeft().reBar.main_dia, mem.getXsecNegLeft().reBar.extra_x_diaT)) {
            if (SR > SL && SR > SM) {
                SMax = SR;
            }
        } else {
            SPen = 1.0;
        }
//        mem.setShearSp(SMax);
        mem.shearYEff = 1.0 - SPen;
        mem.shearZEff = 1.0;
        return SPen;
    }

    public static double sPen(double eff) {
        double pen = 1.0;
        if (eff > 1.0) {
            pen = 2.0;
        } else {
            pen = 2 * (1 - eff);
        }
        return pen;
    }

    public static double shearEfficiency(double Ved, double b, double h, double fck, double fyk, double S, double Asv, double As) {
        double efficiency;
        double Vrds = shearResistance(b, h, fck, fyk, S, Asv, As);
        efficiency = Ved / Vrds;
        return efficiency;
    }

    public static double shearEfficiency(Member3 mem, double VedY, double VedZ) {
        double efficiencyY, efficiencyZ;
        double VrdmaxY = shearResistance(mem.getH(), mem.getB(), mem.getFck(), mem.getFyk(), mem.getXsec().shearS, mem.getXsec().Asv, mem.getXsec().AsY);
        double VrdmaxZ = shearResistance(mem.getB(), mem.getH(), mem.getFck(), mem.getFyk(), mem.getXsec().shearS, mem.getXsec().Asv, mem.getXsec().AsZ);
        efficiencyY = Math.abs(VedY) / VrdmaxY;
        efficiencyZ = Math.abs(VedZ) / VrdmaxZ;
        System.out.println("shearEffY: " + efficiencyY);
        System.out.println("shearEffZ: " + efficiencyZ);
        return efficiencyY + efficiencyZ;
    }

    public static double shearPenaltyY(Member3 mem) {
        //cases
        double shearPenaltyY = 0.0;
        double efficiencyY = 0.0;

        //no shear exists on that member, then penalty =0.0
        if (Math.abs(mem.maxVy) < 5.0) {
            shearPenaltyY = 0.0;
            mem.shearYEff = 1.0;

        } else {
            double VrdmaxY = shearResistance(mem.getH(), mem.getB(), mem.getFck(), mem.getFyk(), mem.getXsec().shearS, mem.getXsec().Asv, mem.getXsec().AsY);
            efficiencyY = Math.abs(mem.maxVy) / VrdmaxY;
            mem.shearYEff = efficiencyY;
//            System.out.println("shearEffY: " + efficiencyY);
            if (efficiencyY > 1.0) {
                //Ted> Trdmax penalty =2.0=> section has failed
                shearPenaltyY = 1.5;
                mem.shearYEff = 0.0;

            } else {
                //Ted< Trdmax penalty =(1-efficiency) => has (100-eff%)left in capacity
                shearPenaltyY = 1 - efficiencyY;

                mem.shearYEff = efficiencyY;

            }
        }
//        System.out.println("ShearY:" + shearPenaltyY);

        return shearPenaltyY;
    }

    public static double shearPenaltyZ(Member3 mem) {
        //cases
        double shearPenaltyZ = 0.0;
        double efficiencyZ = 0.0;

        //no shear exists on that member, then penalty =0.0
        if (Math.abs(mem.maxVz) < 5.0) {
            shearPenaltyZ = 0.0;
            mem.shearZEff = 1.0;
        } else {
            double VrdmaxZ = shearResistance(mem.getB(), mem.getH(), mem.getFck(), mem.getFyk(), mem.getXsec().shearS, mem.getXsec().Asv, mem.getXsec().AsZ);
            efficiencyZ = Math.abs(mem.maxVz) / VrdmaxZ;
            mem.shearZEff = efficiencyZ;
            //           System.out.println("shearEffZ: " + efficiencyZ);
            if (efficiencyZ > 1.0) {
                //Ted> Trdmax penalty =2.0=> section has failed
                shearPenaltyZ = 1.5;
                mem.shearZEff = 0.0;

            } else {
                //Ted< Trdmax penalty =(1-efficiency) => has (100-eff%)left in capacity
                shearPenaltyZ = 1 - efficiencyZ;
                mem.shearZEff = efficiencyZ;

            }
        }
        //       System.out.println("ShearZ:" + shearPenaltyZ);
        return shearPenaltyZ;
    }

    public static void main(String[] args) {
        double resistY = shearResistance(.150, .600, 30, 391 * 1.15, 150, 226, 1500);
        double resistZ = shearResistance(.60, .150, 30, 391 * 1.15, 150, 226, 1500);
        double eff2 = shearEfficiency(300, .150, .600, 30, 391 * 1.15, 450, 226, 1500);
        double eff = shearSpReqd(100, 10, 300, 400, 20, 300, 130, 100, 1500);
        System.out.println("Vrds: " + resistY);
        System.out.println("Vrds: " + resistZ);
        System.out.println("eff: " + eff);
    }
}
