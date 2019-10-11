/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import static GAVAL3D.Algorithm.Evolve;
import GAVAL3D.Individual;
//import GAval.DrawGraph;
import SAM3D.Joint3;
import SAM3D.Member3;
import SAM3D.Structure3;
import UI.SplashScreen;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXDrawersStack;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import static javafx.scene.paint.Color.rgb;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import static UI.SectionInfoFX.createChart;
import static UI.VandMPanel.internalActionCoordinates;

/**
 *
 * @author user
 */
public class drawerDemoMod extends JPanel {

    static double b, h;
    Point2D[] mainR;
    Point2D[] extraRX;
    Point2D[] extraRY;
    int mainDia, extraDiaX, extraDiaY;
    static int dx, dy; //center of rotation
    int mx, my;  // the most recently recorded mouse coordinates
    static double angle;
    int indent = 10;
    int minY, maxY, minX, maxX;
    int NAdepth;
    double Area;
    double concArea;
    double AxForce, AxForceConC, CmForceStl, TsForceStl;
    Point2D currentCentroid;
    Point2D center;
    Point2D leftmost, rightmost, topmost, bottommost;
    static double fyd = 260.86;
    static double fcd = 11.333;
    static double eyd = (fyd / 200000);
    static int frameW = 700, panelW = 600;
    static int frameH = 350, panelH = 200;
    static double ecm = 0.0035;
    static double Eval, Gval, fcuval, fykval;
    DecimalFormat strainFormatter = new DecimalFormat("0.000");
    DecimalFormat stressFormatter = new DecimalFormat("0.00");
    static WireframeViewer wirePanel;
    static JPanel GraphPanel;
    static JLabel statusLabel;
    static JFrame mainW;
    static Label genNo;
    static JFXDrawer progressDrawer;
    static JFXSlider hor_left;
    static ChartPanel cp;
    static WireframeViewer mainF;
    double totalSteelMoment;
    double concMoment;
    double resMoment;
    double maxDepth;
    /*
     color definitions
     */
    static Color pomegranateR = new Color(196, 57, 38);
    static Color emeraldG = new Color(102, 232, 160);
    static Color darkGreen = new Color(12, 113, 24);
    static Color belizeHoleB = new Color(33, 127, 188);
    static Color cloudsW = new Color(236, 240, 241);
    Color BACKGROUNDCOLOR = cloudsW;
    static BackgroundImage materialImage, loadImage, restrImage, runImage, sectionImage, designImage, selJointImage, selMemberImage, drawImage, dnaImage;

