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
public class sizePenalty {

    public static double sizeRatioPenalty(Member3 m) {
        double h_to_b = m.getH() / m.getB();
        double h_to_bPen = 0.0;
        if (!m.isVertical()) {
            if (h_to_b >= 2.5) {
                h_to_bPen = 2.0;
            } else if (h_to_b <= 1.5) {
                h_to_bPen = 2.0;
            } else {
                h_to_bPen = 0.0;
            }
        }
        return h_to_bPen;
    }

    public static double maxflexReinforcement(Member3 m) {
        double maxReinPen = 0.0;
        double maxReinL = 0.04 * m.getB() * m.getH() - m.getXsecNegLeft().reBar.totalReinArea;
        double maxReinM = 0.04 * m.getB() * m.getH() - m.getXsecPos().reBar.totalReinArea;
        double maxReinR = 0.04 * m.getB() * m.getH() - m.getXsecNegRight().reBar.totalReinArea;

        if (maxReinL < m.getXsecNegLeft().reBar.totalReinArea) {
            maxReinPen += 0.2;
        } else {
            maxReinPen += 0.0;
        }
        if (maxReinM < m.getXsecPos().reBar.totalReinArea) {
            maxReinPen += 0.2;
        } else {
            maxReinPen += 0.0;
        }
        if (maxReinR < m.getXsecNegRight().reBar.totalReinArea) {
            maxReinPen += 0.2;
        } else {
            maxReinPen += 0.0;
        }

        return maxReinPen / 3.0;
    }

    public static double minflexReinforcement(Member3 m) {
        double minReinPen = 0.0;
        if (m.isVertical()) {
            double minReinL = 0.002 * 0.04 * m.getB() * m.getH() - m.getXsecNegLeft().reBar.totalReinArea;
            double minReinM = 0.002 * 0.04 * m.getB() * m.getH() - m.getXsecPos().reBar.totalReinArea;
            double minReinR = 0.002 * 0.04 * m.getB() * m.getH() - m.getXsecNegRight().reBar.totalReinArea;
            if (minReinL > m.getXsecNegLeft().reBar.totalReinArea) {
                minReinPen += 0.2;
            } else {
                minReinPen += 0.0;
            }
            if (minReinM > m.getXsecPos().reBar.totalReinArea) {
                minReinPen += 0.2;
            } else {
                minReinPen += 0.0;
            }
            if (minReinR > m.getXsecNegRight().reBar.totalReinArea) {
                minReinPen += 0.2;
            } else {
                minReinPen += 0.0;
            }
        } else {
            double minRein = 0.0013 * m.getB() * m.getH();
            if (minRein > m.getXsecNegLeft().reBar.totalReinArea) {
                minReinPen = 0.2;
            } else {
                minReinPen = 0.0;
            }
        }
        return minReinPen;
    }

    public static double maxShearReinforcement(Member3 m) {
        double smax = 0.75 * (m.getH() - 0.030);
        double sspen = 0.0;
        if (m.getXsecNegLeft().shearS > smax) {
            sspen += 0.2;
        } else {
            sspen += 0.0;
        }
        if (m.getXsecPos().shearS > smax) {
            sspen += 0.2;
        } else {
            sspen += 0.0;
        }
        if (m.getXsecNegRight().shearS > smax) {
            sspen += 0.2;
        } else {
            sspen += 0.0;
        }
        return sspen;
    }

    public static double minShearReinforcement(Member3 m) {
        double sspen=0.0;
        double sminL = m.getXsecNegLeft().Asv * m.getFyk() / (0.08 * m.getB() * Math.sqrt(m.getFck()));

        if (m.getXsecNegLeft().shearS > sminL) {
            sspen += 0.2;
        } else {
            sspen += 0.0;
        }
        double sminM = m.getXsecPos().Asv * m.getFyk() / (0.08 * m.getB() * Math.sqrt(m.getFck()));
        if (m.getXsecPos().shearS > sminM) {
            sspen += 0.2;
        } else {
            sspen += 0.0;
        }
        double sminR = m.getXsecNegRight().Asv * m.getFyk() / (0.08 * m.getB() * Math.sqrt(m.getFck()));
        if (m.getXsecNegRight().shearS > sminR) {
            sspen += 0.2;
        } else {
            sspen += 0.0;
        }
        return sspen;
    }

    public static double totalSizePenalty(Member3 m) {
        double totalPen;
        totalPen = sizeRatioPenalty(m) + maxflexReinforcement(m) + minflexReinforcement(m) + maxShearReinforcement(m) + minShearReinforcement(m);
        return totalPen;
    }

}
