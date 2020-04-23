import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Insets;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JSplitPane;

import java.awt.Color;
import java.awt.SystemColor;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.Font;


public class Main_tm {
 
	//Game framing variables
	private JFrame game_frame; 
	private Thread monsterThread, trapThread, mapThread;
	private RenderEngine_tm gameEngine;
	private Map_tm gameMap;
	private HUD_tm gameHud; 
	private boolean doMonsterThread, doTrapThread, doMapThread;
	private final int SCREEN_HEIGHT = 1080;
	private final int SCREEN_WIDTH = 1920;
	private int gameWidth;
	private int gameHeight;
	

	private int goldAmt;
	
	//Buy mode stuff
	private RenderObj purchase;
	private int purchaseCost; 
	private boolean canPlace;
	
	//Object Instantiation Arrays
	private ArrayList<Trap_tm> traps;
	private ArrayList<Monster_tm> monsters;
	private ArrayList<Tile_tm> tiles;
	
	//Game Interaction Variables
	private int screenScrollXZone;
	private int screenScrollYZone;
	private boolean moveCamLeft, moveCamRight, moveCamUp, moveCamDown;
	private boolean moveCamLeftKey, moveCamRightKey, moveCamUpKey, moveCamDownKey; 
	private int mouseX;
	private int mouseY;
	private int mode;
	
	//Game modes
	public static final int STD_MODE = 0;
	public static final int REPAIR_MODE = 1;
	public static final int SELL_MODE = 2;
	public static final int PAUSE_MODE = 3;
	public static final int BUY_MODE = 4;
	
	private ActionBox testBound;
	private JLabel lblMouse;
	private JLabel lblPause;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main_tm window = new Main_tm();
					window.game_frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main_tm() {
		initialize();
	}

	/**
	 * Set the mode for the game. The modes are as follows: 
	 * STD_MODE, where the game proceeds as expected
	 * SELL_MODE, where clicking will sell traps
	 * REPAIR_MODE, where clicking will repair traps
	 * BUY_MODE, where clicking will place an object
	 * PAUSE_MODE, where the game. Is paused. 
	 * @param The mode the game will be in. 
	 * 
	 */
	public void setMode(int newMode) {
		int oldMode = mode; 
		mode = newMode;
		if(mode != REPAIR_MODE || mode != SELL_MODE) 
			lblMouse.setText("");
		if(mode == REPAIR_MODE) 
			lblMouse.setText("REPAIR");
		else if(mode == SELL_MODE) 
			lblMouse.setText("SELL"); 
		if(mode == PAUSE_MODE) { 
			lblPause.setVisible(true);
			gameEngine.togglePaused();
			gameEngine.stopRender(); 
			doTrapThread = false;
			doMonsterThread = false;
			doMapThread = false;
			gameHud.toggleButtons();
		}
		if(oldMode == PAUSE_MODE && mode != PAUSE_MODE) {
			gameHud.toggleButtons();
			gameEngine.togglePaused();
			gameEngine.startRender();
			doTrapThread = true;
			doMonsterThread = true;
			doMapThread = true;
			lblPause.setVisible(false);
		}
	}
	
