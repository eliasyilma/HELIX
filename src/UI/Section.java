/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author CASE
 */
public class Section {

    public double Area;
    public double Ixx;
    public double Iyy;
    public double Izz;
    public double Breadth;
    public double Height;
    public double Hpp;
    public double Hpn;
    public int rebarID;
    public double AsY;
    public double AsY1, AsY2;
    public double AsZ;
    public double Asv;
    public Reinforcement reBar;
    public double shearS;

    public Section(int rebarID, double shearSp, double width, double height) {
        this.Breadth = width;
        this.Height = height;
        this.reBar = new Reinforcement(rebarID, width, height);
        this.shearS = shearSp;
        computeAs();
        computeHp();
    }

    public Section(int mainID, int extraXTID, int extraXBID, int extraYLID, int extraYRID, double shearSp, double width, double height) {
        this.Breadth = width;
        this.Height = height;
        this.reBar = new Reinforcement(mainID, extraXTID, extraXBID, extraYLID, extraYRID, width, height);
        this.shearS = shearSp;
        computeAs();
        computeHp();
    }

    public double getBreadth() {
        return Breadth;
    }

    public void computeAs() {
        this.AsY = reBar.main_area * 2 + reBar.extra_y_areaL + reBar.extra_y_areaR;
        this.AsY1 = reBar.main_area + reBar.extra_x_areaB;
        this.AsY2 = reBar.main_area + reBar.extra_x_areaT;
        this.AsZ = reBar.main_area * 2 + reBar.extra_x_areaT + reBar.extra_x_areaB;
        this.Asv = Math.PI * 8 * 8 * 2 / 4.0;
    }

    public void computeHp() {
        this.Hpp = (reBar.main_dia + reBar.extra_x_diaT) * 0.5 + 8 + 25;
        this.Hpn = (reBar.main_dia + reBar.extra_x_diaB) * 0.5 + 8 + 25;

    }

    public void setBreadth(double Breadth) {
        this.Breadth = Breadth;
    }

    public double getHeight() {
        return Height;
    }

    public void setHeight(double Height) {
        this.Height = Height;
    }

    public double getShearS() {
        return shearS;
    }

    public void setShearS(int shear) {
        this.shearS = shear;
    }

    public Reinforcement getReBar() {
        return reBar;
    }

    public void setReBar(Reinforcement reBar) {
        this.reBar = reBar;
    }

    public static void main(String[] args) {

    }

}
