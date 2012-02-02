/*
Adam Gressen
gressen.a@husky.neu.edu

Assignment 8
*/

/*
There were many design decisions to be made, specifically 
regarding how to organize all the methods and classes.

Once I decided to use JLabels to display the lights I had 
to figure out how to place them on top of the main panel.
This led me to discover JLayeredPane.

I extended JLabel to create the LightLabel class so it could
have a mouseListener and set and switch colors easily.

I chose the GridBagLayout because it allowed me to customize
the layout of the lights to match the tree on the panel behind them.
I decided to use for loops to populate the ArrayLists of the
positions instead of manually typing each position out.

I found it necessary to create a class extending JPanel so
I could store and change the background and text colors and
the image of the tree. It also made everything much neater
as I needed to override paintComponent as well.

I created a JMenuBar that can be used to change all of the
settings of the greeting card. I decided to create an EditMenu
class that extends JMenu because I need the edit menu to do
way too many things. It looked really messy and now I can store
and change the necessary values. i.e. Timer, TimerAction

The TimerAction class is crucial to handling the timer events.
I needed this class to mainly change the blinking patterns.

The final addition was the ability to change the shape of the
lights. This was simple since the groundwork was already set.
I just used another JOptionPane and JMenuItem.
*/

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

public class HolidayGreeting {
	// x location of window
	private static final int X_COORD = 300;
	// y location of window
    private static final int Y_COORD = 80;
	// horizontal size
    private static final int X_SIZE = 500;
	// vertical size
    private static final int Y_SIZE = 795;
	
	// light size
	private static final int LIGHT_SIZE = 20;
	
	// tick rate of timer in milliseconds
	private static final int DELAY = 1000;
	
	// handle the GUI creation and display
	public static void createAndShowGUI () {
		// create and set up the window
		JFrame frame = new JFrame("Happy Holidays!");
		// set the location of the frame
		frame.setLocation(X_COORD, Y_COORD);
		// set the preferred size of the frame
		frame.setPreferredSize(new Dimension(X_SIZE, Y_SIZE));
		// The EXIT_ON_CLOSE action is the normal one when the windows is closed
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// create the list of lights
		final ArrayList<LightLabel> lights = new ArrayList<LightLabel> ();
		
		// create the list of light positions in x
		final ArrayList<Integer> xs = new ArrayList<Integer> ();
		
		// calculate the x positioning of each light
		int low = 11;
		int high = 11;
		for (int j = 0; j < 10; j++) {
			for (int k = high-low; k >= 0; k--) {
				if(k+low<0)
					xs.add(0);
				else
					xs.add(k+low);
			}
			low--;
			high++;
		}
		
		// create the list of light positions in y
		final ArrayList<Integer> ys = new ArrayList<Integer> ();
		
		// calculate the y positioning of each light
		int each = 0;
		for (int count = 0; count < 10; count+=1) {
			for(int i = each; i >= 0; i--) {
				ys.add(count);
			}
			each += 2;
		}
		
		// creates a JLayeredPane to display everything on
		JLayeredPane layeredPane = new JLayeredPane();
		// set this layeredpane to the frame's content pane
		frame.setContentPane(layeredPane);
		
		// Creates a HolidayPanel
		final HolidayPanel panel = new HolidayPanel();
		// set the bounds of the panel
		panel.setBounds(0, 0, X_SIZE, Y_SIZE);
		// add this panel to the layeredPane
		layeredPane.add(panel, JLayeredPane.FRAME_CONTENT_LAYER);
		
		// create a JPanel to hold the lights
		final JPanel lightPanel = new JPanel();
		// set opaque to false so we can see through this panel
		lightPanel.setOpaque(false);
		// set to grid bag layout
		lightPanel.setLayout(new GridBagLayout());
		// set the constraints of the grid bag layout
		GridBagConstraints c = new GridBagConstraints();
		// set the bounds of the panel
		lightPanel.setBounds(0, 0, X_SIZE, Y_SIZE);
		// add this to the layeredPane
		layeredPane.add(lightPanel, JLayeredPane.DRAG_LAYER);
		
		// create the lights
		for(int i = 0; i < 100; i++) {
			LightLabel light = new LightLabel(Color.GREEN, Color.RED, 0);
			
			// set the preferred size
			light.setPreferredSize(new Dimension(LIGHT_SIZE, LIGHT_SIZE));
			// add the jlabel to the array
			lights.add(light);
		}
		
		// add the lights to the lightPanel
		for(int n = lights.size()-1; n >= 0; n--) {
			// adjust the spacing with insets
			c.insets = new Insets(20,0,20,5);
			// set the location on the grid
			c.gridx = xs.get(n);
			c.gridy = ys.get(n);
			lightPanel.add(lights.get(n), c);
		}
		
		// Creates a menu bar
		JMenuBar mBar = new JMenuBar();
		
		// create the timer
		final Timer t = new Timer(DELAY, null);
		// Create a TimerAction to handle actions of the Timer
		final TimerAction tAction = new TimerAction(0, false, lights);
		// add the TimerAction to the Timer
		t.addActionListener (tAction);
		
		//Create the menu
		final JMenu menu = new JMenu("File");
		// make it traversable through keyevents
		menu.setMnemonic(KeyEvent.VK_A);
		// add the menu to the menu bar
		mBar.add(menu);
		// create the edit menu
		EditMenu edit = new EditMenu(t, tAction, panel, lights, false);
		// set the text of the menu
		edit.setText("Edit");
		edit.setMnemonic(KeyEvent.VK_B);
		mBar.add(edit);
		
		// create the menu item
		JMenuItem menuItem;
		
		// create a menu item to close the window
		menuItem = new JMenuItem("Exit");
		menuItem.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					// exit the program
					System.exit(0);
				}
			}
		);
		menu.add(menuItem);
		
		// start the timer
		t.start();
		
		// add the JMenuBar to the frame
		frame.setJMenuBar(mBar);
		
		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}
	
	public static void main (String[] args) {
		//Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}
}

