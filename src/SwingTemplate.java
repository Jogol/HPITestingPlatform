import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.lwjgl.LWJGLException;

// Swing Program Template
//@SuppressWarnings("serial")
public class SwingTemplate {
	// Name-constants to define the various dimensions

	/**
	 * Om vi vill ha en counter för antalet slutförda objekt. lyckas inte flytta
	 * runt i GridBagConstraints layout :/ så den ligger helt fel.
	 */
	// public int completedImages = 0;
	// public int totalImages = 0;

	public Renderer renderer;
	public File[] list;
	public ArrayList<File> aList;
	public JFrame frame;
	public JPanel panel;
	public JLabel imageLabel;
	public ImageIcon image;
	public JLabel waitLabel;
	public JLabel counter;
	public JTextField inputBox;
	public final int waitTime = 1000;
	
	public String ID;

	public final int frames = 1; //st�ller in antal frames som bilden visas!
	public final int flashTime = 4;

	public Timer waitTimer;
	public Timer flashTimer;
	public File currentFile;
	public PrintWriter writer;
	public BufferedWriter bw;
	public FileWriter fw;
	public String mainPath = "C:\\Users\\Emilio\\IdeaProjects\\HPITestingPlatform"; //d�r projektet ligger
	Color background;
	
	// ......

	// private variables of UI components
	// ......

