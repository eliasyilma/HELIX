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
public class Load3 {

    int type; //0 for point loading, 1 for uniform loading
    double position;//  relative position of point load betweeb start(0) and end(1).
    double X;
    double Y;
    double Z;
    double MX;
    double MY;
    double MZ;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getPosition() {
        return position;
    }

    public void setPosition(double position) {
        this.position = position;
    }

    public double getX() {
        return X;
    }

    public void setX(double X) {
        this.X = X;
    }

    public double getY() {
        return Y;
    }

    public void setY(double Y) {
        this.Y = Y;
    }

    public double getZ() {
        return Z;
    }

    public void setZ(double Z) {
        this.Z = Z;
    }

    public double getMX() {
        return MX;
    }

    public void setMX(double MX) {
        this.MX = MX;
    }

    public double getMY() {
        return MY;
    }

    public void setMY(double MY) {
        this.MY = MY;
    }

    public double getMZ() {
        return MZ;
    }

    public void setMZ(double MZ) {
        this.MZ = MZ;
    }



    public Load3(int type, double pos, double x, double y, double z, double Mx, double My, double Mz) {
        this.type = type;
        this.position = pos;
        this.X = x;
        this.Y = y;
        this.Z = z;
        this.MX = Mx;
        this.MY = My;
        this.MZ = Mz;
        
    }

}
