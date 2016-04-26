//(c) ALEX ELLISON 2014
package tsp;
/*
 graphical representation of a metric graph and it's various TSP tours
 */

import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.ArrayList;
import java.awt.image.*;
import java.awt.*;

public class PANE extends JPanel {

    Graph G;
    boolean drawMSTApprox = true;
    boolean drawVertices = true;

    public PANE(Graph G) {
        this.G = G;
    }

    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaintMode();
        g2d.setStroke(new BasicStroke(4f));
        // screen dimensions useful for making image more dynamic
        double w = getWidth();
        double h = getHeight();
        g2d.clearRect(0, 0, (int) w, (int) h);
        g2d.setPaint(Color.BLUE);

        int n = G.M.length;
        Order exact = null;
        if (G.dynamicOrder != null) {
            exact = G.dynamicOrder;
        }
        if (G.BBOrder != null) {
            exact = G.BBOrder;
        }
        if (exact != null) {
            int[] arr = exact.indices;
            for (int i = 0; i < arr.length; i++) {
                int a = arr[i];
                int b = arr[(i + 1) % arr.length];
                int x1 = (int) (w * G.XY[a][0]);
                int y1 = (int) (h * G.XY[a][1]);
                int x2 = (int) (w * G.XY[b][0]);
                int y2 = (int) (h * G.XY[b][1]);
                g2d.drawLine(x1, y1, x2, y2);
            }
        }

        g2d.setStroke(new BasicStroke(2f));

        g2d.setPaint(Color.GREEN);
        if (G.MSTorder != null && false && this.drawMSTApprox) {
            int[] arr = G.MSTorder.indices;
            for (int i = 0; i < arr.length; i++) {
                int a = arr[i];
                int b = arr[(i + 1) % arr.length];
                int x1 = (int) (w * G.XY[a][0]);
                int y1 = (int) (h * G.XY[a][1]);
                int x2 = (int) (w * G.XY[b][0]);
                int y2 = (int) (h * G.XY[b][1]);
                g2d.drawLine(x1, y1, x2, y2);
            }
        }
        //draw points
        if (drawVertices) {
            for (int i = 0; i < n; i++) {
                int x = (int) (w * G.XY[i][0]);
                int y = (int) (h * G.XY[i][1]);
                int dx = -5;
                int dy = -5;
                g2d.setPaint(Color.black);
                g2d.fillRect(x + dx, y + dy, 15, 10);
                g2d.setPaint(Color.white);
                g2d.drawString("" + i, x + 1 + dy, y + dy + 9);
            }
        }
        /*g2d.setPaint(new Color(1f, 1f, 1f, 0.85f));
        g2d.fillRect(0, 0, 225, 44);
        g2d.setPaint(Color.BLACK);
        g2d.setStroke(new BasicStroke(1f));
        
        g2d.drawRect(0, 0, 225, 44);
        g2d.drawString("Press space to run new computation", 10, 10);
        g2d.drawString("MST approx weight (green): " + (float) G.MSTorder.weight, 10, 25);
        if (exact != null) {
            g2d.drawString("Exact tour weight (blue): " + (float) exact.weight, 10, 40);
        }*/
    }

}
