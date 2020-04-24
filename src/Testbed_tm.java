import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.MouseInfo;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JLabel;

import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.GridLayout;


public class Testbed_tm {

	private JFrame game_frame;
	private testRender tester, tester2, tester4, tester5;
	private invisibleRender tester3;
	private Thread testThread;
	private Thread monsterThread;
	private Thread trapThread;
	private Thread mapThread;
	private DecimalFormat formatter;
	int gamex = 1920;
	int gamey = 1080;
	int i;
	JLabel mousePos, entPosLabel;
	private JLabel entAngleLabel;
	private JLabel entDimLabel, entVelLabel;
	private JLabel centerLabel;
	private JLabel mousePos_1;
	private JLabel viewportPos;
	private 	RenderEngine_tm gameFramer;
	private JLabel lblViewportXPos;
	private JLabel lblCorners;
	private Insets bounds;
	private JLabel lblViewportPos; 
	private ArrayList<RenderObj> testers;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Testbed_tm window = new Testbed_tm();
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
	public Testbed_tm() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		game_frame = new JFrame();
		bounds = game_frame.getInsets();
		game_frame.setBounds(100, 100, gamex + bounds.top + bounds.bottom, gamey); 
		game_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game_frame.setBounds(bounds.top, bounds.left, gamex, gamey);
		game_frame.setUndecorated(true);
		tester = null;
		tester2 = null;
		try { 
			tester = new testRender("/test.png", 1, 1, 200, -200, 3);
			tester2 = new testRender("/Enterprise.jpg", 4, 4, -1, 1, 2); 

			tester4 = new testRender("/Enterprise.jpg", 4, 4, -55, 29, 2); 

			tester5 = new testRender("/Enterprise.jpg", 4, 4, 420, 158, 1); 
			tester3 = new invisibleRender(-200, -200, 200, 200);
			}
		catch(IOException e) {
			e.printStackTrace();
		}
		game_frame.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
		
		gameFramer = new RenderEngine_tm(gamex, gamey, 16, 2000);
		
		
		formatter = new DecimalFormat("#.##");
		game_frame.getContentPane().add(gameFramer);		
		gameFramer.setLayout(null);
		
		mousePos = new JLabel("mouse x: nil mouse y: nil");
		mousePos.setBounds(29, 11, 459, 29);
		gameFramer.add(mousePos);
		
		entPosLabel = new JLabel("position");
		entPosLabel.setBounds(29, 124, 300, 22);
		gameFramer.add(entPosLabel);
		
		entAngleLabel = new JLabel("New label");
		entAngleLabel.setBounds(29, 197, 312, 14);
		gameFramer.add(entAngleLabel);
		
		entDimLabel = new JLabel("df");
		entDimLabel.setBounds(29, 222, 372, 14);
		gameFramer.add(entDimLabel);
		
		entVelLabel = new JLabel("velocities");
		entVelLabel.setBounds(29, 277, 285, 14);
		gameFramer.add(entVelLabel);
		
		 centerLabel = new JLabel("Center");
		centerLabel.setBounds(29, 157, 312, 29);
		gameFramer.add(centerLabel);
		
		mousePos_1 = new JLabel("mouse viewport x: nil mouse viewport y: nil");
		mousePos_1.setBounds(29, 84, 567, 29);
		gameFramer.add(mousePos_1);
		
		viewportPos = new JLabel("Viewport x pos: Viewport y pos: ");
		viewportPos.setBounds(967, 197, 631, 14);
		gameFramer.add(viewportPos);
		
		lblViewportPos = new JLabel("Viewport Position: (" + gameFramer.getViewportX() + ", " + gameFramer.getViewportY() + ")");
		lblViewportPos.setBounds(29, 51, 285, 22);
		gameFramer.add(lblViewportPos);
		
		lblCorners = new JLabel("corners");
		lblCorners.setBounds(29, 311, 716, 14);
		gameFramer.add(lblCorners);
		
		gameFramer.addRenderObj(tester);

		gameFramer.addRenderObj(tester2);

		gameFramer.addRenderObj(tester3);
		
		gameFramer.addRenderObj(tester4);
		gameFramer.addRenderObj(tester5);
 
		
		testers = new ArrayList<RenderObj>();
		
		testers.add(tester);
		testers.add(tester2);
		testers.add(tester3); 
		testers.add(tester4);
		testers.add(tester5);
		
