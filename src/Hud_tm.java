import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.JButton;

import java.awt.GridLayout;

import javax.swing.SwingConstants;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Component;

import javax.swing.Box;
import javax.swing.JScrollPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.text.DecimalFormat; 


@SuppressWarnings("serial")
public class HUD_tm extends JPanel {

	private final double ASPECT_RATIO = 168.0/720.0;
	private int hudHeight;
	private int hudWidth;
	DecimalFormat goldFormatter;
	private JLabel playerGoldAmt; 
	private Main_tm gameRef;
	private JButton trapA, trapB, trapC, trapD, btnSell, btnRepair;
	private boolean buttonsActive;
	private int trapACost, trapBCost, trapCCost, trapDCost;
	
	/**
	 * Create the panel.
	 */
	public HUD_tm(Main_tm ref, int screenWidth, int screenHeight) {
		
		buttonsActive = true;
		gameRef = ref;
	
		goldFormatter = new DecimalFormat("00000");
		setBackground(SystemColor.activeCaption);
		
		hudHeight = 1080;
		
		hudHeight = screenHeight;
		hudWidth = (int)(hudHeight * ASPECT_RATIO);
		
		Dimension dim = new Dimension(hudWidth, hudHeight);
		
		setSize(dim);
		setPreferredSize(dim);
		
		 
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{26, 100, 26};
		gridBagLayout.rowHeights = new int[]{80, 100, 500, 100, 125, 175};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, 1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, 1.0, 1.0, 0.0};
		setLayout(gridBagLayout);
		
		Component horizontalStrut = Box.createHorizontalStrut(26);
		GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
		gbc_horizontalStrut.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut.gridx = 0;
		gbc_horizontalStrut.gridy = 0;
		add(horizontalStrut, gbc_horizontalStrut);
		
		playerGoldAmt = new JLabel("00000");
		playerGoldAmt.setHorizontalAlignment(SwingConstants.CENTER);
		playerGoldAmt.setFont(new Font("Tahoma", Font.PLAIN, 48));
		GridBagConstraints gbc_playerGoldAmt = new GridBagConstraints();
		gbc_playerGoldAmt.fill = GridBagConstraints.BOTH;
		gbc_playerGoldAmt.insets = new Insets(0, 0, 5, 5);
		gbc_playerGoldAmt.gridx = 1;
		gbc_playerGoldAmt.gridy = 0;
		add(playerGoldAmt, gbc_playerGoldAmt);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_1 = new GridBagConstraints();
		gbc_horizontalStrut_1.insets = new Insets(0, 0, 5, 0);
		gbc_horizontalStrut_1.gridx = 2;
		gbc_horizontalStrut_1.gridy = 0;
		add(horizontalStrut_1, gbc_horizontalStrut_1);
		
		JPanel trapMenuPanel = new JPanel();
		trapMenuPanel.setBackground(SystemColor.windowBorder);
		GridBagConstraints gbc_trapMenuPanel = new GridBagConstraints();
		gbc_trapMenuPanel.insets = new Insets(0, 0, 5, 5);
		gbc_trapMenuPanel.fill = GridBagConstraints.BOTH;
		gbc_trapMenuPanel.gridx = 1;
		gbc_trapMenuPanel.gridy = 2;
		add(trapMenuPanel, gbc_trapMenuPanel);
		
