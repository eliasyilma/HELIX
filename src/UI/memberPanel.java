/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import SAM3D.Joint3;
import SAM3D.Load3;
import SAM3D.Member3;
import com.jfoenix.controls.JFXRadioButton;
import java.awt.Color;
import java.text.DecimalFormat;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import static UI.SectionInfoFX.DiagramPanel;
import static UI.SectionInfoFX.GraphPanel;
import static UI.SectionInfoFX.SectionPanel;
import static UI.SectionInfoFX.angle;
import static UI.SectionInfoFX.darkGreen;
import static UI.SectionInfoFX.frameH;
import static UI.SectionInfoFX.frameW;
import static UI.SectionInfoFX.panelH;
import static UI.SectionInfoFX.panelW;
import static UI.VandMPanel.internalActionCoordinates;

/**
 *
 * @author user
 */
public class memberPanel {

    public static Member3 mem;
    static VandMPanel DiagramPanel;
    public static Label geometryTitle;
    public static Label designTitle;
    public static Label reinforcementTitle;
    public static Label width;
    public static Label height;
    public static Label mainR;
    public static Label extraX;
    public static Label extraY;
    public static Label shearSpL;
    public static Label shearSpM;
    public static Label shearSpR;

    public static Label AX;
    public static Label VY;
    public static Label VZ;
    public static Label MX;
    public static Label MY;
    public static Label MZ;
    public static Label SY;
    public static Label SZ;
    public static Label BI;
    public static Label ST;
    public static Label T;
    public static Label EfficiencyT;
    public static Label SYT;
    public static Label SZT;
    public static Label BIT;
    public static Label STT;
    public static Label TT;

    public static Label widthT;
    public static Label heightT;
    public static Label mainRT;
    public static Label extraXT;
    public static Label extraYT;
    public static Label shearSpLT;
    public static Label shearSpMT;
    public static Label shearSpRT;

    public static Label memberIDT;
    public static Label memberID;
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
    public static HBox SYP;
    public static HBox SZP;
    public static HBox BIP;
    public static HBox STP;
    public static HBox TP;

    public static HBox widthP;
    public static HBox heightP;
    public static HBox mainRP;
    public static HBox extraXP;
    public static HBox extraYP;
    public static HBox shearSPPL;
    public static HBox shearSPPM;
    public static HBox shearSPPR;

    public static HBox memberIDP;
    public static VBox geomet;
    public static VBox design;
    public static VBox efficiency;
    public static HBox everything;
    public static VBox subroot;
    public static DecimalFormat stressFormatter = new DecimalFormat("0.00");

    private static void initFX(JFXPanel fxPanel1, JFXPanel fxPanel2) {
// This method is invoked on the JavaFX thread
        Scene scene2 = createScene2();
        Scene scene1 = dataPanel();
        fxPanel1.setScene(scene1);
        fxPanel2.setScene(scene2);

    }

