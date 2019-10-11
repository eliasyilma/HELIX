/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;




import SAM3D.Joint3;
import SAM3D.Load3;
import SAM3D.Member3;
import SAM3D.Structure3;
//import static SAMv2.Structure.printVector;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.text.DecimalFormat;
import java.util.LinkedList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import static UI.SectionInfoFX.darkGreen;
import static UI.SectionInfoFX.pomegranateR;
import static UI.VectorUtil3D.shortestDistancePointToLine3D;

/**
 *
 * @author user
 */
public class WireframeViewer extends JPanel
        implements MouseListener, MouseMotionListener {

    int width, height;
    int mx, my;  // the most recently recorded mouse coordinates

//Menu Items
    public static MenuBar MenuBar;
    public static Menu Assign;
    public static MenuItem JointConstraints;
    public static MenuItem MemberLoads;
    public static MenuItem MemberProperties;
    public static MenuItem MemberSection;
    public static Menu Analyze;
    public static MenuItem Analysis;
    public static MenuItem MemberInfo;
    public static MenuItem StiffnessMatrix;
    public static MenuItem Displacements;
    public static MenuItem MemberForces;
    public static MenuItem Reactions;
    public static MenuItem TxtOutput;
    public static Menu DesignM;
    public static MenuItem Design;
    public static MenuItem Optimize;

//State of the drawing canvas
    public static boolean viewing = false;
    public static boolean drawing = true;
    public static boolean selectJ = false;
    public static boolean selectM = false;

    public boolean clickdragging; //is the mouse in drag mode?
    public boolean dragging; //is the mouse in drag mode?
    public float scale = 1.0f;
    public float scalemin = 0.05f;
    public float scalemax = 5f;
//State of Objects in the canvas
    public boolean startNodeExists = false; //check if the user has clicked once to draw a member(false means it hasn't)
    public boolean rectNodeExists = false; //check if the user has clicked once to draw a member(false means it hasn't)
    public boolean jointVNE = false;//is the joint vector empty?(false == yes)
    public boolean memberVNE = false;//is the member vector empty?(false == yes)
    public boolean selectedJNE = false;
    public boolean selectedMNE = false;
    public static boolean constrainedJNE = false;
    public boolean gridNE = true;//not neccessary but still...
    public static double gX = 10, gY = 10, gZ = 10;
    public static int nX = 2, nY = 2, nZ = 2;
//start and end of members
    public Joint3 start, end, prev, sn;
    public int sx, sy, ex, ey;
    public int prevx, prevy;
    float panX = 0f;
    float panY = 0f;
    int panClickX, panClickY;
    public Joint3 startJ, endJ;

    int azimuth = 0, elevation = 0;
    boolean boxdrawn = false;
    boolean close = false;

    Edge[] memberV;
    Edge[] axesEdges;

    public static LinkedList<Member3> MemberV = new LinkedList<>();
    public static LinkedList<Member3> beamV = new LinkedList<>();
    public static LinkedList<Member3> columnV = new LinkedList<>();
    public static LinkedList<Joint3> JointV = new LinkedList<>();
    LinkedList<Joint3> GridV = new LinkedList<>();
    LinkedList<Joint3> axes = new LinkedList<>();
    ;
    public static LinkedList<Joint3> selectedJointV = new LinkedList<>();
    public static LinkedList<Member3> selectedMemberV = new LinkedList<>();

    int extent = 15; //extent of the canvas in both x and y directions
    Graphics dragGr = getGraphics();
    public static float panXMax = 10, panYMax = 10, panXMin = 10, panYMin = 10;
    //colors to be used on the canvas
//    public static Color BACKGROUNDCOLOR = new Color(247, 222, 202);
//    public static Color SnapC = new Color(230, 0, 0);
//    public static Color GridC = new Color(217, 178, 147);
//    public static Color HighlightM = new Color(0, 0, 230);
//    public static Color HighlightC = new Color(213, 213, 0);
//    public static Color memberC = new Color(213, 0, 0);
//    public static Color jointIDC = new Color(190, 0, 0);
//    public static Color selectedJointsC = new Color(214, 84, 0);
//    public static Color selectedMembersC = new Color(250, 250, 250);
//    public static Color xC = new Color(196, 57, 38);
//    public static Color yC = new Color(12, 113, 24);
//    public static Color zC = new Color(33, 127, 188);

    public static Color BACKGROUNDCOLOR = new Color(250, 250, 250);
    public static Color SnapC = new Color(230, 0, 0);
    public static Color GridC = new Color(190, 195, 199);
    public static Color HighlightM = new Color(0, 0, 230);
    public static Color HighlightC = new Color(213, 213, 0);
    public static Color memberC = darkGreen;
    public static Color jointIDC = new Color(190, 0, 0);
    public static Color selectedJointsC = new Color(214, 24, 0);
    public static Color selectedMembersC = new Color(250, 250, 250);
    public static Color xC = darkGreen;
    public static Color yC = pomegranateR;
    public static Color zC = new Color(33, 127, 188);

    public static Font jointIDF = new Font("century gothic", Font.BOLD, 16);
    public static Font coordIDF = new Font("century gothic", Font.TRUETYPE_FONT, 16);
    public static Font memberIDF = new Font("century gothic", Font.BOLD, 16);

    Joint3 eligiblePoint = null;
    Joint3 previousPoint = null;
    boolean highlighted = false;

    Member3 eligibleMember = null;
    Member3 previousMember = null;
    boolean highlightedM = false;

    public static JLabel Coordinates;
    public static Structure3 Str;

    static int BORDER = 0;
    public static boolean showResults = false;

    public WireframeViewer() {
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(new MouseAdapter() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double delta = 0.05f * e.getWheelRotation();
                scale -= delta;
                if (scale > scalemax) {
                    scale = scalemax;
                }
                if (scale < scalemin) {
                    scale = scalemin;
                }
                revalidate();
                repaint();
            }

        });

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        jointVNE = true;
        memberVNE = true;
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            if (showResults == true) {
                Member3 m = returnClosestMember(sn.getPxlx(), sn.getPxly(), 9);
                if (!(m == null)) {
                    memberPanel.initAndShowGUI(m);
                    SectionInfoFX.initAndShowGUI(m.getXsecPos(),m.getFyk(),m.getFck()*1.25*0.453333333);
                }
            }
        }
        if (drawing) {
            //if this is the first point, then when the user moves the pointer
            // rubber band lines would be drawn
            if (!startNodeExists) {
                dragGr = getGraphics();
                dragGr.setXORMode(BACKGROUNDCOLOR);
                start = prev = SnapToGrid(e.getX(), e.getY());
//                sx = prevx = (int) snapX(e.getX());
//                sy = prevy = (int) snapY(e.getY());
                //if a joint already exists at that location,

                Joint3 j = returnClosestJoint(start.getPxlx(), start.getPxly(), 7);

                if (j == null) {
                    if (JointV.size() == 0) {
                        startJ = new Joint3(1, start.getX(), start.getY(), start.getZ(), start.getPxly(), start.getPxly());
                    } else {
                        startJ = new Joint3(JointV.size() + 1, start.getX(), start.getY(), start.getZ(), start.getPxly(), start.getPxly());
                    }
                } else {
                    //then set the start joint to the already existing joint.
                    startJ = j;
                }

                dragging = true;
                startNodeExists = true;
                addUniqueJoint(startJ);
                jointVNE = true;
                memberVNE = true;

            } else {
                //if this is the second point, draw the line
                dragGr.setPaintMode();
//                Draw.frame(dragGr, sx, sy, ex, ey);
                if (!(start == null || end == null)) {

                    drawMember(dragGr, start, end);
                }
                end = SnapToGrid(e.getX(), e.getY());
                System.out.println("ex: " + end.getX() + "ey: " + end.getY());
                Joint3 j = returnClosestJoint(end.getPxlx(), end.getPxly(), 7);
                if (j == null) {
                    endJ = new Joint3(JointV.size() + 1, end.getX(), end.getY(), end.getZ(), end.getPxlx(), end.getPxly());
                } else {
                    endJ = j;
                    System.out.println("closest: " + j.getJointID());

                }
                dragGr.dispose();
                dragging = false;
                startNodeExists = false;
                addUniqueJoint(endJ);
                System.out.println("start: " + startJ.getJointID() + " end:  " + endJ.getJointID());
                System.out.println(JointV.size());
                addUniqueMember(MemberV.size(), startJ, endJ);
            }
            //debugging print out
            repaint();
        } //if in joint select mode
        else if (selectJ) {
            start = prev = new Joint3(0, 0, 0, 0, e.getX(), e.getY());
            Joint3 j = returnClosestJoint(sn.getPxlx(), sn.getPxly(), 9);
            if (!(j == null)) {
                selectedJNE = true;
                if (!jointAlreadySelected(j, selectedJointV)) {
                    selectedJointV.add(j);
                    repaint();
                } else {
                    selectedJointV.remove(j);
                    repaint();
                }
            }

            if (close == false && boxdrawn == true) {
                dragGr.setColor(BACKGROUNDCOLOR);
                dragGr.drawRect((int) sn.getPxlx() - 8, (int) sn.getPxly() - 8, 16, 16);
                dragGr.drawRect((int) sn.getPxlx() - 9, (int) sn.getPxly() - 9, 18, 18);
                boxdrawn = false;
                dragGr.setXORMode(BACKGROUNDCOLOR);
                repaint();

            } //box drawn and cursor close-->do nothing
            else if (close == true && boxdrawn == true) {
                //box not drawn and cursor not close-->do nothing 
            } else if (close == false && boxdrawn == false) {
                //box not drawn and cursor close-->draw    
            } else if (close == true && boxdrawn == false) {
                dragGr.setColor(SnapC);
                dragGr.drawRect((int) sn.getPxlx() - 8, (int) sn.getPxly() - 8, 16, 16);
                dragGr.drawRect((int) sn.getPxlx() - 9, (int) sn.getPxly() - 9, 18, 18);
                boxdrawn = true;
            }
            System.out.println("selJ:" + selectedJointV.size());

        } //if in member select mode
        else if (selectM) {
            Member3 m = returnClosestMember(sn.getPxlx(), sn.getPxly(), 9);
            if (!(m == null)) {
                selectedMNE = true;
                if (!memberAlreadySelected(m, selectedMemberV)) {
                    selectedMemberV.add(m);
                    repaint();
                } else {
                    selectedMemberV.remove(m);
                    repaint();
                }
            }
            System.out.println("selM:" + selectedMemberV.size());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mx = e.getX();
        my = e.getY();
        panClickX = mx;
        panClickY = my;
        e.consume();
        if (selectJ) {

            if (clickdragging) // If already dragging, don't do anything.
            {
                return;     //  (This can happen if the user presses two mouse buttons.
            }
            dragGr = getGraphics();  // Get a graphics context and draw in XOR mode.
            dragGr.setXORMode(Color.WHITE);
            start = prev = new Joint3(0, 0, 0, 0, e.getX(), e.getY());  // Save coords of mouse position.
            dragGr.setColor(Color.black);
//            System.out.println("x: " + start.getPxlx() + "   y: " + start.getPxly());
            clickdragging = true;  // Start dragging.
            dragGr.drawRect(start.getPxlx(), start.getPxly(), prev.getPxlx() - start.getPxlx(), prev.getPxly() - start.getPxly());

        } else if (selectM) {

            if (clickdragging) // If already dragging, don't do anything.
            {
                return;     //  (This can happen if the user presses two mouse buttons.
            }
            dragGr = getGraphics();  // Get a graphics context and draw in XOR mode.
            dragGr.setXORMode(Color.WHITE);
            start = prev = new Joint3(0, 0, 0, 0, e.getX(), e.getY());  // Save coords of mouse position.
            dragGr.setColor(Color.black);
            clickdragging = true;  // Start dragging.
            dragGr.drawLine(start.getPxlx(), start.getPxly(), prev.getPxlx(), prev.getPxly());

        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // End the dragging operation, if one is in progress.  Erase
        // the last XOR mode figure, and draw the final figure 
        // permanently in paint mode.
        if (selectJ) {
            if (clickdragging) {
                dragGr.drawRect(start.getPxlx(), start.getPxly(), prev.getPxlx() - start.getPxlx(), prev.getPxly() - start.getPxly());  // Erase the previous figure by redrawing in XOR mode.
                selectAllJointsInRegion(dragGr);
                dragGr = getGraphics();
                repaint();
                clickdragging = false;
            }
        } else if (selectM) {
            dragGr.drawLine(start.getPxlx(), start.getPxly(), prev.getPxlx(), prev.getPxly());
            selectAllMembersCrossed(dragGr);
            dragGr = getGraphics();
            repaint();
            clickdragging = false;
        }

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int xx = e.getX();
        int yy = e.getY();

        dragGr = getGraphics();
        dragGr.setPaintMode();
        Joint3 closest = returnClosestJoint(xx, yy, 10);
        dragGr = getGraphics();
        dragGr.setPaintMode();
        close = (closeToJoint(xx, yy, JointV, 9));
//        System.out.println("cl: " + close + " f:" + boxdrawn);
//            Coordinates.setText(" X: "+closest.x+" Y: "+closest.y+" Z: "+closest.z);
        if (drawing) {
            if (dragging) {
                dragGr.setXORMode(BACKGROUNDCOLOR);
                if (!(start == null || prev == null)) {
                    drawMember(dragGr, start, prev);
                }// Erase the previous figure by drawing in XOR mode.
                prev = SnapToGrid(e.getX(), e.getY());
                if (!(start == null || prev == null)) {
                    drawMember(dragGr, start, prev);
                }
            }
// Draw the figure in its new position.
        } else if (selectJ) {
            sn = new Joint3(0, 0, 0, 0, e.getX(), e.getY());

            //if cursor isnt close and the Point is highlighted
            if (close == false && highlighted == true) {
                //draw the original line
//                dragGr.setColor(Color.white);
//                drawHighlightedPoint(dragGr, eligiblePoint);
                highlighted = false;
                dragGr.setXORMode(BACKGROUNDCOLOR);
                repaint();
                if (!(eligiblePoint == null)) {
//                eligiblePoint=returnClosestPoint(snx,sny,9);
                    drawHighlightedPoint(dragGr, eligiblePoint);
                }

            } //if cursor is close and the Point is highlighted
            else if (close == true && highlighted == true) {
                eligiblePoint = returnClosestJoint(xx, yy, 9);
                dragGr.setColor(SnapC);
                drawHighlightedPoint(dragGr, previousPoint);
                dragGr.setColor(Color.RED);
                drawHighlightedPoint(dragGr, eligiblePoint);
                previousPoint = eligiblePoint;

            } //if cursor is not close and nothing is highlighted
            else if (close == false && highlighted == false) {
                //do nothing
            } //if cursor is close but Point not highlighted
            else if (close == true && highlighted == false) {
                //draw highlighted line
                dragGr.setColor(SnapC);
                previousPoint = returnClosestJoint(xx, yy, 9);
                eligiblePoint = returnClosestJoint(xx, yy, 9);
                drawHighlightedPoint(dragGr, eligiblePoint);
                highlighted = true;
            }

        } else if (selectM) {
            //                sn = SnapToGrid(e.getX(), e.getY());
            sn = new Joint3(0, 0, 0, 0, e.getX(), e.getY());
            dragGr = getGraphics();
            dragGr.setPaintMode();
            close = (closeToMember(sn.getPxlx(), sn.getPxly(), 9));

            //if cursor isnt close and the member is highlighted
            if (close == false && highlightedM == true) {
                //draw the original line
//                dragGr.setColor(Color.white);
//                dragGr.drawRect((int) snapX(snx) - 7, (int) snapY(sny) - 7, 14, 14);
//                dragGr.drawRect((int) snapX(snx) - 8, (int) snapY(sny) - 8, 16, 16);
                highlightedM = false;
                dragGr.setXORMode(BACKGROUNDCOLOR);
                repaint();
                if (!(eligibleMember == null)) {
//                eligibleMember=returnClosestMember(snx,sny,9);
                    drawHighlightedMember(dragGr, eligibleMember);
                }

            } //if cursor is close and the member is highlighted
            else if (close == true && highlightedM == true) {
                eligibleMember = returnClosestMember(sn.getPxlx(), sn.getPxly(), 9);
                dragGr.setColor(memberC);
                drawHighlightedMember(dragGr, previousMember);
                dragGr.setColor(Color.RED);
                drawHighlightedMember(dragGr, eligibleMember);
                previousMember = eligibleMember;

            } //if cursor is not close and nothing is highlighted
            else if (close == false && highlightedM == false) {
                //do nothing
            } //if cursor is close but member not highlighted
            else if (close == true && highlightedM == false) {
                //draw highlighted line
                dragGr.setColor(SnapC);
                previousMember = returnClosestMember(sn.getPxlx(), sn.getPxly(), 9);
                eligibleMember = returnClosestMember(sn.getPxlx(), sn.getPxly(), 9);
                drawHighlightedMember(dragGr, eligibleMember);
                highlightedM = true;
            }

        }
    }

    //               System.out.println("Untransformed :" + JointV[1].Print() + " Transformed :" + points[1].toString());
    public void drawHighlightedPoint(Graphics g, Joint3 point) {
        g.setColor(SnapC);
        orthoProject(azimuth, elevation, point);
        g.drawOval((int) point.getPxlx() - 8, (int) point.getPxly() - 8, 16, 16);
        g.drawOval((int) point.getPxlx() - 7, (int) point.getPxly() - 7, 14, 14);

    }

    public void drawHighlightedMember(Graphics g, Member3 member) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(HighlightM);
        orthoProject(azimuth, elevation, member.getStart());
        orthoProject(azimuth, elevation, member.getEnd());
        g2d.drawLine(member.getStart().getPxlx(), member.getStart().getPxly(), member.getEnd().getPxlx(), member.getEnd().getPxly());
    }

    @Override
    public void mouseDragged(MouseEvent e) {

        if (SwingUtilities.isRightMouseButton(e)) {
            int new_mx = e.getX();
            int new_my = e.getY();
            float panDistX = Math.abs(panClickX - new_mx);
            float panDistY = Math.abs(panClickY - new_my);
            float mouseSpeedX = Math.abs(mx - new_mx);
            float mouseSpeedY = Math.abs(my - new_my);
            System.out.println("panX: " + panX + "panY: " + panY);
//            System.out.println("mX: "+mouseSpeedX+"mY: "+mouseSpeedY+" pX: "+panDistX+" py: "+panDistY);
            if (new_mx - mx > 0) {
                panX += 0.1f;
            } else if ((new_mx - mx < 0)) {
                panX -= 0.1f;
            } else {

            }

            if (new_my - my < 0) {
                panY += 0.1f;
            } else if ((new_my - my > 0)) {
                panY -= 0.1f;
            } else {

            }
            mx = new_mx;
            my = new_my;
            repaint();
            e.consume();

        } else if (SwingUtilities.isMiddleMouseButton(e)) {
            // get the latest mouse position
            int new_mx = e.getX();
            int new_my = e.getY();

            // adjust angles according to the distance travelled by the mouse
            // since the last event
            azimuth -= new_mx - mx;
            elevation += new_my - my;

            // update the backbuffer
//        drawWireframe(backg);
            // update our data
            mx = new_mx;
            my = new_my;
            repaint();
            e.consume();

//        System.out.println("azimuth : " + azimuth + " elevation : " + elevation);
        } else if (SwingUtilities.isLeftMouseButton(e)) {
//            boolean rectStartPoint = true;
            //           System.out.println("st: " + start.getPxlx() + "     " + start.getPxly());
            //           System.out.println("end: " + prev.getPxlx() + "     " + prev.getPxly());
            //           dragGr.setPaintMode();
            if (selectJ) {
                if (clickdragging) {

                    dragGr.setColor(Color.black);
                    dragGr.drawRect(start.getPxlx(), start.getPxly(), prev.getPxlx() - start.getPxlx(), prev.getPxly() - start.getPxly());
//                    System.out.println("x: " + prev.getPxlx() + "   y: " + prev.getPxly());
                    prev = new Joint3(0, 0, 0, 0, e.getX(), e.getY());
                    dragGr.setColor(Color.black);
                    dragGr.drawRect(start.getPxlx(), start.getPxly(), prev.getPxlx() - start.getPxlx(), prev.getPxly() - start.getPxly());
                }
            } else if (selectM) {
                if (clickdragging) {
                    dragGr.setColor(Color.black);
                    dragGr.drawLine(start.getPxlx(), start.getPxly(), prev.getPxlx(), prev.getPxly());
                    prev = new Joint3(0, 0, 0, 0, e.getX(), e.getY());
                    dragGr.setColor(Color.black);
                    dragGr.drawLine(start.getPxlx(), start.getPxly(), prev.getPxlx(), prev.getPxly());

                }
            }

        }
    }

    public Joint3 returnClosestJoint(double x, double y, double radius) {
        Joint3 closestJoint = null;
        if (JointV != null) {
            for (int i = 0; i < JointV.size(); i++) {
                int jx = (int) JointV.get(i).getPxlx();
                int jy = (int) JointV.get(i).getPxly();
                if (Math.sqrt((x - jx) * (x - jx) + (y - jy) * (y - jy)) - radius < 0) {
                    closestJoint = JointV.get(i);
                }
            }
        }
        return closestJoint;
    }

    public Joint3 snapToGrid(double x, double y) {
        Joint3 closestJoint = null;
        if (JointV != null) {

            for (int i = 0; i < JointV.size(); i++) {
                int jx = (int) JointV.get(i).getPxlx();
                int jy = (int) JointV.get(i).getPxly();
                double dist = (Math.sqrt((x - jx) * (x - jx) + (y - jy) * (y - jy)));
                closestJoint = JointV.get(i);

            }
        }
        return closestJoint;

    }

    public void drawAxes(Graphics2D g) {

        axes.add(new Joint3(0, 0, 0));
        axes.add(new Joint3(2, 0, 0));
        axes.add(new Joint3(0, 2, 0));
        axes.add(new Joint3(0, 0, 2));

        axesEdges = new Edge[3];
        axesEdges[0] = new Edge(0, 1);
        axesEdges[1] = new Edge(0, 2);
        axesEdges[2] = new Edge(0, 3);
        Point[] points;
        points = new Point[axes.size()];
        for (int j = 0; j < axes.size(); ++j) {
            // compute an orthographic projection
            orthoProject(azimuth, elevation, axes.get(j));
            // the 0.5 is to round off when converting to int

//            String coordinateInfo = j + "(" + axes[j].x + "," + axes[j].y + "," + axes[j].z + ")";
//            drawText(g, coordinateInfo, coor.x, coor.y);
        }
        g.setColor(xC);
        g.setStroke(new BasicStroke(2));
//x-axis
        g.setColor(xC);
        g.drawLine(
                axes.get(axesEdges[0].a).getPxlx(), axes.get(axesEdges[0].a).getPxly(),
                axes.get(axesEdges[0].b).getPxlx(), axes.get(axesEdges[0].b).getPxly());
        drawText(g, "X", axes.get(1).getPxlx() + 5, axes.get(1).getPxly() + 5);

//y-axis
        g.setColor(yC);
        g.drawLine(
                axes.get(axesEdges[1].a).getPxlx(), axes.get(axesEdges[1].a).getPxly(),
                axes.get(axesEdges[1].b).getPxlx(), axes.get(axesEdges[1].b).getPxly());
        drawText(g, "Y", axes.get(2).getPxlx() + 5, axes.get(2).getPxly() + 5);

//z-axis
        g.setColor(zC);
        g.drawLine(
                axes.get(axesEdges[2].a).getPxlx(), axes.get(axesEdges[2].a).getPxly(),
                axes.get(axesEdges[2].b).getPxlx(), axes.get(axesEdges[2].b).getPxly());
        drawText(g, "Z", axes.get(3).getPxlx() + 5, axes.get(3).getPxly() + 5);

    }

    public void drawText(Graphics2D g, String s, int x, int y) {
        //       g.setColor(Color.blue);
        g.drawString(s, x, y);
    }

    public void drawMember(Graphics g, Joint3 StartJ, Joint3 EndJ) {

        g.setColor(new Color(84, 31, 173));
        g.drawLine(StartJ.getPxlx(), StartJ.getPxly(), EndJ.getPxlx(), EndJ.getPxly());
    }

    public void orthoProject(int azimuth, int elevation, Joint3 point) {
        width = getSize().width;
        height = getSize().height;
        float scaleFactor = width * scale / extent;
        float near = 15f;  // distance from eye to near plane
        float nearToObj = 1.5f;  // distance from near plane to center of object
        float[] coordinates = new float[2];
        double x0 = point.getX(), y0 = point.getY(), z0 = point.getZ();
        double theta = Math.PI * azimuth / 180.0;
        double phi = Math.PI * elevation / 180.0;
        float cosT = (float) Math.cos(theta), sinT = (float) Math.sin(theta);
        float cosP = (float) Math.cos(phi), sinP = (float) Math.sin(phi);
        float cosTcosP = cosT * cosP, cosTsinP = cosT * sinP,
                sinTcosP = sinT * cosP, sinTsinP = sinT * sinP;
        if (panX > panXMax) {
            panX = panXMax;
        }
        if (panX > panXMin) {
            panX = panXMin;
        }
        if (panY > panYMax) {
            panY = panYMax;
        }
        if (panY > panYMin) {
            panY = panYMin;
        }
        coordinates[0] = (float) (cosT * x0 + sinT * z0);
        coordinates[1] = (float) (-sinTsinP * x0 + cosP * y0 + cosTsinP * z0);
        //
        point.setPxlx((int) (width / 2 + scaleFactor * coordinates[0] + 0.5 + panX * 50));
        point.setPxly((int) (height / 2 - scaleFactor * coordinates[1] + 0.5 - panY * 50));

        // now adjust things to get a perspective projection
//                   float z1 = (float) (cosTcosP * z0 - sinTcosP * x0 - sinP * y0);
//                   x1 = x1 * near / (z1 + near + nearToObj);
//                  y1 = y1 * near / (z1 + near + nearToObj);
//
    }

    public void Grid(Graphics2D g, int noX, int noY, int noZ, double extentX, double extentY, double extentZ) {
        g.setStroke(new BasicStroke(2));
        g.setColor(GridC);
        GridV = new LinkedList<>();
        double GridSizeX = extentX / (float) (noX - 1);
        double GridSizeY = extentY / (float) (noY - 1);
        double GridSizeZ = extentZ / (float) (noZ - 1);

        double StartX = -extentX / 2.0;
        double StartY = -extentY / 2.0;
        double StartZ = -extentZ / 2.0;
        for (double z = StartZ; z <= extentZ + StartZ; z = z + GridSizeZ) {
            for (double x = StartX; x <= extentX + StartX; x = x + GridSizeX) {
                Joint3 p1 = new Joint3(x, StartY, z);
                Joint3 p2 = new Joint3(x, extentY + StartY, z);
                orthoProject(azimuth, elevation, p1);
                orthoProject(azimuth, elevation, p2);
                GridV.add(p1);
                GridV.add(p2);

                g.drawLine(p1.getPxlx(), p1.getPxly(), p2.getPxlx(), p2.getPxly());
            }
            for (double y = StartY; y <= extentY + StartY; y = y + GridSizeY) {
                Joint3 p1 = new Joint3(StartX, y, z);
                Joint3 p2 = new Joint3(extentX + StartX, y, z);
                orthoProject(azimuth, elevation, p1);
                orthoProject(azimuth, elevation, p2);
                GridV.add(p1);
                GridV.add(p2);
                g.drawLine(p1.getPxlx(), p1.getPxly(), p2.getPxlx(), p2.getPxly());
            }

        }
        for (double x = StartX; x <= extentX + StartX; x = x + GridSizeX) {
            for (double y = StartY; y <= extentY + StartY; y = y + GridSizeY) {
                Joint3 p1 = new Joint3(x, y, StartZ);
                Joint3 p2 = new Joint3(x, y, extentZ + StartZ);
                orthoProject(azimuth, elevation, p1);
                orthoProject(azimuth, elevation, p2);
                GridV.add(p1);
                GridV.add(p2);
                g.drawLine(p1.getPxlx(), p1.getPxly(), p2.getPxlx(), p2.getPxly());
            }

        }
    }

    public void drawJointID(Graphics2D g, LinkedList<Joint3> joints) {
        g.setColor(jointIDC);
        g.setFont(jointIDF);
        if (joints.size() > 0) {
            for (int i = 0; i < joints.size(); i++) {
                orthoProject(azimuth, elevation, joints.get(i));
                int x = (int) joints.get(i).getPxlx();
                int y = (int) joints.get(i).getPxly();
                g.drawString(String.valueOf(joints.get(i).getJointID()), x + 7, y);
            }
        }
    }

    public void drawFrameVector(Graphics g, LinkedList<Member3> frameV) {
        g.setColor(memberC);
        if (frameV.size() > 0) {
            for (int i = 0; i < frameV.size(); i++) {
                orthoProject(azimuth, elevation, frameV.get(i).getStart());
                orthoProject(azimuth, elevation, frameV.get(i).getEnd());
                int smx = (int) frameV.get(i).getStart().getPxlx();
                int smy = (int) frameV.get(i).getStart().getPxly();
                int emx = (int) frameV.get(i).getEnd().getPxlx();
                int emy = (int) frameV.get(i).getEnd().getPxly();
                g.drawLine(smx, smy, emx, emy);
            }
        }
    }

    public void drawSelectedJoints(Graphics g, LinkedList<Joint3> selectedJVector) {

        g.setColor(selectedJointsC);
        for (int i = 0; i < selectedJVector.size(); i++) {
            orthoProject(azimuth, elevation, selectedJVector.get(i));
            int x = (int) selectedJVector.get(i).getPxlx();
            int y = (int) selectedJVector.get(i).getPxly();
            g.fillOval(x - 5, y - 5, 10, 10);
            //       g.drawOval(x - 6, y - 6, 12, 12);
        }
    }

    public void drawSelectedMembers(Graphics g, LinkedList<Member3> frameV) {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(selectedMembersC);
        Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
        g2d.setStroke(dashed);

        if (frameV.size() > 0) {
            for (int i = 0; i < frameV.size(); i++) {
                orthoProject(azimuth, elevation, frameV.get(i).getStart());
                orthoProject(azimuth, elevation, frameV.get(i).getEnd());
                int smx = (int) frameV.get(i).getStart().getPxlx();
                int smy = (int) frameV.get(i).getStart().getPxly();
                int emx = (int) frameV.get(i).getEnd().getPxlx();
                int emy = (int) frameV.get(i).getEnd().getPxly();
                g2d.drawLine(smx, smy, emx, emy);
            }
        }
    }

    public void DrawPortalFrame(int noX, int noY, int noZ, double extentX, double extentY, double extentZ) {

        double GridSizeX = extentX / (float) (noX - 1);
        double GridSizeY = extentY / (float) (noY - 1);
        double GridSizeZ = extentZ / (float) (noZ - 1);

        double StartX = -extentX / 2.0;
        double StartY = -extentY / 2.0;
        double StartZ = -extentZ / 2.0;
        for (double z = StartZ; z <= extentZ + StartZ; z = z + GridSizeZ) {
            for (double x = StartX; x <= extentX + StartX; x = x + GridSizeX) {
                for (double y = StartY; y <= StartY + extentY - GridSizeY; y = y + GridSizeY) {
                    Joint3 p1 = null;
                    if (JointV.size() == 0) {
                        p1 = new Joint3(1, x, y, z, 0, 0);
                        orthoProject(azimuth, elevation, p1);
                    } else {
                        p1 = new Joint3(JointV.size() + 1, x, y, z, 0, 0);
                        orthoProject(azimuth, elevation, p1);
                    }
                    p1 = returnUniqueJoint(p1);
                    Joint3 p2 = new Joint3(JointV.size() + 1, x, GridSizeY + y, z, 0, 0);
                    orthoProject(azimuth, elevation, p2);
                    p2 = returnUniqueJoint(p2);
                    addUniqueMember(MemberV.size(), p1, p2);
                }
            }
            for (double y = StartY + GridSizeY; y <= extentY + StartY; y = y + GridSizeY) {
                for (double x = StartX; x <= StartX + extentX - GridSizeX; x = x + GridSizeX) {
                    Joint3 p1 = null;
                    if (JointV.size() == 0) {
                        p1 = new Joint3(1, x, y, z, 0, 0);
                        orthoProject(azimuth, elevation, p1);
                    } else {
                        p1 = new Joint3(JointV.size() + 1, x, y, z, 0, 0);
                        orthoProject(azimuth, elevation, p1);
                    }
                    p1 = returnUniqueJoint(p1);
                    Joint3 p2 = new Joint3(JointV.size() + 1, GridSizeX + x, y, z, 0, 0);
                    orthoProject(azimuth, elevation, p2);
                    p2 = returnUniqueJoint(p2);
                    addUniqueMember(MemberV.size(), p1, p2);
                }
            }
        }
        for (double x = StartX; x <= extentX + StartX; x = x + GridSizeX) {
            for (double y = StartY + GridSizeY; y <= extentY + StartY; y = y + GridSizeY) {
                for (double z = StartZ; z <= StartZ + extentZ - GridSizeZ; z = z + GridSizeZ) {
                    Joint3 p1 = null;
                    if (JointV.size() == 0) {
                        p1 = new Joint3(1, x, y, z, 0, 0);
                        orthoProject(azimuth, elevation, p1);
                    } else {
                        p1 = new Joint3(JointV.size() + 1, x, y, z, 0, 0);
                        orthoProject(azimuth, elevation, p1);
                    }
                    p1 = returnUniqueJoint(p1);
                    Joint3 p2 = new Joint3(JointV.size() + 1, x, y, GridSizeZ + z, 0, 0);
                    orthoProject(azimuth, elevation, p2);
                    p2 = returnUniqueJoint(p2);
                    addUniqueMember(MemberV.size(), p1, p2);
                }
            }
        }
        memberVNE = true;
        repaint();
    }

    public void drawRestraints(Graphics g, LinkedList<Joint3> jV) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2));
        for (int i = 0; i < jV.size(); i++) {
            Joint3 j = jV.get(i);
            boolean fx, fy, fz, mx, my, mz;
            fx = j.isRrx();
            fy = j.isRry();
            fz = j.isRrz();
            mx = j.isRrmx();
            my = j.isRrmy();
            mz = j.isRrmz();
            orthoProject(azimuth, elevation, j);
            int x = (int) j.getPxlx();
            int y = (int) j.getPxly();
            if (fx && fy && fz && mx && my && mz) {//fixed
                x = (int) j.getPxlx() - 7;
                y = (int) j.getPxly();

                g2d.drawRect(x, y, 14, 10);
                g2d.drawRect(x - 1, y - 1, 16, 12);

            } else if (fx && fy && fz && !mx && !my && !mz) {//pin supports
                int[] trixi = new int[3];
                int[] trixo = new int[3];
                trixo[0] = x;
                trixo[1] = x - 10;
                trixo[2] = x + 10;
                trixi[0] = x;
                trixi[1] = x - 9;
                trixi[2] = x + 9;

                int[] triyi = new int[3];
                int[] triyo = new int[3];

                triyo[0] = y;
                triyo[1] = y + 14;
                triyo[2] = y + 14;
                triyi[0] = y + 1;
                triyi[1] = y + 13;
                triyi[2] = y + 13;

                g2d.drawPolygon(trixi, triyi, 3);
                g2d.drawPolygon(trixo, triyo, 3);

                g2d.drawLine(x - 12, y + 14, x + 12, y + 14);
                g2d.drawLine(x - 12, y + 15, x + 12, y + 15);
            }
//                else if (!h && v && !m) {//roller
//                g.drawOval(x - 7, y, 14, 14);
//                g.drawOval(x - 8, y - 1, 16, 16);
//                g.drawLine(x - 9, y + 14, x + 9, y + 14);
//                g.drawLine(x - 9, y + 15, x + 9, y + 15);
//                //g.drawOval(x-8, y-1, 16, 16);
//            }
        }
    }

    public void Loads(Graphics2D g, LinkedList<Member3> frameV) {
        Font loadsF = new Font("raleway", Font.BOLD, 16);
        g.setFont(loadsF);
        for (int i = 0; i < frameV.size(); i++) {
            Member3 currentMember = frameV.get(i);
            Joint3 st = currentMember.getStart();
            Joint3 end = currentMember.getEnd();
            double stx = st.getX();
            double sty = st.getY();
            double stz = st.getZ();
            double endx = end.getX();
            double endy = end.getY();
            double endz = end.getZ();
            for (int j = 0; j < currentMember.getLoads().size(); j++) {
                Load3 currentLoad = currentMember.getLoads().get(j);
                int type = currentLoad.getType();
                if (type == 0) {
                    boolean neg = false;
                    double X = currentLoad.getX();
                    double Y = currentLoad.getY();
                    double Z = currentLoad.getZ();
                    double Mx = currentLoad.getMX();
                    double My = currentLoad.getMY();
                    double Mz = currentLoad.getMZ();
                    double Pos = currentLoad.getPosition();
                    double pointOfApplicationX = (stx + (endx - stx) * Pos);
                    double pointOfApplicationY = (sty + (endy - sty) * Pos);
                    double pointOfApplicationZ = (stz + (endz - stz) * Pos);
//                    System.out.println("lx: " + pointOfApplicationX + " ly: " + pointOfApplicationY + " lz: " + pointOfApplicationZ);
                    if (Math.abs(X) > 0.001) {
                        if (X < 0) {
                            neg = true;
                        } else {
                            neg = false;
                        }
                        Joint3 loadlineStart = new Joint3(pointOfApplicationX, pointOfApplicationY, pointOfApplicationZ);
                        Joint3 loadlineEnd = new Joint3(pointOfApplicationX - 1, pointOfApplicationY, pointOfApplicationZ);
                        g.setColor(xC);
                        drawArrowLine3D(g, loadlineStart, loadlineEnd, neg, 1, (float) X, true);
                    }
                    if (Math.abs(Y) > 0.001) {
                        if (Y < 0) {
                            neg = true;
                        } else {
                            neg = false;
                        }
                        Joint3 loadlineStart = new Joint3(pointOfApplicationX, pointOfApplicationY, pointOfApplicationZ);
                        Joint3 loadlineEnd = new Joint3(pointOfApplicationX, pointOfApplicationY - 1, pointOfApplicationZ);
                        g.setColor(yC);
                        drawArrowLine3D(g, loadlineStart, loadlineEnd, neg, 2, (float) Y, true);

                    }
                    if (Math.abs(Z) > 0.001) {
                        if (Z < 0) {
                            neg = true;
                        } else {
                            neg = false;
                        }
                        Joint3 loadlineStart = new Joint3(pointOfApplicationX, pointOfApplicationY, pointOfApplicationZ);
                        Joint3 loadlineEnd = new Joint3(pointOfApplicationX, pointOfApplicationY, pointOfApplicationZ - 1);
                        g.setColor(zC);
                        drawArrowLine3D(g, loadlineStart, loadlineEnd, neg, 3, (float) Z, true);

                    }

                    if (Math.abs(Mx) > 0.001) {
                        if (Mx < 0) {
                            neg = true;
                        } else {
                            neg = false;
                        }
                        Joint3 loadlineStart = new Joint3(pointOfApplicationX, pointOfApplicationY, pointOfApplicationZ);
                        Joint3 loadlineEnd = new Joint3(pointOfApplicationX - 1, pointOfApplicationY, pointOfApplicationZ);
                        g.setColor(xC);
                        drawArrowLine3D(g, loadlineStart, loadlineEnd, neg, 1, (float) Mx, false);
                    }
                    if (Math.abs(My) > 0.001) {
                        if (My < 0) {
                            neg = true;
                        } else {
                            neg = false;
                        }
                        Joint3 loadlineStart = new Joint3(pointOfApplicationX, pointOfApplicationY, pointOfApplicationZ);
                        Joint3 loadlineEnd = new Joint3(pointOfApplicationX, pointOfApplicationY - 1, pointOfApplicationZ);
                        g.setColor(yC);
                        drawArrowLine3D(g, loadlineStart, loadlineEnd, neg, 2, (float) My, false);

                    }
                    if (Math.abs(Mz) > 0.001) {
                        if (Mz < 0) {
                            neg = true;
                        } else {
                            neg = false;
                        }
                        Joint3 loadlineStart = new Joint3(pointOfApplicationX, pointOfApplicationY, pointOfApplicationZ);
                        Joint3 loadlineEnd = new Joint3(pointOfApplicationX, pointOfApplicationY, pointOfApplicationZ - 1);
                        g.setColor(zC);
                        drawArrowLine3D(g, loadlineStart, loadlineEnd, neg, 3, (float) Mz, false);

                    }

                } else {
                    //draw distributed line loading in parallel with the member
                    double X = currentLoad.getX();
                    double Y = currentLoad.getY();
                    double Z = currentLoad.getZ();
                    double Mx = currentLoad.getMX();
                    double My = currentLoad.getMY();
                    double Mz = currentLoad.getMZ();
                    int neg = 1;
                    if (Math.abs(X) > 0.001) {
                        if (X < 0) {
                            neg = -1;
                        } else {
                            neg = 1;
                        }
                        Joint3 loadlineLB = new Joint3(stx, sty, stz);
                        Joint3 loadlineLT = new Joint3(stx - 1 * neg, sty, stz);
                        Joint3 loadlineRB = new Joint3(endx, endy, endz);
                        Joint3 loadlineRT = new Joint3(endx - 1 * neg, endy, endz);

                        orthoProject(azimuth, elevation, loadlineLB);
                        orthoProject(azimuth, elevation, loadlineLT);
                        orthoProject(azimuth, elevation, loadlineRB);
                        orthoProject(azimuth, elevation, loadlineRT);

                        int[] x0 = {loadlineLB.getPxlx(), loadlineLT.getPxlx(), loadlineRT.getPxlx(), loadlineRB.getPxlx()};
                        int[] y0 = {loadlineLB.getPxly(), loadlineLT.getPxly(), loadlineRT.getPxly(), loadlineRB.getPxly()};
                        g.setColor(new Color(102, 232, 160, 100));
                        g.fillPolygon(x0, y0, 4);
                        g.setColor(xC);
                        g.drawPolygon(x0, y0, 4);
                        int textX = (int) loadlineRT.getPxlx() - 45;
                        int textY = (int) loadlineRT.getPxly() + 20;
                        g.drawString((Math.abs(X) + "KN/m"), textX, textY);
                    }
                    if (Math.abs(Y) > 0.001) {
                        if (Y < 0) {
                            neg = -1;
                        } else {
                            neg = 1;
                        }
                        Joint3 loadlineLB = new Joint3(stx, sty, stz);
                        Joint3 loadlineLT = new Joint3(stx, sty - 1 * neg, stz);
                        Joint3 loadlineRB = new Joint3(endx, endy, endz);
                        Joint3 loadlineRT = new Joint3(endx, endy - 1 * neg, endz);

                        orthoProject(azimuth, elevation, loadlineLB);
                        orthoProject(azimuth, elevation, loadlineLT);
                        orthoProject(azimuth, elevation, loadlineRB);
                        orthoProject(azimuth, elevation, loadlineRT);

                        int[] x0 = {loadlineLB.getPxlx(), loadlineLT.getPxlx(), loadlineRT.getPxlx(), loadlineRB.getPxlx()};
                        int[] y0 = {loadlineLB.getPxly(), loadlineLT.getPxly(), loadlineRT.getPxly(), loadlineRB.getPxly()};
                        g.setColor(new Color(255, 168, 168, 100));
                        g.fillPolygon(x0, y0, 4);
                        g.setColor(yC);
                        g.drawPolygon(x0, y0, 4);
                        int textX = (int) loadlineRT.getPxlx() - 45;
                        int textY = (int) loadlineRT.getPxly() + 20;
                        g.drawString((Math.abs(Y) + "KN/m"), textX, textY);

                    }
                    if (Math.abs(Z) > 0.001) {
                        if (Z < 0) {
                            neg = -1;
                        } else {
                            neg = 1;
                        }
                        Joint3 loadlineLB = new Joint3(stx, sty, stz);
                        Joint3 loadlineLT = new Joint3(stx, sty, stz - 1 * neg);
                        Joint3 loadlineRB = new Joint3(endx, endy, endz);
                        Joint3 loadlineRT = new Joint3(endx, endy, endz - 1 * neg);

                        orthoProject(azimuth, elevation, loadlineLB);
                        orthoProject(azimuth, elevation, loadlineLT);
                        orthoProject(azimuth, elevation, loadlineRB);
                        orthoProject(azimuth, elevation, loadlineRT);

                        int[] x0 = {loadlineLB.getPxlx(), loadlineLT.getPxlx(), loadlineRT.getPxlx(), loadlineRB.getPxlx()};
                        int[] y0 = {loadlineLB.getPxly(), loadlineLT.getPxly(), loadlineRT.getPxly(), loadlineRB.getPxly()};
                        g.setColor(new Color(135, 225, 248, 100));
                        g.fillPolygon(x0, y0, 4);
                        g.setColor(zC);
                        g.drawPolygon(x0, y0, 4);
                        int textX = (int) loadlineRT.getPxlx() - 45;
                        int textY = (int) loadlineRT.getPxly() + 20;
                        g.drawString((Math.abs(Z) + "KN/m"), textX, textY);

                    }
                }
            }
        }
    }

    public void drawArrowLine3D(Graphics g, Joint3 st, Joint3 end, boolean neg, int dir, float magn, boolean ForM) {
        //1 for x,2 for y and 3 for z 
        String units;
        Joint3 stj;
        Joint3 endj;
        Joint3 s1, s2, s3;
        DecimalFormat formatter = new DecimalFormat("0.00");
        int n;
        if (ForM == true) {
            units = "KN";
        } else {
            units = "KN.m";
        }
        if (neg == true) {
            n = 1;
        } else {
            n = -1;
        }

        if (neg) {
            stj = end;
            endj = st;
        } else {
            stj = st;
            endj = end;

        }
        if (dir == 1) {
            s1 = new Joint3(stj.getX() + 0.35 * n, stj.getY(), stj.getZ() - 0.35 * n);
            s2 = new Joint3(stj.getX(), stj.getY(), stj.getZ());
            s3 = new Joint3(stj.getX() + 0.35 * n, stj.getY(), stj.getZ() + 0.35 * n);
        } else if (dir == 2) {
            s1 = new Joint3(stj.getX() - 0.35 * n, stj.getY() + 0.35 * n, stj.getZ());
            s2 = new Joint3(stj.getX(), stj.getY(), stj.getZ());
            s3 = new Joint3(stj.getX() + 0.35 * n, stj.getY() + 0.35 * n, stj.getZ());

        } else {
            s1 = new Joint3(stj.getX(), stj.getY() - 0.35 * n, stj.getZ() + 0.35 * n);
            s2 = new Joint3(stj.getX(), stj.getY(), stj.getZ());
            s3 = new Joint3(stj.getX(), stj.getY() + 0.35 * n, stj.getZ() + 0.35 * n);

        }

        orthoProject(azimuth, elevation, stj);
        orthoProject(azimuth, elevation, endj);
        orthoProject(azimuth, elevation, s1);
        orthoProject(azimuth, elevation, s2);
        orthoProject(azimuth, elevation, s3);

        int[] trixo = {s1.getPxlx(), s2.getPxlx(), s3.getPxlx()};
        int[] triyo = {s1.getPxly(), s2.getPxly(), s3.getPxly()};
        if (ForM == true) {
            g.fillPolygon(trixo, triyo, 3);
        } else {
            g.drawPolygon(trixo, triyo, 3);

        }
        g.drawLine(stj.getPxlx(), stj.getPxly(), endj.getPxlx(), endj.getPxly());
        int textX = (int) end.getPxlx() + 10;
        int textY = (int) end.getPxly() + 10;
        String magnStr = formatter.format(Math.abs(magn));
        g.drawString((magnStr + units), textX, textY);

    }

    public void drawReactions(Graphics g, Structure3 s) {
        Font loadsF = new Font("raleway", Font.BOLD, 14);
        g.setFont(loadsF);

        for (int i = 0; i < s.jointVector.length; i++) {
            Joint3 j = s.jointVector[i];
            Joint3 lEndrx, lEndry, lEndrz, lEndmx, lEndmy, lEndmz;
            Joint3 lStartrx, lStartry, lStartrz, lStartmx, lStartmy, lStartmz;
            double pointOfApplicationX = j.getX();
            double pointOfApplicationY = j.getY();
            double pointOfApplicationZ = j.getZ();
            boolean neg;
            double rcx = j.getRcx();
            double rcy = j.getRcy();
            double rcz = j.getRcz();
            double rcmx = j.getRcmx();
            double rcmy = j.getRcmy();
            double rcmz = j.getRcmz();

            lStartrx = new Joint3(pointOfApplicationX, pointOfApplicationY, pointOfApplicationZ);
            lStartry = new Joint3(pointOfApplicationX, pointOfApplicationY, pointOfApplicationZ);
            lStartrz = new Joint3(pointOfApplicationX, pointOfApplicationY, pointOfApplicationZ);
            lStartmx = new Joint3(pointOfApplicationX - 2, pointOfApplicationY, pointOfApplicationZ);
            lStartmy = new Joint3(pointOfApplicationX, pointOfApplicationY - 2, pointOfApplicationZ);
            lStartmz = new Joint3(pointOfApplicationX, pointOfApplicationY, pointOfApplicationZ - 2);

            lEndrx = new Joint3(pointOfApplicationX - 1.5, pointOfApplicationY, pointOfApplicationZ);
            lEndry = new Joint3(pointOfApplicationX, pointOfApplicationY - 1.5, pointOfApplicationZ);
            lEndrz = new Joint3(pointOfApplicationX, pointOfApplicationY, pointOfApplicationZ - 1.5);
            lEndmx = new Joint3(pointOfApplicationX - 3.5, pointOfApplicationY, pointOfApplicationZ);
            lEndmy = new Joint3(pointOfApplicationX, pointOfApplicationY - 3.5, pointOfApplicationZ);
            lEndmz = new Joint3(pointOfApplicationX, pointOfApplicationY, pointOfApplicationZ - 3.5);

            if ((Math.abs(rcx) > 0.001)) {
                neg = rcx > 0;
                orthoProject(azimuth, elevation, lStartrx);
                orthoProject(azimuth, elevation, lEndrx);
                g.setColor(xC);
                drawArrowLine3D(g, lStartrx, lEndrx, neg, 1, (float) rcx, true);
            }
            if ((Math.abs(rcy) > 0.001)) {
                neg = rcy < 0;
                orthoProject(azimuth, elevation, lStartry);
                orthoProject(azimuth, elevation, lEndry);
                g.setColor(yC);
                drawArrowLine3D(g, lStartry, lEndry, neg, 2, (float) rcy, true);

            }
            if ((Math.abs(rcz) > 0.001)) {
                neg = rcz > 0;
                orthoProject(azimuth, elevation, lStartrz);
                orthoProject(azimuth, elevation, lEndrz);
                g.setColor(zC);
                drawArrowLine3D(g, lStartrz, lEndrz, neg, 3, (float) rcz, true);

            }
            if ((Math.abs(rcmx) > 0.001)) {
                neg = rcmx > 0;
                g.setColor(xC);
                orthoProject(azimuth, elevation, lStartmx);
                orthoProject(azimuth, elevation, lEndmx);
                drawArrowLine3D(g, lStartmx, lEndmx, neg, 1, (float) rcmx, false);

            }
            if ((Math.abs(rcmy) > 0.001)) {
                neg = rcmy > 0;
                g.setColor(yC);
                orthoProject(azimuth, elevation, lStartmy);
                orthoProject(azimuth, elevation, lEndmy);
                drawArrowLine3D(g, lStartmy, lEndmy, neg, 2, (float) rcmy, false);

            }
            if ((Math.abs(rcmz) > 0.001)) {
                neg = rcmz > 0;
                g.setColor(zC);
                orthoProject(azimuth, elevation, lStartmz);
                orthoProject(azimuth, elevation, lEndmz);
                drawArrowLine3D(g, lStartmz, lEndmz, neg, 3, (float) rcmz, false);

            }
        }
    }

    public Joint3 SnapToGrid(int mouseX, int mouseY) {
        //int radius = 15;
        Joint3 closest = null;
        double[] distanceArray = new double[GridV.size()];
        for (int i = 0; i < GridV.size(); i++) {
            Joint3 GridPoint = GridV.get(i);
            int jx = (int) GridPoint.getPxlx();
            int jy = (int) GridPoint.getPxly();
            distanceArray[i] = Math.sqrt((mouseX - jx) * (mouseX - jx) + (mouseY - jy) * (mouseY - jy));
        }
        int max = 0;
        for (int i = 0; i < distanceArray.length - 1; i++) {
            if (distanceArray[max] > distanceArray[i + 1]) {
                max = i + 1;
            }
        }
        closest = GridV.get(max);
        System.out.println("grid size: " + GridV.size() + "closest: " + max);
        return closest;
    }

    public boolean closeToJoint(double x, double y, LinkedList<Joint3> joint, double radius) {
        boolean jointProx = false;
        for (int i = 0; i < joint.size(); i++) {
            int jx = (int) joint.get(i).getPxlx();
            int jy = (int) joint.get(i).getPxly();
            if (Math.sqrt((x - jx) * (x - jx) + (y - jy) * (y - jy)) - radius < 0) {
                jointProx = true;
            }
        }
        return jointProx;
    }

    public boolean closeToMember(int x, int y, double radius) {
        boolean memberProx = false;
        for (int i = 0; i < MemberV.size(); i++) {
            double dist = VectorUtil3D.shortestDistancePointToLine3D(x, y, MemberV.get(i));
            if (dist < radius) {
                memberProx = true;
            }

        }
        //         System.out.println(memberProx);
        return memberProx;
    }

    public void addUniqueJoint(Joint3 j) {
        /**
         * check if the joint coordinates already exist[MEANING THERE ALREADY IS
         * A JOINT at that location]: if they do, do nothing. otherwise add this
         * joint to the joint vector*
         */
        boolean jointAlreadyExists = false;
        if (!jointVNE) {
            JointV.add(j);
            jointVNE = true;
        } else {
            int len = JointV.size();
            for (int i = 0; i < len; i++) {
                Joint3 existentJoint = JointV.get(i);
                if ((Math.abs(existentJoint.getX() - j.getX()) < 0.01) && (Math.abs(existentJoint.getY() - j.getY()) < 0.01) && (Math.abs(existentJoint.getZ() - j.getZ()) < 0.01)) {
                    jointAlreadyExists = true;
                }
            }
            if (!jointAlreadyExists) {
                JointV.add(j);
            }
        }
    }

    public Joint3 returnUniqueJoint(Joint3 j) {
        /**
         * check if the joint coordinates already exist[MEANING THERE ALREADY IS
         * A JOINT at that location]: if they do, do nothing. otherwise add this
         * joint to the joint vector*
         */
        Joint3 ej = null;
        boolean jointAlreadyExists = false;
        if (!jointVNE) {
            ej = j;
            JointV.add(j);
            jointVNE = true;
        } else {
            int len = JointV.size();
            for (int i = 0; i < len; i++) {
                Joint3 existentJoint = JointV.get(i);
                if ((Math.abs(existentJoint.getX() - j.getX()) < 0.01) && (Math.abs(existentJoint.getY() - j.getY()) < 0.01) && (Math.abs(existentJoint.getZ() - j.getZ()) < 0.01)) {
                    jointAlreadyExists = true;
                    ej = JointV.get(i);
                }
            }
            if (!jointAlreadyExists) {
                JointV.add(j);
                ej = j;
            }
        }
        return ej;
    }

    public void addUniqueMember(int ID, Joint3 startJ, Joint3 endJ) {
        boolean jointAlreadyExists = false;
        Member3 m = new Member3(ID, startJ, endJ);
 //       System.out.println(m.getMemberID() + "              " + m.getStart().getJointID() + "     " + m.getEnd().getJointID() + "     " + m.getStart().getX() + "     " + m.getStart().getY() + "      " + m.getEnd().getX() + "     " + m.getEnd().getY() + "      " + "     " + m.getCx() + "     " + m.getCy());

        if (!memberVNE) {
            MemberV.add(m);
            AddtoBeamorColumnV(m);
            memberVNE = true;
        } else {
            int len = MemberV.size();
            for (int i = 0; i < len; i++) {
                Joint3 existentStartJoint = MemberV.get(i).getStart();
                Joint3 existentEndJoint = MemberV.get(i).getEnd();
                if (((Math.abs(existentStartJoint.getX() - startJ.getX()) < 0.01) && (Math.abs(existentStartJoint.getY() - startJ.getY()) < 0.01) && (Math.abs(existentStartJoint.getZ() - startJ.getZ()) < 0.01))
                        && ((Math.abs(existentEndJoint.getX() - startJ.getX()) < 0.01) && (Math.abs(existentEndJoint.getY() - startJ.getY()) < 0.01) && (Math.abs(existentEndJoint.getZ() - startJ.getZ()) < 0.01))
                        || (((Math.abs(existentStartJoint.getX() - endJ.getX()) < 0.01) && (Math.abs(existentStartJoint.getY() - endJ.getY()) < 0.01) && (Math.abs(existentStartJoint.getZ() - endJ.getZ()) < 0.01))
                        && ((Math.abs(existentEndJoint.getX() - endJ.getX()) < 0.01) && (Math.abs(existentEndJoint.getY() - endJ.getY()) < 0.01) && (Math.abs(existentEndJoint.getZ() - endJ.getZ()) < 0.01)))) {
                    jointAlreadyExists = true;
                }
            }
            if (!jointAlreadyExists) {
 //               System.out.println("new member!");
                MemberV.add(m);
                AddtoBeamorColumnV(m);

            }
        }
    }

    private boolean jointAlreadySelected(Joint3 j, LinkedList<Joint3> selectedJointV) {
        boolean selected = false;
        for (int i = 0; i < selectedJointV.size(); i++) {
            int x = (int) selectedJointV.get(i).getPxlx();
            int y = (int) selectedJointV.get(i).getPxly();
            if (x == (int) j.getPxlx() && y == (int) j.getPxly()) {
                selected = true;
            }
        }
        return selected;
    }

    private boolean memberAlreadySelected(Member3 m, LinkedList<Member3> selectedMemberV) {
        boolean selected = false;
        for (int i = 0; i < selectedMemberV.size(); i++) {
            if (m == selectedMemberV.get(i)) {
                selected = true;
            }
        }
        return selected;
    }

    public Member3 returnClosestMember(int x, int y, double radius) {
        Member3 memberProx = null;
        for (int i = 0; i < MemberV.size(); i++) {
            Joint3 strt = MemberV.get(i).getStart();
            Joint3 end = MemberV.get(i).getEnd();
            double dist = shortestDistancePointToLine3D(x, y, MemberV.get(i));
            if (dist < radius) {
                memberProx = MemberV.get(i);
            }
        }
        return memberProx;
    }

    public void selectAllJointsInRegion(Graphics g) {
        int xs = start.getPxlx();
        int ys = start.getPxly();
        int xe = prev.getPxlx();
        int ye = prev.getPxly();

        int len = JointV.size();
        for (int i = 0; i < len; i++) {
            Joint3 j = JointV.get(i);
            if (j.getPxlx() > xs && j.getPxlx() < xe && j.getPxly() < ye && j.getPxly() > ys) {
                selectedJointV.add(j);
                selectedJNE = true;
            }
        }
        repaint();
    }

    public void selectAllMembersCrossed(Graphics g) {
        int xs = start.getPxlx();
        int ys = start.getPxly();
        int xe = prev.getPxlx();
        int ye = prev.getPxly();

        int len = MemberV.size();
        for (int i = 0; i < len; i++) {
            Member3 m = MemberV.get(i);
            if (intersect(m.getStart(), m.getEnd(), start, prev)) {
                selectedMemberV.add(m);
                selectedMNE = true;
            }
        }
        repaint();
    }

    public static int orientation(Joint3 p, Joint3 q, Joint3 r) {
        double val = (q.getPxly() - p.getPxly()) * (r.getPxlx() - q.getPxlx())
                - (q.getPxlx() - p.getPxlx()) * (r.getPxly() - q.getPxly());
        if (val == 0.0) {
            return 0; // colinear
        }
        return (val > 0) ? 1 : 2; // clock or counterclock wise
    }

    public static boolean intersect(Joint3 p1, Joint3 q1, Joint3 p2, Joint3 q2) {

        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);
        if (o1 != o2 && o3 != o4) {
            return true;
        }
        return false;
    }

    public void AddtoBeamorColumnV(Member3 m) {
        int typ = m.getType();
        if (typ == 0) {
            beamV.add(m);
        } else {
            columnV.add(m);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(BACKGROUNDCOLOR);
        g2.fillRect(0, 0, width, height);
        Grid(g2, nX, nY, nZ, gX, gY, gZ);
        Loads(g2, MemberV);

        if (jointVNE) {
            drawJointID(g2, JointV);
        }
        if (memberVNE) {
            drawFrameVector(g2, MemberV);
        }
        if (selectedJNE) {
            drawSelectedJoints(g, selectedJointV);
        }
        if (selectedMNE) {
            drawSelectedMembers(g, selectedMemberV);
        }

        if (constrainedJNE) {
            g.setPaintMode();
            g.setColor(SnapC);
            drawRestraints(g, JointV);
        }
        if (!(Str == null)) {
            drawReactions(g, Str);
        }
        g2.setStroke(new BasicStroke(2));

//        DrawPortalFrame(g2, 5, 5, 5, 9, 9, 9);
//        Point[] points;
//        points = new Point[JointV.size()];
//        int j;
//        for (j = 0; j < JointV.size(); ++j) {
//            // compute an orthographic projection
//            orthoProject(azimuth, elevation, JointV.get(j));
//            // the 0.5 is to round off when converting to int
////            points[j] = coor;
//            g.setColor(Color.blue);
//            String coordinateInfo = j + "(" + JointV.get(j).getX() + "," + JointV.get(j).getY() + "," + JointV.get(j).getZ()+ ")";
//            g.drawString(coordinateInfo, JointV.get(j).getPxlx(), JointV.get(j).getPxly());
//        }
        // draw the wireframe
        drawAxes(g2);

        g2.setColor(new Color(213, 0, 0));
        g2.setStroke(new BasicStroke(2));
//        for (j = 0; j < memberV.length; ++j) {
//            g2.drawLine(
//                    JointV[ memberV[j].a].pxlx, JointV[ memberV[j].a].pxly,
//                    JointV[ memberV[j].b].pxlx, JointV[ memberV[j].b].pxly
        //           );
        //       }

    }

    public static void AddMenu() {

        MenuBar = new MenuBar();

        Assign = new Menu("Structure");
        Analyze = new Menu("Analysis");
        DesignM = new Menu("Design");
        JointConstraints = new MenuItem("Restraints");
        MemberLoads = new MenuItem("Member Loads");
        MemberProperties = new MenuItem("Material Properties");

        MemberSection = new MenuItem("Member Section");
        MemberInfo = new MenuItem("Member Info.");
        Analysis = new MenuItem("ANALYZE");
        StiffnessMatrix = new MenuItem("Stiffness Matrix");
        Displacements = new MenuItem("Displacement");
        MemberForces = new MenuItem("Member Forces");
        Reactions = new MenuItem("Reactions");
        TxtOutput = new MenuItem("Summary");
        Design = new MenuItem("Design");
        Optimize = new MenuItem("Optimize");

        Assign.add(JointConstraints);
        Assign.add(MemberProperties);
        Assign.add(MemberLoads);
        Assign.add(MemberSection);

        Analyze.add(Analysis);
        Analyze.add(MemberInfo);
        Analyze.add(StiffnessMatrix);
        Analyze.add(Displacements);
        Analyze.add(MemberForces);
        Analyze.add(Reactions);
        Analyze.add(TxtOutput);

        DesignM.add(Design);
        DesignM.add(Optimize);

        MenuBar.add(Assign);
        MenuBar.add(Analyze);
        MenuBar.add(DesignM);
    }

    public void RestrainSelectedJoints(boolean dx, boolean dy, boolean dz, boolean rx, boolean ry, boolean rz) {
        constrainedJNE = true;
        for (int i = 0; i < selectedJointV.size(); i++) {
            Joint3 j = selectedJointV.get(i);
            j.setRrx(dx);
            j.setRry(dy);
            j.setRrz(dz);
            j.setRrmx(rx);
            j.setRrmy(ry);
            j.setRrmz(rz);
        }
//        for (int i = 0; i < JointV.size(); i++) {
        //         System.out.println("mx: " + JointV.get(i).isRrx() + "my: " + jointV.get(i).isRry() + "rz: " + jointV.get(i).isRrmz());
//        }
        selectedJointV = new LinkedList<>();
        repaint();
    }
    
    
   


    public void AssignMaterials(double E, double G, double Fcu, double Fyk) {
        for (int i = 0; i < MemberV.size(); i++) {
            Member3 curr = MemberV.get(i);
            curr.setE(E);
            curr.setG(G);
            curr.setFcu(Fcu);
            curr.setFyk(Fyk);
        }
    }

    public static void AssignSectiontoAll(double b, double h, double As1, double As2) {
        for (int i = 0; i < MemberV.size(); i++) {
            Member3 m = MemberV.get(i);
            m.setB(b);
            m.setH(h);
            m.setAs1(As1);
            m.setAs2(As2);
        }
    }

    public void LoadSelectedMembers(double fx, double px, double fy, double py, double fz, double pz, double mx, double pmx, double my, double pmy, double mz, double pmz, double Ux, double Uy, double Uz) {

        for (int i = 0; i < selectedMemberV.size(); i++) {
            Member3 curr = selectedMemberV.get(i);
            if (!(Math.abs(fx) < 0.001)) {
                curr.addLoads(new Load3(0, px, fx, 0, 0, 0, 0, 0));
            }
            if (!(Math.abs(fy) < 0.001)) {
                curr.addLoads(new Load3(0, py, 0, fy, 0, 0, 0, 0));
            }
            if (!(Math.abs(fz) < 0.001)) {
                curr.addLoads(new Load3(0, pz, 0, 0, fz, 0, 0, 0));
            }
            if (!(Math.abs(mx) < 0.001)) {
                curr.addLoads(new Load3(0, pmx, 0, 0, 0, mx, 0, 0));
            }
            if (!(Math.abs(my) < 0.001)) {
                curr.addLoads(new Load3(0, pmy, 0, 0, 0, 0, my, 0));
            }
            if (!(Math.abs(mz) < 0.001)) {
                curr.addLoads(new Load3(0, pmz, 0, 0, 0, 0, 0, mz));
            }
            if (!(Math.abs(Ux) < 0.001)) {
                curr.addLoads(new Load3(1, 0, Ux, 0, 0., 0, 0, 0));
            }
            if (!(Math.abs(Uy) < 0.001)) {
                curr.addLoads(new Load3(1, 0, 0, Uy, 0., 0, 0, 0));
            }
            if (!(Math.abs(Uz) < 0.001)) {
                curr.addLoads(new Load3(1, 0, 0, 0, Uz, 0, 0, 0));
            }

        }
        selectedMemberV = new LinkedList<>();
        repaint();
//        for (int i = 0; i < MemberV.size(); i++) {
//            LinkedList<Load3> l = MemberV.get(i).getLoads();
//            for (int j = 0; j < l.size(); j++) {
//                Load3 r = l.get(j);
//                System.out.println(r.getType() + "    " + r.getX() + "    " + r.getY() + "     " + r.getM() + "    " + r.getPosition());
//            }
//        }
    }

    public static void setGridSizeandExtent(double gSizeX, int gNoX, double gSizeY, int gNoY, double gSizeZ, int gNoZ) {
        gX = gSizeX;
        gY = gSizeY;
        gZ = gSizeZ;
        nX = gNoX;
        nY = gNoY;
        nZ = gNoZ;
    }

 

    public void addToBeamandColumnV() {
        for (int i = 0; i < MemberV.size(); i++) {
            Member3 mem = MemberV.get(i);
            if (mem.Type == 0) {
                beamV.add(mem);
            } else if (mem.Type == 1) {
                columnV.add(mem);
            }
        }
    }

 
  
}
