import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;


/**
 * RenderObj is the superclass to all the visible objects in game. 
 * This abstract class makes a few fundamental assumptions: 
 * - That an object uses a sprite sheet for its graphics 
 * - That all sprites in a sprite sheet are the same size 
 * - That successive sprites in an animation are ordered left to right, top to bottom
 * Render objects are also designed to work with two coordinate systems- the RenderEngine system,
 * And the world system. The RenderEngine system is the Java standard, while the world system places
 * Its origin at its center. RenderObj keeps track of its position in the world space. It 
 * may seamlessly transition from worldspace to viewport space using the renderXPos and renderYPos
 * @author Bb Peterson
 *
 */
public abstract class RenderObj {
	private String objName = "Render Obj"; 
	private BufferedImage spriteSheet; //The spriteSheet of an object- i.e., all states of its animation.  
	private BufferedImage currSprite; //The currently displayed sprite of an object, including its rotation.  
	private int spriteSheetCols; //The number of columns on the sprite sheet
	private int spriteSheetRows; //The number of rows on the sprite sheet 
	
	private int currSpriteCol; //The column currSprite is from on spriteSheet- goes from 0 to (spriteSheetCols-1)
	private int currSpriteRow; //The row currSprite is from on spriteSheet- goes from 0 to (spriteSheetRows-1)
	private int currSpriteHeight; //The (y) height of each sprite with respect to its angle 
	private int currSpriteWidth;  //The (x) height of the current sprite with respect to its angle
	
	private int modelSpriteHeight; //The (y) height of each sprite on the sprite sheet
	private int modelSpriteWidth; //The (x) width of each sprite on the sprite sheet
	
	private double xPosWorld; //The horizontal coordinate of the renderObj center
	private double yPosWorld; //The vertical coordinate of the renderObj center
	
	private double xPos = 0; //The horizontal (x) position of the object 
	private double yPos = 0; //The vertical (y) position of the object 
	private int zPos = 0; //The "depth" (z?) position of the object; used to determine the draw order
	private double angle; //The angle of the sprite- can be any number between 0 and 360 degrees
	private boolean noFilterYet = true;
	private boolean doFilter;
	private Color filterColor; 
	private int filterOpacity = 100;
	
	 
	
	
	
	/**
	 * Returns the sprite sheet of the renderObj- protected function, potential use for a debugging method.
	 * @return The sprite sheet, which holds all frames of animation/states for renderObj
	 */
	protected BufferedImage getSpriteSheet() {
		return spriteSheet;
	}
	

	/**
	 * Changes the sprite sheet to newSS, which has numRows rows- each row being spriteHeight tall,
	 * and numCols columns- each column being spriteWidth wide
	 * @param newSS The new sprite sheet
	 * @param numRows The number of rows in the new sprite sheet
	 * @param numCols The number of columns in the new sprite sheet
	 * @param spriteWidth The width of sprites on the new sprite sheet
	 * @param spriteHeight The height of sprites on the new sprite sheet
	 */
	protected void setSpriteSheet(BufferedImage newSS, int numRows, int numCols, int spriteWidth, int spriteHeight) {
		
		spriteSheet = newSS;
		spriteSheetRows = numRows;
		spriteSheetCols = numCols;
		modelSpriteWidth = spriteWidth;
		modelSpriteHeight = spriteHeight;
		currSpriteWidth = spriteWidth;
		currSpriteHeight = spriteHeight;	
		currSpriteRow = 0;
		currSpriteCol = 0;
		setCurrSprite(0, 0);
		if(angle > 0)
			rotateCurrSprite();
	}
	/**
	 * Sets the sprite sheet to a bufferedImage defined by newSS, which has numRows rows- 
	 */
	/**
	 * Changes the sprite sheet to newSS, which has numRows rows- each row being spriteHeight tall,
	 * and numCols columns- each column being spriteWidth wide
	 * @param newSS The new sprite sheet
	 * @param numRows The number of rows in the new sprite sheet
	 * @param numCols The number of columns in the new sprite sheet
	 * @param spriteWidth The width of sprites on the new sprite sheet
	 * @param spriteHeight The height of sprites on the new sprite sheet
	 */
	protected void setSpriteSheet(String newSS, int numRows, int numCols, int spriteWidth, int spriteHeight) throws IOException {
		
		spriteSheet = ImageIO.read(this.getClass().getResourceAsStream(newSS));
		spriteSheetRows = numRows;
		spriteSheetCols = numCols;
		modelSpriteWidth = spriteWidth;
		modelSpriteHeight = spriteHeight;
		currSpriteWidth = spriteWidth;
		currSpriteHeight = spriteHeight;	
		currSpriteRow = 0;
		currSpriteCol = 0;
		setCurrSprite(0, 0);
		if(angle > 0)
			rotateCurrSprite();
	}
	
