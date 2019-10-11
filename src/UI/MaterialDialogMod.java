/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;


import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 *
 * @author user
 */
public class MaterialDialogMod extends JFrame implements ActionListener {

    private JPanel topP, concP, concSelectP, concMatP, concFieldsP, concGFieldsP, concEP, concGP, concFckP, concFykP;
    private JPanel concGeoP, concGeoBP, concGeoHP, concGeoAs1P, concGeoAs2P;
    private Label concMatL, concgeoL, concEL, concGL, concfykL, concfckL, concBL, concHL, concas1L, concas2L;
    private TextField concET, concGT, concfykT, concfckT, concBT, concHT, concas1T, concas2T;
    private JRadioButton concreteSelectRB;

    private JPanel ButtonP;

    private Label areaL;
    private TextField areaT;
    private JLabel areaUnitsL;

    private Label youngL;
    private TextField youngT;
    private JLabel modUnitsL;

    private Label izzL;
    private TextField izzT;
    private JLabel izzUnitsL;

    private Button assignB;
    private Button cancelB;
    WireframeViewer drawPanel;

    public MaterialDialogMod(WireframeViewer drawPanel) {
        this.drawPanel = drawPanel;
        concSelectP = new JPanel();
        concFieldsP = new JPanel();
        concGFieldsP = new JPanel();
        concP = new JPanel();
        topP = new JPanel();

        concMatP = new JPanel();
        concEP = new JPanel();
        concGP = new JPanel();
        concFckP = new JPanel();
        concFykP = new JPanel();

//        concGeoP = new JPanel();
//        concGeoBP = new JPanel();
//        concGeoHP = new JPanel();
//        concGeoAs1P = new JPanel();
//        concGeoAs2P = new JPanel();

        ButtonP = new JPanel();

        concMatL = new Label("MATERIAL PROPERTIES");
        concgeoL = new Label("GEOMETRIC PROPERTIES");
        concEL = new Label("E");
        concGL = new Label("G");

        concfykL = new Label("Fyk");
        concfckL = new Label("Fck");
        concBL = new Label("B");
        concHL = new Label("H");
        concas1L = new Label("As1");
        concas2L = new Label("As2");

        assignB = new Button("Assign");
        cancelB = new Button("Cancel");
        assignB.addActionListener(this);
        cancelB.addActionListener(this);

        concET = new TextField("200000000", 7);
        concGT = new TextField("80000000", 7);
        concfykT = new TextField("300000", 7);
        concfckT = new TextField("25000", 7);
        concBT = new TextField(".300", 7);
        concHT = new TextField(".300", 7);
        concas1T = new TextField("400", 7);
        concas2T = new TextField("400", 7);

        concreteSelectRB = new JRadioButton("CONCRETE");

        concSelectP.setLayout(new FlowLayout(FlowLayout.RIGHT));
        concSelectP.add(concreteSelectRB);

        concMatP.setLayout(new FlowLayout(FlowLayout.CENTER));
        concMatP.add(concMatL);

        concEP.setLayout(new FlowLayout(FlowLayout.RIGHT));
        concEP.add(concEL);
        concEP.add(concET);

        concGP.setLayout(new FlowLayout(FlowLayout.RIGHT));
        concGP.add(concGL);
        concGP.add(concGT);

        concFckP.setLayout(new FlowLayout(FlowLayout.RIGHT));
        concFckP.add(concfckL);
        concFckP.add(concfckT);

        concFykP.setLayout(new FlowLayout(FlowLayout.RIGHT));
        concFykP.add(concfykL);
        concFykP.add(concfykT);

//        concGeoP.setLayout(new FlowLayout(FlowLayout.CENTER));
//        concGeoP.add(concgeoL);

//        concGeoHP.setLayout(new FlowLayout(FlowLayout.RIGHT));
//        concGeoHP.add(concHL);
//        concGeoHP.add(concHT);
//
//        concGeoBP.setLayout(new FlowLayout(FlowLayout.RIGHT));
//        concGeoBP.add(concBL);
//        concGeoBP.add(concBT);
//
//        concGeoAs1P.setLayout(new FlowLayout(FlowLayout.RIGHT));
//        concGeoAs1P.add(concas1L);
//        concGeoAs1P.add(concas1T);
//
//        concGeoAs2P.setLayout(new FlowLayout(FlowLayout.RIGHT));
//        concGeoAs2P.add(concas2L);
//        concGeoAs2P.add(concas2T);

        ButtonP.setLayout(new FlowLayout(FlowLayout.CENTER));
        ButtonP.add(assignB);
        ButtonP.add(cancelB);

        concFieldsP.setLayout(new GridLayout(6, 1));
        concFieldsP.add(concMatP);
        concFieldsP.add(concEP);
        concFieldsP.add(concGP);
        concFieldsP.add(concFckP);
        concFieldsP.add(concFykP);
//        concFieldsP.add(concGeoP);
//        concFieldsP.add(concGeoBP);
//        concFieldsP.add(concGeoHP);
//        concFieldsP.add(concGeoAs1P);
//        concFieldsP.add(concGeoAs2P);
        concFieldsP.add(ButtonP);

        concP.setLayout(new FlowLayout(FlowLayout.LEFT));
        concP.add(concFieldsP);

        topP.setLayout(new GridLayout(1, 1));
        topP.add(concP);

        add(topP);

        addWindowListener(new WindowAdapter() { //make window closable
            public void windowClosing(WindowEvent e) {
                //dispose();
                setVisible(false);
            }
        });
    }

    public void init(MaterialDialogMod md) {
        md.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        md.pack();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        double  E, G, fcu, fyk;

        if (ae.getSource() == assignB) {
            try {
                G = Double.valueOf(concGT.getText());
            } catch (NumberFormatException exception) {
                G = 80000;
            }
            try {
                E = Double.valueOf(concET.getText());
            } catch (NumberFormatException exception) {
                E = 200000;
            }
            try {
                fcu = Double.valueOf(concfckT.getText());
            } catch (NumberFormatException exception) {
                fcu = 25;
            }
                        try {
                fyk = Double.valueOf(concfykT.getText());
            } catch (NumberFormatException exception) {
                fyk = 300;
            }
            drawPanel.AssignMaterials(E, G, fcu, fyk);
            setVisible(false);
        } else if (ae.getSource() == cancelB) {
            setVisible(false);
        }
    }

    public static void main(String[] args) {
        WireframeViewer x = new WireframeViewer();
        MaterialDialogMod test = new MaterialDialogMod(x);
        test.init(test);
        test.pack();
        test.setVisible(true);
    }
}
