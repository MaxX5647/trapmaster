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

	private Hud_tm gameHud; 

	private boolean doMonsterThread, doTrapThread, doMapThread;

	private final int SCREEN_HEIGHT = 1080;

	private final int SCREEN_WIDTH = 1920;

	private int gameWidth;

	private int gameHeight;

	private boolean debug;



	private int goldAmt;

	

	//Buy mode stuff

	private RenderObj purchase;

	private int purchaseCost; 

	private int purchaseType;

	private boolean canPlace;

	

	//Object Instantiation Arrays

	private ArrayList<Trap_tm> traps;

	private ArrayList<Monster_tm> monsters;

	private ArrayList<Tile_tm> tiles;

	private ArrayList<ActionBox> gameBounds;

	private int trapIt, monsterIt, tileIt, aBIt; 

	private Trap_tm trap;

	private Monster_tm monster;

	private Tile_tm tile;

	private ActionBox aB;

	

	//Game Interaction Variables

	private int screenScrollXZone;

	private int screenScrollYZone;

	private boolean moveCamLeft, moveCamRight, moveCamUp, moveCamDown;

	private boolean moveCamLeftKey, moveCamRightKey, moveCamUpKey, moveCamDownKey; 

	private int mouseX, mouseXClick;

	private int mouseY, mouseYClick;

	private int mode;

	

	

	//Game modes

	public static final int STD_MODE = 0;

	public static final int REPAIR_MODE = 1;

	public static final int SELL_MODE = 2;

	public static final int PAUSE_MODE = 3;

	public static final int BUY_MODE = 4;

	

	//Debug Stuff

	private ActionBox testBound;

	private JLabel lblMouseLocation;

	

	//Object Types

	public static final int MONSTER = 0;

	public static final int TRAP = 1;

	public static final int TILE = 2;

	

	private JLabel lblMouse;

	private JLabel lblPause;

	

	public static void main(String[] args) {

		

		EventQueue.invokeLater(new Runnable() {

			public void run() {

				try {

					

					Main_tm window = new Main_tm(true);

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

	public Main_tm(boolean deb) {

		debug = deb;

		initialize();

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

		gameEngine = new RenderEngine_tm(1668, 1080, 16, 5000);

		gameEngine.setRefreshColor(Color.LIGHT_GRAY);

		game_frame.getContentPane().add(gameEngine); 

		gameEngine.setLayout(null);

		

		lblPause = new JLabel ("PAUSE");

		lblPause.setFont(new Font("Tahoma", Font.PLAIN, 60));

		lblPause.setHorizontalAlignment(SwingConstants.CENTER);

		lblPause.setSize(375, 104);

		lblPause.setLocation(672, 454); 

		gameEngine.add(lblPause);

		lblPause.setVisible(false);

		

		

		lblMouse = new JLabel("Mouse: ");

		lblMouse.setBounds(834, 5, 80, 14);

		gameEngine.add(lblMouse); 

		

		if(debug) {

			lblMouseLocation = new JLabel("Mouse x: Mouse Y:");

			lblMouseLocation.setBounds(900, 10, 80, 14);

			lblMouseLocation.setLocation(10, 10);

		}

		gameHud = new Hud_tm(this, SCREEN_WIDTH, SCREEN_HEIGHT);

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

		goldAmt = 1000;



		

		

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

		/*

		 * Some code/method call to populate the monsters

		 */

		

		gameBounds = new ArrayList<ActionBox>();

		/*

		 * Some code/method call to populate the ActionBox (psst: base it off the tile list)

		 */

		

		if(debug) {

			testBound = ActionBox.makeActionBox(0, 0, 10, 10);

			gameBounds.add(testBound);

		}

		

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

		

		arraySize = gameBounds.size(); 

		for(i = 0; i < arraySize; i++) {

			curr = gameBounds.get(i);

			gameEngine.addRenderObj(curr);

		}

		

		

		

		

		

		/****************************************************

		 *                Game Listeners                    *

		 ****************************************************/

	

		/*

		 * Use the mouse motion listener to accomplish any function while the mouse is moving across the game world

		 */

		gameEngine.addMouseMotionListener(new MouseMotionAdapter() {

			

			public void mouseMoved(MouseEvent e) { 

				gameEngine.requestFocus();

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

							if(traps.get(i) != null) {

								thing = traps.get(i);

								if(purchase.isColliding(thing)) {

									canPlace = false;

									i = size;

								}

							}

						}

						size = monsters.size();

						for(i = 0; i < size; i++) {

							thing = monsters.get(i);

							if(purchase.isColliding(thing)) {

								canPlace = false;

								i = size;

							}

						}

						size = gameBounds.size();

						for(i = 0; i < size; i++) {

							thing = gameBounds.get(i);

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

					else {

						

						purchase.addFilter(Color.GREEN, 45);

					}

				}

				

				lblMouseLocation.setText("Mouse X: " + mouseX + " ScrollX: " + screenScrollXZone + " MouseY: " + mouseY + " ScrollY: " + screenScrollYZone);

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

		

		/*

		 * The mouseListener relates to any functions that involve clicking on the game world

		 */

		gameEngine.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {

				mouseXClick = e.getX();

				mouseYClick = e.getY();

				

				if(mode == SELL_MODE) {

					Trap_tm currTrap;

					int i; 

					for(i = 0; i < traps.size(); i++) {

						currTrap = traps.get(i); 

						if(currTrap.contains(mouseXClick + gameEngine.getViewportX(), mouseYClick + gameEngine.getViewportY())) {

							traps.remove(i); 

							gameEngine.removeRenderObj(currTrap);

							giveGold(currTrap.tr_sell());

							i = traps.size();

						}

					} 

				}

				else if(mode == REPAIR_MODE) {

					Trap_tm currTrap;

					int i; 

					for(i = 0; i < traps.size(); i++) {

						currTrap = traps.get(i); 

						if(currTrap.contains(mouseXClick + gameEngine.getViewportX(), mouseYClick + gameEngine.getViewportY())) {

							

							//currTrap.toggleRepair(); 

							i = traps.size();

						}

					} 

				}

				

				if(canPlace == true && mode == BUY_MODE) {

					purchase.removeFilter();

					setMode(STD_MODE);

					takeGold(purchaseCost);

					

					switch(purchaseType) {

						case MONSTER:

							monsters.add((Monster_tm)purchase);

						break;

						case TRAP:

							traps.add((Trap_tm)purchase);

						break;

						case TILE:

							tiles.add((Tile_tm)purchase);

						break;

						default:

					}

				}

			}

		});

		

		/*

		 * The gameHud listener is for anything to do with moving the mouse over the gameHud

		 */

		gameHud.addMouseMotionListener(new MouseMotionAdapter() {

			public void mouseMoved(MouseEvent e) {

				moveCamRight = false;

			}

		});

		

		/*

		 * The key listener is for responding to keyPress events. 

		 * If you wanted to customize the controls, you could have a set of ints set by some sort of

		 * Menu the player has access to? To change a control, you can just poll the player. But that 

		 * Is beyond the scope of this game

		 */

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

					if(debug) {

						switch(mode) {

							case STD_MODE: System.out.println("Standard mode");

							break;

							case REPAIR_MODE: System.out.println("Repair Mode");break ;

							case SELL_MODE: System.out.println("Sell Mode"); break;

							case PAUSE_MODE: System.out.println("Pause Mode"); break;

							case BUY_MODE: System.out.println("BUY_MODE");

						}

					}

					if(mode != STD_MODE) {

						if(mode == BUY_MODE) {

							gameEngine.removeRenderObj(purchase);

						}

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
				
				if(e.getKeyCode() == KeyEvent.VK_Q) {
					if(purchase != null) {
						if (((Trap_tm)purchase).get_facing() != 0) {
							((Trap_tm)purchase).set_facing(((Trap_tm)purchase).get_facing() - 1);
						} else {
							((Trap_tm)purchase).set_facing(3);
						}
						purchase.setCurrSpriteCol(((Trap_tm)purchase).get_facing());
					}
				}
				
				if(e.getKeyCode() == KeyEvent.VK_E) {
					if(purchase != null) {
						if (((Trap_tm)purchase).get_facing() != 3) {
							((Trap_tm)purchase).set_facing(((Trap_tm)purchase).get_facing() + 1);
						} else {
							((Trap_tm)purchase).set_facing(0);
						}
						purchase.setCurrSpriteCol(((Trap_tm)purchase).get_facing());
					}
				}

			}



			public void keyTyped(KeyEvent arg0) { }

			

		});

		

		

		

			

		gameEngine.setFocusable(true);

		

		

		

		/******************************************************

		 *                                                    *

		 *                                                    *

		 *              GAME EXECUTION THREADS                *

		 *                                                    *              

		 *                                                    *

		 ******************************************************/

		//The monster thread handles everything related to monsters- their movements, their health, possible additions or removals...

		monsterThread = new Thread() {

			public void run() {

				while(true) {

					if(doMonsterThread) {

						for(monsterIt = 0; monsterIt < monsters.size(); monsterIt++) {

							monster = monsters.get(monsterIt);

							if(monster != null) {

								/*

								 * Monster Move action

								 */

								/*

								 * Monster damage-taking actions

								 */

								/*

								 * Monster attack actions

								 */

								/*

								 * Monster treasure-taking actions

								 */

								

							}

						}

						/*

						 * Put monster actions here

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

						for(trapIt = 0; trapIt < traps.size(); trapIt++) {

							trap = traps.get(trapIt);

							if(trap != null) {

								trap.tr_attack();
								/*

								 * Trap target actions

								 */

								/*

								 * Trap Attack actions

								 */

								/*

								 * Trap cooldown actions

								 */

								/*if(trap.isRepairable() && getGoldAmt() > 10) {

									trap.setTr_currentHealth(trap.getTr_currentHealth() + 10);

									takeGold(10);

									if(trap.getTr_currentHelp() >= trap.getTr_maximumHealth()) 

										trap.setRepairable(false);

								}*/

							}

						}

						 

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

	

	/**********************************************************

	 *                     Helper Methods                     *

	 **********************************************************/

	

	

	/**

	 * Returns the boundary of the RenderEngine

	 * @return The lowest value that the appropriate edge of the renderEngine is allowed to go to

	 */

	public int getGameBoundary() {

		return gameEngine.getBoundary();

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

			//gameEngine.stopRender(); 

			doTrapThread = false;

			doMonsterThread = false;

			doMapThread = false;

			gameHud.toggleButtons();

		}

		if(oldMode == PAUSE_MODE && mode != PAUSE_MODE) {

			gameHud.toggleButtons();

			gameEngine.togglePaused();

			//gameEngine.startRender();

			doTrapThread = true;

			doMonsterThread = true;

			doMapThread = true;

			lblPause.setVisible(false);

		}

		

		if(oldMode == BUY_MODE && mode != BUY_MODE) {

			//purchase = null;

			

		}

		

	}

	

	/**

	 * As the name implies, this puts the game into buy mode and makes obj follow the mouse. 

	 * If the player has enough gold, they will be able to purchase the object. 

	 * @param obj The object to be purchased

	 * @param objType The type of the object- probably a trap

	 * @param cost The cost of the object, in gold. Whole numbers only. 

	 */

	public void makePurchase(RenderObj obj, int objType, int cost) {

		setMode(BUY_MODE);

		purchase = obj;

		purchase.addFilter(Color.GREEN, 45);

		purchaseCost = cost;

		purchaseType = objType;

		gameEngine.addRenderObj(purchase);

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

