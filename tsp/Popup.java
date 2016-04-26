//(c) Alex Ellison 2014
package tsp;

/**
 * popup utility to reset the graph and determine its computation and drawing
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Popup extends JFrame implements ActionListener {

    Window W;
    JComboBox size;
    JComboBox method;
    JCheckBox mst;
    JCheckBox vertices;

    public Popup(Window W) {
        super("Select Computation Parameters");
        setSize(450, 150);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        this.setAlwaysOnTop(true);
        JLabel label1 = new JLabel("size: ");
        size = new JComboBox();
        for (int i = 3; i < 70; i++) {
            size.addItem("" + i);
        }
        JLabel label2 = new JLabel("method: ");
        method = new JComboBox();
        method.addItem("Dynamic Programming");
        method.addItem("Branch and Bound");
        method.addItem("Parallel Branch and Bound");
        method.addItem("Just MST");
        mst = new JCheckBox("Show MST Approx.", true);
        vertices = new JCheckBox("Draw Vertices", true);
        JButton go = new JButton("Go");
        FlowLayout f = new FlowLayout();
        add(label1);
        add(size);
        add(label2);
        add(method);
        add(mst);
        add(vertices);
        add(go);
        this.setLayout(f);
        //pack();
        this.W = W;
        go.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        //only one button, no need to find out which it is
        PANE p = W.p;
        int n = size.getSelectedIndex();
        //index zero maps to size 3
        if (n == -1) {
            System.out.println("error - using default size 10");
            n = 10;
        }
        n += 3;
        p.G = TSP.random(n);
        int technique = method.getSelectedIndex();
        switch (technique) {
            case 0:
                p.G.computeDynamic();
                break;
            case 1:
                p.G.computeBB();
                break;
            case 2:
                p.G.computeBBParallel();
                break;
            case 3:
                p.G.BBOrder = null;
                p.G.dynamicOrder = null;
                break;
        }
        p.drawMSTApprox = mst.isSelected();
        p.drawVertices = vertices.isSelected();
        p.repaint();
        this.dispose();
    
    }
}
