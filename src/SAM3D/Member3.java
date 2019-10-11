/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SAM3D;

import java.util.LinkedList;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import UI.Point2D;
import UI.Section;
import UI.SparseArray;

/**
 *
 * @author user
 */
public class Member3 {

    int memberID; //member identifier
    Joint3 start, end; //member start and end joints
    double b, h, hp;

    public double getHp() {
        return hp;
    }

    public void setHp(double hp) {
        this.hp = hp;
    }
    public Section xsec;
    public Section xsecNegLeft;
    public Section xsecPos;
    public Section xsecNegRight;

    double A; //cross-sectional area of member
    double E; //modulus of elasticity of member material
    double G; //Shear modulus
    double alpha; //Roll angle
    double Ix, Iy, Iz; // moments of inertia (in the minor and major axes) of the x-section, and polar moment of inertia(Ix)
    LinkedList<Load3> loading = new LinkedList<>();
    public double fcu, fck, fcd, fyk, fyd;
    double as1, as2; //reinforcement area for columns
    double cx, cy, cz; //direction cosines
    public double sFx, eFx, sFy, eFy, sFz, eFz, sMx, eMx, sMy, eMy, sMz, eMz; //start and end joint forces and moments
    //loading here
    double length; //length of the member
    RealMatrix localStiffness = new Array2DRowRealMatrix(); //local stiffness  matrix
    RealMatrix globalStiffness = new Array2DRowRealMatrix();//global stiffness matrix
    RealMatrix rotMatrix = new Array2DRowRealMatrix();//transformational/member rotation matrix
    RealMatrix rotMatrix6by6 = new Array2DRowRealMatrix();//transformational/member rotation matrix

    SparseArray localStiffnessC;
    SparseArray globalStiffnessC;
    SparseArray rotMatrixC;
    SparseArray rotMatrixTC;
    SparseArray rotMatrix6by6C;
    SparseArray rotMatrix6by6TC;

    double[] Aml;
    public int Type; //type of member: 0 for beam , 1 for column and 2 for inclined members
    double weight;

    public LinkedList<Point2D> axialCoords = new LinkedList<>();
    public LinkedList<Point2D> shearYCoords = new LinkedList<>();
    public LinkedList<Point2D> shearZCoords = new LinkedList<>();
    public LinkedList<Point2D> torsionCoords = new LinkedList<>();
    public LinkedList<Point2D> momentYCoords = new LinkedList<>();
    public LinkedList<Point2D> momentZCoords = new LinkedList<>();
    public double maxAx, maxVy, maxVz, maxMx, maxMy, maxMz;
    public double minAx, minVy, minVz, minMx, minMy, minMz;
    public LinkedList<Point2D> crAx, crVy, crVz, crMx, crMy, crMz;
    public double memberEff, biaxialEff, shearYEff, shearZEff, STEff, TorsionEff, unieffL, unieffM, unieffR;
    public double totalPenalty;

    public Member3(int MemberID, Joint3 start, Joint3 end) {
        this.start = start;
        this.end = end;
        this.memberID = MemberID;
        memLength();
        directionCosines();
        setOrientation();

    }

    public void memLength() {
        double xs = this.start.x, xe = this.end.x, ys = this.start.y, ye = this.end.y, zs = this.start.z, ze = this.end.z;
        length = Math.sqrt((xe - xs) * (xe - xs) + (ye - ys) * (ye - ys) + (ze - zs) * (ze - zs));

    }

    public void computeArea() {
        A = b * h;
    }

    public void computeIxx() {
        double beta = (1 / 3.0) - (0.21 * (b / h) * (1 - ((b * b * b * b) / (12 * h * h * h * h))));
        this.Ix = beta * h * b * b * b;
    }

    public void computeIyy() {
        double h = this.h;
        double b = this.b;
        this.Iy = h * b * b * b / (12.0);
    }

    public void computeIzz() {
        double h = this.h;
        double b = this.b;
        this.Iz = b * h * h * h / (12.0);
    }

    public void directionCosines() {
        double xs = this.start.x, xe = this.end.x, ys = this.start.y, ye = this.end.y, zs = this.start.z, ze = this.end.z;
        cx = (xe - xs) / length;
        cy = (ye - ys) / length;
        cz = (ze - zs) / length;
    }

    public void addLoads(Load3 l) {
        this.loading.add(l);
    }