	/**
	 * Constructor to setup the UI components
	 * 
	 * @throws LWJGLException
	 * 
	 */
	public SwingTemplate() throws LWJGLException {
		while (ID == null || ID.equals("")){
			ID = JOptionPane.showInputDialog
					(null,"<html>Enter your personal ID","");
		}
		
		renderer = new Renderer();
		background = new Color(255, 255, 255);
		frame = new JFrame();
		frame.getContentPane().setLayout(new BorderLayout());
		// panel = new JPanel(new GridBagLayout());
		// panel.setBackground(background);
		final Canvas canvas = new Canvas();
		//canvas.setSize(new Dimension(800,600));
		System.out.println(canvas.getSize());
		frame.getContentPane().add(canvas,BorderLayout.CENTER);
		
		// frame.add(panel);
		frame.setVisible(true);
		waitLabel = new JLabel("An image will appear shortly");
		frame.getContentPane().add(waitLabel, BorderLayout.SOUTH);
		waitLabel.setFont(new Font("Arial", Font.PLAIN, 20));
		waitLabel.setVisible(false);
		

		inputBox = new JTextField(20);
		inputBox.setFont(new Font("Arial", Font.PLAIN, 20));
		inputBox.setVisible(false);
		frame.getContentPane().add(inputBox, BorderLayout.SOUTH);
		inputBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				handleInput(inputBox.getText());
				inputBox.setVisible(false);
				inputBox.setText("");
				frame.revalidate();
				try {
					testWarning();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		/**
		 * Om vi vill ha en counter för antalet slutförda objekt. lyckas inte
		 * flytta runt i GridBagConstraints layout :/ så den ligger helt fel.
		 */
		// counter = new JLabel("");
		// panel.add(counter);
		// counter.setVisible(false);

		try {
			fw = new FileWriter(mainPath + "\\results\\resultFile" + ID + ".txt");
			bw = new BufferedWriter(fw);

			// writer = new
			// PrintWriter("/afs/nada.kth.se/home/7/u1k944b7/Desktop/results/resultFile.txt",
			// "UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		waitTimer = new Timer(waitTime, new WaitForImage());

		waitTimer.setRepeats(false);

		flashTimer = new Timer(flashTime, new FlashImage());

		flashTimer.setRepeats(false);

		initUI();
		new Thread(new Runnable() {
			public void run() {
				try {
					renderer.createContext(canvas);
					renderer.start();
				} catch (LWJGLException e) {
					System.exit(1);
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

	}

	
	public void handleInput(String text) {
		// TODO handle text
		String newText = text.replaceAll(" ", "");
		Boolean correct = false;

		String fullName = currentFile.getName();
		String[] parts = fullName.split("_|\\.");

		if (newText.toLowerCase().equals(parts[0])) {
			correct = true;
		}

		try {
			bw.write(parts[0] + " " + parts[1] + " " + parts[2] + " " + correct.toString() + " "
					+ newText.toLowerCase() + "\n");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// writer.println(parts[0] + " " + parts[1] + " " + correct.toString());
		// writer.println("test");
		System.out.println("handler");

	}

	/**
	 * Step before shows the text "Next image in 5 seconds" This waits 5 seconds
	 * Then shows the image and starts next timer
	 */
	class WaitForImage implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			System.out.println("waittimer triggered");
			// image = new ImageIcon(currentFile.getAbsolutePath());
			waitLabel.setVisible(false);
			try {
				renderer.showImage(currentFile, frames);
			} catch (IllegalArgumentException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			inputBox.setVisible(true);
			inputBox.requestFocus();
			frame.revalidate();

			// flashTimer.restart();

			// Image preImage = toolkit.getImage(currentFile.getAbsolutePath());
			// //Image scaledImage =
			// preImage.getScaledInstance(panel.getWidth(),panel.getHeight(),Image.SCALE_SMOOTH);
			// Image scaledImage =
			// preImage.getScaledInstance(800,400,Image.SCALE_SMOOTH);
			// image = new ImageIcon(scaledImage);
			// imageLabel = new JLabel(image);
			// //panel.remove(waitLabel);
			// waitLabel.setVisible(false);
			// panel.add(imageLabel, new GridBagConstraints());
			// frame.revalidate();
			// start = System.nanoTime();
			// flashTimer.restart();

		}
	}

	/**
	 * Step before shows the image This waits short time Then hides the image
	 * and pulls up input box
	 */
	class FlashImage implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			imageLabel.setIcon(null);
			frame.revalidate();
			// imageLabel.setVisible(false);

			inputBox.setVisible(true);
			inputBox.requestFocus();
			frame.revalidate();

		}
	}

	@SuppressWarnings("static-access")
	private void initUI() {
		createMenuBar();

		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE); // Exit when close
																// button
																// clicked
		frame.setTitle("Pilot Test"); // "this" JFrame sets title
		frame.setExtendedState(frame.MAXIMIZED_BOTH);
		frame.setVisible(true); // show it
	}

	private void createMenuBar() {

		JMenuBar menubar = new JMenuBar();
		ImageIcon icon = new ImageIcon("exit.png"); // TODO No image atm

		JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);

		JMenuItem sMenu = new JMenuItem("Start Test");
		sMenu.setMnemonic(KeyEvent.VK_S);
		sMenu.setToolTipText("Start the experiment");
		sMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.out.println("Started");
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
		menubar.add(file);
		menubar.add(sMenu);

		frame.setJMenuBar(menubar);

	}

	private void startTest() {
		
		// kanske fastnar här? start har en oändlig loop.

		initTest();
		try {
			testWarning();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void testWarning() throws IOException { // TODO loop?

		if (aList.size() > 0) {
			currentFile = aList.get(0); // TODO kolla om det finns bilder kvar
			aList.remove(0);

			/**
			 * Om vi vill ha en counter för antalet slutförda objekt. lyckas
			 * inte flytta runt i GridBagConstraints layout :/ så den ligger
			 * helt fel.
			 */
			// counter.setText(completedImages++ + "/" + totalImages);

			System.out.println("test");
			waitLabel.setVisible(true);
			frame.revalidate();
			waitTimer.restart();

			System.out.println("after");
		} else {
			testEnd();
		}

	}

	public void testEnd() {
		System.out.println("test end");
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// writer.close();
	}

	private void initTest() {

		File f = new File(mainPath + "\\images");
		list = f.listFiles();

		/**
		 * Om vi vill ha en counter för antalet slutförda objekt. lyckas inte
		 * flytta runt i GridBagConstraints layout :/ så den ligger helt fel.
		 */
		// counter.setVisible(true);
		// totalImages = list.length;

		aList = new ArrayList<File>();
		// TODO shuffle array

		for (File file : list) {
			aList.add(file);
		}

		Collections.shuffle(aList);

	}

	/** The entry main() method */
	public static void main(String[] args) {
		// Run GUI codes in the Event-Dispatching thread for thread safety
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new SwingTemplate();

				} catch (LWJGLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // Let the constructor do the job
			}
		});

	}
}