		gameFramer.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				mousePos.setText("Mouse x is: " + ((gameFramer.getViewportX()) + e.getX()) + " Mouse y is: " +
						((gameFramer.getViewportY()) + e.getY()));
				
				mousePos_1.setText("Mouse viewport x is: " + (e.getX()) + 
						"Mouse viewport Y is: " + e.getY());
				
				if(e.getX() < 10) {
					gameFramer.setViewportX((double)(gameFramer.getViewportX() - 1));
				}
			}
			
		});
		

		testers.get(0).addFilter(Color.GREEN, 20);

		 
		
		Thread testThread = new Thread() {
			public void run() {
				int j = 0;
				
				double i = -50; 
				double rightX, leftX, bottomY, topY, cos, sin;
				int time = (int)(System.nanoTime()/1000000);
				//tester2.rotateCurrSprite(50);
				
				while(true) { 
					
						//tester.move(0, 0, gamex, gamey);
						//tester2.move(-gamex/2 + tester2.getRotatedSpriteWidth()/2, -gamey/2 + tester2.getRotatedSpriteHeight()/2,
//								gamex/2 - tester2.getRotatedSpriteWidth()/2, gamey/2 - tester2.getRotatedSpriteWidth()/2);

						
						testers.get(0).rotateCurrSprite(i);
						testers.get(1).rotateCurrSprite(i*-1);
						testers.get(2).rotateCurrSprite(i);
						testers.get(0).addFilter(Color.PINK, 25);
							
						//testers.get(0).addFilter(new Color(j%255, (j > 255 ? j%255 : 0), (j > 511 ? j%255 : 0)), 25);
						time = tester2.animate(time);
		
						j++;
						
						i -= 1;
						i %= 360;
						if(i < 0) {
							i = 360 + i;
						}
						
						rightX = (tester2.getXPosWorld() + tester2.getSpriteWidth()/2); 
						leftX = (tester2.getXPosWorld() - tester2.getSpriteWidth()/2);
						bottomY = (tester2.getYPosWorld() + tester2.getSpriteHeight()/2);
						topY = (tester2.getYPosWorld() - tester2.getSpriteHeight()/2);
						cos = Math.cos(Math.toRadians(i));
						sin = Math.sin(Math.toRadians(i));
						
						lblViewportPos.setText("Viewport Position: (" + gameFramer.getViewportX() + ", " + gameFramer.getViewportY() + ")");
						entPosLabel.setText("Test pos: (" + formatter.format(tester.getXPosWorld()) + ", " + formatter.format(tester.getYPosWorld()) + ")"); 
						centerLabel.setText("Ent renderPos: (" + formatter.format(tester2.getXPosRender(gameFramer.getViewportX())) + ", " +
								formatter.format(tester2.getYPosRender(gameFramer.getViewportY())) + ")" +
								"- Ought to be (" + (tester2.getXPosWorld() - gameFramer.getViewportX()) + ", " + (tester2.getYPosWorld() - gameFramer.getViewportY()) + ")");
						entAngleLabel.setText("Ent angle: " + tester2.getAngle());
						entDimLabel.setText("Ent width: " + tester2.getRotatedSpriteWidth() + " Ent Height: " + tester2.getRotatedSpriteHeight());
						entVelLabel.setText("Ent velocity: " + tester2.getVelocity() + "(" + formatter.format(tester2.getXVel()) + ", " + 
						formatter.format(tester2.getYVel()) + ")"); 
						viewportPos.setText("Testers colliding? Tester-tester2: " + tester2.isColliding(tester) + "Tester-tester3: " + tester2.isColliding(tester3));
						lblCorners.setText(
								"Upper Left: (" + (leftX*cos - topY*sin) + ", " + (leftX*sin + topY*cos) + ") " +
								"Upper Right: (" + (rightX*cos - topY*sin) + ", " + (rightX*sin + topY*cos) + ") " +
								"Lower Left: (" + (tester2.getXPosWorld() - tester2.getRotatedSpriteWidth()/2) + ", " + (tester2.getYPosWorld() + (tester2.getRotatedSpriteHeight()/2)) + ") " +
								"Lower Right: (" + (tester2.getXPosWorld() + tester2.getRotatedSpriteWidth()/2) + ", " + (tester2.getYPosWorld() + (tester2.getRotatedSpriteHeight()/2)) + ")");
						
					try {
						sleep(20);
					}
					catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		testThread.start();
		gameFramer.startRender();  
		
		
	}
	

	private class invisibleRender extends RenderObj {
		
		public invisibleRender(double xWorldPos, double yWorldPos, int x, int y) {
			setSpriteSheet((BufferedImage)null, 1, 1, x, y);	 
			setXPosWorld(xWorldPos);
			setYPosWorld(yWorldPos);
			setZPos(0);
		}
		
		
	
	}

	//testRender is special- it niavely assumes it has been given a one image sprite sheet. 
	public class testRender extends RenderObj {
		private double xRate;
		private double yRate;
		private long frameDelay;
		public testRender(String fileName, int sheetRows, int sheetCols, int x, int y, int z) throws IOException {
			BufferedImage buf = ImageIO.read(this.getClass().getResourceAsStream(fileName));
		 
			frameDelay = 250; //Number of ms
			int newWidth = buf.getWidth(); 
			int newHeight = buf.getHeight(); 
			
			/*if(buf.getWidth() > 256 || buf.getHeight() > 256) {
				
 
				int div; 
				if(newWidth > newHeight) {
					div = (int)Math.floor(newWidth/256);
				}
				else {
					div = (int)Math.floor(newHeight/256);
				}
				System.out.println("Div is: " + div);
				newWidth /= div;
				newHeight /= div;
				
				
				System.out.println("NewWidth is : " + newWidth + " and newHeight is: " + newHeight);
				BufferedImage newBuf = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = newBuf.createGraphics();
				int test = 2; 
				
				g2d.drawImage(buf.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_DEFAULT), null, null);
				g2d.dispose();
				buf = newBuf;
					
			
			} 
			*/
			setSpriteSheet(buf, sheetRows, sheetCols, (int)Math.floor(buf.getWidth()/sheetCols), (int)Math.floor(buf.getHeight()/sheetRows));
			
			setXPosWorld(x);
			setYPosWorld(y);
			setZPos(z);
			yRate = Math.random() * 5.0 - 5.0;
			
			xRate = Math.random() * 5.0;
			setAngle(0);
		}
		
		
		public String getVelocity() {
			String heading = "No motion"; 
			double velocity = Math.sqrt(Math.pow(xRate, 2) + Math.pow(yRate, 2));
			double direction = 0; //Position in degrees. 
			if(xRate != 0 || yRate != 0) {
				//If xRate AND yRate are less than zero or xRate AND yRate are greater than zero, take their absolute  
				if(xRate < 0 && yRate < 0 || xRate > 0 && yRate > 0) {
					direction = Math.toDegrees(Math.asin(Math.abs(yRate/velocity)));
					if(xRate < 0) {
						direction += 180;
					}
				}
				else if(xRate < 0) {
					direction = Math.toDegrees(Math.acos(xRate/velocity));
				}
				else {
					direction = Math.toDegrees(Math.asin(yRate/velocity));
					direction = 360 + direction;
				}
				DecimalFormat formatter = new DecimalFormat("#.##");
				heading = formatter.format(velocity) + " at " + formatter.format(direction) + " °";
			}
			return heading;
		}
		
		
		
		public double getXVel() {
			return xRate;
		}
		public double getYVel() {
			return yRate;
		}
		
		
		
		public int animate(int timeSinceFrame) {
			int newTime = (int)(System.nanoTime()/1000000);
				if(newTime - timeSinceFrame > frameDelay) 
					cycleAnimation();
				else 
					newTime = timeSinceFrame;
			return newTime;
		}
		
		
		
		
		
		
		public void move(int x1, int y1, int x2, int y2) {
			
			
			setXPosWorld(getXPosWorld() + xRate);
			setYPosWorld(getYPosWorld() + yRate); 
			
			if(getXPosWorld() + getRotatedSpriteWidth() > x2 || getXPosWorld() < x1) {
				xRate *= -1.0;
				if(Math.random() > .5)
					xRate += Math.random() * 2 - 2;
				else 
					xRate -= Math.random() * 2 - 2;
				if(getXPosWorld() + getSpriteWidth() > x2) {
					setXPosWorld(x2 - getSpriteWidth());
				}
				 
			}
			
			if(getYPosWorld() + getRotatedSpriteHeight() > y2 || getYPosWorld() < y1) {
				yRate *= -1.0;
				if(Math.random() > .5)
					yRate += Math.random() * 2 - 2;
				else 
					yRate -= Math.random() * 2 - 2;
				if(getYPosWorld() + getRotatedSpriteHeight() > y2) {
					setYPosWorld(y2 - getRotatedSpriteHeight());
				}
			 
			}
		}
	}
}