/*
The HolidayPanel Class

Handles all of the background drawing
including the background color, image and text.
*/
class HolidayPanel extends JPanel{
	// horizontal size
    private static final int IMG_X_SIZE = 480;
	// vertical size
    private static final int IMG_Y_SIZE = 730;
	// background color
	private Color backCol = Color.BLUE;
	// set string color
	private Color stringCol = Color.RED;
	// get the christmas tree image
	private static final Image tree = new ImageIcon("xmas tree.png").getImage();
	
	// override the paintComponent method to display the background
	@ Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// set the background color
		this.setBackground(backCol);
		// draw the tree
		g.drawImage(tree, 0, 0, IMG_X_SIZE, IMG_Y_SIZE, null);
		
		// set the color of the greeting
		g.setColor(stringCol);
		// change the font
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 36));
		// draw the greeting
		g.drawString("Happy    Holidays!", 114, 80);
	}
	
	// setStringCol : Color ->
	// set the text color field
	public void setStringCol (Color c) {
		// ignore a null color
		if(c != null)
			this.stringCol = c;
		
		this.repaint();
	}
	
	// setBackCol : Color ->
	// sets the background color field
	public void setBackCol (Color c) {
		// ignore a null color
		if(c != null)
			this.backCol = c;
		
		this.repaint();
	}
}

/*
The LightLabel Class

LightLabel : Color Color int

Used to hold and display the lights.
*/
class LightLabel extends JLabel implements MouseListener {
	// light size
	private static final int LIGHT_SIZE = 10;
	// the first color
	Color col;
	// the secondary color
	Color blink;
	// the type of the light
	int type;
	
	public LightLabel (Color col, Color blink, int type) {
		this.col = col;
		this.blink = blink;
		this.type = type;
		// add the MouseListener to this
		this.addMouseListener(this);
	}
	
	// setCol : Color ->
	// change the col field
	public void setCol (Color c) {
		// ignore a null color
		if(c != null)
			this.col = c;
		
		this.repaint();
	}
	
	// setBlink : Color ->
	// change the blink field
	public void setBlink (Color blink) {
		// ignore a null color
		if(blink != null)
			this.blink = blink;
		
		this.repaint();
	}
	
	// switchColors : ->
	// switches the colors so blinking works
	public void switchColors () {
		// temporary color to switch col to
		Color temp = this.blink;
		// switch blink to col
		this.blink = this.col;
		// switch col to blink
		this.col = temp;
		
		this.repaint();
	}
	
	// override the mouseClicked method to let the user change the light's colors
	@ Override
	public void mouseClicked (MouseEvent e) {
		// show the JColorChooser for the primary color
		final Color newColor = JColorChooser.showDialog(
							new JFrame(),
							"Choose a Primary Color for this Light",
							this.col);
		// set the primary color of the light
		this.setCol(newColor);
		// show the JColorChooser for the secondary color
		final Color blinkCol = JColorChooser.showDialog(
								new JFrame(),
								"Choose a Secondary Color for this Light",
								this.blink);
		// set the secondary color of the light
		this.setBlink(blinkCol);
		
		this.repaint();
	}
	