		Dimension buttonSize = new Dimension(125, 125);
		GridBagLayout gbl_trapMenuPanel = new GridBagLayout();
		gbl_trapMenuPanel.columnWidths = new int[]{1, 125, 125, 1};
		gbl_trapMenuPanel.rowHeights = new int[]{1, 125, 125, 10, 1};
		gbl_trapMenuPanel.columnWeights = new double[]{1.0, 1.0, 1.0, 1.0};
		gbl_trapMenuPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0};
		trapMenuPanel.setLayout(gbl_trapMenuPanel);
		
		trapACost = 500;
		
		trapA = new JButton("<html><center>Ballista <br/>($" + trapACost + ")</center></html>");
		trapA.setBackground(new Color(169, 169, 169));
		trapA.setSize(buttonSize);
		trapA.setPreferredSize(buttonSize);
		trapA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				/**
				 * This will be an actual thing some day, don't worry about it
				 */
				Trap_Ballista nuBallista = new Trap_Ballista(gameRef.getGameBoundary() - 100, gameRef.getGameBoundary() - 100, 0);
				spawnObj(nuBallista, nuBallista.getTr_cost());
			}
		});
		
		GridBagConstraints gbc_trapA = new GridBagConstraints();
		gbc_trapA.fill = GridBagConstraints.BOTH;
		gbc_trapA.insets = new Insets(0, 0, 5, 5);
		gbc_trapA.gridx = 1;
		gbc_trapA.gridy = 1;
		trapMenuPanel.add(trapA, gbc_trapA);
		
		trapBCost = 850;
		
		trapB = new JButton("<html><center>Wind Trap <br/>($" + trapBCost + ")</center></html>");
		trapB.setBackground(new Color(245, 245, 245));
		trapB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		
		GridBagConstraints gbc_trapB = new GridBagConstraints();
		gbc_trapB.fill = GridBagConstraints.BOTH;
		gbc_trapB.insets = new Insets(0, 0, 5, 0);
		gbc_trapB.gridx = 2;
		gbc_trapB.gridy = 1;
		trapMenuPanel.add(trapB, gbc_trapB);
		
		trapCCost = 1500;
		
		trapC = new JButton("<html><center>Flamethrower <br/>($" + trapCCost + ")</center></html>");
		trapC.setBackground(new Color(255, 127, 80));
		trapC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		GridBagConstraints gbc_trapC = new GridBagConstraints();
		gbc_trapC.fill = GridBagConstraints.BOTH;
		gbc_trapC.insets = new Insets(0, 0, 0, 5);
		gbc_trapC.gridx = 1;
		gbc_trapC.gridy = 2;
		trapMenuPanel.add(trapC, gbc_trapC);
		
		trapDCost = 2000;
		
		
		trapD = new JButton("<html><center>Freeze Rune <br/>($" + trapDCost + ")</center></html>");
		trapD.setBackground(new Color(135, 206, 235));
		trapD.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		GridBagConstraints gbc_trapD = new GridBagConstraints();
		gbc_trapD.fill = GridBagConstraints.BOTH;
		gbc_trapD.gridx = 2;
		gbc_trapD.gridy = 2;
		trapMenuPanel.add(trapD, gbc_trapD);
		
		
		
		JPanel sellRepairButtonPanel = new JPanel();
		GridBagConstraints gbc_sellRepairButtonPanel = new GridBagConstraints();
		gbc_sellRepairButtonPanel.insets = new Insets(0, 0, 5, 5);
		gbc_sellRepairButtonPanel.fill = GridBagConstraints.BOTH;
		gbc_sellRepairButtonPanel.gridx = 1;
		gbc_sellRepairButtonPanel.gridy = 4;
		add(sellRepairButtonPanel, gbc_sellRepairButtonPanel);
		
		btnSell = new JButton("Sell");
		btnSell.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				gameRef.setMode(Main_tm.SELL_MODE);
			}
		});
		btnSell.setBackground(new Color(50, 205, 50));
		sellRepairButtonPanel.add(btnSell);
		
		 
		
		btnRepair = new JButton("Repair");
		btnRepair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				gameRef.setMode(Main_tm.REPAIR_MODE);
			}
		});
		btnRepair.setBackground(new Color(255, 255, 0));
		sellRepairButtonPanel.add(btnRepair);

	}

	public Dimension getPreferredSize() {
		return new Dimension(hudWidth, hudHeight);
	}
	
	/**
	 * Sets the gold counter to newAmt
	 * @param newAmt The amount of gold displayed on the gold counter
	 */
	public void setGold(int newAmt) {
		playerGoldAmt.setText(goldFormatter.format(newAmt));
	}
	
	/**
	 * Spawns an object in the usual button-spawning fashion- i.e., green if it can be placed in the indicated place, and red otherwise.
	 * @param obj The object to be placed
	 * @param cost The amount of gold to be taken 
	 */
	public void spawnObj(RenderObj obj, int cost) {
		gameRef.makePurchase(obj, Main_tm.TRAP, cost);
	}
	
	/**
	 * Turns all the buttons on or off- when off, they cannot be clicked. 
	 */
	public void toggleButtons() {
		buttonsActive = !buttonsActive; 
		trapA.setEnabled(buttonsActive);
		trapB.setEnabled(buttonsActive);
		trapC.setEnabled(buttonsActive);
		trapD.setEnabled(buttonsActive);
		btnRepair.setEnabled(buttonsActive);
		btnSell.setEnabled(buttonsActive);
	}
	
}
