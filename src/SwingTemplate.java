import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// Swing Program Template
//@SuppressWarnings("serial")
public class SwingTemplate extends JFrame {
    // Name-constants to define the various dimensions
    //public static final int WINDOW_WIDTH = 300;
    //public static final int WINDOW_HEIGHT = 150;
    // ......

    // private variables of UI components
    // ......

    /** Constructor to setup the UI components */
    public SwingTemplate() {

        initUI();

    }

    private void initUI(){
        createMenuBar();

        setDefaultCloseOperation(EXIT_ON_CLOSE);  // Exit when close button clicked
        setTitle("Pilot Test"); // "this" JFrame sets title
        setExtendedState(MAXIMIZED_BOTH);
        setVisible(true);   // show it
    }

    private void createMenuBar() {

        JMenuBar menubar = new JMenuBar();
        ImageIcon icon = new ImageIcon("exit.png"); //TODO No image atm

        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);

        JMenuItem sMenuItem = new JMenuItem("Start Test");
        sMenuItem.setMnemonic(KeyEvent.VK_S);
        sMenuItem.setToolTipText("Start the experiment");
        sMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                startTest();
            }
        });

        JMenuItem eMenuItem = new JMenuItem("Exit", icon);
        eMenuItem.setMnemonic(KeyEvent.VK_E);
        eMenuItem.setToolTipText("Exit application");
        eMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });

        file.add(eMenuItem);
        file.add(sMenuItem);
        menubar.add(file);

        setJMenuBar(menubar);

    }

    private void startTest() {

    }

    /** The entry main() method */
    public static void main(String[] args) {
        // Run GUI codes in the Event-Dispatching thread for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SwingTemplate();  // Let the constructor do the job
            }
        });
    }
}