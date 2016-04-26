//(c) Alex Ellison
package tsp;

/**
 *window class for viewing TSP solutions
 */
import javax.swing.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class Window extends JFrame implements KeyListener {
    PANE p;
    public Window(PANE p) {
        super("TSP - Alex Ellison");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        this.addKeyListener(this);
        this.setAlwaysOnTop(true);
        this.add(p);
        this.p=p;
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == ' ') {
            Popup p = new Popup(this);
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }
}