	/**
	 * Changes the sprite sheet to newSS- it only has one sprite in it, and its width/height is the width/height of newSS
	 * If you wish to use a null sprite sheet, do not use this method- it will not work. 
	 * @param newSS The new sprite sheet
	 */
	protected void setSpriteSheet(BufferedImage newSS) {
		setSpriteSheet(newSS, 1, 1, newSS.getWidth(), newSS.getHeight());
	}
	
	
	
	/**
	 * Iterates from one frame of animation to the next, going left to right, top to bottom. 
	 * @return 1 on a normal cycle, 2 if it has cycled to the last sprite in a row, 
	 * and 3 if it has cycled to the last sprite in a sheet
	 */
	public int  cycleAnimation() {
		int ret = 1;
		currSpriteCol++;
		//If currSpriteCol = spriteSheetCols, then the end of a row has been reached
		if(currSpriteCol == spriteSheetCols) {
			currSpriteCol = 0;
			ret = 2;
			currSpriteRow++;
			//If currSpriteRow = spriteSheetCols, then the end of the sheet has been reached
			if(currSpriteRow == spriteSheetRows) {
				currSpriteRow = 0;
				ret = 3;
			}
		}
		setCurrSprite(currSpriteRow, currSpriteCol);
		
		return ret;
	}
	

	/**
	 * An alternative method of animation, where it cycles the animation across a single row. 
	 * Returns 1 if the animation proceeds as normal, 2 if the animation has swapped rows, and
	 * 3 if the animation has reached the end and restarted
	 * @param row The row of the sprite sheet on which to animate
	 * @param col The column of the sprite sheet to start the animation on 
	 */
	public int cycleAnimation(int row, int col) {
		int ret = 1;
		
			if(currSpriteRow != row) {
				currSpriteRow = row;
				currSpriteCol = col;
				ret = 2;
			}
			else {
				currSpriteCol++;
				//If currSpriteCol = spriteSheetCols, then the end of a row has been reached
				if(currSpriteCol == spriteSheetCols) {
					currSpriteCol = 0;
					ret = 3;
				}
			}
			setCurrSprite(currSpriteRow, currSpriteCol);
		
		return ret;
	}
	
	/**
	 * An alternative method of animation, where it cycles the animation across a single row.
	 * Returns 1 if the animation proceeds as normal, 2 if the animation swapped rows first,
	 * and 3 if the animation has reached the end and restarted
	 * @param row
	 * @return
	 */
	public int cycleAnimation(int row) {
		return cycleAnimation(row, 0);
	}
	
	/**
	 * Returns the current sprite.
	 * @return The currently displayed sprite of an object, possibly rotated from its normal 
	 */
	public BufferedImage getCurrSprite() {
		return currSprite;
	}
	
	/**
	 * Sets the current sprite to row and col (also changes currSpriteRow and currSpriteCol)
	 * @param row
	 * @param col
	 */
	public void setCurrSprite(int row, int col) {
		if(row > spriteSheetRows || row < 0 || col > spriteSheetCols || col < 0) {
			System.out.println("INVALID SPRITE INPUT, NO CHANGE MADE");
		}
		else {
			rotateCurrSprite();
		}
	}
	