    public void localStiffness() {
        double L;
        L = this.length;
        double k1 = A * E / L;
        double k2 = 12 * E * Iz / (L * L * L);
        double k3 = 12 * E * Iy / (L * L * L);
        double k4 = 6 * E * Iz / (L * L);
        double k5 = 6 * E * Iy / (L * L);
        double k6 = 4 * E * Iz / (L);
        double k7 = 4 * E * Iy / (L);
        double k8 = 2 * E * Iz / (L);
        double k9 = 2 * E * Iy / (L);
        double k10 = G * Ix / (L);
        //assign stiffness values...
        double[][] lsval = {{k1, -k1}, {k2, k4, -k2, k4}, {k3, -k5, -k3, -k5}, {k10, -k10}, {-k5, k7, k5, k9}, {k4, k6, -k4, k8},
        {-k1, k1}, {-k2, -k4, k2, -k4}, {-k3, k5, k3, k5}, {-k10, k10}, {-k5, k9, k5, k7}, {k4, k8, -k4, k6}};
        int[][] lsindex = {{0, 6}, {1, 5, 7, 11}, {2, 4, 8, 10}, {3, 9}, {2, 4, 8, 10}, {1, 5, 7, 11},
        {0, 6}, {1, 5, 7, 11}, {2, 4, 8, 10}, {3, 9}, {2, 4, 8, 10}, {1, 5, 7, 11}};
        localStiffnessC = new SparseArray(lsval, lsindex, 12, 12, 40);
    }

    public void rotationMatrix() {
        double cosB = Math.cos(alpha * Math.PI / 180.0f);
        double sinB = Math.sin(alpha * Math.PI / 180.0f);
        double cxz = Math.sqrt(cx * cx + cz * cz);
        //add contingencies for vertical members
        if (isVertical()) {
            double k1 = -cy * cosB;
            double k2 = 0;
            double k3 = sinB;
            double k4 = cy * sinB;
            double k5 = 0;
            double k6 = cosB;
            double[][] rmatC = {
                {cy},
                {k1, k2, k3},
                {k4, k5, k6},
                {cy},
                {k1, k2, k3},
                {k4, k5, k6},
                {cy},
                {k1, k2, k3},
                {k4, k5, k6},
                {cy},
                {k1, k2, k3},
                {k4, k5, k6}};

            int[][] rindex = {
                {1},
                {0, 1, 2},
                {0, 1, 2},
                {4},
                {3, 4, 5},
                {3, 4, 5},
                {7},
                {6, 7, 8},
                {6, 7, 8},
                {10},
                {9, 10, 11},
                {9, 10, 11}};

            int[][] rindexT = {
                {1, 2},
                {0, 1, 2},
                {1, 2},
                {4, 5},
                {3, 4, 5},
                {4, 5},
                {7, 8},
                {6, 7, 8},
                {7, 8},
                {10, 11},
                {9, 10, 11},
                {10, 11}};

            double[][] rmatTC = {
                {k1, k4},
                {cy, k2, k5},
                {k3, k6},
                {k1, k4},
                {cy, k2, k5},
                {k3, k6},
                {k1, k4},
                {cy, k2, k5},
                {k3, k6},
                {k1, k4},
                {cy, k2, k5},
                {k3, k6}};

            int[][] rindexsml = {
                {1},
                {0, 1, 2},
                {0, 1, 2},
                {4},
                {3, 4, 5},
                {3, 4, 5}};

            int[][] rindexTsml = {
                {1, 2},
                {0, 1, 2},
                {1, 2},
                {4, 5},
                {3, 4, 5},
                {4, 5}};

            double[][] rmatCsml = {
                {cy},
                {k1, k2, k3},
                {k4, k5, k6},
                {cy},
                {k1, k2, k3},
                {k4, k5, k6}};

            double[][] rmatTCsml = {
                {k1, k4},
                {cy, k2, k5},
                {k3, k6},
                {k1, k4},
                {cy, k2, k5},
                {k3, k6}};
            rotMatrixC = new SparseArray(rmatC, rindex, 12, 12, 28);
            rotMatrixTC = new SparseArray(rmatTC, rindexT, 12, 12, 28);
            rotMatrix6by6C = new SparseArray(rmatCsml, rindexsml, 6, 6, 14);
            rotMatrix6by6TC = new SparseArray(rmatTCsml, rindexTsml, 6, 6, 14);

        } else {

            double k1 = (-cx * cy * cosB - cz * sinB) / cxz;
            double k2 = cxz * cosB;
            double k3 = (-cy * cz * cosB + cx * sinB) / cxz;
            double k4 = (cx * cy * sinB - cz * cosB) / cxz;
            double k5 = -cxz * sinB;
            double k6 = (cy * cz * sinB + cx * cosB) / cxz;

            double[][] rmatC = {
                {cx, cy, cz},
                {k1, k2, k3},
                {k4, k5, k6},
                {cx, cy, cz},
                {k1, k2, k3},
                {k4, k5, k6},
                {cx, cy, cz},
                {k1, k2, k3},
                {k4, k5, k6},
                {cx, cy, cz},
                {k1, k2, k3},
                {k4, k5, k6}};

            double[][] rmatTC = {
                {cx, k1, k4},
                {cy, k2, k5},
                {cz, k3, k6},
                {cx, k1, k4},
                {cy, k2, k5},
                {cz, k3, k6},
                {cx, k1, k4},
                {cy, k2, k5},
                {cz, k3, k6},
                {cx, k1, k4},
                {cy, k2, k5},
                {cz, k3, k6}};

            int[][] rindex = {
                {0, 1, 2},
                {0, 1, 2},
                {0, 1, 2},
                {3, 4, 5},
                {3, 4, 5},
                {3, 4, 5},
                {6, 7, 8},
                {6, 7, 8},
                {6, 7, 8},
                {9, 10, 11},
                {9, 10, 11},
                {9, 10, 11}};

            int[][] rindexsml = {
                {0, 1, 2},
                {0, 1, 2},
                {0, 1, 2},
                {3, 4, 5},
                {3, 4, 5},
                {3, 4, 5}};

            int[][] rindexTsml = {
                {0, 1, 2},
                {0, 1, 2},
                {0, 1, 2},
                {3, 4, 5},
                {3, 4, 5},
                {3, 4, 5}};

            double[][] rmatCsml = {
                {cx, cy, cz},
                {k1, k2, k3},
                {k4, k5, k6},
                {cx, cy, cz},
                {k1, k2, k3},
                {k4, k5, k6},
                {cx, cy, cz}};

            double[][] rmatTCsml = {
                {cx, k1, k4},
                {cy, k2, k5},
                {cz, k3, k6},
                {cx, k1, k4},
                {cy, k2, k5},
                {cz, k3, k6}};

            rotMatrixC = new SparseArray(rmatC, rindex, 12, 12, 36);
            rotMatrixTC = new SparseArray(rmatTC, rindex, 12, 12, 36);
            rotMatrix6by6C = new SparseArray(rmatCsml, rindexsml, 6, 6, 18);
            rotMatrix6by6TC = new SparseArray(rmatTCsml, rindexTsml, 6, 6, 18);

        }
    }

