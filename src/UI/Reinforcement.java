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
//column reinforcement
public class Reinforcement {

    public int mid; //main reinforcement id
    public int eid; //extra reinforcment id
    public double totalReinArea;
    public double main_dia;
    public double main_area;
    public int main_num;

    public double extra_x_diaT;
    public double extra_x_areaT;
    public int extra_x_numT;

    public double extra_x_diaB;
    public double extra_x_areaB;
    public int extra_x_numB;

    public double extra_y_diaL;
    public double extra_y_areaL;
    public int extra_y_numL;

    public double extra_y_diaR;
    public double extra_y_areaR;
    public int extra_y_numR;

    public Point2D[] mainCoor;
    public Point2D[] extraCoorX;
    public Point2D[] extraCoorY;
    public Point2D[] extraCoorXT;
    public Point2D[] extraCoorXB;
    public Point2D[] extraCoorYL;
    public Point2D[] extraCoorYR;

    public double[] diameterData = {12, 14, 16, 20, 24, 32, 0, 0, 32, 24, 20, 16, 14, 12};
    public double[] extraData = {12, 12, 12, 14, 14, 14, 16, 16, 16, 20, 20, 20, 24, 24, 24, 32, 32, 32, 0, 0, 0};
    public double[] mainData = {12, 14, 16, 20, 24, 32};
    public int[] extraNum = {1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3, 1, 2, 3};

//    public static void main(String[] args) {
//        int id = 34;
//        double b = 300;
//        double h = 400;
//        Reinforcement r = new Reinforcement(id, b, h);
//        System.out.println("reinforcement schedule:");
//        System.out.println("---------------------------------------------------------------------");
//        System.out.println("main----| diameter: " + r.main_dia + " | -- | number: " + r.main_num + " |");
//        System.out.println(" bar No.     X       Y       ");
//        System.out.println("    1        " + r.mainCoor[0].x + "    " + r.mainCoor[0].y);
//        System.out.println("    2        " + r.mainCoor[1].x + "    " + r.mainCoor[1].y);
//        System.out.println("    3        " + r.mainCoor[2].x + "    " + r.mainCoor[2].y);
//        System.out.println("    4        " + r.mainCoor[3].x + "    " + r.mainCoor[3].y);
//
//        System.out.println("---------------------------------------------------------------------");
//
//        System.out.println("extraX--| diameter: " + r.extra_x_dia + " | -- | number: " + r.extra_x_num + " |");
//        System.out.println(" bar No.     X       Y       ");
//        System.out.println("    1        " + r.extraCoorX[0].x + "    " + r.extraCoorX[0].y);
//        System.out.println("    2        " + r.extraCoorX[1].x + "    " + r.extraCoorX[1].y);
//
//        System.out.println("---------------------------------------------------------------------");
//        System.out.println("extraY--| diameter: " + r.extra_y_dia + " | -- | number: " + r.extra_y_num + " |");
//        System.out.println(" bar No.     X       Y       ");
//        System.out.println("    1        " + r.extraCoorY[0].x + "    " + r.extraCoorY[0].y);
//        System.out.println("    2        " + r.extraCoorY[1].x + "    " + r.extraCoorY[1].y);
//        System.out.println("---------------------------------------------------------------------");
//        System.out.println(" total area");
//    }

    public Reinforcement(int id) {
        mainCoor = new Point2D[4];
        extraCoorX = new Point2D[2];
        extraCoorY = new Point2D[2];
        interpret_id(id);
    }

    public Reinforcement(int mainId, int extXTId, int extXBId, int extYLId, int extYRId, double width, double height) {

        interpretId(mainId, extXTId, extXBId, extYLId, extYRId);
        mainCoor = new Point2D[this.main_num];
        barMainCoordinates(width, height);
        extraCoorXT = new Point2D[this.extra_x_numT];
        extraCoorXB = new Point2D[this.extra_x_numB];
        extraCoorYL = new Point2D[this.extra_y_numL];
        extraCoorYR = new Point2D[this.extra_y_numR];
        barExtraCoordinates(width, height);

        this.main_area = this.main_num/2.0 * Math.PI * main_dia * main_dia / 4.0;
        this.extra_x_areaT = this.extra_x_numT * Math.PI * extra_x_diaT * extra_x_diaT / 4.0;
        this.extra_x_areaB = this.extra_x_numB * Math.PI * extra_x_diaB * extra_x_diaB / 4.0;
        this.extra_y_areaL = this.extra_y_numL * Math.PI * extra_y_diaL * extra_y_diaL / 4.0;
        this.extra_y_areaR = this.extra_y_numR * Math.PI * extra_y_diaR * extra_y_diaR / 4.0;
        this.totalReinArea = this.main_area + this.extra_x_areaT + this.extra_x_areaB + this.extra_y_areaL + this.extra_y_areaR;
    }

