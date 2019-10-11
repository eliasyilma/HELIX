/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

/**
 *
 * @author CASE
 */

    import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
public class SplashScreen extends JWindow {
  private int duration;
  public SplashScreen(int d) {
    duration = d;
    String arg="src/resources/finale2.gif";
   JLabel l=new JLabel();
    ImageIcon icon=new ImageIcon(arg);
     
        JPanel content = (JPanel) getContentPane();
     content.add(l);
        l.setIcon(icon);  
//        content.setBackground(Color.white);
    int width = 446;
    int height = 664;
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (screen.width - width) / 2;
    int y = (screen.height - height) / 2;
    setBounds(x, y, width, height);
//    content.add(new JLabel("asdf"), BorderLayout.CENTER);
//    Color oraRed = new Color(156, 20, 20, 255);
//    content.setBorder(BorderFactory.createLineBorder(oraRed, 10));
    setVisible(true);
    try {
      Thread.sleep(duration);
    } catch (Exception e) {
    }
    setVisible(false);
        dispose();

  }
  public static void main(String[] args) {
    SplashScreen splash = new SplashScreen(10000);
  }

}