    public boolean isVertical() {
        double tolerance = 0.01;

        double x1 = this.start.getX();
        double x2 = this.end.getX();
        double y1 = this.start.getY();
        double y2 = this.end.getY();
        double z1 = this.start.getZ();
        double z2 = this.end.getZ();

        double birdsEyeDistance = Math.sqrt((x1 - x2) * (x1 - x2) + (z1 - z2) * (z1 - z2));
        double verticalDistance = Math.abs(y2 - y1);

        if (birdsEyeDistance / verticalDistance < tolerance) {
            return true;
        } else {
            return false;
        }
    }

    public void globalStiffness() {
        //transpose the rotation matrix
        //find the product of R'*LK*R
        globalStiffnessC = rotMatrixTC.mmult(localStiffnessC);
        globalStiffnessC = globalStiffnessC.mmult(rotMatrixC);
    }

    public void setOrientation() {
        //type of member: 0 for beam , 1 for column and 2 for inclined members
        if ((Math.abs(Math.abs(cy) - 0) < 0.00001)) {
            this.Type = 0;
        } else if ((Math.abs(Math.abs(cy) - 1) < 0.00001)) {
            this.Type = 1;
        } else {
            this.Type = 2;
        }
    }

    public String getStringType() {
        String str;
        if (this.Type == 0) {
            str = "beam";
        } else if (this.Type == 1) {
            str = "column";
        } else {
            str = "inclined";
        }
        return str;
    }

