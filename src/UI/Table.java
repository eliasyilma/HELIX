/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import SAM3D.Member3;
import SAM3D.Structure3;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import static UI.memberPanel.stressFormatter;

/**
 *
 * @author user
 */
public class Table {

    static JButton export;
    static JTable table;

    public static void initAndShowGUI(Structure3 S) {
        String[] columns = new String[]{"Member ID", "Breadth", "Height", "Basic R.", "Extra Y.", "Extra Z.", "Shear L.", "Shear M.", "Shear R.", "e, BIAX ", "e, VY ", "e, VZ", "Penalty"};
        Object[][] sdata = populateDesignTable(S);
        table = new JTable(sdata, columns);
        table.setFont(new Font("Aliquam", Font.PLAIN, 16));
        resizeColumnWidth(table);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JFrame frame = new JFrame("Table test");
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            SwingUtilities.updateComponentTreeUI(frame);
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
        }
        Color darkGreen = new Color(12, 113, 24);

        JPanel panel = new JPanel(new BorderLayout());
        JScrollPane pane = new JScrollPane(table);
        JPanel Bpanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        export = new JButton("EXPORT TO TSV");
        table.getTableHeader().setFont(new Font("Aliquam", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(1, 161, 133));
        table.getTableHeader().setForeground(Color.white);
        table.getTableHeader().setBorder(new LineBorder(Color.white, 1));
        panel.add(pane);
        Bpanel.add(export);
        panel.add(pane, BorderLayout.NORTH);
        panel.add(Bpanel, BorderLayout.SOUTH);

        int height = Math.min((table.getRowCount() + 2) * (table.getRowHeight()), 600);
        pane.setPreferredSize(new Dimension(table.getPreferredSize().width, height));
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        export.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {

                final JFileChooser fc = new JFileChooser();
                int returnVal = fc.showSaveDialog(export); //parent component to JFileChooser
                if (returnVal == JFileChooser.APPROVE_OPTION) { //OK button pressed by user
                    File file = fc.getSelectedFile(); //get File selected by user
                    try {
                        toExcel(table, file);

                        //your writing code goes here
                    } catch (IOException ex) {
                        Logger.getLogger(Table.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        Structure3 S = new Structure3();
        initAndShowGUI(S);
    }

    public static void resizeColumnWidth(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();

        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 15;
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 10, width);
            }
            if (width > 300) {
                width = 300;
            }
            columnModel.getColumn(column).setPreferredWidth(width);

        }
        table.setRowHeight(20);

    }

    public static void toExcel(JTable table, File file) throws IOException {
        TableModel model = table.getModel();
        FileWriter excel = new FileWriter(file);
        for (int i = 0; i < model.getColumnCount(); i++) {
            excel.write(model.getColumnName(i) + "\t");
        }
        excel.write("\n");
        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                excel.write(model.getValueAt(i, j).toString() + "\t");
            }
            excel.write("\n");
        }
        excel.close();
    }

    public static Object[][] populateDesignTable(Structure3 S) {
        Object[][] data = new Object[S.memberVector.length][12];
        for (int i = 0; i < S.memberVector.length; i++) {
            Member3 mem = S.memberVector[i];
            String memberID = "" + mem.getMemberID();
            String width = "" + (int) mem.getXsecNegLeft().Breadth;
            String height = "" + (int) mem.getXsecNegLeft().Height;
            String mainR, extraX, extraY, shearSpL, shearSpM, shearSpR;
            if (!mem.isVertical()) {
                mainR = " " + mem.getXsecNegLeft().reBar.main_num + " d" + (int) mem.getXsecNegLeft().reBar.main_dia;
                extraX = " " + mem.getXsecNegLeft().reBar.extra_x_numT + " d " + (int) mem.getXsecNegLeft().reBar.extra_x_diaT + "(TL) , " +(int) mem.getXsecNegRight().reBar.extra_x_numT + " d " +(int) mem.getXsecNegRight().reBar.extra_x_diaT + "(TR) , " +(int) mem.getXsecPos().reBar.extra_x_numB + " d " + (int) mem.getXsecPos().reBar.extra_x_diaB + "(B)";
                extraY = " " + mem.getXsecNegLeft().reBar.extra_y_numL + " d " + (int) mem.getXsecNegLeft().reBar.extra_y_diaL + "(L) , " +(int) mem.getXsecPos().reBar.extra_y_numR + " d " + (int) mem.getXsecPos().reBar.extra_y_diaR + "(R)";
                shearSpL = " d8/ " + mem.getXsecNegLeft().shearS;
                shearSpM = " d8/ " + mem.getXsecPos().shearS;
                shearSpR = " d8/ " + mem.getXsecNegRight().shearS;
            } else {
                mainR = " " + mem.getXsecNegLeft().reBar.main_num + " d" + (int) mem.getXsecNegLeft().reBar.main_dia;
                extraX = " " + mem.getXsec().reBar.extra_x_numT + " d " + (int) mem.getXsec().reBar.extra_x_diaT ;
                extraY = " " +(int) mem.getXsecNegLeft().reBar.extra_y_numL + " d " + (int) mem.getXsecNegLeft().reBar.extra_y_diaL + "(L) , " +(int) mem.getXsecPos().reBar.extra_y_numR + " d " + (int) mem.getXsecPos().reBar.extra_y_diaR + "(R)";
                shearSpL = " d8/ " + mem.getXsecPos().shearS;
                shearSpM = " d8/ " + mem.getXsecPos().shearS;
                shearSpR = " d8/ " + mem.getXsecPos().shearS;
            }
            String BI = " " + stressFormatter.format(mem.biaxialEff * 100) + "%";
            String SY = " " + stressFormatter.format(mem.shearYEff * 100) + "%";
            String SZ = " " + stressFormatter.format(mem.shearZEff * 100) + "%";
            String Total = " " + stressFormatter.format(mem.totalPenalty);

            String[] rowData = new String[]{memberID, width, height, mainR, extraX, extraY, shearSpL, shearSpM, shearSpR, BI, SY, SZ, Total};
            data[i] = rowData;
        }
        return data;
    }
}