	/**
	 * 
	 * @param obj
	 * @param cost
	 */
	public void makePurchase(RenderObj obj, int cost) {
		setMode(BUY_MODE);
		purchase = obj;
		purchase.addFilter(Color.GREEN, 45);
		purchaseCost = cost;
		gameEngine.addRenderObj(purchase);
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		/***************************************************
		 *                  Frame Setup                    *
		 ***************************************************/
		game_frame = new JFrame();  
		//game_frame.setBounds(0, 0, 1920, 1080);
		game_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game_frame.getContentPane().setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
		game_frame.setResizable(false);
		
		doMonsterThread = true;
		doTrapThread = true;
		doMapThread = true;
		//1668
		gameEngine = new RenderEngine_tm(1668, 1080, 16);
		gameEngine.setRefreshColor(Color.LIGHT_GRAY);
		game_frame.getContentPane().add(gameEngine); 
		gameEngine.setLayout(null);
		
		lblPause = new JLabel ("PAUSE");
		lblPause.setFont(new Font("Tahoma", Font.PLAIN, 60));
		lblPause.setHorizontalAlignment(SwingConstants.CENTER);
		lblPause.setSize(375, 104);
		gameEngine.add(lblPause);
		lblPause.setVisible(false);
		
		
		lblMouse = new JLabel("Mouse: ");
		lblMouse.setBounds(834, 5, 38, 14);
		gameEngine.add(lblMouse); 
		
		gameHud = new HUD_tm(this, SCREEN_WIDTH, SCREEN_HEIGHT);
		game_frame.getContentPane().add(gameHud);
		game_frame.pack();
		
		
		/***************************************************************
		 *                      Game Variables                         *
		 ***************************************************************/
		gameWidth = gameEngine.getViewportWidth();
		gameHeight = gameEngine.getViewportHeight();
		screenScrollXZone = gameWidth/50;
		screenScrollYZone = gameHeight/50; 
		moveCamLeft = false;
		moveCamRight = false;
		moveCamUp = false;
		moveCamDown = false; 
		canPlace = false;
		mouseX = 0;
		mouseY = 0;
		mode = 0;
		goldAmt = 421;

		
		/***************************************************************
		 *            Game Object References & Initializations         *
		 ***************************************************************/
		
		traps = new ArrayList<Trap_tm>(); 
		/*
		 * Some code/method call to populate the traps
		 */
		
		
		
		tiles = new ArrayList<Tile_tm>(); 
		/*
		 * Some code/method call to populate the tiles
		 */
		
		monsters = new ArrayList<Monster_tm>();
		
		int i, arraySize;
		
		RenderObj curr;
		
		arraySize = traps.size();
		for(i = 0; i < arraySize; i++) {
			curr = traps.get(i);
			gameEngine.addRenderObj(curr);
		}
		
		arraySize = tiles.size(); 
		for(i = 0; i < arraySize; i++) {
			curr = tiles.get(i);
			gameEngine.addRenderObj(curr);
		}
		
		arraySize = monsters.size();
		for(i = 0; i < arraySize; i++) {
			curr = monsters.get(i);
			gameEngine.addRenderObj(curr);
		}
		
		
		
		
		
		/****************************************************
		 *                Game Listeners                    *
		 ****************************************************/
	
		lblPause.setLocation(672, 454); 
		
		testBound = ActionBox.makeActionBox(0, 0, 10, 10);
		
		gameEngine.addRenderObj(testBound);
		
		gameEngine.addMouseMotionListener(new MouseMotionAdapter() {
			
			public void mouseMoved(MouseEvent e) {
				
				mouseX = e.getX(); 
				mouseY = e.getY();

				lblMouse.setLocation(mouseX + 8, mouseY - 5);
				if(mode== STD_MODE) {
					lblMouse.setText("");
				}
				if(mode == REPAIR_MODE) {
					lblMouse.setText("REPAIR");
				}
				else if(mode == SELL_MODE) {
					lblMouse.setText("SELL");
				}
				else if(mode == BUY_MODE) {
					purchase.setXPosWorld(gameEngine.getViewportX() + mouseX);
					purchase.setYPosWorld(gameEngine.getViewportY() + mouseY);
					if(getGoldAmt() >= purchaseCost) {
						canPlace = true;
						int i, size; 
						size = traps.size();
						RenderObj thing;
					
						for(i = 0; i < size; i++) {
							thing = traps.get(i);
							if(purchase.isColliding(thing)) {
								canPlace = false;
								i = size;
							}
						}
						for(i = 0; i < size; i++) {
							thing = monsters.get(i);
							if(purchase.isColliding(thing)) {
								canPlace = false;
								i = size;
							}
						}
					}
					else {
						canPlace = false;
					}
					if(canPlace == false)
						purchase.addFilter(Color.RED, 45);
				}
				
				//lblMouse.setText("Mouse X: " + mouseX + " ScrollX: " + screenScrollXZone + " MouseY: " + mouseY + " ScrollY: " + screenScrollYZone);
				if(mouseX < screenScrollXZone) {
					moveCamRight = false;
					moveCamLeft = true;
				}
				else if(mouseX > gameWidth - screenScrollXZone) {
					moveCamRight = true;
					moveCamLeft = false; 
				}
				else {
					moveCamRight = false;
					moveCamLeft = false;
				}
				if(mouseY < screenScrollYZone) {
					moveCamUp = true;
					moveCamDown = false; 
				}
				else if(mouseY > gameHeight - screenScrollYZone) {
					moveCamUp = false;
					moveCamDown = true; 
				}
				else {
					moveCamUp = false;
					moveCamDown = false;
				}
			}
		}); 
		
		gameEngine.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
				if(canPlace == true) {
					purchase.removeFilter();
					setMode(STD_MODE);
					setGoldAmt(getGoldAmt() - purchaseCost);
				}
			}
		});
		
		gameHud.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				moveCamRight = false;
			}
		});
		
		gameEngine.addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
					moveCamLeftKey = true;
				}
				if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
					moveCamUpKey = true;
				}
				if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
					moveCamRightKey = true;
				}
				if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
					moveCamDownKey = true;
				}
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					
					
					 if(mode != STD_MODE) {
						 if(mode == BUY_MODE) 
							 gameEngine.removeRenderObj(purchase);
						 setMode(STD_MODE);
						
					}
					else {
						setMode(PAUSE_MODE);
					}
				}
			}

			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
					moveCamLeftKey = false;
				}
				if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
					moveCamUpKey = false;
				}
				if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
					moveCamRightKey = false;
				}
				if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
					moveCamDownKey = false;
				}
			}

			public void keyTyped(KeyEvent arg0) { }
			
		});
			
		gameEngine.setFocusable(true);
		
		
		
		/******************************************************
		 *              GAME EXECUTION THREADS                *
		 ******************************************************/
		//The monster thread handles everything related to monsters- their movements, their health, possible additions or removals...
		monsterThread = new Thread() {
			public void run() {
				while(true) {
					if(doMonsterThread) {
						/*
						 * Put monster actions here
						 * 
						 */
					}
					try {
						sleep(20);
					}
					catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		//Trap Thread handles trap working- placing or removing damage zones, etc. 
		trapThread = new Thread() {
			public void run() {
				while(true) {
					if(doTrapThread) {
						/*
						 * Put Trap actions here
						 */
					}
					try {
						sleep(20);
					}
					catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		//mapThread handles the workings of the map- routing, viewport adjustments, all that. 
		mapThread = new Thread() {
			public void run() {
				while(true) {
					if(doMapThread) {
					/*
					 * Put map actions(?) here
					 */
						gameHud.setGold(getGoldAmt());
						/*********************
						 * Scrolling options *
						 *********************/
						if(moveCamLeft) 
							gameEngine.setViewportX(gameEngine.getViewportX() - (screenScrollXZone - mouseX)/3.8);
						else if(moveCamRight) 
							gameEngine.setViewportX(gameEngine.getViewportX() + (mouseX%(gameWidth - screenScrollXZone))/3.8);
					
						
						if(moveCamLeftKey) {
							gameEngine.setViewportX(gameEngine.getViewportX() - 8);
						}
						else if(moveCamRightKey) 
							gameEngine.setViewportX(gameEngine.getViewportX() + 8);
						
						
						if(moveCamUp) {
							gameEngine.setViewportY(gameEngine.getViewportY() - (screenScrollYZone - mouseY)/3.8); 
						}
						else if(moveCamDown) {
							gameEngine.setViewportY(gameEngine.getViewportY() + (mouseY%(gameHeight - screenScrollYZone))/3.8);
						}
						
	
						if(moveCamUpKey) {
							gameEngine.setViewportY(gameEngine.getViewportY() - 8); 
						}
						else if(moveCamDownKey) {
							gameEngine.setViewportY(gameEngine.getViewportY() + 8);
						}
						
						/*************************
						 * End Scrolling Options *
						 *************************/
					}
					try {
						sleep(20);
					}
					catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		
		
		gameEngine.startRender(); 
		monsterThread.start();
		trapThread.start();
		mapThread.start();
	}
	
	/**
	 * Returns the amount of gold the player has
	 * @return The amount of gold the player has
	 */
	public int getGoldAmt() {
		return goldAmt;
	}
	/**
	 * Sets the amount of gold the player has, both the value 
	 * And the counter
	 * @param newAmt The new amount of gold the player has
	 */
	public void setGoldAmt(int newAmt) {
		goldAmt = newAmt;
		gameHud.setGold(goldAmt);
	}
	/**
	 * Takes amtTaken gold from the player
	 */
	public void takeGold(int amtTaken) {
		setGoldAmt(getGoldAmt() - amtTaken);
	}
	/**
	 * Adds amtGiven to the amount of gold the player has
	 */
	public void giveGold(int amtGiven) {
		setGoldAmt(getGoldAmt() + amtGiven);
	}
}