    public static void getImages() {
        materialImage = new BackgroundImage(new Image(drawerDemoMod.class.getResource("/resources/material-iconm1.png").toExternalForm()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        loadImage = new BackgroundImage(new Image(drawerDemoMod.class.getResource("/resources/load-iconm1.png").toExternalForm()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        restrImage = new BackgroundImage(new Image(drawerDemoMod.class.getResource("/resources/restraint-iconm1.png").toExternalForm()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        runImage = new BackgroundImage(new Image(drawerDemoMod.class.getResource("/resources/run-iconm1.png").toExternalForm()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        sectionImage = new BackgroundImage(new Image(drawerDemoMod.class.getResource("/resources/section-iconm1.png").toExternalForm()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        designImage = new BackgroundImage(new Image(drawerDemoMod.class.getResource("/resources/design-iconm1.png").toExternalForm()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        selJointImage = new BackgroundImage(new Image(drawerDemoMod.class.getResource("/resources/selJoint-iconm1.png").toExternalForm()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        selMemberImage = new BackgroundImage(new Image(drawerDemoMod.class.getResource("/resources/selMember-iconm1.png").toExternalForm()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        drawImage = new BackgroundImage(new Image(drawerDemoMod.class.getResource("/resources/draw-iconm1.png").toExternalForm()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        dnaImage = new BackgroundImage(new Image(drawerDemoMod.class.getResource("/resources/dna-iconm1.png").toExternalForm()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);

    }

    private static void initFX(JFXPanel fxPanel) {
// This method is invoked on the JavaFX thread
        Scene scene = createScene();
        fxPanel.setScene(scene);

    }

    public static void initAndShowGUI() {
// This method is invoked on the EDT thread
        mainW = new JFrame("HELIX v0.1");
        mainF = new WireframeViewer();
        final JFXPanel fxPanel = new JFXPanel();
        wirePanel = new WireframeViewer();
        statusLabel = new JLabel();
        statusLabel.setSize(840, 30);
        statusLabel.setLocation(0, 552);
        double b = 400;
        double h = 500;
        double mx = 200, my = 200;
        angle = Math.atan(mx / my) * 180.0 / Math.PI;
        dx = (int) (b * 0.5);
        dy = (int) (h * 0.45);
        System.out.println("angle: " + angle);
//        Section sect = new Section(17, 300, b, h);
        Border Lborder = new LineBorder(pomegranateR, 2);
        wirePanel.setBorder(Lborder);

        mainW.setLayout(null);
        mainW.setLocation(100, 100);
        mainW.setSize((int) 840, 620);
        wirePanel.setSize((int) 550, (int) 550);
        wirePanel.setLocation(272, 0);
        mainW.add(fxPanel);
        mainW.add(wirePanel);

        mainW.add(statusLabel);
        statusLabel.setFont(new Font("Century gothic", Font.TRUETYPE_FONT, 16));
        statusLabel.setText("Ready");
        fxPanel.setLocation(0, 0);
        fxPanel.setSize(270, 550);
        wirePanel.setBackground(Color.white);
        mainW.setVisible(true);
        mainW.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        System.out.println("Main re:" + sect.reBar.main_num + "d" + sect.reBar.main_dia);
//        System.out.println("ExtraX re:" + sect.reBar.extra_x_num + "d" + sect.reBar.extra_x_dia);
//        System.out.println("ExtraY re:" + sect.reBar.extra_y_num + "d" + sect.reBar.extra_y_dia);
//        final XYSeries fitVSgen = new XYSeries("first", false, false);
//        XYDataset xyDataset = new XYSeriesCollection(fitVSgen);
//        JFreeChart chart = createChart(xyDataset, "Biaxial Interaction", "MOMENT, KN.m.", "FORCE, KN");
//        ChartPanel cp = new ChartPanel(chart) {
//
//            @Override
//            public Dimension getPreferredSize() {
//                return new Dimension((int) (b / 2 + panelW * 0.6), (int) h / 2 + panelH);
//            }
//        };
//        GraphPanel.add(cp);
        mainW.setVisible(true);
//        mainW.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);
            }
        });
    }

    private static Scene createScene() {
        Group root = new Group();
        Scene scene = new Scene(root);
        Label materialTitle = new Label(" Materials...");
        Label concTitle = new Label(" Concrete");
        Label steelTitle = new Label(" Steel");

        materialTitle.setFont(javafx.scene.text.Font.font("Aliquam", 46));
        materialTitle.setTextFill(rgb(1, 161, 133));
        concTitle.setFont(javafx.scene.text.Font.font("Aliquam", 26));
        concTitle.setTextFill(rgb(1, 161, 133));
        steelTitle.setFont(javafx.scene.text.Font.font("Aliquam", 26));
        steelTitle.setTextFill(rgb(1, 161, 133));

        StackPane content = new StackPane();
//material properties---------------------------------------------------------------
        //E ,g fck, fyk accept cancel
        JFXDrawer materialDrawer = new JFXDrawer();
        VBox materialDrawerPane = new VBox();
        materialDrawerPane.setSpacing(14.0);
        materialDrawerPane.setPadding(new Insets(20, 50, 50, 20));

        JFXCheckBox defaultMat = new JFXCheckBox("Defaults");
        defaultMat.setFont(javafx.scene.text.Font.font("Aliquam", 19));
        defaultMat.setCheckedColor(rgb(33, 127, 188));

        JFXTextField E = new JFXTextField();
        E.setFocusColor(rgb(0, 98, 41));
        E.setMaxWidth(250);
        E.setLabelFloat(true);
        E.setPromptText("Young's Modulus,Ec, GPa");
        E.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");

        JFXTextField Es = new JFXTextField();
        Es.setFocusColor(rgb(0, 98, 41));
        Es.setMaxWidth(250);
        Es.setLabelFloat(true);
        Es.setPromptText("Young's Modulus,Es, GPa");
        Es.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");

//        E.focusedProperty().addListener((arg0, oldValue, newValue) -> {
//            if (!newValue) { //when focus lost
//                if (!E.getText().matches("/^[+-]?((\\d+(\\.\\d*)?)|(\\.\\d+))$/")) {
//                    //when it not matches the pattern (1.0 - 6.0)
//                    //set the textField empty
//                    E.setText("");
//                }
//            }
//
//        });
        E.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("^[-]{1}[1-9]\\d*(\\.\\d+)?$")) {
                    E.setText(newValue.replaceAll("^[-]{1}[1-9]\\d*(\\.\\d+)?$", ""));
                }
            }
        });
        JFXTextField G = new JFXTextField();
        G.setFocusColor(rgb(0, 98, 41));
        G.setMaxWidth(250);
        G.setLabelFloat(true);
        G.setPromptText("Shear Modulus,G, GPa");
        G.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");

//        JFXTextField Gs = new JFXTextField();
//        Gs.setFocusColor(rgb(0,98,41));
//        Gs.setMaxWidth(250);
//        Gs.setLabelFloat(true);
//        Gs.setPromptText("Shear Modulus,G, GPa");
//        Gs.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");
        JFXTextField fyk = new JFXTextField();
        fyk.setFocusColor(rgb(0, 98, 41));
        fyk.setMaxWidth(250);
        fyk.setLabelFloat(true);
        fyk.setPromptText("Steel Yield Strength(fyk), MPa");
        fyk.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");

        JFXTextField fck = new JFXTextField();
        fck.setFocusColor(rgb(0, 98, 41));
        fck.setMaxWidth(250);
        fck.setLabelFloat(true);
        fck.setPromptText("Conc. Char. Strength(fck), MPa");
        fck.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");

        JFXButton acceptMat = new JFXButton("Accept");
        acceptMat.setButtonType(JFXButton.ButtonType.RAISED);
        acceptMat.setRipplerFill(rgb(238, 190, 182));
        acceptMat.setStyle("-fx-background-color:rgb(1,161,133);-fx-font-size: 16px;-fx-text-fill: WHITE;");

        JFXButton cancelMat = new JFXButton("Cancel");
        cancelMat.setButtonType(JFXButton.ButtonType.RAISED);
        cancelMat.setRipplerFill(rgb(238, 190, 182));
        cancelMat.setStyle("-fx-background-color:rgb(1,161,133);-fx-font-size: 16px;-fx-text-fill: WHITE;");

        HBox buttonPane = new HBox();
        buttonPane.setSpacing(30.0);
        buttonPane.getChildren().addAll(new HBox(), acceptMat, cancelMat);

        materialDrawerPane.getChildren().addAll(materialTitle, defaultMat, concTitle, fck, E, G, steelTitle, fyk, Es, buttonPane);
        materialDrawerPane.setStyle("-fx-background-color:rgb(236,240,241)");
        materialDrawer.setDefaultDrawerSize(300);
        materialDrawer.setDirection(JFXDrawer.DrawerDirection.LEFT);
        materialDrawer.setSidePane(materialDrawerPane);
        materialDrawer.setOverLayVisible(false);
        materialDrawer.setResizableOnDrag(false);
//-----------------------------------------------------------------------------------

//restraints-------------------------------------------------------------------------
        JFXDrawer restraintDrawer = new JFXDrawer();
        VBox restraintDrawerPane = new VBox();
        restraintDrawerPane.setSpacing(20.0);
        restraintDrawerPane.setPadding(new Insets(20, 50, 50, 20));

        Label restraintTitle = new Label("Restraints");
        restraintTitle.setFont(javafx.scene.text.Font.font("Aliquam", 46));
        restraintTitle.setTextFill(rgb(1, 161, 133));

        Label displacementTitle = new Label("Displacements");
        displacementTitle.setFont(javafx.scene.text.Font.font("Aliquam", 35));
        displacementTitle.setTextFill(rgb(1, 161, 133));
        JFXCheckBox dx = new JFXCheckBox("X");
        dx.setCheckedColor(rgb(153, 49, 29));
        JFXCheckBox dy = new JFXCheckBox("Y");
        dy.setCheckedColor(rgb(229, 136, 121));
        JFXCheckBox dz = new JFXCheckBox("Z");
        dz.setCheckedColor(rgb(255, 98, 29));

        Label rotationTitle = new Label(" Rotations");
        rotationTitle.setFont(javafx.scene.text.Font.font("Aliquam", 35));
        rotationTitle.setTextFill(rgb(1, 161, 133));
        JFXCheckBox rx = new JFXCheckBox("X");
        rx.setCheckedColor(rgb(153, 49, 29));
        JFXCheckBox ry = new JFXCheckBox("Y");
        ry.setCheckedColor(rgb(229, 136, 121));
        JFXCheckBox rz = new JFXCheckBox("Z");
        rz.setCheckedColor(rgb(255, 98, 29));

        JFXButton acceptRes = new JFXButton("Accept");
        acceptRes.setButtonType(JFXButton.ButtonType.RAISED);
        acceptRes.setRipplerFill(rgb(238, 190, 182));
        acceptRes.setStyle("-fx-background-color:rgb(1,161,133);-fx-font-size: 16px;-fx-text-fill: WHITE;");

        JFXButton cancelRes = new JFXButton("Cancel");
        cancelRes.setButtonType(JFXButton.ButtonType.RAISED);
        cancelRes.setRipplerFill(rgb(238, 190, 182));
        cancelRes.setStyle("-fx-background-color:rgb(1,161,133);-fx-font-size: 16px;-fx-text-fill: WHITE;");

        HBox buttonPaneRes = new HBox();
        buttonPaneRes.setSpacing(30.0);
        buttonPaneRes.getChildren().addAll(new HBox(), acceptRes, cancelRes);

        restraintDrawerPane.getChildren().addAll(new HBox(), restraintTitle, displacementTitle, dx, dy, dz, rotationTitle, rx, ry, rz, buttonPaneRes);
        restraintDrawerPane.setStyle("-fx-background-color:rgb(236,240,241)");
        restraintDrawer.setDefaultDrawerSize(300);
        restraintDrawer.setDirection(JFXDrawer.DrawerDirection.LEFT);
        restraintDrawer.setSidePane(restraintDrawerPane);
        restraintDrawer.setOverLayVisible(false);
        restraintDrawer.setResizableOnDrag(false);
//------------------------------------------------------------------------------------------------------
//genetic optimization toolbar--------------------------------------------------------------------------
        JFXDrawer GADrawer = new JFXDrawer();
        VBox GADrawerPane = new VBox();
        GADrawerPane.setSpacing(12.0);
        GADrawerPane.setPadding(new Insets(20, 50, 50, 20));

        Label GATitle = new Label(" Optimization");
        GATitle.setFont(javafx.scene.text.Font.font("Aliquam", 46));
        GATitle.setTextFill(rgb(1, 161, 133));

        JFXTextField PopSize = new JFXTextField();
        PopSize.setFocusColor(rgb(0, 98, 41));
        PopSize.setMaxWidth(250);
        PopSize.setLabelFloat(true);
        PopSize.setPromptText("Population Size");
        PopSize.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");

        JFXTextField IterationNo = new JFXTextField();
        IterationNo.setFocusColor(rgb(0, 98, 41));
        IterationNo.setMaxWidth(250);
        IterationNo.setLabelFloat(true);
        IterationNo.setPromptText("Number of Iterations");
        IterationNo.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");

        JFXTextField MutationProb = new JFXTextField();
        MutationProb.setFocusColor(rgb(0, 98, 41));
        MutationProb.setMaxWidth(250);
        MutationProb.setLabelFloat(true);
        MutationProb.setPromptText("Mutation Probability");
        MutationProb.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");

        JFXTextField XOverProb = new JFXTextField();
        XOverProb.setFocusColor(rgb(0, 98, 41));
        XOverProb.setMaxWidth(250);
        XOverProb.setLabelFloat(true);
        XOverProb.setPromptText("Crossover Probability");
        XOverProb.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");

        JFXTextField BiaxW = new JFXTextField();
        BiaxW.setFocusColor(rgb(0, 98, 41));
        BiaxW.setMaxWidth(250);
        BiaxW.setLabelFloat(true);
        BiaxW.setPromptText("Biaxial Weight, Kw");
        BiaxW.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");

        JFXCheckBox optBuild=new JFXCheckBox("OPTIMIZE AS BUILDING"); 
        optBuild.setCheckedColor(rgb(0, 98, 41));
        optBuild.setMaxWidth(250);
        
        
        JFXTextField ShearW = new JFXTextField();
        ShearW.setFocusColor(rgb(0, 98, 41));
        ShearW.setMaxWidth(250);
        ShearW.setLabelFloat(true);
        ShearW.setPromptText("Shear Weight, Kv");
        ShearW.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");

        JFXTextField TorsionW = new JFXTextField();
        TorsionW.setFocusColor(rgb(0, 98, 41));
        TorsionW.setMaxWidth(250);
        TorsionW.setLabelFloat(true);
        TorsionW.setPromptText("Torsion Weight, Kt");
        TorsionW.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");

        JFXButton acceptGA = new JFXButton("Simulate");
        acceptGA.setButtonType(JFXButton.ButtonType.RAISED);
        acceptGA.setRipplerFill(rgb(238, 190, 182));
        acceptGA.setStyle("-fx-background-color:rgb(1,161,133);-fx-font-size: 16px;-fx-text-fill: WHITE;");

        JFXButton cancelGA = new JFXButton("Cancel");
        cancelGA.setButtonType(JFXButton.ButtonType.RAISED);
        cancelGA.setRipplerFill(rgb(238, 190, 182));
        cancelGA.setStyle("-fx-background-color:rgb(1,161,133);-fx-font-size: 16px;-fx-text-fill: WHITE;");

        HBox buttonPaneGA = new HBox();
        buttonPaneGA.setSpacing(30.0);
        buttonPaneGA.getChildren().addAll(new HBox(), acceptGA, cancelGA);

        GADrawerPane.getChildren().addAll(GATitle, PopSize, IterationNo, MutationProb, XOverProb, BiaxW, ShearW,optBuild, buttonPaneGA);
        GADrawerPane.setStyle("-fx-background-color:rgb(236,240,241)");
        GADrawer.setDefaultDrawerSize(300);
        GADrawer.setDirection(JFXDrawer.DrawerDirection.LEFT);
        GADrawer.setSidePane(GADrawerPane);
        GADrawer.setOverLayVisible(false);
        GADrawer.setResizableOnDrag(false);
//---------------------------------------------------------------------------------
//Optimization Progress Dialog
        Label GAProg = new Label("Generation");
        GAProg.setFont(javafx.scene.text.Font.font("Aliquam", 46));
        GAProg.setTextFill(rgb(1, 161, 133));

        StackPane pane = new StackPane();

        JFXSpinner spin = new JFXSpinner();
        spin.setScaleX(5.0);
        spin.setScaleY(5.0);
        genNo = new Label("0");
        genNo.setFont(javafx.scene.text.Font.font("Aliquam", 46));
        genNo.setTextFill(rgb(1, 161, 133));
        pane.getChildren().addAll(spin, genNo);
        progressDrawer = new JFXDrawer();
        VBox progressDrawerPane = new VBox();
        progressDrawerPane.setSpacing(25.0);
        progressDrawerPane.setPadding(new Insets(20, 50, 50, 20));
        progressDrawerPane.getChildren().addAll(new HBox(), GAProg, pane);
        progressDrawerPane.setStyle("-fx-background-color:rgb(236,240,241)");
        progressDrawer.setDefaultDrawerSize(300);
        progressDrawer.setDirection(JFXDrawer.DrawerDirection.LEFT);
        progressDrawer.setSidePane(progressDrawerPane);
        progressDrawer.setOverLayVisible(false);
        progressDrawer.setResizableOnDrag(false);
//section dialog---------------------------------------------------------------------------------------------
        //b,h,As1,As2
        JFXTextField B = new JFXTextField();
        B.setFocusColor(rgb(0, 98, 41));
        B.setMaxWidth(250);
        B.setLabelFloat(true);
        B.setPromptText("Breadth,B, mm");
        B.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");

        JFXTextField H = new JFXTextField();
        H.setFocusColor(rgb(0, 98, 41));
        H.setMaxWidth(250);
        H.setLabelFloat(true);
        H.setPromptText("Height,H, mm");
        H.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");

        JFXTextField As1 = new JFXTextField();
        As1.setFocusColor(rgb(0, 98, 41));
        As1.setMaxWidth(250);
        As1.setLabelFloat(true);
        As1.setPromptText("Bottom Reinforcement, mm2");
        As1.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");

        JFXTextField As2 = new JFXTextField();
        As2.setFocusColor(rgb(0, 98, 41));
        As2.setMaxWidth(250);
        As2.setLabelFloat(true);
        As2.setPromptText("Top Reinforcement, mm2");
        As2.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");

        JFXButton acceptSect = new JFXButton("Accept");
        acceptSect.setButtonType(JFXButton.ButtonType.RAISED);
        acceptSect.setRipplerFill(rgb(238, 190, 182));
        acceptSect.setStyle("-fx-background-color:rgb(1,161,133);-fx-font-size: 16px;-fx-text-fill: WHITE;");

        JFXButton cancelSect = new JFXButton("Cancel");
        cancelSect.setButtonType(JFXButton.ButtonType.RAISED);
        cancelSect.setRipplerFill(rgb(238, 190, 182));
        cancelSect.setStyle("-fx-background-color:rgb(1,161,133);-fx-font-size: 16px;-fx-text-fill: WHITE;");

        HBox buttonPaneSect = new HBox();
        buttonPaneSect.setSpacing(30.0);
        buttonPaneSect.getChildren().addAll(new HBox(), acceptSect, cancelSect);

        JFXDrawer sectionDrawer = new JFXDrawer();
        VBox sectionDrawerPane = new VBox();
        sectionDrawerPane.setSpacing(25.0);
        sectionDrawerPane.setPadding(new Insets(20, 50, 50, 20));
        sectionDrawerPane.getChildren().addAll(new HBox(), B, H, As1, As2, buttonPaneSect);
        sectionDrawerPane.setStyle("-fx-background-color:rgb(236,240,241)");
        sectionDrawer.setDefaultDrawerSize(300);
        sectionDrawer.setDirection(JFXDrawer.DrawerDirection.LEFT);
        sectionDrawer.setSidePane(sectionDrawerPane);
        sectionDrawer.setOverLayVisible(false);
        sectionDrawer.setResizableOnDrag(false);
//-----------------------------------------------------------------------------------------------

//Loading dialog---------------------------------------------------------------------------------
        Label loadTitle = new Label(" Loads...");
        loadTitle.setFont(javafx.scene.text.Font.font("Aliquam", 46));
        loadTitle.setTextFill(rgb(1, 161, 133));

        //load properties...
        //E ,g fck, fyk accept cancel
        JFXDrawer loadDrawer = new JFXDrawer();
        VBox loadDrawerPane = new VBox();
        loadDrawerPane.setSpacing(20.0);
        loadDrawerPane.setPadding(new Insets(20, 50, 50, 20));

        HBox fx = new HBox();
        JFXCheckBox dist = new JFXCheckBox("DISTRIBUTED");
        dist.setFont(javafx.scene.text.Font.font("Aliquam", 19));
        dist.setCheckedColor(rgb(33, 127, 188));
        fx.setSpacing(25.0);
        JFXTextField fxM = new JFXTextField();
        fxM.setFocusColor(rgb(0, 98, 41));
        fxM.setMaxWidth(250);
        fxM.setLabelFloat(true);
        fxM.setPromptText("Force, X, KN");
        fxM.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");
        JFXTextField fxP = new JFXTextField();
        fxP.setFocusColor(rgb(0, 98, 41));
        fxP.setMaxWidth(250);
        fxP.setLabelFloat(true);
        fxP.setPromptText("Rel Position, X");
        fxP.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");
        fx.getChildren().addAll(fxM, fxP);

        HBox fy = new HBox();
        fy.setSpacing(20.0);
        JFXTextField fyM = new JFXTextField();
        fyM.setFocusColor(rgb(0, 98, 41));
        fyM.setMaxWidth(250);
        fyM.setLabelFloat(true);
        fyM.setPromptText("Force, Y, KN");
        fyM.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");
        JFXTextField fyP = new JFXTextField();
        fyP.setFocusColor(rgb(0, 98, 41));
        fyP.setMaxWidth(250);
        fyP.setLabelFloat(true);
        fyP.setPromptText("Rel. Position, Y");
        fyP.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");
        fy.getChildren().addAll(fyM, fyP);

        HBox fz = new HBox();
        fz.setSpacing(25.0);
        JFXTextField fzM = new JFXTextField();
        fzM.setFocusColor(rgb(0, 98, 41));
        fzM.setMaxWidth(250);
        fzM.setLabelFloat(true);
        fzM.setPromptText("Force, Z, KN");
        fzM.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");
        JFXTextField fzP = new JFXTextField();
        fzP.setFocusColor(rgb(0, 98, 41));
        fzP.setMaxWidth(250);
        fzP.setLabelFloat(true);
        fzP.setPromptText("Rel. Position, Z");
        fzP.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");
        fz.getChildren().addAll(fzM, fzP);

        HBox mx = new HBox();
        mx.setSpacing(30.0);
        JFXTextField mxM = new JFXTextField();
        mxM.setFocusColor(rgb(0, 98, 41));
        mxM.setMaxWidth(250);
        mxM.setLabelFloat(true);
        mxM.setPromptText("Moment, X, KN.m.");
        mxM.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");
        JFXTextField mxP = new JFXTextField();
        mxP.setFocusColor(rgb(0, 98, 41));
        mxP.setMaxWidth(250);
        mxP.setLabelFloat(true);
        mxP.setPromptText("Rel Position, X");
        mxP.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");
        mx.getChildren().addAll(mxM, mxP);

        HBox my = new HBox();
        my.setSpacing(30.0);
        JFXTextField myM = new JFXTextField();
        myM.setFocusColor(rgb(0, 98, 41));
        myM.setMaxWidth(250);
        myM.setLabelFloat(true);
        myM.setPromptText("Moment, Y, KN.m.");
        myM.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");
        JFXTextField myP = new JFXTextField();
        myP.setFocusColor(rgb(0, 98, 41));
        myP.setMaxWidth(250);
        myP.setLabelFloat(true);
        myP.setPromptText("Rel. Position, Y");
        myP.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");
        my.getChildren().addAll(myM, myP);

        HBox mz = new HBox();
        mz.setSpacing(30.0);
        JFXTextField mzM = new JFXTextField();
        mzM.setFocusColor(rgb(0, 98, 41));
        mzM.setMaxWidth(250);
        mzM.setLabelFloat(true);
        mzM.setPromptText("Moment, Z, KN.m.");
        mzM.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");
        JFXTextField mzP = new JFXTextField();
        mzP.setFocusColor(rgb(0, 98, 41));
        mzP.setMaxWidth(250);
        mzP.setLabelFloat(true);
        mzP.setPromptText("Rel. Position, Z");
        mzP.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");
        mz.getChildren().addAll(mzM, mzP);

        JFXButton apply = new JFXButton("Accept");
        apply.setButtonType(JFXButton.ButtonType.RAISED);
        apply.setRipplerFill(rgb(238, 190, 182));
        apply.setStyle("-fx-background-color:rgb(1,161,133);-fx-font-size: 16px;-fx-text-fill: WHITE;");

        JFXButton cancelLoad = new JFXButton("Cancel");
        cancelLoad.setButtonType(JFXButton.ButtonType.RAISED);
        cancelLoad.setRipplerFill(rgb(238, 190, 182));
        cancelLoad.setStyle("-fx-background-color:rgb(1,161,133);-fx-font-size: 16px;-fx-text-fill: WHITE;");

        HBox buttonPaneLoad = new HBox();
        buttonPaneLoad.setSpacing(25.0);
        buttonPaneLoad.getChildren().addAll(new HBox(), apply, cancelLoad);

        loadDrawerPane.getChildren().addAll(loadTitle, dist, fx, fy, fz, mx, my, mz, buttonPaneLoad);
        loadDrawerPane.setStyle("-fx-background-color:rgb(236,240,241)");
        loadDrawer.setDefaultDrawerSize(300);
        loadDrawer.setDirection(JFXDrawer.DrawerDirection.LEFT);
        loadDrawer.setSidePane(loadDrawerPane);
        loadDrawer.setOverLayVisible(false);
        loadDrawer.setResizableOnDrag(false);
//-------------------------------------------------------------------------------------------------
//Grid Dialog---------------------------------------------------------------------------------
        Label gridTitle = new Label(" Grid Definition...");
        gridTitle.setFont(javafx.scene.text.Font.font("Aliquam", 40));
        gridTitle.setTextFill(rgb(1, 161, 133));

        VBox Story = new VBox();
        Story.setSpacing(20.0);
        JFXTextField noStory = new JFXTextField();
        noStory.setFocusColor(rgb(0, 98, 41));
        noStory.setMaxWidth(250);
        noStory.setLabelFloat(true);
        noStory.setPromptText("Number of Stories");
        noStory.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");
        JFXTextField StoryHeight = new JFXTextField();
        StoryHeight.setFocusColor(rgb(0, 98, 41));
        StoryHeight.setMaxWidth(250);
        StoryHeight.setLabelFloat(true);
        StoryHeight.setPromptText("Story Height, m");
        StoryHeight.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");
        Story.getChildren().addAll(noStory, StoryHeight);

        VBox BayZ = new VBox();
        BayZ.setSpacing(20.0);
        JFXTextField noBayZ = new JFXTextField();
        noBayZ.setFocusColor(rgb(0, 98, 41));
        noBayZ.setMaxWidth(250);
        noBayZ.setLabelFloat(true);
        noBayZ.setPromptText("Number of Bays, Z");
        noBayZ.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");
        JFXTextField BayZHeight = new JFXTextField();
        BayZHeight.setFocusColor(rgb(0, 98, 41));
        BayZHeight.setMaxWidth(250);
        BayZHeight.setLabelFloat(true);
        BayZHeight.setPromptText("Bay Width, Z, m");
        BayZHeight.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");
        BayZ.getChildren().addAll(noBayZ, BayZHeight);

        VBox BayX = new VBox();
        BayX.setSpacing(20.0);
        JFXTextField noBayX = new JFXTextField();
        noBayX.setFocusColor(rgb(0, 98, 41));
        noBayX.setMaxWidth(250);
        noBayX.setLabelFloat(true);
        noBayX.setPromptText("Number of Bays, X");
        noBayX.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");
        JFXTextField BayXHeight = new JFXTextField();
        BayXHeight.setFocusColor(rgb(0, 98, 41));
        BayXHeight.setMaxWidth(250);
        BayXHeight.setLabelFloat(true);
        BayXHeight.setPromptText("Bay Width, X, m");
        BayXHeight.setStyle("-fx-text-fill: BLACK;-fx-font-size: 16px;-fx-unfocus-color: rgb(1,140,60);-fx-prompt-text-fill: rgb(149,165,165);");
        BayX.getChildren().addAll(noBayX, BayXHeight);

        JFXButton AcceptGrid = new JFXButton("Generate");
        AcceptGrid.setButtonType(JFXButton.ButtonType.RAISED);
        AcceptGrid.setRipplerFill(rgb(238, 190, 182));
        AcceptGrid.setStyle("-fx-background-color:rgb(33, 127, 188);-fx-font-size: 16px;-fx-text-fill: WHITE;");

        JFXButton cancelGrid = new JFXButton("Cancel");
        cancelGrid.setButtonType(JFXButton.ButtonType.RAISED);
        cancelGrid.setRipplerFill(rgb(238, 190, 182));
        cancelGrid.setStyle("-fx-background-color:rgb(33, 127, 188);-fx-font-size: 16px;-fx-text-fill: WHITE;");

        JFXCheckBox genFrame = new JFXCheckBox("Generate Frame Elements");
        genFrame.setCheckedColor(rgb(33, 127, 188));
        genFrame.setFont(javafx.scene.text.Font.font("Century Gothic", 14));
        HBox buttonPaneGrid = new HBox();
        buttonPaneGrid.setSpacing(30.0);
        buttonPaneGrid.getChildren().addAll(new HBox(), AcceptGrid, cancelGrid);

        JFXDrawer gridDrawer = new JFXDrawer();
        VBox gridDrawerPane = new VBox();
        gridDrawerPane.setSpacing(20.0);
        gridDrawerPane.setPadding(new Insets(20, 50, 50, 20));
        gridDrawerPane.getChildren().addAll(gridTitle, Story, BayX, BayZ, genFrame, buttonPaneGrid);
        gridDrawerPane.setStyle("-fx-background-color:rgb(236,240,241)");
        gridDrawer.setDefaultDrawerSize(300);
        gridDrawer.setDirection(JFXDrawer.DrawerDirection.LEFT);
        gridDrawer.setSidePane(gridDrawerPane);
        gridDrawer.setOverLayVisible(true);
        gridDrawer.setResizableOnDrag(false);

        //load properties...
        //E ,g fck, fyk accept cancel
//--------------------------------------------------------------------------------------------
//primary toolbar: restraint, materials, section,loading
        JFXButton materialM = new JFXButton("");
        materialM.setRipplerFill(rgb(220, 220, 220));
        Button matBackgrd = new Button("");
        matBackgrd.setMinSize(270, 55);
        materialM.setMinSize(270, 55);

        JFXButton loadM = new JFXButton("");
        loadM.setRipplerFill(rgb(220, 220, 220));
        Button loadBackgrd = new Button("");
        loadBackgrd.setMinSize(270, 55);
        loadM.setMinSize(270, 55);

        JFXButton restrM = new JFXButton("");
        restrM.setRipplerFill(rgb(220, 220, 220));
        Button restrBackgrd = new Button("");
        restrBackgrd.setMinSize(270, 55);
        restrM.setMinSize(270, 55);

        JFXButton sectionM = new JFXButton("");
        sectionM.setRipplerFill(rgb(220, 220, 220));
        Button sectionBackgrd = new Button("");
        sectionBackgrd.setMinSize(270, 55);
        sectionM.setMinSize(270, 55);

        JFXButton designM = new JFXButton("");
        designM.setRipplerFill(rgb(220, 220, 220));
        Button designBackgrd = new Button("");
        designBackgrd.setMinSize(270, 55);
        designM.setMinSize(270, 55);

        JFXButton selJointM = new JFXButton("");
        selJointM.setRipplerFill(rgb(220, 220, 220));
        Button selJointBackgrd = new Button("");
        selJointBackgrd.setMinSize(270, 55);
        selJointM.setMinSize(270, 55);

        JFXButton selMemberM = new JFXButton("");
        selMemberM.setRipplerFill(rgb(220, 220, 220));
        Button selMemberBackgrd = new Button("");
        selMemberBackgrd.setMinSize(270, 55);
        selMemberM.setMinSize(270, 55);

        JFXButton runM = new JFXButton("");
        runM.setRipplerFill(rgb(220, 220, 220));
        Button runBackgrd = new Button("");
        runBackgrd.setMinSize(270, 55);
        runM.setMinSize(270, 55);

        JFXButton dnaM = new JFXButton("");
        dnaM.setRipplerFill(rgb(220, 220, 220));
        Button dnaBackgrd = new Button("");
        dnaBackgrd.setMinSize(270, 55);
        dnaM.setMinSize(270, 55);

        JFXButton drawM = new JFXButton("");
        drawM.setRipplerFill(rgb(220, 220, 220));
        Button drawBackgrd = new Button("");
        drawBackgrd.setMinSize(270, 55);
        drawM.setMinSize(270, 55);
        getImages();
        Background material = new Background(materialImage);
        Background load = new Background(loadImage);
        Background restr = new Background(restrImage);
        Background run = new Background(runImage);
        Background section = new Background(sectionImage);
        Background design = new Background(designImage);
        Background selJoint = new Background(selJointImage);
        Background selMember = new Background(selMemberImage);
        Background draw = new Background(drawImage);
        Background dna = new Background(dnaImage);

        matBackgrd.setBackground(material);
        loadBackgrd.setBackground(load);
        dnaBackgrd.setBackground(dna);
        sectionBackgrd.setBackground(section);
        designBackgrd.setBackground(design);
        selMemberBackgrd.setBackground(selMember);
        selJointBackgrd.setBackground(selJoint);
        drawBackgrd.setBackground(draw);
        restrBackgrd.setBackground(restr);
        runBackgrd.setBackground(run);

        StackPane materialS = new StackPane();
        materialS.getChildren().addAll(matBackgrd, materialM);
        StackPane dnaS = new StackPane();
        dnaS.getChildren().addAll(dnaBackgrd, dnaM);
        StackPane sectionS = new StackPane();
        sectionS.getChildren().addAll(sectionBackgrd, sectionM);
        StackPane drawS = new StackPane();
        drawS.getChildren().addAll(drawBackgrd, drawM);
        StackPane selJointS = new StackPane();
        selJointS.getChildren().addAll(selJointBackgrd, selJointM);
        StackPane selMemberS = new StackPane();
        selMemberS.getChildren().addAll(selMemberBackgrd, selMemberM);
        StackPane designS = new StackPane();
        designS.getChildren().addAll(designBackgrd, designM);
        StackPane runS = new StackPane();
        runS.getChildren().addAll(runBackgrd, runM);
        StackPane restrS = new StackPane();
        restrS.getChildren().addAll(restrBackgrd, restrM);
        StackPane loadS = new StackPane();
        loadS.getChildren().addAll(loadBackgrd, loadM);

        JFXDrawer toolbarDrawer = new JFXDrawer();
        VBox toolbarDrawerPane = new VBox();
        toolbarDrawerPane.setSpacing(0.0);
        toolbarDrawerPane.setPadding(new Insets(0, 10, 0, 0));
        toolbarDrawerPane.getChildren().addAll(new HBox());
        toolbarDrawerPane.setStyle("-fx-background-color:rgb(236,240,241)");
        toolbarDrawer.setDefaultDrawerSize(20);
        toolbarDrawer.setDirection(JFXDrawer.DrawerDirection.LEFT);
        toolbarDrawer.setSidePane(toolbarDrawerPane);
        toolbarDrawer.setOverLayVisible(false);
        toolbarDrawer.setResizableOnDrag(false);
        toolbarDrawerPane.getChildren().addAll(drawS, selJointS, selMemberS, materialS, restrS, loadS, sectionS, runS, designS, dnaS);

        JFXDrawersStack drawersStack = new JFXDrawersStack();
        drawersStack.setContent(content);

        drawersStack.toggle(toolbarDrawer, true);
        drawersStack.toggle(gridDrawer, true);

        materialM.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, (e) -> {
            drawersStack.toggle(materialDrawer);
        });
        restrM.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, (e) -> {
            drawersStack.toggle(restraintDrawer);
        });
        dnaM.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, (e) -> {
            drawersStack.toggle(GADrawer);
        });
        materialM.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, (e) -> {
            drawersStack.toggle(materialDrawer);
        });
        drawM.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, (e) -> {
            mainF.drawing = true;
            mainF.selectJ = false;
            mainF.selectM = false;
        });
        selJointM.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, (e) -> {
            mainF.drawing = false;
            mainF.selectJ = true;
            mainF.selectM = false;
        });
        selMemberM.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, (e) -> {
            mainF.drawing = false;
            mainF.selectJ = false;
            mainF.selectM = true;
        });

        designM.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, (e) -> {
 
        });

        sectionM.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, (e) -> {
            drawersStack.toggle(sectionDrawer);
        });

        runM.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, (e) -> {
            long start = System.nanoTime();
            mainF.Str = new Structure3();
            int jSize = mainF.JointV.size();
            int mSize = mainF.MemberV.size();

            Joint3[] j = new Joint3[jSize];
            for (int i = 0; i < jSize; i++) {
                j[i] = mainF.JointV.get(i);
            }
            Member3[] m = new Member3[mSize];
            for (int i = 0; i < mSize; i++) {
                m[i] = mainF.MemberV.get(i);
            }
            mainF.Str.setJointVector(j);
            mainF.Str.setMemberVector(m);
            for (int i = 0; i < mainF.JointV.size(); i++) {
                Joint3 cj = mainF.JointV.get(i);
                System.out.println("Joint: " + cj.getJointID() + "DofIndex: " + cj.getDofIndex());
            }

            for (int i = 0; i < mainF.MemberV.size(); i++) {
                Member3 cm = mainF.MemberV.get(i);
                System.out.println("Start: " + cm.getStart().getJointID() + "DofIndex: " + cm.getStart().getDofIndex());
                System.out.println("End: " + cm.getEnd().getJointID() + "DofIndex: " + cm.getEnd().getDofIndex());

            }
            //generate population

            //decode individual and insert data into each member
            for (int i = 0; i < mainF.MemberV.size(); i++) {
                mainF.MemberV.get(i).computeArea();
                mainF.MemberV.get(i).computeIxx();
                mainF.MemberV.get(i).computeIyy();
                mainF.MemberV.get(i).computeIzz();
                mainF.MemberV.get(i).memLength();
                mainF.MemberV.get(i).directionCosines();
                mainF.MemberV.get(i).rotationMatrix();
                mainF.MemberV.get(i).localStiffness();
                mainF.MemberV.get(i).globalStiffness();
            }
            mainF.Str.DetermineDof();
//            mainF.Str.DetermineDof();
            System.out.println("-----------------------------------------------------------");
            System.out.println("STRUCTURAL DOF: " + mainF.Str.StrDof);
            System.out.println("-----------------------------------------------------------");
            mainF.Str.CountRestrainedDOFs();
            System.out.println("TOTAL NUMBER OF RESTRAINED DOF: " + mainF.Str.ConstrDof);
            System.out.println("-----------------------------------------------------------");
            mainF.Str.AssembleStiffnessC();
            System.out.println("STRUCTURE STIFFNESS MATRIX: ");
            System.out.println("-----------------------------------------------------------");
            mainF.Str.globalStiffnessC.printM();
            System.out.println("");
            mainF.Str.EquivalentLoadVectorC();
//            System.out.println("-----------------------------------------------------------");
//            System.out.println("EQUIVALENT LOAD VECTOR: ");
//            System.out.println("-----------------------------------------------------------");
//            printVector(mainF.Str.loadVector, 2);
            mainF.Str.ApplyBoundaryConditionsC();
//            System.out.println("REDUCED STRUCTURAL STIFFNESS MATRIX: ");
//            System.out.println("-----------------------------------------------------------");
//            printMatrix(mainF.Str.reducedStiffness);
//            System.out.println("-----------------------------------------------------------");
//            System.out.println("REDUCED LOAD VECTOR: ");
//            System.out.println("-----------------------------------------------------------");
//            printVector(mainF.Str.reducedLoadVector, 2);
            mainF.Str.AssembleReactionStiffnessC();
//            System.out.println("-----------------------------------------------------------");
//            System.out.println("REACTION STIFFNESS MATRIX: ");
//            System.out.println("-----------------------------------------------------------");
//            printMatrix(mainF.Str.reactionStiffness);
//            System.out.println("-----------------------------------------------------------");
//            System.out.println("REACTION LOAD VECTOR: ");
//            System.out.println("-----------------------------------------------------------");
//            printVector(mainF.Str.reactionLoadVector, 2);
            mainF.Str.ComputeDisplacementsC(false);
////            System.out.println("-----------------------------------------------------------");
////            System.out.println("UNKNOWN DISPLACEMENTS: ");
////            System.out.println("-----------------------------------------------------------");
//            printVector(mainF.Str.displacementVector, 7);
            mainF.Str.ComputeReactionsC();
//            System.out.println("-----------------------------------------------------------");
//            System.out.println("UNKNOWN REACTIONS: ");
//            System.out.println("-----------------------------------------------------------");
            mainF.Str.AssembleReactionsC();

//            printVector(mainF.Str.reactionVector, 2);
            System.out.println("hello");
            mainF.Str.AssembleDisplacementsC();
            mainF.Str.MemberEndActionsC();
//            for (int i = 0; i < mainF.JointV.size(); i++) {
//                Joint3 cj = mainF.JointV.get(i);
//                System.out.println("Joint: " + cj.getJointID() + "rx: " + cj.getRcx() + "ry: " + cj.getRcy() + "rz: " + cj.getRcz() + "mx: " + cj.getRcmx() + "my: " + cj.getRcmy() + "mz: " + cj.getRcmz());
//            }
            long timeElapsed = System.nanoTime() - start;
            for (int i = 0; i < mainF.Str.memberVector.length; i++) {
                Member3 mem = mainF.Str.memberVector[i];
                internalActionCoordinates(mem);
            }

            System.out.println("time elapsed: " + timeElapsed / 1000000000.0 + "seconds");
            mainF.showResults = true;

        });

        loadM.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, (e) -> {
            drawersStack.toggle(loadDrawer);
        });
        materialM.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, (e) -> {
            drawersStack.toggle(materialDrawer);
        });
        drawM.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_ENTERED, (e) -> {
            statusLabel.setText("Draw Structural Elements");
        });
        selJointM.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_ENTERED, (e) -> {
            statusLabel.setText("Select Joints");
        });
        selMemberM.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_ENTERED, (e) -> {
            statusLabel.setText("Select Members");
        });
        loadM.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_ENTERED, (e) -> {
            statusLabel.setText("Apply Loads and Members");
        });
        restrM.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_ENTERED, (e) -> {
            statusLabel.setText("Restrain Selected Joints");
        });
        designM.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_ENTERED, (e) -> {
            statusLabel.setText("Open Section Design Dialog... ");

        });
        runM.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_ENTERED, (e) -> {
            statusLabel.setText("Perform Structural Analysis");
        });
        sectionM.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_ENTERED, (e) -> {
            statusLabel.setText("Assign Section Geometry and Define Reinforcement");
        });
        dnaM.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_ENTERED, (e) -> {
            statusLabel.setText("Genetic Optimization Tools");
        });

        cancelMat.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED,
                (e) -> {
                    drawersStack.toggle(materialDrawer, false);

                }
        );
        cancelLoad.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED,
                (e) -> {
                    drawersStack.toggle(loadDrawer, false);
                }
        );
        cancelRes.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED,
                (e) -> {
                    drawersStack.toggle(restraintDrawer, false);
                }
        );
        cancelSect.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED,
                (e) -> {
                    drawersStack.toggle(sectionDrawer, false);
                }
        );
        cancelGA.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED,
                (e) -> {
                    drawersStack.toggle(GADrawer, false);
                }
        );
        cancelGrid.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED,
                (e) -> {
                    drawersStack.toggle(gridDrawer, false);
                }
        );
        acceptRes.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED,
                (e) -> {
                    mainF.RestrainSelectedJoints(dx.isSelected(), dy.isSelected(), dz.isSelected(), rx.isSelected(), ry.isSelected(), rz.isSelected());
                    mainF.repaint();
                    drawersStack.toggle(restraintDrawer, false);

                }
        );
        acceptGA.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED,
                (e) -> {
                    double mutationProb, crossoverProb,  kb, kv, kt;
                    boolean uniax;
                    int iterationNo, popSize;
                    long timeThen = System.nanoTime();
                    try {
                        mutationProb = Double.valueOf(MutationProb.getText());
                    } catch (NumberFormatException exception) {
                        mutationProb = 0.05;
                    }
                    try {
                        iterationNo = Integer.valueOf(IterationNo.getText());
                    } catch (NumberFormatException exception) {
                        iterationNo = 70;
                    }
                    try {
                        crossoverProb = Double.valueOf(XOverProb.getText());
                    } catch (NumberFormatException exception) {
                        crossoverProb = 0.5;
                    }
                    try {
                        popSize = Integer.valueOf(PopSize.getText());
                    } catch (NumberFormatException exception) {
                        popSize = 70;
                    }
                    try {
                        kb = Integer.valueOf(BiaxW.getText());
                    } catch (NumberFormatException exception) {
                        kb = 70;
                    }
                    try {
                        kv = Integer.valueOf(ShearW.getText());
                    } catch (NumberFormatException exception) {
                        kv = 70;
                    }
                    try {
                        kt = Integer.valueOf(TorsionW.getText());
                    } catch (NumberFormatException exception) {
                        kt = 70;
                    }
                    uniax=optBuild.isSelected();

                    drawersStack.toggle(GADrawer, false);
                    final XYSeries fitVSgen = new XYSeries("fitness vs. gen");
                    final XYSeries penVSgen = new XYSeries("penalty vs. gen");
                    final XYSeries wgtVSgen = new XYSeries("weight vs. gen");

                    Structure3 Str = new Structure3();
                    int jSize = mainF.JointV.size();
                    int mSize = mainF.MemberV.size();
                    //****************designate members as beams and columns
                    int bSize = mainF.beamV.size();
                    int cSize = mainF.columnV.size();

                    Joint3[] j = new Joint3[jSize];
                    for (int i = 0; i < jSize; i++) {
                        j[i] = mainF.JointV.get(i);
                    }
                    Member3[] m = new Member3[mSize];
                    for (int i = 0; i < mSize; i++) {
                        m[i] = mainF.MemberV.get(i);
                    }
                    Member3[] beam = new Member3[bSize];
                    for (int i = 0; i < bSize; i++) {
                        beam[i] = mainF.beamV.get(i);
                    }
                    Member3[] column = new Member3[cSize];
                    for (int i = 0; i < cSize; i++) {
                        column[i] = mainF.columnV.get(i);
                    }
                    Str.setKb(kb);
                    Str.setKv(kv);
                    Str.setKt(kt);
                    Str.setJointVector(j);
                    Str.setMemberVector(m);
                    Str.setBeamVector(beam);
                    Str.setColumnVector(column);
                    Str.memberRotPara();
                    int generation = 0;
                    GAResults res = new GAResults(Str);
  
                    GAVAL3D.Population myPop = new GAVAL3D.Population(popSize, true, 10, m.length,uniax);
                    myPop.computeFitnessforAll(Str, res);
                    Individual prevFittest = myPop.getFittestStructure();
                    int numOfEvolutions = iterationNo;
                    Individual[] fittest = new Individual[numOfEvolutions];
                    fittest[0] = prevFittest;
                    Individual fit = null;
                    Str.ResetResults();
                    while (generation < numOfEvolutions) {
                        prevFittest = myPop.getFittestStructure();
                        myPop = Evolve(myPop, mutationProb, crossoverProb);
                        Str.ResetResults();
                        myPop.computeFitnessforAll(Str, res);
                        myPop.computeInterFitforAll();
                        myPop.computeAverageInterfit();
                        myPop.computeFitnessFactorforAll();
                        fit = myPop.getFittestStructure();
                        System.out.println("generation " + generation + " fittest: " + fit.getFitness());
                        fittest[generation] = fit;
                        fitVSgen.add(generation, fit.getFitness());
                        penVSgen.add(generation, fit.penalty);
                        wgtVSgen.add(generation, fit.weight);
                        double progress = generation / (double) iterationNo;
                        res.update(generation, fit.weight, fit.getFitness(), fit.penalty, progress);
                        generation++;
                        //genNo.setText("" + generation);
                        long timeNow = System.nanoTime();
                        double timeElapsed = (timeNow - timeThen) / 1000000000.0;
                        int minutes = (int) (timeElapsed / 60);
                        int seconds = (int) (timeElapsed - (minutes * 60));
                        String time = " -- " + minutes + "min. " + seconds + "sec.";
                        res.timeL.setText(time);
                        statusLabel.setForeground(Color.RED);
                        statusLabel.setText("[GENERATION: " + generation + "] [BEST FITNESS: " + fit.getFitness() + "] [LEAST COST: " + fit.weight + "]");
                    }
                    statusLabel.setForeground(Color.BLACK);
                    res.setVisible(false);
                    Str.ResetResults();
                                       fit.DecodeAndDisplay(Str);
                    final XYSeriesCollection datafit = new XYSeriesCollection();
                    datafit.addSeries(fitVSgen);
                    final XYSeriesCollection datapen = new XYSeriesCollection();
                    datapen.addSeries(penVSgen);
                    final XYSeriesCollection datawt = new XYSeriesCollection();
                    datawt.addSeries(wgtVSgen);
                    DrawGraph fitg = null;
                    JFreeChart fitC = createChart(datafit, "Fitness Vs. Generation", "Generation No.", "Fitness",Color.blue);
                    ChartPanel fitPanel = new ChartPanel(fitC);
                    JFreeChart penC = createChart(datapen, "Penalty Vs. Generation", "Generation No.", "Penalty",pomegranateR);
                    ChartPanel penPanel = new ChartPanel(penC);
                    JFreeChart costC = createChart(datawt, "Cost Vs. Generation", "Generation No.", "Cost",darkGreen);
                    ChartPanel costPanel = new ChartPanel(costC);
                    ResultDialog.initAndShowGUI(fitPanel, penPanel, costPanel);
//                    try {
//                        fitg = new DrawGraph("GA Simulation", datafit, "Fitness Vs. Generation", "Generation No.", "Fitness");
//                    } catch (IOException ex) {
//                        Logger.getLogger(drawerDemoMod.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                    fitg.pack();
//                    RefineryUtilities.centerFrameOnScreen(fitg);
//                    fitg.setVisible(true);
//                    DrawGraph pen = null;
//                    try {
//                        pen = new DrawGraph("GA Simulation", datapen, "Penalty Vs. Generation", "Generation No.", "Penalty");
//                    } catch (IOException ex) {
//                        Logger.getLogger(drawerDemoMod.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                    pen.pack();
//                    RefineryUtilities.centerFrameOnScreen(pen);
//                    pen.setVisible(true);
//                    DrawGraph wgt = null;
//                    try {
//                        wgt = new DrawGraph("GA Simulation", datawt, "Weight Vs. Generation", "Generation No.", "Weight");
//                    } catch (IOException ex) {
//                        Logger.getLogger(drawerDemoMod.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                    wgt.pack();
//                    RefineryUtilities.centerFrameOnScreen(wgt);
//                    wgt.setVisible(true);
                    mainF.showResults = true;
                    long timeNow = System.nanoTime();
                    double timeElapsed = (timeNow - timeThen) / 1000000000.0;
                    int minutes = (int) (timeElapsed / 60);
                    int seconds = (int) (timeElapsed - (minutes * 60));
                    String time = " -- " + minutes + "min. " + seconds + "sec.";
                    res.timeL.setText(time);
                }
        );
        AcceptGrid.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED,
                (e) -> {
                    int nox = Integer.valueOf(noBayX.getText());
                    int noy = Integer.valueOf(noStory.getText());
                    int noz = Integer.valueOf(noBayZ.getText());
                    double GridSizeX = Double.valueOf(BayXHeight.getText());
                    double GridSizeY = Double.valueOf(StoryHeight.getText());
                    double GridSizeZ = Double.valueOf(BayZHeight.getText());
                    mainF.setGridSizeandExtent(GridSizeX * (nox), (nox + 1), GridSizeY * (noy), (noy + 1), GridSizeZ * (noz), (noz + 1));
                    if (genFrame.isSelected()) {
                        mainF.DrawPortalFrame((nox + 1), (noy + 1), (noz + 1), GridSizeX * (nox), GridSizeY * (noy), GridSizeZ * (noz));
                        mainF.repaint();
                    }
                    System.out.println("main: " + mainF.MemberV.size());
                    System.out.println("mainJoint: " + mainF.JointV.size());
                    for (int i = 0; i < mainF.JointV.size(); i++) {
                        Joint3 j = mainF.JointV.get(i);
                        System.out.println("x: " + j.getX() + "y: " + j.getY() + "z: " + j.getZ());
                    }
                    mainF.repaint();
                    drawersStack.toggle(gridDrawer, false);

                });
        acceptSect.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED,
                (e) -> {
                    double b, h, As1v, As2v;

                    try {
                        b = Double.valueOf(B.getText());
                        b = b / 1000.0;
                    } catch (NumberFormatException exception) {
                        b = .100;
                    }
                    try {
                        h = Double.valueOf(H.getText());
                        h = h / 1000.0;
                    } catch (NumberFormatException exception) {
                        h = .100;
                    }
                    try {
                        As1v = Double.valueOf(As1.getText());
                        As1v = As1v / 1000000.0;
                    } catch (NumberFormatException exception) {
                        As1v = 0.00400;
                    }
                    try {
                        As2v = Double.valueOf(As2.getText());
                        As2v = As2v / 1000000.0;
                    } catch (NumberFormatException exception) {
                        As2v = 0.00400;
                    }
                    mainF.AssignSectiontoAll(b, h, As1v, As2v);
                    drawersStack.toggle(sectionDrawer, false);
                }
        );
        apply.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED,
                (e) -> {
                    double Fx, Fy, Fz, Mx, My, Mz, Ux = 0, Uy = 0, Uz = 0, PFx, PFy, PFz, PMx, PMy, PMz;
                    //the try-catch clauses are there to circumvent invalid info from being written
                    //to the vars
                    try {
                        Fx = Double.valueOf(fxM.getText());
                    } catch (Exception exception) {
                        Fx = 0;
                    }
                    try {
                        Fy = Double.valueOf(fyM.getText());
                    } catch (Exception exception) {
                        Fy = 0;
                    }
                    try {
                        Fz = Double.valueOf(fzM.getText());
                    } catch (Exception exception) {
                        Fz = 0;
                    }
                    try {
                        Mx = Double.valueOf(mxM.getText());
                    } catch (Exception exception) {
                        Mx = 0;
                    }
                    try {
                        My = Double.valueOf(myM.getText());
                    } catch (Exception exception) {
                        My = 0;
                    }

                    try {
                        Mz = Double.valueOf(mzM.getText());
                    } catch (Exception exception) {
                        Mz = 0;
                    }

                    try {
                        PFx = Double.valueOf(fxP.getText());
                    } catch (Exception exception) {
                        PFx = 0;
                    }
                    try {
                        PFy = Double.valueOf(fyP.getText());
                    } catch (Exception exception) {
                        PFy = 0;
                    }
                    try {
                        PFz = Double.valueOf(fzP.getText());
                    } catch (Exception exception) {
                        PFz = 0;
                    }
                    try {
                        PMx = Double.valueOf(mxP.getText());
                    } catch (Exception exception) {
                        PMx = 0;
                    }
                    try {
                        PMy = Double.valueOf(myP.getText());
                    } catch (Exception exception) {
                        PMy = 0;
                    }

                    try {
                        PMz = Double.valueOf(mzP.getText());
                    } catch (Exception exception) {
                        PMz = 0;
                    }
                    if (dist.isSelected()) {
                        try {
                            Ux = Double.valueOf(fxM.getText());
                        } catch (Exception exception) {
                            Ux = 0;
                        }
                        try {
                            Uy = Double.valueOf(fyM.getText());
                        } catch (Exception exception) {
                            Uy = 0;
                        }
                        try {
                            Uz = Double.valueOf(fzM.getText());
                        } catch (Exception exception) {
                            Uz = 0;
                        }
                    }
                    if (dist.isSelected()) {
                        mainF.LoadSelectedMembers(0, 0, 0, 0, 0, 0, Mx, PMx, My, PMy, Mz, PMz, Ux, Uy, Uz);
                    } else {
                        mainF.LoadSelectedMembers(Fx, PFx, Fy, PFy, Fz, PFz, Mx, PMx, My, PMy, Mz, PMz, 0, 0, 0);
                    }
                    drawersStack.toggle(loadDrawer, false);

                }
        );
        acceptMat.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED,
                (e) -> {
                    double Eval, Gval, fckval, fykval;

                    try {
                        Gval = Double.valueOf(G.getText()) * 1000000;
                    } catch (NumberFormatException exception) {
                        Gval = 80000000;
                    }
                    try {
                        Eval = Double.valueOf(E.getText()) * 1000000;
                    } catch (NumberFormatException exception) {
                        Eval = 29000000;
                    }
                    try {
                        fckval = Double.valueOf(fck.getText());
                    } catch (NumberFormatException exception) {
                        fckval = 25;
                    }
                    try {
                        fykval = Double.valueOf(fyk.getText());
                    } catch (NumberFormatException exception) {
                        fykval = 300;
                    }
                    mainF.AssignMaterials(Eval, Gval, fckval, fykval);
                    drawersStack.toggle(materialDrawer, false);

                }
        );
        dist.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED,
                (e) -> {
                    fxP.setDisable(true);
                    fyP.setDisable(true);
                    fzP.setDisable(true);
                    mxM.setDisable(true);
                    myM.setDisable(true);
                    mzM.setDisable(true);
                    mxM.setDisable(true);
                    myM.setDisable(true);
                    mzM.setDisable(true);

                });
        dist.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED,
                (e) -> {
                    if (dist.isSelected()) {
                        fxP.setDisable(true);
                        fyP.setDisable(true);
                        fzP.setDisable(true);
                        mxM.setDisable(true);
                        myM.setDisable(true);
                        mzM.setDisable(true);
                        mxP.setDisable(true);
                        myP.setDisable(true);
                        mzP.setDisable(true);
                    } else {
                        fxP.setDisable(false);
                        fyP.setDisable(false);
                        fzP.setDisable(false);
                        mxM.setDisable(false);
                        myM.setDisable(false);
                        mzM.setDisable(false);
                        mxP.setDisable(false);
                        myP.setDisable(false);
                        mzP.setDisable(false);
                    }
                });
        defaultMat.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED,
                (e) -> {
                    if (defaultMat.isSelected()) {
                        E.setText("29");
                        Es.setText("200");
                        G.setText("11.15");
                        fyk.setText("300");
                        fck.setText("20");
                    } else {
                        E.setText("");
                        Es.setText("");
                        G.setText("");
                        fyk.setText("");
                        fck.setText("");

                    }
                });

        root.getChildren().add(drawersStack);
        return (scene);
    }

    public static void main(String[] args) {
        SplashScreen splash = new SplashScreen(7000);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initAndShowGUI();
            }
        });
    }

}