    public Reinforcement(int id, double width, double height) {
        mainCoor = new Point2D[4];
        extraCoorX = new Point2D[2];
        extraCoorY = new Point2D[2];

        this.main_num = mainCoor.length;
//        this.extra_x_num = extraCoorX.length;
//        this.extra_y_num = extraCoorY.length;

        interpret_id(id);
        barMainCoordinates(width, height);
        barExtraCoordinates(width, height);

        this.main_area = Math.PI * main_dia * main_dia / 4.0;
 //       this.extra_x_area = Math.PI * extra_x_dia * extra_x_dia / 4.0;
//        this.extra_y_area = Math.PI * extra_y_dia * extra_y_dia / 4.0;
//        this.totalReinArea = this.main_area + this.extra_x_area + this.extra_y_area;
    }

    public double barArea(double dia) {
        double A;
        A = Math.PI * dia * dia / 4.0;
        return A;
    }

    public void interpret_id(int id) {
        this.main_dia = diameterData[id / 12];
 //       this.extra_x_dia = diameterData[id % 6];
  //      this.extra_y_dia = diameterData[id % 6];

    }

    public void interpretId(int mainId, int extXTId, int extXBId, int extYLId, int extYRId) {
        this.main_dia = mainData[mainId];
        this.extra_x_diaT = extraData[extXTId];
        this.extra_y_diaL = extraData[extYLId];
        this.extra_x_diaB = extraData[extXBId];
        this.extra_y_diaR = extraData[extYRId];

        this.main_num = 4;
        this.mainCoor = new Point2D[4];
        if (extra_x_diaT < 1) {
            this.extra_x_numT = 0;
            this.extraCoorXT = new Point2D[0];
        } else {
            this.extra_x_numT = extraNum[extXTId];
            this.extraCoorXT = new Point2D[this.extra_x_numT];
        }

        if (extra_x_diaB < 1) {
            this.extra_x_numB = 0;
            this.extraCoorXB = new Point2D[0];
        } else {
            this.extra_x_numB = extraNum[extXBId];
            this.extraCoorXB = new Point2D[this.extra_x_numB];
        }

        if (extra_y_diaL < 1) {
            this.extra_y_numL = 0;
            this.extraCoorYL = new Point2D[0];
        } else {
            this.extra_y_numL = extraNum[extYLId];
            this.extraCoorYL = new Point2D[this.extra_y_numL];
        }

        if (extra_y_diaR < 1) {
            this.extra_y_numR = 0;
            this.extraCoorYR = new Point2D[0];
        } else {
            this.extra_y_numR = extraNum[extYRId];
            this.extraCoorYR = new Point2D[this.extra_y_numR];
        }
    }

    public void barMainCoordinates(double b, double h) {
        double hp = 25 + 8 + this.main_dia / 2.0;
        this.mainCoor[0] = new Point2D(hp, hp);
        this.mainCoor[1] = new Point2D(hp, h - hp);
        this.mainCoor[2] = new Point2D(b - hp, h - hp);
        this.mainCoor[3] = new Point2D(b - hp, hp);
    }

    public void barExtraCoordinates(double b, double h) {

        double hpmain = 25 + 8 + this.main_dia;
        double hpextXT = 25 + 8 + this.extra_x_diaT / 2.0;
        double hpextYL = 25 + 8 + this.extra_y_diaL / 2.0;
        double hpextXB = 25 + 8 + this.extra_x_diaB / 2.0;
        double hpextYR = 25 + 8 + this.extra_y_diaR / 2.0;

        double spXT = (b - hpmain * 2) / (this.extra_x_numT + 1);
        double spXB = (b - hpmain * 2) / (this.extra_x_numB + 1);
        double spYL = (h - hpmain * 2) / (this.extra_y_numL + 1);
        double spYR = (h - hpmain * 2) / (this.extra_y_numR + 1);

        double currX = hpmain;
        double currY = hpmain;

        for (int i = 0; i < extra_x_numT; i++) {
            currX = currX + spXT;
            this.extraCoorXT[i] = new Point2D(currX, h - hpextXT);
        }
        currX = hpmain;
        for (int i = 0; i < extra_y_numL; i++) {
            currY = currY + spYL;
            this.extraCoorYL[i] = new Point2D(hpextYL, currY);
        }
        currY = hpmain;
        for (int i = 0; i < extra_x_numB; i++) {
            currX = currX + spXB;
            this.extraCoorXB[i] = new Point2D(currX, hpextXB);
        }

        for (int i = 0; i < extra_y_numR; i++) {
            currY = currY + spYR;
            this.extraCoorYR[i] = new Point2D(b - hpextYR, currY);
        }

//        this.extraCoorX[0] = new Point2D(b / 2, hp);
//        this.extraCoorX[1] = new Point2D(b / 2, h - hp);
//
//        hp = 25 + 8 + this.extra_y_dia / 2.0;
//        this.extraCoorY[0] = new Point2D(hp, h / 2);
//        this.extraCoorY[1] = new Point2D(b - hp, h / 2);
    }

}