    public void AML() {
        // compute local fixed end actions
        double[] Aml = new double[12];
        for (int i = 0; i < this.loading.size(); i++) {
            Load3 L = this.loading.get(i);
            int type = L.type;
            double Len = this.length;
            //fixed end actions of the member on the local coordinates...
            //these need to be converted back to global coordinates.
            //Moments remain the same in both coordinates.
            double srcxp, srcyp, srczp, srcmxp, srcmyp, srcmzp, ercxp, ercyp, erczp, ercmxp, ercmyp, ercmzp;
            //horizontal and vertical force components for the local coordinate system of
            // the member
            double[] LoadV = {L.X, L.Y, L.Z, L.MX, L.MY, L.MZ};
            //________________________________________________________________________
            double[] LoadLocalC = rotMatrix6by6C.mmultV(LoadV);
            double X = LoadLocalC[0];
            double Y = LoadLocalC[1];
            double Z = LoadLocalC[2];
            double MX = LoadLocalC[3];
            double MY = LoadLocalC[4];
            double MZ = LoadLocalC[5];

            //________________________________________________________________________
//            double X = LoadLocal.getEntry(0);
//            double Y = LoadLocal.getEntry(1);
//            double Z = LoadLocal.getEntry(2);
//            double MX = LoadLocal.getEntry(3);
//            double MY = LoadLocal.getEntry(4);
//            double MZ = LoadLocal.getEntry(5);
            double a = L.position * Len;
            double b = this.length - a;
            //System.out.println(this.cosx + "  " + X + "  " + this.cosy + "    " + Y + "   ");
            //distributed loads===1---MAKE COMPUTATIONS MORE GENERAL(partial loading)
            //concentrated loads===0
            //add code for concentrated moments in
            if (type == 1) {
                srcxp = -X * b / Len;
                ercxp = -X * a / Len;
                srcyp = -Y * Len / 2 + 6 * MZ * a * b / Math.pow(Len, 2);
                ercyp = -Y * Len / 2 - 6 * MZ * a * b / Math.pow(Len, 2);
                srczp = -Z * Len / 2 + 6 * MY * a * b / Math.pow(Len, 2);
                erczp = -Z * Len / 2 - 6 * MY * a * b / Math.pow(Len, 2);
                srcmxp = -MX * b / Len;
                ercmxp = -MX * a / Len;
                srcmyp = (Z * Len * Len / 12) + MY * b * (2 * a - b) / (Len * Len);
                ercmyp = (-Z * Len * Len / 12) + MY * a * (2 * b - a) / (Len * Len);
                srcmzp = (Y * Len * Len / 12) + MZ * b * (2 * a - b) / (Len * Len);
                ercmzp = (-Y * Len * Len / 12) + MZ * a * (2 * b - a) / (Len * Len);
            } else {
                srcxp = -X * b / Len;
                ercxp = -X * a / Len;
                srcyp = -Y * b / Len + 6 * MZ * a * b / Math.pow(Len, 2);
                ercyp = -Y * a / Len - 6 * MZ * a * b / Math.pow(Len, 2);
                srczp = -Z * b / Len + 6 * MY * a * b / Math.pow(Len, 2);
                erczp = -Z * a / Len - 6 * MY * a * b / Math.pow(Len, 2);
                srcmxp = -MX * b / Len;
                ercmxp = -MX * a / Len;
                srcmyp = (Z * a * b * b) / (Len * Len) + MY * b * (2 * a - b) / (Len * Len);
                ercmyp = (-Z * a * a * b) / (Len * Len) + MY * a * (2 * b - a) / (Len * Len);
                srcmzp = (Y * a * b * b) / (Len * Len) + MZ * b * (2 * a - b) / (Len * Len);
                ercmzp = (-Y * a * a * b) / (Len * Len) + MZ * a * (2 * b - a) / (Len * Len);
            }
            double[] AMLLocal = {srcxp, srcyp, srczp, srcmxp, srcmyp, srcmzp, ercxp, ercyp, erczp, ercmxp, ercmyp, ercmzp};
            //____________________________________________________________________
            double[] AMLGlobalC = rotMatrixTC.mmultV(AMLLocal);
            this.start.rcx += AMLGlobalC[0];
            this.start.rcy += AMLGlobalC[1];
            this.start.rcz += AMLGlobalC[2];
            this.start.rcmx += AMLGlobalC[3];
            this.start.rcmy += AMLGlobalC[4];
            this.start.rcmz += AMLGlobalC[5];
            this.end.rcx += AMLGlobalC[6];
            this.end.rcy += AMLGlobalC[7];
            this.end.rcz += AMLGlobalC[8];
            this.end.rcmx += AMLGlobalC[9];
            this.end.rcmy += AMLGlobalC[10];
            this.end.rcmz += AMLGlobalC[11];

            //____________________________________________________________________
            // *****FOR THE LOVE OF GOD, DO NOT DELETE OR MODIFY THE NEXT LINES OF CODE IN ANY WAY*!!!!!!!!*****
            // THESE LOOK REDUNDANT,BUT THEY ACTUALLY ARE A FAIL SAFE FOR FINDING AML ARRAYS
            // FOR COMPUTING MEMBER END ACTIONS(USING AM=AML+SM1*DM1) LATER ON SINCE, UNLIKE THE PREVIOUS SIX LINES,
            // THIS REACTION VECTOR WONT BE MODIFIED BY FORCES FROM OTHER MEMBERS
            Aml[0] += AMLLocal[0];
            Aml[1] += AMLLocal[1];
            Aml[2] += AMLLocal[2];
            Aml[3] += AMLLocal[3];
            Aml[4] += AMLLocal[4];
            Aml[5] += AMLLocal[5];
            Aml[6] += AMLLocal[6];
            Aml[7] += AMLLocal[7];
            Aml[8] += AMLLocal[8];
            Aml[9] += AMLLocal[9];
            Aml[10] += AMLLocal[10];
            Aml[11] += AMLLocal[11];

        }
        this.Aml = Aml;
    }