	/**
	 * Returns a boundary polygon for local
	 * @param local The RenderObj which this method translates into a polygon
	 */
	private static CollideEntry getObjBounds(RenderObj local) {
		CollideEntry result = null;
		if(local != null) {
			double localRightX = (local.getXPosWorld() + local.getSpriteWidth()/2); 
			double localLeftX = (local.getXPosWorld() - local.getSpriteWidth()/2);
			double localBottomY = (local.getYPosWorld() + local.getSpriteHeight()/2);
			double localTopY = (local.getYPosWorld() - local.getSpriteHeight()/2);
			
			double localAngle = Math.toRadians(local.getAngle());
			double localSin = Math.sin(localAngle);
			double localCos = Math.cos(localAngle);
			
			double[][] localCoords = new double[4][2];
			
			//TopLeft
			localCoords[0][0] = localLeftX*localCos - localTopY*localSin;
			localCoords[0][1] = localLeftX*localSin + localTopY*localCos;
			//TopRight
			localCoords[1][0] = localRightX*localCos - localTopY*localSin;
			localCoords[1][1] = localRightX*localSin + localTopY*localCos;
			//BottomRight
			localCoords[2][0] = localRightX*localCos - localBottomY*localSin;
			localCoords[2][1] = localRightX*localSin + localBottomY*localCos;
			//BottomLeft
			localCoords[3][0] = localLeftX*localCos - localBottomY*localSin;
			localCoords[3][1] = localLeftX*localSin + localBottomY*localCos;
			
			int xPolyArray[] = {(int)localCoords[0][0], (int)localCoords[1][0], (int) localCoords[2][0], (int)localCoords[3][0]};
			int yPolyArray[] = {(int)localCoords[0][1], (int)localCoords[1][1], (int) localCoords[2][1], (int) localCoords[3][1]};
			
			xPolyArray[0] = (int)localCoords[0][0];
			xPolyArray[1] = (int)localCoords[1][0];
			xPolyArray[2] = (int)localCoords[2][0];
			xPolyArray[3] = (int)localCoords[3][0];
			
			yPolyArray[0] = (int)localCoords[0][1];
			yPolyArray[1] = (int)localCoords[1][1];
			yPolyArray[2] = (int)localCoords[2][1];
			yPolyArray[3] = (int)localCoords[3][1];
		
			
			result = new CollideEntry(new Polygon(xPolyArray, yPolyArray, 4), localCoords);
		}
		return result;
	}
 
	/**
	 * Tests to see if a point, (x,y), is inside of the shape formed by local
	 * @param local The shape whose bounds shall be defined
	 * @param x The x coordinate to be tested
	 * @param y The y coordinate to be tested
	 * @return
	 */
	public boolean contains(double x, double y) { 
		CollideEntry objBounds = getObjBounds(this);
		if(this != null) {
			System.out.println("Does objBounds contain (" + x + ", " + y + ")? " + objBounds.getShape().contains(x, y));
			return objBounds.getShape().contains(x, y);
		}
		return false;
	}
	
	/**
	 * Returns whether or not one object, local, is colliding with another, other. 
	 * @param local The first object possibly colliding
	 * @param other The second object possibly colliding
	 * @return True if the two objects are colliding, false otherwise
	 */
	
