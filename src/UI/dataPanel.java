/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.swing.JFrame;

import javax.swing.JPanel;

/**
 *
 * @author CASE
 */
public class dataPanel extends JPanel {

    public static Label geometryTitle;
    public static Label designTitle;
    public static Label width;
    public static Label height;
    public static Label mainR;
    public static Label extraX;
    public static Label extraY;
    public static Label AX;
    public static Label VY;
    public static Label VZ;
    public static Label MX;
    public static Label MY;
    public static Label MZ;
    public static Label widthT;
    public static Label heightT;
    public static Label mainRT;
    public static Label extraXT;
    public static Label extraYT;
    public static Label AXT;
    public static Label VYT;
    public static Label VZT;
    public static Label MXT;
    public static Label MYT;
    public static Label MZT;
    public static HBox AXP;
    public static HBox VYP;
    public static HBox VZP;
    public static HBox MXP;
    public static HBox MYP;
    public static HBox MZP;
    public static HBox widthP;
    public static HBox heightP;
    public static HBox mainRP;
    public static HBox extraXP;
    public static HBox extraYP;

    public static VBox geomet;
    public static VBox design;
    public static HBox everything;

    public static Scene dataPanel() {
        Group root = new Group();
        Scene scene = new Scene(root);
        geometryTitle = new Label("Geometry");
        designTitle = new Label("Design Actions");
        mainRT = new Label("continuous Reinforcement");
        extraXT = new Label("extra Reinforcement,X");
        extraYT = new Label("extra Reinforcement,Y");
        widthT = new Label("width, mm");
        heightT = new Label("height, mm");
        AXT = new Label("A X-X");
        VYT = new Label("V Y-Y");
        VZT = new Label("V Z-Z");
        MXT = new Label("T X-X");
        MYT = new Label("M Y-Y");
        MZT = new Label("M Z-Z");
        AXP = new HBox();
        VYP = new HBox();
        VZP = new HBox();
        MXP = new HBox();
        MYP = new HBox();
        MZP = new HBox();
        widthP = new HBox();
        heightP = new HBox();
        mainRP = new HBox();
        extraXP = new HBox();
        extraYP = new HBox();
        geomet = new VBox();
        design = new VBox();
        width = new Label();
        height = new Label();
        mainR = new Label();
        extraX = new Label();
        extraY = new Label();
        AX = new Label();
        VY = new Label();
        VZ = new Label();
        MX = new Label();
        MY = new Label();
        MZ = new Label();
        AX.setFont(javafx.scene.text.Font.font("Aliquam", 13));
        AXT.setFont(javafx.scene.text.Font.font("Aliquam", 13));
        VYT.setFont(javafx.scene.text.Font.font("Aliquam", 13));
        VY.setFont(javafx.scene.text.Font.font("Aliquam", 13));
        VZT.setFont(javafx.scene.text.Font.font("Aliquam", 13));
        VZ.setFont(javafx.scene.text.Font.font("Aliquam", 13));
        MXT.setFont(javafx.scene.text.Font.font("Aliquam", 13));
        MX.setFont(javafx.scene.text.Font.font("Aliquam", 13));
        MYT.setFont(javafx.scene.text.Font.font("Aliquam", 13));
        MY.setFont(javafx.scene.text.Font.font("Aliquam", 13));
        MZT.setFont(javafx.scene.text.Font.font("Aliquam", 13));
        MZ.setFont(javafx.scene.text.Font.font("Aliquam", 13));
        widthT.setFont(javafx.scene.text.Font.font("Aliquam", 13));
        width.setFont(javafx.scene.text.Font.font("Aliquam", 13));
        heightT.setFont(javafx.scene.text.Font.font("Aliquam", 13));
        height.setFont(javafx.scene.text.Font.font("Aliquam", 13));
        mainRT.setFont(javafx.scene.text.Font.font("Aliquam", 13));
        mainR.setFont(javafx.scene.text.Font.font("Aliquam", 13));
        extraXT.setFont(javafx.scene.text.Font.font("Aliquam", 13));
        extraX.setFont(javafx.scene.text.Font.font("Aliquam", 13));
        extraYT.setFont(javafx.scene.text.Font.font("Aliquam", 13));
        extraY.setFont(javafx.scene.text.Font.font("Aliquam", 13));
        AX.setFont(javafx.scene.text.Font.font("Aliquam", 13));
        geometryTitle.setFont(javafx.scene.text.Font.font("Aliquam", 13));
        designTitle.setFont(javafx.scene.text.Font.font("Aliquam", 13));

        AXP.setSpacing(8.0);
        AXP.getChildren().add(AXT);
        AXP.getChildren().add(AX);
        AXP.setSpacing(8.0);
        VYP.getChildren().add(VYT);
        VYP.getChildren().add(VY);
        AXP.setSpacing(8.0);
        VZP.getChildren().add(VZT);
        VZP.getChildren().add(VZ);
        AXP.setSpacing(8.0);
        MXP.getChildren().add(MXT);
        MXP.getChildren().add(MX);
        AXP.setSpacing(8.0);
        MYP.getChildren().add(MYT);
        MYP.getChildren().add(MY);
        AXP.setSpacing(8.0);
        MZP.getChildren().add(MZT);
        MZP.getChildren().add(MZ);

        AXP.setSpacing(8.0);
        widthP.getChildren().add(widthT);
        widthP.getChildren().add(width);
        AXP.setSpacing(8.0);

        heightP.getChildren().add(heightT);
        heightP.getChildren().add(height);
        AXP.setSpacing(8.0);

        mainRP.getChildren().add(mainRT);
        mainRP.getChildren().add(mainR);

        AXP.setSpacing(8.0);
        extraXP.getChildren().add(extraXT);
        extraXP.getChildren().add(extraX);
        AXP.setSpacing(8.0);

        extraYP.getChildren().add(extraYT);
        extraYP.getChildren().add(extraY);

        AXP.setSpacing(8.0);

        design.getChildren().add(designTitle);
        design.getChildren().add(AXP);
        design.getChildren().add(VYP);
        design.getChildren().add(VZP);
        design.getChildren().add(MXP);
        design.getChildren().add(MYP);
        design.getChildren().add(MZP);

        AXP.setSpacing(8.0);
        geomet.getChildren().add(geometryTitle);
        geomet.getChildren().add(widthP);
        geomet.getChildren().add(heightP);
        geomet.getChildren().add(mainRP);
        geomet.getChildren().add(extraXP);
        geomet.getChildren().add(extraYP);

        everything.getChildren().add(geomet);
        everything.getChildren().add(design);
        root.getChildren().add(everything);
        return scene;
    }

    public static void main(String[] args) {
        JFrame x = new JFrame();
        dataPanel test = new dataPanel();
        x.pack();
        x.setVisible(true);
        x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
