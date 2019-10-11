/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import SAM3D.Structure3;
import static UI.Table.initAndShowGUI;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import static javafx.scene.paint.Color.rgb;
import static javafx.scene.paint.Color.rgb;
import static javafx.scene.paint.Color.rgb;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author user
 */
public class GAResults extends JFrame {

    public JLabel gen;
    public JLabel fit;
    public JLabel cost;
    public JLabel pen;
    public JLabel currgen;
    public JLabel currfit;
    public JLabel currcost;
    public JLabel currpen;
    public JLabel timeL;
    public JLabel timeI;
    public static double progValue;
    public static JFXProgressBar jfxBar;
    public JLabel progPercent;
    public JLabel progressT;
    public DecimalFormat stressFormatter = new DecimalFormat("0.00");
    public static Structure3 Str;

    public static Scene progressScene() {
        Group root = new Group();
        Scene scene = new Scene(root);
        VBox prog = new VBox();
        HBox buttonP = new HBox();
        buttonP.setSpacing(67.0);
        JFXButton jb = new JFXButton("SHOW TABULAR RESULTS");
        jb.setButtonType(JFXButton.ButtonType.RAISED);
        jb.setRipplerFill(rgb(238, 190, 182));
        jb.setStyle("-fx-background-color:rgb(33, 127, 188);-fx-font-size: 16px;-fx-text-fill: WHITE;");

        jfxBar = new JFXProgressBar();
        jfxBar.setPrefWidth(350);
        jfxBar.setStyle("-fx-track-color: #C9C8C8;-fx-progress-color: rgb(1,140,60);-fx-stroke-width: 10;");
        jfxBar.setProgress(progValue);
        prog.setPadding(new Insets(0, 0, 0, 0));
        prog.setSpacing(28);
        buttonP.getChildren().addAll(new HBox(), jb);
        prog.getChildren().addAll(jfxBar, new HBox(), new HBox(), new HBox(), new HBox(), new HBox(), new HBox(), new HBox(), new HBox(), buttonP);
        root.getChildren().add(prog);
        jb.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED,
                (e) -> {
                    initAndShowGUI(Str);
                });
        return scene;
    }

    private static void initFX(JFXPanel fxPanel1) {
// This method is invoked on the JavaFX thread
        Scene scene1 = progressScene();
        fxPanel1.setScene(scene1);

    }

    public GAResults(Structure3 S) {
// This method is invoked on the EDT thread
        JFrame ResultFrame = new JFrame("Simulation Progress");
        ResultFrame.setBackground(Color.WHITE);
        ResultFrame.setLayout(null);
        ResultFrame.setSize(400, 450);
        final JFXPanel fxPanel = new JFXPanel();
        Str = S;
        fxPanel.setSize(350, 430);
        fxPanel.setLocation(15, 80);
        JLabel genT = new JLabel("GENERATION: ");
        gen = new JLabel("");
        JLabel fitT = new JLabel("BEST FITNESS: ");
        fit = new JLabel("");
        JLabel costT = new JLabel("BEST COST: ");
        cost = new JLabel("");
        JLabel penT = new JLabel("LEAST PENALTY: ");
        pen = new JLabel("");
        JLabel igenT = new JLabel("INDIVIDUAL NO.: ");
        currgen = new JLabel("");
        JLabel ifitT = new JLabel("INDIVIDUAL FITNESS: ");
        currfit = new JLabel("");
        JLabel icostT = new JLabel("INDIVIDUAL COST: ");
        currcost = new JLabel("");
        JLabel ipenT = new JLabel("INDIVIDUAL PENALTY: ");
        currpen = new JLabel("");
        timeI = new JLabel("TOTAL TIME: ");
        timeL = new JLabel();
        progPercent = new JLabel("");
        JLabel progressT = new JLabel("PROGRESS");
        gen.setSize(400, 30);
        pen.setSize(400, 30);
        cost.setSize(400, 30);
        fit.setSize(400, 30);
        genT.setSize(400, 30);
        costT.setSize(400, 30);
        fitT.setSize(400, 30);
        penT.setSize(400, 30);
        currpen.setSize(400, 30);
        currgen.setSize(400, 30);
        currcost.setSize(400, 30);
        currfit.setSize(400, 30);
        igenT.setSize(400, 30);
        ipenT.setSize(400, 30);
        icostT.setSize(400, 30);
        ifitT.setSize(400, 30);
        timeL.setSize(400, 30);
        timeI.setSize(400, 30);
        progressT.setSize(400, 30);
        progPercent.setSize(400, 50);

        gen.setForeground(SectionInfoFX.pomegranateR);
        progPercent.setForeground(SectionInfoFX.belizeHoleB);
        progPercent.setFont(new Font("Aliquam", Font.PLAIN, 50));
        progressT.setFont(new Font("Aliquam", Font.PLAIN, 20));
        genT.setFont(new Font("Aliquam", Font.PLAIN, 20));
        gen.setFont(new Font("Aliquam", Font.BOLD, 20));
        costT.setFont(new Font("Aliquam", Font.PLAIN, 20));
        cost.setFont(new Font("Aliquam", Font.PLAIN, 20));
        fitT.setFont(new Font("Aliquam", Font.PLAIN, 20));
        fit.setFont(new Font("Aliquam", Font.PLAIN, 20));
        igenT.setFont(new Font("Aliquam", Font.PLAIN, 20));
        currgen.setFont(new Font("Aliquam", Font.PLAIN, 20));
        icostT.setFont(new Font("Aliquam", Font.PLAIN, 20));
        currcost.setFont(new Font("Aliquam", Font.PLAIN, 20));
        ifitT.setFont(new Font("Aliquam", Font.PLAIN, 20));
        currfit.setFont(new Font("Aliquam", Font.PLAIN, 20));
        penT.setFont(new Font("Aliquam", Font.PLAIN, 20));
        pen.setFont(new Font("Aliquam", Font.PLAIN, 20));
        ipenT.setFont(new Font("Aliquam", Font.PLAIN, 20));
        currpen.setFont(new Font("Aliquam", Font.PLAIN, 20));

        timeL.setFont(new Font("Aliquam", Font.BOLD, 20));
        timeI.setFont(new Font("Aliquam", Font.BOLD, 20));

        igenT.setLocation(5, 100);
        currgen.setLocation(190, 100);
        icostT.setLocation(5, 125);
        currcost.setLocation(190, 125);
        ifitT.setLocation(5, 150);
        currfit.setLocation(190, 150);
        ipenT.setLocation(5, 175);
        currpen.setLocation(190, 175);
        genT.setLocation(5, 200);
        gen.setLocation(190, 200);
        costT.setLocation(5, 225);
        cost.setLocation(190, 225);
        fitT.setLocation(5, 250);
        fit.setLocation(190, 250);
        penT.setLocation(5, 275);
        pen.setLocation(190, 275);
        timeI.setLocation(5, 300);
        timeL.setLocation(190, 300);
        progressT.setLocation(130, 5);
        progPercent.setLocation(114, 30);

        ResultFrame.add(igenT);
        ResultFrame.add(currgen);
        ResultFrame.add(icostT);
        ResultFrame.add(currcost);
        ResultFrame.add(ifitT);
        ResultFrame.add(currfit);
        ResultFrame.add(ipenT);
        ResultFrame.add(currpen);
        ResultFrame.add(genT);
        ResultFrame.add(gen);
        ResultFrame.add(costT);
        ResultFrame.add(cost);
        ResultFrame.add(fitT);
        ResultFrame.add(fit);
        ResultFrame.add(penT);
        ResultFrame.add(pen);
        ResultFrame.add(timeI);
        ResultFrame.add(timeL);
        ResultFrame.add(fxPanel);
        ResultFrame.add(progPercent);
        ResultFrame.add(progressT);

        ResultFrame.setLocationRelativeTo(null);
        ResultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ResultFrame.setVisible(true);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);
            }
        });

    }

    public void update(int genno, double costval, double fitval, double penval, double progVal) {
        this.gen.setText("" + genno);
        this.fit.setText("" + fitval);
        this.cost.setText("" + costval);
        this.pen.setText(""+penval);
        String prog = "" + stressFormatter.format(progVal * 100.0) + "%";
        this.progPercent.setText(prog);
        progValue = progVal;
        jfxBar=new JFXProgressBar();
        jfxBar.setProgress(progValue);

    }

    public void updatecurr(int genno, double costval, double fitval,double penval) {
        this.currgen.setText("" + genno);
        this.currpen.setText("" + penval);
        this.currfit.setText("" + fitval);
        this.currcost.setText("" + costval);
    }

//    public static void main(String[] args) {
//        Structure3 S = new Structure3();
//        GAResults x = new GAResults(S);
//        for (int i = 0; i < 100; i++) {
//            x.update(12, 12.1, 34, 0.01 * i);
//        }
//    }
}