	public static boolean isColliding(RenderObj local, RenderObj other) {
		
		double centerDistances = Math.pow(local.getXPosWorld() - other.getXPosWorld(), 2);
		centerDistances += Math.pow(local.getYPosWorld() - other.getYPosWorld(), 2);
		centerDistances = Math.sqrt(centerDistances);
		
		double localExtent = Math.pow(local.getSpriteHeight()/2, 2);
		localExtent += Math.pow(local.getSpriteWidth()/2, 2);
		localExtent = Math.sqrt(localExtent);
		
		double otherExtent = Math.pow(other.getSpriteHeight()/2,  2);
		otherExtent += Math.pow(other.getSpriteWidth()/2, 2);
		otherExtent = Math.sqrt(otherExtent);
		
		if(centerDistances > localExtent + otherExtent)
			return false;
		
		CollideEntry localHolder = getObjBounds(local);
		CollideEntry otherHolder = getObjBounds(other);
		
 
		
		if(otherHolder.getShape().contains(local.getXPosWorld(), local.getYPosWorld()))
			return true;
		if(otherHolder.getShape().contains(localHolder.getCoords()[0][0], localHolder.getCoords()[0][1])) 
			return true;
		if(otherHolder.getShape().contains(localHolder.getCoords()[1][0], localHolder.getCoords()[1][1]))
			return true;
		if(otherHolder.getShape().contains(localHolder.getCoords()[2][0], localHolder.getCoords()[2][1]))
			return true;
		if(otherHolder.getShape().contains(localHolder.getCoords()[3][0], localHolder.getCoords()[3][1]))
			return true;
 
		if(localHolder.getShape().contains(other.getXPosWorld(), other.getYPosWorld()))
			return true;
		if(localHolder.getShape().contains(otherHolder.getCoords()[0][0], otherHolder.getCoords()[0][1])) 
			return true;
		if(localHolder.getShape().contains(otherHolder.getCoords()[1][0], otherHolder.getCoords()[1][1]))
			return true;
		if(localHolder.getShape().contains(otherHolder.getCoords()[2][0], otherHolder.getCoords()[2][1]))
			return true;
		if(localHolder.getShape().contains(otherHolder.getCoords()[3][0], otherHolder.getCoords()[3][1]))
			return true;


		
		int i, j, k, m;
		for(i = 0; i < 4; i++) {
			if(i == 3) 
				j = 0;
			else 
				j = i+1;
			for(k = 0; k < 4; k++) {
				if(k == 3) 
					m = 0;
				else 
					m = k+1;		
				if(Line2D.linesIntersect(localHolder.getCoords()[i][0], localHolder.getCoords()[i][1], 
										 localHolder.getCoords()[j][0], localHolder.getCoords()[j][1], 
										 otherHolder.getCoords()[k][0], otherHolder.getCoords()[k][1], 
										 otherHolder.getCoords()[m][0], otherHolder.getCoords()[m][1])) { 
					return true;
					
				}
				
			}
		}
		
		
		
		
		return false;
	}
	
	
	/**
	 * Returns whether or not this object is colliding with other. 
	 * @return True is other is colliding with this object, false otherwise
	 */
	public boolean isColliding(RenderObj other) {
		return isColliding(this, other);
	}
	
	/**
	 * Returns the number of columns on the current sprite sheet
	 * @return The number of columns on the sprite sheet
	 */
	protected int getSpriteSheetCols() {
		return spriteSheetCols;
	}

	/**
	 * Returns the number of rows on the current sprite sheet
	 * @return The number of rows on the current sprite sheet
	 */
	protected int getSpriteSheetRows() {
		return spriteSheetRows;
	}
 
	
	/**
	 * Returns the column of the current sprite 
	 * @return The number of the current sprite, between zero and one less spriteSheetCols
	 */
	protected int getCurrSpriteCol() {
		return currSpriteCol;
	}

	/**
	 * Sets the column of the current sprite. Could be useful to 
	 * Swap between rows, if you made each row correspond to one
	 * animation
	 * @param currSpriteCol The new column of the current sprite
	 */
	protected void setCurrSpriteCol(int newSpriteCol) {
		currSpriteCol = newSpriteCol;
		setCurrSprite(currSpriteRow, currSpriteCol);
	}
	
	/**
	 * Gets the row of the current sprite
	 * @return The numeric value of the current sprite row, between zero and one less spriteSheetRows
	 */
	protected int getCurrSpriteRow() {
		return currSpriteRow;
	}
	
	/**
	 * Sets the row of the current sprite to newSpriteRow. Could be
	 * useful to skip frames of an animation
	 * @param newSpriteRow The new row sprite 
	 */
	protected void setCurrSpriteRow(int newSpriteRow) {
		currSpriteRow = newSpriteRow;
		setCurrSprite(currSpriteRow, currSpriteCol);
	}
	