    private static Scene createScene2() {
        Group root = new Group();
        Scene scene = new Scene(root);
        VBox vb = new VBox();
        HBox hb = new HBox();
        vb.setSpacing(12);
        hb.setSpacing(60);
        JFXRadioButton Ax = new JFXRadioButton("AXIAL FORCE");
        JFXRadioButton Vy = new JFXRadioButton("SHEAR 2-2");
        JFXRadioButton Vz = new JFXRadioButton("SHEAR 3-3");
        JFXRadioButton Tx = new JFXRadioButton("TORSION");
        JFXRadioButton My = new JFXRadioButton("MOMENT 2-2");
        JFXRadioButton Mz = new JFXRadioButton("MOMENT 3-3");
        Mz.setSelected(true);
        Ax.setFont(javafx.scene.text.Font.font("Aliquam", 15));
        Vy.setFont(javafx.scene.text.Font.font("Aliquam", 15));
        Vz.setFont(javafx.scene.text.Font.font("Aliquam", 15));
        Tx.setFont(javafx.scene.text.Font.font("Aliquam", 15));
        My.setFont(javafx.scene.text.Font.font("Aliquam", 15));
        Mz.setFont(javafx.scene.text.Font.font("Aliquam", 15));

        Ax.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, (e) -> {
            Ax.setSelected(true);
            Vy.setSelected(false);
            Vz.setSelected(false);
            Tx.setSelected(false);
            My.setSelected(false);
            Mz.setSelected(false);
            System.out.println(Ax.isSelected() + " " + Vy.isSelected() + " " + Vz.isSelected() + " " + Tx.isSelected() + " " + My.isSelected() + " " + Mz.isSelected());
            DiagramPanel.currDiagType = 0;
            DiagramPanel.repaint();
        });
        Vy.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, (e) -> {
            Ax.setSelected(false);
            Vy.setSelected(true);
            Vz.setSelected(false);
            Tx.setSelected(false);
            My.setSelected(false);
            Mz.setSelected(false);
            DiagramPanel.currDiagType = 1;
            DiagramPanel.repaint();

            System.out.println(Ax.isSelected() + " " + Vy.isSelected() + " " + Vz.isSelected() + " " + Tx.isSelected() + " " + My.isSelected() + " " + Mz.isSelected());

        });
        Vz.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, (e) -> {
            Ax.setSelected(false);
            Vy.setSelected(false);
            Vz.setSelected(true);
            Tx.setSelected(false);
            My.setSelected(false);
            Mz.setSelected(false);
            DiagramPanel.currDiagType = 2;
            DiagramPanel.repaint();

            System.out.println(Ax.isSelected() + " " + Vy.isSelected() + " " + Vz.isSelected() + " " + Tx.isSelected() + " " + My.isSelected() + " " + Mz.isSelected());

        });
        Tx.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, (e) -> {
            Ax.setSelected(false);
            Vy.setSelected(false);
            Vz.setSelected(false);
            Tx.setSelected(true);
            My.setSelected(false);
            Mz.setSelected(false);
            DiagramPanel.currDiagType = 3;
            DiagramPanel.repaint();

            System.out.println(Ax.isSelected() + " " + Vy.isSelected() + " " + Vz.isSelected() + " " + Tx.isSelected() + " " + My.isSelected() + " " + Mz.isSelected());

        });
        My.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, (e) -> {
            Ax.setSelected(false);
            Vy.setSelected(false);
            Vz.setSelected(false);
            Tx.setSelected(false);
            My.setSelected(true);
            Mz.setSelected(false);
            DiagramPanel.currDiagType = 4;
            DiagramPanel.repaint();

            System.out.println(Ax.isSelected() + " " + Vy.isSelected() + " " + Vz.isSelected() + " " + Tx.isSelected() + " " + My.isSelected() + " " + Mz.isSelected());

        });
        Mz.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, (e) -> {
            Ax.setSelected(false);
            Vy.setSelected(false);
            Vz.setSelected(false);
            Tx.setSelected(false);
            My.setSelected(false);
            Mz.setSelected(true);
            DiagramPanel.currDiagType = 5;
            DiagramPanel.repaint();

            System.out.println(Ax.isSelected() + " " + Vy.isSelected() + " " + Vz.isSelected() + " " + Tx.isSelected() + " " + My.isSelected() + " " + Mz.isSelected());

        });
        vb.getChildren().addAll(new HBox(), Ax, Vy, Vz, Tx, My, Mz);
        root.getChildren().add(vb);
        return (scene);
    }

    public static Scene dataPanel() {
        Group root = new Group();
        Scene scene = new Scene(root);

        memberIDT = new Label("MEMBER ID: ");
        everything = new HBox();
        efficiency = new VBox();
        VBox efficiencyN = new VBox();
        VBox geometryN = new VBox();
        VBox designN = new VBox();
        HBox efficiencysRoot = new HBox();
        HBox geometrysRoot = new HBox();
        HBox designsRoot = new HBox();
        VBox efficiencyRoot = new VBox();
        VBox geometryRoot = new VBox();
        VBox designRoot = new VBox();

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
        shearSPPL = new HBox();
        shearSPPM = new HBox();
        shearSPPR = new HBox();

        SZP = new HBox();
        BIP = new HBox();
        SYP = new HBox();
        STP = new HBox();
        TP = new HBox();
        geomet = new VBox();
        design = new VBox();
        geometryTitle = new Label("Geometry");
        designTitle = new Label("Design Actions");
        reinforcementTitle = new Label(" Reinforcement");
        mainRT = new Label("MAIN ");
        extraXT = new Label("EXTRA-Y ");
        extraYT = new Label("EXTRA-Z ");
        shearSpLT = new Label("LINK SP.L");
        shearSpMT = new Label("LINK SP.M");
        shearSpRT = new Label("LINK SP.R");

        widthT = new Label("WIDTH");
        heightT = new Label("HEIGHT");
        AXT = new Label("A X-X");
        VYT = new Label("V Y-Y");
        VZT = new Label("V Z-Z");
        MXT = new Label("T X-X");
        MYT = new Label("M Y-Y");
        MZT = new Label("M Z-Z");
        SYT = new Label("SHEAR Y-Y");
        SZT = new Label("SHEAR Z-Z");
        BIT = new Label("P-MY-MZ");
        STT = new Label("SHEAR-TORSION");
        TT = new Label("TORSION X-X");

        memberID = new Label("" + mem.getMemberID());
        width = new Label("" + mem.getXsecNegLeft().Breadth + " mm");
        height = new Label("" + mem.getXsecNegLeft().Height + " mm");
        mainR = new Label(" " + mem.getXsecNegLeft().reBar.main_num + " \u03D5 " + (int) mem.getXsecNegLeft().reBar.main_dia);
        extraX = new Label(" " + mem.getXsecNegLeft().reBar.extra_x_numT + " \u03D5 " + (int) mem.getXsecNegLeft().reBar.extra_x_diaT + "(TL) , " + mem.getXsecPos().reBar.extra_x_numB + " \u03D5 " + (int) mem.getXsecPos().reBar.extra_x_diaB + "(B)");
        extraY = new Label(" " + mem.getXsecNegLeft().reBar.extra_y_numL + " \u03D5 " + (int) mem.getXsecNegLeft().reBar.extra_y_diaL + "(L) , " + mem.getXsecPos().reBar.extra_y_numR + " \u03D5 " + (int) mem.getXsecPos().reBar.extra_y_diaR + "(R)");
        shearSpL = new Label(" \u03D58 c/c " + mem.getXsecNegLeft().shearS + " mm");
        shearSpM = new Label(" \u03D58 c/c " + mem.getXsecPos().shearS + " mm");
        shearSpR = new Label(" \u03D58 c/c " + mem.getXsecNegRight().shearS + " mm");

        AX = new Label(" " + stressFormatter.format(Math.max(mem.maxAx, Math.abs(mem.minAx))) + " kN");
        VY = new Label(" " + stressFormatter.format(Math.max(mem.maxVy, Math.abs(mem.minVy))) + " kN");
        VZ = new Label(" " + stressFormatter.format(Math.max(mem.maxVz, Math.abs(mem.minVz))) + " kN");
        MX = new Label(" " + stressFormatter.format(Math.max(mem.maxMx, Math.abs(mem.minMx))) + " kN.m.");
        MY = new Label(" " + stressFormatter.format(Math.max(mem.maxMy, Math.abs(mem.minMy))) + " kN.m.");
        MZ = new Label(" " + stressFormatter.format(Math.max(mem.maxMz, Math.abs(mem.minMz))) + " kN.m.");
        BI = new Label(" " + stressFormatter.format(mem.biaxialEff * 100) + "%");
        SY = new Label(" " + stressFormatter.format(mem.shearYEff * 100) + "%");
        SZ = new Label(" " + stressFormatter.format(mem.shearZEff * 100) + "%");
        ST = new Label(" " + stressFormatter.format(mem.STEff * 100) + "%");
        T = new Label(" " + stressFormatter.format(mem.TorsionEff * 100) + "%");

        EfficiencyT = new Label("Efficiency");
        memberID.setFont(javafx.scene.text.Font.font("Aliquam", 30));
        memberIDT.setFont(javafx.scene.text.Font.font("Aliquam", 30));
        AX.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        AXT.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        VYT.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        VY.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        VZT.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        VZ.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        MXT.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        MX.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        MYT.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        MY.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        MZT.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        MZ.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        widthT.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        width.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        heightT.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        height.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        mainRT.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        mainR.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        extraXT.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        extraX.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        extraYT.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        extraY.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        shearSpLT.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        shearSpMT.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        shearSpRT.setFont(javafx.scene.text.Font.font("Aliquam", 20));

        shearSpL.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        shearSpM.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        shearSpR.setFont(javafx.scene.text.Font.font("Aliquam", 20));

        AX.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        BIT.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        BI.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        SZT.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        SZ.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        SYT.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        SY.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        STT.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        ST.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        TT.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        T.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        EfficiencyT.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        geometryTitle.setFont(javafx.scene.text.Font.font("Aliquam", 20));
        designTitle.setFont(javafx.scene.text.Font.font("Aliquam", 20));

        memberIDP = new HBox();
        memberIDP.setSpacing(15.0);
        memberIDP.getChildren().add(memberIDT);
        memberIDP.getChildren().add(memberID);
        efficiencyN.setSpacing(10.0);
        designN.setSpacing(10.0);
        geometryN.setSpacing(10.0);

        AXP.setSpacing(10.0);
        AXP.getChildren().add(AXT);
        designN.getChildren().add(AX);
        VYP.setSpacing(10.0);
        VYP.getChildren().add(VYT);
        designN.getChildren().add(VY);
        VZP.setSpacing(10.0);
        VZP.getChildren().add(VZT);
        designN.getChildren().add(VZ);
        MXP.setSpacing(10.0);
        MXP.getChildren().add(MXT);
        designN.getChildren().add(MX);
        MYP.setSpacing(10.0);
        MYP.getChildren().add(MYT);
        designN.getChildren().add(MY);
        MZP.setSpacing(10.0);
        MZP.getChildren().add(MZT);
        designN.getChildren().add(MZ);

        widthP.setSpacing(20.0);
        widthP.getChildren().add(widthT);
        geometryN.getChildren().add(width);

        heightP.setSpacing(20.0);
        heightP.getChildren().add(heightT);
        geometryN.getChildren().add(height);

        mainRP.setSpacing(20.0);
        mainRP.getChildren().add(mainRT);
        geometryN.getChildren().add(mainR);

        extraXP.setSpacing(20.0);
        extraXP.getChildren().add(extraXT);
        geometryN.getChildren().add(extraX);

        extraYP.setSpacing(20.0);
        extraYP.getChildren().add(extraYT);
        geometryN.getChildren().add(extraY);

        shearSPPL.setSpacing(20.0);
        shearSPPL.getChildren().add(shearSpLT);
        geometryN.getChildren().add(shearSpL);

        shearSPPM.setSpacing(20.0);
        shearSPPM.getChildren().add(shearSpMT);
        geometryN.getChildren().add(shearSpM);

        shearSPPR.setSpacing(20.0);
        shearSPPR.getChildren().add(shearSpRT);
        geometryN.getChildren().add(shearSpR);

        design.setSpacing(10.0);
        design.getChildren().add(designTitle);
        design.getChildren().add(AXP);
        design.getChildren().add(VYP);
        design.getChildren().add(VZP);
        design.getChildren().add(MXP);
        design.getChildren().add(MYP);
        design.getChildren().add(MZP);

        geomet.setSpacing(10.0);
        geomet.getChildren().add(geometryTitle);
        geomet.getChildren().add(widthP);
        geomet.getChildren().add(heightP);
        geomet.getChildren().add(mainRP);
        geomet.getChildren().add(extraXP);
        geomet.getChildren().add(extraYP);
        geomet.getChildren().add(shearSPPL);
        geomet.getChildren().add(shearSPPM);
        geomet.getChildren().add(shearSPPR);

        BIP.setSpacing(20.0);
        BIP.getChildren().add(BIT);
        efficiencyN.getChildren().add(BI);

        SYP.setSpacing(20.0);
        SYP.getChildren().add(SYT);
        efficiencyN.getChildren().add(SY);

        SZP.setSpacing(20.0);
        SZP.getChildren().add(SZT);
        efficiencyN.getChildren().add(SZ);

        STP.setSpacing(20.0);
        STP.getChildren().add(STT);
        efficiencyN.getChildren().add(ST);

        TP.setSpacing(20.0);
        TP.getChildren().add(TT);
        efficiencyN.getChildren().add(T);

        efficiency.setSpacing(10.0);
        efficiency.getChildren().add(EfficiencyT);
        efficiency.getChildren().add(BIP);
        efficiency.getChildren().add(SYP);
        efficiency.getChildren().add(SZP);
        efficiency.getChildren().add(STP);
        efficiency.getChildren().add(TP);

        everything.setSpacing(40);
        everything.getChildren().add(memberIDP);

        efficiencysRoot.getChildren().addAll(efficiency, efficiencyN);
        geometrysRoot.getChildren().addAll(geomet, geometryN);
        designsRoot.getChildren().addAll(design, designN);

        efficiencysRoot.setSpacing(15);
        efficiencyRoot.setSpacing(15);
        designsRoot.setSpacing(15);
        designRoot.setSpacing(15);
        geometrysRoot.setSpacing(15);
        geometryRoot.setSpacing(15);

        efficiencyRoot.getChildren().addAll(EfficiencyT, efficiencysRoot);
        geometryRoot.getChildren().addAll(geometryTitle, geometrysRoot);
        designRoot.getChildren().addAll(designTitle, designsRoot);

        everything.getChildren().add(geometryRoot);
        everything.getChildren().add(designRoot);
        everything.getChildren().add(efficiencyRoot);

        subroot = new VBox();
        subroot.getChildren().addAll(memberIDP, everything);
        root.getChildren().add(subroot);
        return scene;
    }

    public static void initAndShowGUI(Member3 m) {
// This method is invoked on the EDT thread
        JFrame SectionFrame = new JFrame("Member Information");
        mem = m;
        final JFXPanel fxPanel = new JFXPanel();
        final JFXPanel fxPanel2 = new JFXPanel();
        GraphPanel = new JPanel();
        DiagramPanel = new VandMPanel();
        Border Lborder = new LineBorder(darkGreen, 2);
        SectionFrame.setLayout(null);
        SectionFrame.setSize((int) 780, 520);
        SectionFrame.setLocationRelativeTo(null);
        SectionFrame.add(GraphPanel);
        fxPanel2.setLocation((int) 610, (int) 10);
        fxPanel2.setSize((int) 130, 190);
        fxPanel.setLocation(10, 210);
        fxPanel.setSize(700, 340);
        width = new Label("" + m.getB());
        height = new Label("" + m.getH());
//        mainR = new Label("" + m.getXsecNegLeft().reBar.main_area);
//        extraX = new Label("" + m.getXsecNegLeft().reBar.extra_x_areaT);
//        extraY = new Label("" + m.getXsecNegLeft().reBar.extra_y_areaL);
        AX = new Label("" + m.maxAx);
        System.out.println("" + m.maxAx);
        VY = new Label();
        VY.setText("" + m.maxVy);
        VZ = new Label("" + m.maxVz);
        MX = new Label("" + m.maxMx);
        MY = new Label("" + m.maxMy);
        MZ = new Label("" + m.maxMz);
        DiagramPanel.setSize((int) 600, 190);
        DiagramPanel.setLocation(10, 10);
//        fxPanel2.setLocation(50, (int) h / 2 + panelH + 190);
        Joint3 j5 = new Joint3(5, -5, 5, 5, 5, 5);
        Joint3 j6 = new Joint3(6, 5, 5, 5, 5, 5);
        double W = 100;
        DiagramPanel.width = DiagramPanel.getWidth() - 140;
        DiagramPanel.height = DiagramPanel.getHeight() - 40;
        DiagramPanel.maxX = DiagramPanel.width + DiagramPanel.insets;
        DiagramPanel.minX = DiagramPanel.insets;
        System.out.println("aaaaaaa" + DiagramPanel.width + "aaaaaaa" + DiagramPanel.height);
        DiagramPanel.setBorder(new LineBorder(darkGreen, 2));
        DiagramPanel.mem = m;
        SectionFrame.getContentPane().setBackground(Color.WHITE);
        SectionFrame.add(fxPanel2);
        SectionFrame.add(fxPanel);
        SectionFrame.add(DiagramPanel);
        SectionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        SectionFrame.setVisible(true);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel, fxPanel2);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Joint3 j5 = new Joint3(5, -5, 5, 5, 5, 5);
                Joint3 j6 = new Joint3(6, 5, 5, 5, 5, 5);
                double W = 100;
                Load3 l1 = new Load3(0, 0.5, 0, -W, 0, 0, 0, 0);
                Load3 l2 = new Load3(1, 0.5, 0, W / 10, 0, 0, 0, 0);

                Member3 t5 = new Member3(5, j5, j6);
                t5.addLoads(l1);
//        t5.addLoads(l2);

                t5.memLength();
                t5.sFx = -7.5;
                t5.sFy = 50;
                t5.sFz = 0;
                t5.sMx = 0;
                t5.sMy = 0;
                t5.sMz = -75;
                t5.eFx = 7.5;
                t5.eFy = 50;
                t5.eFz = 0;
                t5.eMx = 0;
                t5.eMy = 0;
                t5.eMz = 75;
                Section x = new Section(8, 150, 300, 400);
                t5.setSection(x);
                internalActionCoordinates(t5);

                initAndShowGUI(t5);
            }
        });
    }

}