	// the following empty methods must be overriden
	@ Override
	public void mouseExited (MouseEvent e) {
	}
	
	@ Override
	public void mousePressed (MouseEvent e) {
	}

	@ Override
	public void mouseEntered (MouseEvent e) {
	}
	
	@ Override
	public void mouseReleased (MouseEvent e) {
	}
	
	// override the paintComponent to draw the lights
	@ Override
	public void paintComponent (Graphics g) {
		super.paintComponent(g);
		
		// to create a square
		if (type == 1) {
			g.setColor(this.col);
			g.fillRect(0, 0, LIGHT_SIZE, LIGHT_SIZE);
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, LIGHT_SIZE, LIGHT_SIZE);
		}
		// to create an arc
		else if (type == 2) {
			g.setColor(this.col);
			g.fillArc(0, 0, LIGHT_SIZE, LIGHT_SIZE, 180, 180);
			g.setColor(Color.BLACK);
			g.drawArc(0, 0, LIGHT_SIZE, LIGHT_SIZE, 180, 180);
		}
		// to create an oval, the default
		else {
			g.setColor(this.col);
			g.fillOval(0, 0, LIGHT_SIZE, LIGHT_SIZE);
			g.setColor(Color.BLACK);
			g.drawOval(0, 0, LIGHT_SIZE, LIGHT_SIZE);
		}
	}
	
	// setType : int ->
	// sets the type of the light
	public void setType (int t) {
		this.type = t;
		
		this.repaint();
	}
}

/*
The TimerAction Class

TimerAction : int boolean ArrayList<LightLabel>

Used to handle actions of the Timer.
*/
class TimerAction implements ActionListener {
	// int to change the pattern in which the lights change
	int switcher;
	// true if the colors are alternating
	boolean alt;
	// the size of the list of the lights
	ArrayList<LightLabel> lights;
	
	public TimerAction (int switcher, boolean alt, ArrayList<LightLabel> lights) {
		this.switcher = switcher;
		this.alt = alt;
		this.lights = lights;
	}
	
	// setSwitch : int ->
	// set the switcher to the given int
	public void setSwitch (int s) {
		this.switcher = s;
		
		// if the colors need to be set to alternate
		if(switcher != 0)
			// the colors aren't yet alternating
			this.alt = false;
	}
	
	// overrides the actionPerformed method to handle the timer actions
	@ Override
	public void actionPerformed(ActionEvent event) {
		// the case where all lights should blink
		if(switcher == 0) {
			if(!alt) {
				// switch the lights color
				for(int n = 0; n < lights.size(); n++) {
					lights.get(n).switchColors();
				}
			}
			// to make sure they stop alternating on the next update
			else {
				for(int n = 0; n < lights.size(); n+=2) {
					lights.get(n).switchColors();
				}
				// not alternating
				alt = false;
			}
		}
		
		// the case where they blink in alternating fashion
		else {
			// to start with, just change every other light
			if(!alt) {
				for(int i = 0; i < lights.size(); i+=2) {
					lights.get(i).switchColors();
				}
				// set alternating to true
				alt = true;
			}
			// then just blink normally
			else {
				for(int i = 0; i < lights.size(); i++) {
					lights.get(i).switchColors();
				}
			}
		}
	}
}

/*
The EditMenu Class

EditMenu : Timer TimerAction HolidayPanel ArrayList<LightLabel> boolean

Used in the JMenuBar to give the user
control of all the program's settings
*/
class EditMenu extends JMenu {
	// the timer
	final Timer t;
	// the timeraction
	final TimerAction tAction;
	// the holiday panel
	final HolidayPanel panel;
	// arraylist of lights
	final ArrayList<LightLabel> lights;
	// boolean to display start or stop
	boolean stopped;
	
	// create a menu item to stop the blinking
	final JMenuItem stopItem = new JMenuItem("Stop Blinking");
	// create a menu item to start the blinking
	final JMenuItem startItem = new JMenuItem("Start Blinking Again");
	