    public void ComputeWeight() {
        int typ = this.Type;
        float length = (float) this.length;
        float W = 0;
        float Cc, Cs, Cf;//cost of concrete and steel
        float Cconc, Csteel, Cbeamf, CbeamS, Ccolf;

        Cconc = 112.13f; //euros per m3
        Csteel = 10205f;//euros per m3
        Cbeamf = 25.05f; //euros per m2
        CbeamS = 38.89f; //euros per m2
        Ccolf = 22.75f; //euros per m2


        float b = (float) this.b * 1000.0f;
        float h = (float) this.h * 1000.0f;
        float reinVolL = (float) this.getXsecNegLeft().reBar.totalReinArea * 0.2f * length;
        float reinVolM = (float) this.getXsecPos().reBar.totalReinArea * 0.6f * length;
        float reinVolR = (float) this.getXsecNegRight().reBar.totalReinArea * 0.2f * length;

        float stirrupLength=((b-50)*2+(h-50)*2)/1000.0f;
        float shearArea = (float) (8 * 8 * Math.PI / 4);
        float shearAreaL = (float) (this.length * 0.3 / (this.getXsecNegLeft().shearS / 1000.0) * stirrupLength * shearArea);
        float shearAreaM = (float) (this.length * 0.4 / (this.getXsecPos().shearS / 1000.0) * stirrupLength * shearArea);
        float shearAreaR = (float) (this.length * 0.3 / (this.getXsecNegRight().shearS / 1000.0) * stirrupLength * shearArea);


        double CConcV = length * b * h * Cconc / 1000000.0;
        double CBeamS = (b * length) * CbeamS / 1000.0;
        double CBeamF = length * (b + 2 * h) * Cbeamf / 1000.0;
        double CColumnF = length * (2 * b + 2 * h) * Ccolf / 1000.0;
        double CBeamSt = (reinVolL + reinVolM + reinVolR + shearAreaL + shearAreaM + shearAreaR) * Csteel / 1000000.0;
        double CColumnSt = (reinVolL + reinVolM + reinVolR + shearAreaM) * Csteel / 1000000.0;
        if (typ == 0) {//if member is a beam
            W = (float) (CConcV + CbeamS + CBeamF + CBeamSt);
        } else {
            W = (float) (CConcV + CColumnF + CColumnSt);
        }
        this.weight = W;
    }

    public void printInformation() {
        System.out.println("memberID: " + this.memberID);
        System.out.println("start: " + this.start.jointID + "  end: " + this.end.jointID);
        System.out.println("direction cosines: " + "cosX: " + this.cx + "cosY: " + this.cy + "cosZ: " + this.cz);
        System.out.println("B: " + this.b + "H: " + this.h);
        System.out.println("Ixx: " + this.Ix + " Iyy: " + this.Iy + " Izz: " + this.Iz);
        System.out.println("local stiffness:");
        this.localStiffnessC.printM();
        System.out.println("rotation matrix:");
        this.rotMatrixC.print();
        this.rotMatrixTC.print();
        System.out.println("global stiffness:");
        this.globalStiffnessC.printM();
        System.out.println("length: " + this.length);

    }

