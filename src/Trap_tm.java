import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.ClassLoader;
import java.util.ArrayList;

import javax.imageio.ImageIO;
/**
 * Trap_tm is the superclass for all individual trap classes.
 * It provides the necessary variables for all traps as well as 
 * methods for controlling trap behavior.
 * Extends the RenderObj class.
 * @author Max Martin
 *
 */

public abstract class Trap_tm extends RenderObj{
	
	static int trap_count = 0;
	private int tr_maxHealth;			//Trap maximum health
	private int tr_currentHealth;		//Trap current health
	private int tr_range;				//Trap range
	private int tr_damage;				//Trap damage
	private int tr_cost;				//Trap cost
	private int tr_cooldown;			//Trap cooldown/firerate
	private int timer;					//Used to keep track of cooldown
	private int tr_ID;					//numerical ID of the trap
	private int facing;					//direction trap is facing. 0 = north, 1 = east, 2 = south, 3 = west
	private Monster_tm target;			//The target of the trap
	private ActionBox AOE;		//The tiles covered by this trap.
	
	/**
	 * Abstract constructor. Just to make things easier for the actual trap classes.
	 */
	public Trap_tm(double xPos, double yPos, int facing) {
		this.setXPosWorld(xPos);
		this.setYPosWorld(yPos);
		this.setZPos(2);
		this.timer = this.tr_cooldown;
		this.facing = facing;
		tr_ID = trap_count;
		trap_count++;
	}
	
	
	
	/**
	 * Getters and setters.
	 */
	public int getTr_maxHealth() {
		return tr_maxHealth;
	}
	public void setTr_maxHealth(int tr_maxHealth) {
		this.tr_maxHealth = tr_maxHealth;
	}
	public int getTr_currentHealth() {
		return tr_currentHealth;
	}
	public void setTr_currentHealth(int tr_currentHealth) {
		this.tr_currentHealth = tr_currentHealth;
	}
	public int getTr_range() {
		return tr_range;
	}
	public void setTr_range(int tr_range) {
		this.tr_range = tr_range;
	}
	public int getTr_damage() {
		return tr_damage;
	}
	public void setTr_damage(int tr_damage) {
		this.tr_damage = tr_damage;
	}
	public int getTr_cost() {
		return tr_cost;
	}
	public void setTr_cost(int tr_cost) {
		this.tr_cost = tr_cost;
	}
	public int getTr_cooldown() {
		return tr_cooldown;
	}
	public void setTr_cooldown(int tr_cooldown) {
		this.tr_cooldown = tr_cooldown;
	}
	public int getTr_ID() {
		return tr_ID;
	}
	public int get_facing() {
		return facing;
	}
	public void set_facing(int facing) {
		if (facing >= 0 && facing < 4) {
			this.facing = facing;
		} else {
			this.facing = 0;
			System.out.println("Unexceptable facing received. " + facing);
		}
	}
	public Monster_tm getTarget() {
		return target;
	}
	public void setTarget(Monster_tm target) {
		this.target = target;
	}
	
	/**
	 * Allows a trap to select a monster to begin attacking.
	 * @param monsters The list of spawned monsters from Map_tm
	 * @return The targeted monster
	 */
	public boolean acquireTarget (ActionBox AOE) {
		return false;
	}
	
	/**
	 * Checks if the traps target is still valid. If yes,
	 * triggers an attack from the trap to its target.
	 * If no, acquires a new target.
	 */
	public void tr_attack () {
		if (this.timer == -1) {
			if (acquireTarget(AOE)) {
				//fire at target
				this.timer = tr_cooldown;
				this.setCurrSpriteRow(1);
			} else {
			}
		} else if (this.timer == 0) {
			if (acquireTarget(AOE)) {
				//fire at target
				this.timer = tr_cooldown;
				this.setCurrSpriteRow(1);
			} else {
				this.timer = -1;
			}
		} else if (this.timer == (int)(tr_cooldown / 4)) {
			this.setCurrSpriteRow(0);
			this.timer = this.timer - 1;
		} else {
			this.timer = this.timer - 1;
		}
	}
	
	/**
	 * Called when the trap is destroyed.
	 * Removes the trap from the map.
	 */
	public void tr_destroy() {
		
	}
	
	/**
	 * Called when the trap is sold.
	 * Gives the player treasure equal to cost * (current health / max health).
	 * Returns the gold received as an int.
	 */
	public int tr_sell() {	
		double total = (double)this.tr_cost * ((double)this.tr_currentHealth / (double)this.tr_maxHealth);
		return (int)total;
	}
	
	
}