	public EditMenu (final Timer t, final TimerAction tAction, final HolidayPanel panel, 
					final ArrayList<LightLabel> lights, boolean stopped) {
		this.t = t;
		this.tAction = tAction;
		this.panel = panel;
		this.lights = lights;
		this.stopped = stopped;
		
		// create a menu item to choose the color of the lights
		JMenuItem menuItem = new JMenuItem("Light Color");
		// add actionlistener
		menuItem.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					// choose the primary color of the lights
					final Color newColor = JColorChooser.showDialog(
										new JFrame(),
										"Choose a Color for All of the Lights",
										panel.getBackground());
										
					// choose the secondary color of the lights
					final Color secCol = JColorChooser.showDialog(
										new JFrame(),
										"Choose a Secondary Color for All of the Lights",
										panel.getBackground());
										
					// changes the color of all the lights
					for(int n = lights.size()-1; n >= 0; n--) {
						// changes the color
						lights.get(n).setCol(newColor);
						// changes the secondary color
						lights.get(n).setBlink(secCol);
					}
				}
			}
		);
		// add the menu item to the menu
		this.add(menuItem);
		
		// create a menu item to change the blinking pattern
		menuItem = new JMenuItem("Blinking Pattern");
		menuItem.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					// choose the blinking pattern
					//Custom button text
					Object[] options = {"Blinking",
										"Alternating",
										"Start/Stop Blinking",
										"Cancel"};
					// option dialog to change the pattern
					int switcher = JOptionPane.showOptionDialog(
						new JFrame(),
						"What would you like the lighting pattern to be?",
						"Change Lighting Pattern",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options,
						options[3]);
						
					if(switcher == 0 || switcher == 1) {
						// set the switcher for the lighting pattern
						tAction.setSwitch(switcher);
					}
					else if (switcher == 2) {
						// start or stop the timer
						startStop();
					}
				}
			}
		);
		this.add(menuItem);
		
		// create a menu item to change the background color
		menuItem = new JMenuItem("Background Color");
		menuItem.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					// choose the background color of the holidaypanel
					Color newBack = JColorChooser.showDialog(
										new JFrame(),
										"Choose a Background Color",
										panel.getBackground());
					// set the background of the panel to the chosen color
					panel.setBackCol(newBack);
				}
			}
		);
		this.add(menuItem);
		
		// create a menu item to change the text color
		menuItem = new JMenuItem("Text Color");
		menuItem.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					// choose the text color
					Color newBack = JColorChooser.showDialog(
										new JFrame(),
										"Choose a Background Color",
										panel.getBackground());
					// set the text to the chosen color
					panel.setStringCol(newBack);
				}
			}
		);
		this.add(menuItem);
		
		// create a menu item to change the speed of the timer
		menuItem = new JMenuItem("Timer Speed");
		menuItem.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					String newDelay = JOptionPane.showInputDialog(new JTextField(), "Enter a Timer Speed in Milliseconds");
					// make sure it's an int
					if(isInt(newDelay)) {
						// set the timer speed
						t.setDelay(Integer.parseInt(newDelay));
					}
				}
			}
		);
		this.add(menuItem);
		
		// create a menu item to change the shape of the lights
		menuItem = new JMenuItem("Light Shape");
		menuItem.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					Object[] shapes = {"Circle",
										"Square",
										"Arc"};
					
					// option dialog to display the choices
					int shape = JOptionPane.showOptionDialog(
						new JFrame(),
						"What would you like the shape of the lights to be?",
						"Change Light Shape",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						shapes,
						shapes[2]);
					
					// change the shape of all of the lights
					for(int i = 0; i < lights.size(); i++) {
						lights.get(i).setType(shape);
					}
				}
			}
		);
		this.add(menuItem);
		
		stopItem.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					startStop();
				}
			}
		);
		
		startItem.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					startStop();
				}
			}
		);
		
		this.add(stopItem);
	}
	
	// isInt : String -> boolean
	// make sure the given string is an int
	public boolean isInt (String s) {
		try {
			// try to parse the string to an int
			Integer.parseInt (s);
			return true;
		}
		catch (Exception e) {
			// tell them you can only enter ints
			JOptionPane.showMessageDialog(null,
					"You must enter an integer.");
			return false;
		}
	}
	
	// startStop : ->
	// start or stop the timer
	public void startStop() {
		if(stopped) {
			// start the blinking
			t.start();
			// add the stop blinking menu item
			this.add(stopItem);
			// remove the start blinking menu item
			this.remove(startItem);
			// set the boolean to false
			stopped = false;
		}
		else {
			// stop the timer
			t.stop();
			// add the start blinking menu item
			this.add(startItem);
			// remove the stop blinking menu item
			this.remove(stopItem);
			// set the boolean to true
			stopped = true;
		}
	}
}