    public static void main(String[] args) {
        double A = 0.01;
        double E = 200000;
        double Iz = 1.33 * 0.00001;
        Joint3 j1 = new Joint3(1, 0, 0, 0, 10, 10);
        Joint3 j2 = new Joint3(2, 0, 6, 0, 10, 10);
        Joint3 j3 = new Joint3(3, 4, 6, 0, 10, 10);

        Load3 l1 = new Load3(0, 0.5, 24, 0, 0, 0, 0, 0);
        Load3 l2 = new Load3(0, 0.5, 0, -48, 0, 0, 0, 0);

        Member3 t1 = new Member3(1, j1, j2);
        Member3 t2 = new Member3(2, j2, j3);

        t1.loading.add(l1);
        t2.loading.add(l2);

        t1.setA(A);
        t2.setA(A);
        t1.setE(E);
        t2.setE(E);
        t1.setIz(Iz);
        t2.setIz(Iz);
        t1.memLength();
        t2.memLength();
        t1.directionCosines();
        t2.directionCosines();
        long tt = System.nanoTime();
        t1.rotationMatrix();
        t2.rotationMatrix();
        t1.localStiffness();
        t2.localStiffness();
        t1.globalStiffness();
        t2.globalStiffness();
        t1.AML();
        t2.AML();
        long tn = System.nanoTime();
        System.out.println("time el: " + (tn - tt) / 1000000000.0);
        t1.printInformation();
        t2.printInformation();
//        j1.displayInformation();
//        j2.displayInformation();
//        j3.displayInformation();
    }
//**********************************************************************************************
    //SETTERS AND GETTERS//

//**********************************************************************************************
    public void setSectionNegLeft(Section sect) {
        xsecNegLeft = sect;
    }

    public void setSectionPos(Section sect) {
        xsecPos = sect;
    }

    public void setSectionNegRight(Section sect) {
        xsecNegRight = sect;
    }

    public Section getXsecNegLeft() {
        return xsecNegLeft;
    }

    public Section getXsecPos() {
        return xsecPos;
    }

    public Section getXsecNegRight() {
        return xsecNegRight;
    }

    public void setSection(Section sect) {
        xsec = sect;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }

    public double getA() {
        return A;
    }

    public void setA(double A) {
        this.A = A;
    }

    public double getE() {
        return E;
    }

    public void setE(double E) {
        this.E = E;
    }

    public double getG() {
        return G;
    }

    public void setG(double G) {
        this.G = G;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getIx() {
        return Ix;
    }

    public void setIx(double Ix) {
        this.Ix = Ix;
    }

    public double getIy() {
        return Iy;
    }

    public void setIy(double Iy) {
        this.Iy = Iy;
    }

    public double getIz() {
        return Iz;
    }

    public void setIz(double Iz) {
        this.Iz = Iz;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public int getType() {
        return Type;
    }

    public void setType(int Type) {
        this.Type = Type;
    }

    public int getMemberID() {
        return memberID;
    }

    public Joint3 getStart() {
        return start;
    }

    public void setStart(Joint3 start) {
        this.start = start;
    }

    public Joint3 getEnd() {
        return end;
    }

    public void setEnd(Joint3 end) {
        this.end = end;
    }

    public double getCx() {
        return cx;
    }

    public void setCx(double cx) {
        this.cx = cx;
    }

    public double getCy() {
        return cy;
    }

    public void setCy(double cy) {
        this.cy = cy;
    }

    public double getCz() {
        return cz;
    }

    public void setCz(double cz) {
        this.cz = cz;
    }

    public double getFcu() {
        return fcu;
    }

    public double getFck() {
        return fck;
    }

    public void setFcu(double fcu) {
        this.fcu = fcu;
        this.fck = fcu * 0.85;
        this.fcd = 0.85 * fcu / 1.5;
    }

    public void setFck(double fcu) {
        this.fcu = fcu;
        this.fck = 0.8 * fcu;
    }

    public double getFyk() {
        return fyk;
    }

    public void setFyk(double fyk) {
        this.fyk = fyk;
        this.fyd = fyk / 1.15;
    }

    public void setAs1(double asb) {
        this.as1 = asb;
    }

    public Section getXsec() {
        return xsec;
    }

    public double getAs1() {
        return as1;
    }

    public double getAs2() {
        return as2;
    }

    public void setAs2(double ast) {
        this.as2 = ast;
    }

    public LinkedList<Load3> getLoads() {
        return loading;
    }
}
