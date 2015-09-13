import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

// Swing Program Template
//@SuppressWarnings("serial")
public class SwingTemplate {
    // Name-constants to define the various dimensions
    public File[] list;
    public JFrame frame;
    public JPanel panel;
    public JLabel imageLabel;
    public Timer waitTimer;
    public Timer flashTimer;
    // ......

    // private variables of UI components
    // ......

    /** Constructor to setup the UI components */
    public SwingTemplate() {

        frame = new JFrame();
        panel = new JPanel();

        waitTimer = new Timer(5000, new WaitForImage());

        flashTimer = new Timer(2000, new FlashImage());

        initUI();

    }

    /**
     * Step before shows the text "Next image in 5 seconds"
     * This waits 5 seconds
     * Then shows the image and starts next timer
     */
    class WaitForImage implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            System.out.println("asdf");

        }
    }

    /**
     * Step before shows the image
     * This waits short time
     * Then hides the image and pulls up input box
     */
    class FlashImage implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            System.out.println("asdf");

        }
    }

    private void initUI(){
        createMenuBar();

        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);  // Exit when close button clicked
        frame.setTitle("Pilot Test"); // "this" JFrame sets title
        frame.setExtendedState(frame.MAXIMIZED_BOTH);
        frame.setVisible(true);   // show it
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

        frame.setJMenuBar(menubar);

    }

    private void startTest() {
        
        initTest();
        try {
            testingLoop();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void testingLoop() throws IOException {



        for(File file : list) {
            System.out.println("test");
            ImageIcon image = new ImageIcon(file.getAbsolutePath());
            imageLabel = new JLabel(image);
            panel.add(imageLabel);
            frame.add(panel);
            frame.revalidate();

            //timer.setRepeats(false);
            //timer.start();
            //
            System.out.println("after");


        }

    }

    private void initTest() {

        File f = new File("D:\\Library\\Documents\\GitHub\\HPITestingPlatform\\images");
        list = f.listFiles();
        //TODO shuffle array
        /*
        for(File file : list) {
            System.out.println(file.getName());
        }
        */

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