	/**
	 * Rotates the sprite by degree degrees from its normal position
	 * @param degree The number of degrees that the sprite is to be rotated
	 */
	protected void rotateCurrSprite(double degree) {
		BufferedImage modelSprite = getModelSprite();
		BufferedImage filter = null;
		degree = degree % 360;
		if(degree < 0) 
			degree = 360 + degree;
		angle = degree;
	
		
		
		double radsAngle = Math.toRadians(degree);
		double sin = Math.abs(Math.sin(radsAngle));
		double cos = Math.abs(Math.cos(radsAngle));
		int w = modelSpriteWidth;
		int h = modelSpriteHeight; 
		int newWidth = (int)Math.floor(w*cos + h*sin);
		int newHeight = (int)Math.floor(h*cos + w*sin);
	//	System.out.println("Sin is: " + sin + " and cos is: " + cos); 
		
		BufferedImage rotatedSprite = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB); 
		Graphics2D g2D = rotatedSprite.createGraphics(); 
		AffineTransform rotator = new AffineTransform();
	
		rotator.translate((newWidth - w)/2, (newHeight-h)/2);
		rotator.rotate(radsAngle, w/2, h/2);
		g2D.setTransform(rotator);
		g2D.drawImage(modelSprite, 0, 0, null);
		if(doFilter == true) {
			filter = new BufferedImage(getSpriteWidth(), getSpriteHeight(), BufferedImage.TYPE_INT_ARGB); 
			Graphics2D model2D = filter.createGraphics();
			model2D.setColor(filterColor);
			model2D.fillRect(0,  0, getSpriteWidth(), getSpriteHeight());
			model2D.dispose();
			g2D.drawImage(filter, 0,  0, null);
		} 
	
		
		g2D.dispose();

