/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SAM3D;



/**
 *
 * @author user
 */
public class Joint3 {

    int jointID;//joint identifier number
    double x, y, z;//joints coordinates
    int pxlx, pxly, pxlz;//graphics coordinates
    double dx, dy, dz, rx, ry, rz;//displacements in the x,y and rotation in the z
    double fx, fy, fz, mx, my, mz;//applied forces and moments in the x,y,z directions
    double rcx, rcy, rcz, rcmx, rcmy, rcmz;//reaction forces in x,y,z; moments in the y,z and torsional moments in the x
    boolean rrx, rry, rrz, rrmx, rrmy, rrmz;//restraints for the displacement(x,y,z) and rotational(x,y,z) axes(true==restrained)
    int dof_index;// an index indicating the degree of freedom index wrt its location in the structural stiffness matrix
    //constructor

    public Joint3(int jointID, double x, double y, double z, int pxlx, int pxly) {
        this.jointID = jointID;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pxlx = pxlx;
        this.pxly = pxly;
         this.dx = 0;
        this.dy = 0;
        this.dz = 0;
        this.rx = 0;
        this.ry = 0;
        this.rz = 0;
        this.fx = 0;
        this.fy = 0;
        this.fz = 0;
        this.mx = 0;
        this.my = 0;
        this.mz = 0;
        this.rrx = false;
        this.rry = false;
        this.rrz = false;
        this.rrmx = false;
        this.rrmy = false;
        this.rrmz = false;
        setDofIndex();
    }

    public Joint3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
         this.dx = 0;
        this.dy = 0;
        this.dz = 0;
        this.rx = 0;
        this.ry = 0;
        this.rz = 0;
        this.fx = 0;
        this.fy = 0;
        this.fz = 0;
        this.mx = 0;
        this.my = 0;
        this.mz = 0;
        this.rrx = false;
        this.rry = false;
        this.rrz = false;
        this.rrmx = false;
        this.rrmy = false;
        this.rrmz = false;
        setDofIndex();

    }

    public void setDofIndex() {
        //all joints have 6 degrees of freedom, 3 for displacement and 3 for rotation
        //EG. if jointID is 3, the dof_index will be 3*6-5=13
        this.dof_index = this.jointID * 6 - 5;
    }

    /*
     Default getters and setters
     */
    public int getJointID() {
        return jointID;
    }

    public void setJointID(int jointID) {
        this.jointID = jointID;
        setDofIndex();
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public int getPxlx() {
        return pxlx;
    }

    public void setPxlx(int pxlx) {
        this.pxlx = pxlx;
    }

    public int getPxly() {
        return pxly;
    }

    public void setPxly(int pxly) {
        this.pxly = pxly;
    }

    public int getPxlz() {
        return pxlz;
    }

    public void setPxlz(int pxlz) {
        this.pxlz = pxlz;
    }

    public double getDx() {
        return dx;
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public double getDy() {
        return dy;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }

    public double getDz() {
        return dz;
    }

    public void setDz(double dz) {
        this.dz = dz;
    }

    public double getRx() {
        return rx;
    }

    public void setRx(double rx) {
        this.rx = rx;
    }

    public double getRy() {
        return ry;
    }

    public void setRy(double ry) {
        this.ry = ry;
    }

    public double getRz() {
        return rz;
    }

    public void setRz(double rz) {
        this.rz = rz;
    }

    public double getFx() {
        return fx;
    }

    public void setFx(double fx) {
        this.fx = fx;
    }

    public double getFy() {
        return fy;
    }

    public void setFy(double fy) {
        this.fy = fy;
    }

    public double getFz() {
        return fz;
    }

    public void setFz(double fz) {
        this.fz = fz;
    }

    public double getMx() {
        return mx;
    }

    public void setMx(double mx) {
        this.mx = mx;
    }

    public double getMy() {
        return my;
    }

    public void setMy(double my) {
        this.my = my;
    }

    public double getMz() {
        return mz;
    }

    public void setMz(double mz) {
        this.mz = mz;
    }

    public double getRcx() {
        return rcx;
    }

    public void setRcx(double rcx) {
        this.rcx = rcx;
    }

    public double getRcy() {
        return rcy;
    }

    public void setRcy(double rcy) {
        this.rcy = rcy;
    }

    public double getRcz() {
        return rcz;
    }

    public void setRcz(double rcz) {
        this.rcz = rcz;
    }

    public double getRcmx() {
        return rcmx;
    }

    public void setRcmx(double rcmx) {
        this.rcmx = rcmx;
    }

    public double getRcmy() {
        return rcmy;
    }

    public void setRcmy(double rcmy) {
        this.rcmy = rcmy;
    }

    public double getRcmz() {
        return rcmz;
    }

    public void setRcmz(double rcmz) {
        this.rcmz = rcmz;
    }

    public boolean isRrx() {
        return rrx;
    }

    public void setRrx(boolean rrx) {
        this.rrx = rrx;
    }

    public boolean isRry() {
        return rry;
    }

    public void setRry(boolean rry) {
        this.rry = rry;
    }

    public boolean isRrz() {
        return rrz;
    }

    public void setRrz(boolean rrz) {
        this.rrz = rrz;
    }

    public boolean isRrmx() {
        return rrmx;
    }

    public void setRrmx(boolean rrmx) {
        this.rrmx = rrmx;
    }

    public boolean isRrmy() {
        return rrmy;
    }

    public void setRrmy(boolean rrmy) {
        this.rrmy = rrmy;
    }

    public boolean isRrmz() {
        return rrmz;
    }

    public void setRrmz(boolean rrmz) {
        this.rrmz = rrmz;
    }
    
    public int getDofIndex(){
        return this.dof_index;
    }

    public void displayInformation() {
        System.out.println("Joint: " + this.jointID);
        System.out.println("Displacement Restraints:--- " + "X:" + this.isRrx() + " Y: " + this.isRry() + " Z: " + this.isRrz());
        System.out.println("Rotation Restraints:--- " + "RX:" + this.isRrmx() + " RY: " + this.isRrmy() + " RZ: " + this.isRrmz());

        System.out.println("End Actions(forces):--- " + "rx: " + this.rcx + " ry: " + this.rcy + " rz: " + this.rcz);
        System.out.println("End Actions(moments):--- " + "mx: " + this.rcmx + " my: " + this.rcmy + " mz: " + this.rcmz);

        System.out.println("Displacements--- " + "dx: " + this.dx + " dy: " + this.dy + " dz: " + this.dz);
        System.out.println("Rotations--- " + "rx: " + this.rx + " ry: " + this.ry + " rz: " + this.rz);

    }

}