		currSpriteWidth = (int) newWidth;
		currSpriteHeight = (int) newHeight; 
		currSprite = rotatedSprite;
		
 
		 
	}
	
	/**
	 * Rotates the sprite by however many degrees its angle is set to. Sort of a "reset" function.
	 */
	protected void rotateCurrSprite() {
		rotateCurrSprite(angle);
	}

	/**
	 * GetModelSprite returns an unrotated version of the current sprite
	 * @return The model sprite, an unrotated version of the current sprite
	 */
	private BufferedImage getModelSprite() {
			if(spriteSheet != null) {
				int spriteSheetY = modelSpriteHeight * currSpriteRow;
				int spriteSheetX = modelSpriteWidth * currSpriteCol;
				return spriteSheet.getSubimage(spriteSheetX, spriteSheetY, modelSpriteWidth, modelSpriteHeight);
			}
			else {
				return generateModelSprite();
			}
		}
 
	
	/**
	 * Use this if you are working without a sprite sheet. As the name implies, it generates a new sprite as a red rectangle 
	 * @return A new sprite with a rectangle about its edge
	 */
	private BufferedImage generateModelSprite() {
		BufferedImage newSprite = new BufferedImage(modelSpriteWidth, modelSpriteHeight, BufferedImage.TYPE_INT_ARGB);
	 	Graphics2D g2D = newSprite.createGraphics(); 
		g2D.setColor(Color.RED);
		g2D.drawRect(0, 0, modelSpriteWidth - 1, modelSpriteHeight - 1);
		return newSprite;
	}

 
	/**
	 * Adds a filter of color col at opacity op to the sprite
	 * @param col The color of the sprite
	 * @param op The opacity of the sprite
	 */
	public void addFilter(Color col, int op) {
		if(filterColor != col || filterOpacity != op) {
			doFilter = true; 
			filterColor = new Color(col.getRed(), col.getGreen(), col.getBlue(), op);
			filterOpacity = op;
			rotateCurrSprite();
		}
	}
	
	/**
	 * Adds a filter of color col at the current filterOpacity to the sprite
	 * @param col The color of the sprite filter
	 */
	public void addFilter(Color col) {
		addFilter(col, filterOpacity);
	}
	
	
	/**
	 * Removes the filter from the sprite 
	 */
	public void removeFilter() {
		doFilter = false;
		rotateCurrSprite();
	}
	
	
	
	/**
	 * Returns the ideal, unrotated height of the sprite
	 * @return The sprite's height
	 */
	public int getSpriteHeight() {
		return modelSpriteHeight;
	}

	/**
	 * Returns the ideal, unrotated width of the sprite
	 * @return The sprite's width
	 */
	public int getSpriteWidth() {
		return modelSpriteWidth;
	}
	
	/**
	 * Returns the 'actual' height of the sprite, rotation included
	 * @return The sprite's height including its bounding box
	 */
	public int getRotatedSpriteHeight() {
		return currSpriteHeight;
	}

	/**
	 * Returns the 'actual' width of the sprite, rotation included
	 * @return The sprite's width, including its bounding box
	 */
	public int getRotatedSpriteWidth() {
		return currSpriteWidth;
	}

	/**
	 * Due to the way RenderEngine works, rotation needs to be accounted for 
	 * By moving the sprite by (-spriteWidth/2)^2+(-spriteHeight/2)^2 to make sure
	 * Rotations actually look the way they ought to 
	 * @return The 'apparent' position of the sprite along the x axis 
	 * @deprecated use {link {@link #getXPosRender(double)}
	 */
	public double getXPosRender() {
		double xPosRotated = xPos;
		xPosRotated -= currSprite.getWidth();
		xPosRotated /= 2; 
		return xPosRotated;
	}
	
	/**
	 * Due to the way RenderEngine works, rotation needs to be accounted for 
	 * By moving the sprite by (-spriteWidth/2)^2+(-spriteHeight/2)^2 to make sure
	 * Rotations actually look the way they ought to. Also, note that you shouldn't use this 
	 * if you aren't working on the renderer.
	 * @return The 'apparent' position of the sprite along the x axis 
	 */
	public double getXPosRender(double viewportXPos) {
		double xPosRotated = xPosWorld - viewportXPos;  
		xPosRotated -= currSprite.getWidth()/2;
		//xPosRotated /= 2; 
		return xPosRotated;
	}
	
	
	
	/**
	 * Due to the way RenderEngine works, rotation needs to be accounted for 
	 * By moving the sprite by (-spriteWidth/2)^2+(-spriteHeight/2)^2 to make sure
	 * Rotations actually look the way they ought to 
	 * @return The 'apparent' position of the sprite along the y axis 
	 * @deprecated use {link #getYPosRender(double)} instead.
	 */
	public double getYPosRender() {
		double yPosRotated = yPos;
		yPosRotated -= currSprite.getHeight()/2;
		yPosRotated /= 2; 
		return yPosRotated;
	}
	
	/**
	 * Due to the way RenderEngine works, rotation needs to be accounted for 
	 * By moving the sprite by (-spriteWidth/2)^2+(-spriteHeight/2)^2 to make sure
	 * Rotations actually look the way they ought to. Also, note that you shouldn't
	 * use this if you aren't working on the renderer.
	 * @return The 'apparent' position of the sprite along the y axis 
	 */
	public double getYPosRender(double viewportYPos) {
		double yPosRotated = yPosWorld - viewportYPos;  

		yPosRotated -= currSprite.getHeight()/2;
		return yPosRotated;
	}
	
	
	/**
	 * Returns the horizontal world position of RenderObj's upper left hand corner
	 * @return The x position of the RenderObj's upper left hand corner
	 */
	public double getXPosWorld() {
		return xPosWorld;
	}
	
	
	/**
	 * Sets the horizontal world position of RenderObj's upper left hand corner
	 * @param The new x position of renderObj's upper left hand corner
	 */
	public void setXPosWorld(double newXPosWorld) {
		xPosWorld = newXPosWorld;
	}
	
	
	/**
	 * Returns the vertical world position of RenderObj's upper left hand corner
	 * @return The y position of the RenderObj's upper left hand corner
	 */
	public double getYPosWorld() {
		return yPosWorld;
	}
	
	/**
	 * Sets the vertical world position of RenderObj's upper left hand corner
	 * @param The new y position of renderObj's upper left hand corner
	 */
	public void setYPosWorld(double newYPosWorld) {
		yPosWorld = newYPosWorld;
	}
	
	
	
	/**
	 * Returns the horizontal position of sprite's upper left hand corner
	 * @return The x position of the sprite's center
	 */
	public double getPosX() {
		return getXPosWorld();
	}

	/**
	 * Changes the horizontal position of sprite's upper left hand corner 
	 * @param newXPos The new x position of sprite's center
	 */
	public void setPosX(double newXPos) {
		xPosWorld = newXPos;
	}

	
	
	/**
	 * Returns the vertical position of sprite's upper left hand corner
	 * @return The y position of the sprite's center
	 */
	public double getPosY() {
		return getYPosWorld();
	}

	/**
	 * Changes the vertical position of sprite's upper left hand corner 
	 * @param newXPos The new x position of sprite's upper left hand corner
	 */
	public void setPosY(double newYPos) {
		yPosWorld = newYPos;
	}
	
	/**
	 * Returns the depth position of sprite- determines draw order/hierarchy
	 * @return The sprite's depth position
	 */
	public int getZPos() {
		return zPos;
	}
	
	/**
	 * Sets the depth position of sprite
	 * @param newZPos The new depth of sprite- higher numbers are drawn over lower ones
	 */
	public void setZPos(int newZPos) {
		zPos = newZPos;
	}
	
	/**
	 * Equivalent to getZPos
	 * @return The sprite's depth position
	 */
	 public int getZPosWorld() {
		 return zPos;
	 }
	 /**
	  * Equivalent to setZPos
	  * @param The sprite's new depth position
	  */
	 public void setZPosWorld(int newZPos) {
		 zPos = newZPos;
	 }
	
	/**
	 * Returns the angle of current sprite from the normal, clockwise. 
	 * Goes between 0 and 360 degrees.
	 * @return
	 */
	public double getAngle() {
		return angle;
	}
	
	/**
	 * Sets the angle of the current sprite to degree
	 * @param degree The new angle of the current sprite
	 */
	public void setAngle(double degree) {
		if(degree == 0) {
			resetSprite(); 
		}
		else {
			rotateCurrSprite(degree);
		}
	}
	
	/**
	 * ResetSprite returns the current sprite back to an angle of zero, usually so it can be rotated once more. 
	 */
	protected void resetSprite() {
		setCurrSprite(currSpriteCol, currSpriteRow);
		currSpriteWidth = modelSpriteWidth;
		currSpriteHeight = modelSpriteHeight;
		angle = 0;
	}
	
	/**
	 * Sets the object's name (what is returned by toString) to newName
	 * @param newName The new name of the object
	 */
	public void setObjName(String newName) {
		objName = newName;
	}
	
	/**
	 * Returns the 'name' of the object- usually the name of its class, really.
	 * @return The name of the object.
	 */
	public String getObjName() {
		return objName; 
	}
	
	 
	
	
	
	/**
	 * Returns a string representation of the object's name and world coordinate position
	 */
	public String toString() {
		DecimalFormat formatter = new DecimalFormat("#.###");
		return objName + "(" + formatter.format(getXPosWorld()) + ", " + formatter.format(getYPosWorld()) + ", " + getZPos() + ")";
	}


	/**
	 * CollideEntry is a helper method to return both a polygon, as well as the points which compose it. 
	 * This is a slightly hacky workaround to a problem I was having, don't worry about it. 
	 * @author Bradley 'Bb' Peterson
	 */
	private static class CollideEntry {
		private Polygon shape;
		private double[][] coords;
		
		/**
		 * Creates a new collideEntry of shape sh, xArray x, and yArray y
		 * @param sh The shape of the polygon
		 * @param x Shape's constituent x coordinates
		 * @param y Shape's constituent y coordinates
		 */
		public CollideEntry(Polygon sh, double[][] coo) {
			shape = sh;
			coords = coo;
		}

		/**
		 * Returns shape
		 * @return The polygon collideEntry contains
		 */
		public Polygon getShape() {
			return shape;
		}
		/**
		 * Returns The (x,y) coordinate array for shape
		 * @return The (x,y) coordinate array for shape. As many as it takes.
		 */
		public double[][] getCoords() {
			return coords;
		}
		
		
		
	}
}
