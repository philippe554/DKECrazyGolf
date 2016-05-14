package CrazyGolf.Menu;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.sun.awt.AWTUtilities;

import CrazyGolf.Editor.EditorPanel;
import CrazyGolf.Editor.RadioButtons;
import CrazyGolf.Game.FrameGolf;
import CrazyGolf.Game.Player;

public class StartMenu {

	final int  FRAME_WIDTH = 1200 ;
	final int FRAME_HEIGHT = 900 ;

	private JFrame frame;
	private EditorPanel editor;

	private BackgroundPanel main;
	private JPanel startMenu;
	private JPanel playerMenu;
	private JPanel pauseMenu;
	private JPanel editorPanel;
	private JPanel gamePanel;
	private JPanel pausePanel;
	private JPanel backPanel;
	private JPanel backMenu;
	private JPanel levelMenu;

	private JButton button3;

	private JButton button2;
	private JButton button;
	private JButton saveButton;

	private JButton oneP;
	private JButton twoP;
	private JButton	threeP;
	private JButton fourP;

	private JButton level1b;
	private JButton level2b;
	private JButton	level3b;
	private JButton level4b;

	private static JButton resumeb;
	private JButton playp;
	private JButton exitp;
	private JButton backb;
	private JButton backEd;

	private File f1;
	private File f2;
	private File f3;
	private File f4;


	private boolean pauseVisible=false;
	private boolean gameVisible=false;

	private ActionListener listener;

	public static void main(String[] args){
		new StartMenu();

	}

	public StartMenu() {

		f1 = new File("Slot1.txt");
		f2 = new File("Slot2.txt");
		f3 = new File("Slot3.txt");
		f4 = new File("Slot4.txt");

		java.awt.Image background = new ImageIcon("game.jpg").getImage();

		BackgroundPanel main =new BackgroundPanel(background, BackgroundPanel.SCALED, 0.0f,0.0f);
		main.setPreferredSize(new Dimension(FRAME_WIDTH,FRAME_HEIGHT));

		main.addKeyListener(new KeyListener() {


			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
					System.out.println("ESCAPE main");
					if(editorPanel!= null)
						editorPanel.setVisible(false);
					createBackMenu(main);
				}
			}

			public void keyTyped(KeyEvent e) {
			}
			public void keyReleased(KeyEvent arg0) {
			}
		});

		main.setLayout(new BorderLayout());

		class ButtonListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == level1b){
					if(f1.exists() && !f1.isDirectory()) {
					}
//					 editor.writeItDown(editor.getDataForFileWriting(),1);
//					 levelMenu.setVisible(false);
//					createLevelMenu(main);
				}

				if(e.getSource() == level2b){
//		             editor.writeItDown(editor.getDataForFileWriting(),2);
//		             levelMenu.setVisible(false);
//						createLevelMenu(main);
				}
				if(e.getSource() == level3b){
//		            	 editor.writeItDown(editor.getDataForFileWriting(),3);
//		            	 levelMenu.setVisible(false);
//							createLevelMenu(main);
				}
				if(e.getSource() == level4b){
//		            	editor.writeItDown(editor.getDataForFileWriting(),4);
//		            	 levelMenu.setVisible(false);
//							createLevelMenu(main);
				}
				if(e.getSource() == button3 || e.getSource() == exitp){
					System.exit(0);
				}
				if(e.getSource() == button2){
					startMenu.setVisible(false);
					createEditor(main);
				}
				if(e.getSource() ==button){
					if(startMenu.isVisible() == true){
						startMenu.setVisible(false);
					}
					if(pauseVisible == true){
						pauseMenu.setVisible(false);
					}
					createPlayerMenu(main);
				}
				if(e.getSource() == playp){
					startMenu.setVisible(false);
					pauseMenu.setVisible(false);
					if(playerMenu== null)
						createPlayerMenu(main);
					else
						playerMenu.setVisible(true);

				}
				if(e.getSource() ==oneP){
					main.removeAll();
					createGame(main);
				}
				if(e.getSource() ==twoP){
					main.removeAll();
					createGame(main);
				}
				if(e.getSource() ==threeP){
					main.removeAll();
					createGame(main);
				}
				if(e.getSource() ==fourP){
					main.removeAll();
					createGame(main);
				}

				if(e.getSource() == getResumeb()){
					main.remove(pausePanel);
					main.revalidate();
					main.repaint();
					gamePanel.setVisible(true);

				}

				if(e.getSource() == backb){
					if(editorPanel!= null)
						main.remove(editorPanel);

					if(playerMenu!=null)
						playerMenu.setVisible(false);

					if(backPanel!=null)
						main.remove(backPanel);

					if(levelMenu!=null)
						levelMenu.setVisible(false);

					startMenu.setVisible(true);
				}
				if(e.getSource() == backEd){
					if(backPanel!=null)
						main.remove(backPanel);
//						if(levelMenu!=null)
					main.remove(levelMenu);

					editorPanel.setVisible(true);
				}
				if(saveButton!=null){
					if(e.getSource() == saveButton){
						editorPanel.setVisible(false);
						if(levelMenu!= null)
							levelMenu.setVisible(true);
						else
							createLevelMenu(main);
					}
				}
			}
		}

		main.setFocusable(true);

		listener = new ButtonListener();

		createMainMenu(main);
		frame = new JFrame();

		frame.add(main);

		frame.setSize(new Dimension(FRAME_WIDTH,FRAME_HEIGHT));
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setVisible(true);


	}

	public void createMainMenu(BackgroundPanel panel){
		JPanel tmp = new JPanel(new FlowLayout());

		startMenu = new JPanel();
		startMenu.setPreferredSize(new Dimension((int)(FRAME_WIDTH*0.30),400));
		startMenu.setOpaque(false);

//		change the height of the button here
		int vGap = 25;
		startMenu.setLayout(new GridLayout(7,10,0,vGap));

		//PLAY button
		button = new JButton("PLAY");
		button.setBackground(new Color(0,0,0,210));
		button.setForeground(Color.WHITE);
		button.setFont(new Font("Calibri",Font.PLAIN, 23));
		button.setUI(new StyledButtonUI());
		button.addActionListener(listener);

		//Create YOur map button
		button2 = new JButton("CREATE YOUR MAP");
		button2.setBackground(new Color(0,0,0,200));
		button2.setForeground(Color.WHITE);

//		button2.setAlignmentX(Component.CENTER_ALIGNMENT);

		button2.setFont(new Font("Calibri",Font.PLAIN, 23));
		button2.setUI(new StyledButtonUI());
		button2.setBorder(new EmptyBorder(3, 35,3,34));
		button2.setPreferredSize(new Dimension(150, 3));
		button2.addActionListener(listener);

		//EXIT button
		button3 = new JButton("EXIT");
		button3.setBackground(new Color(0,0,0,200));
		button3.setForeground(Color.WHITE);


		button3.setFont(new Font("Calibri",Font.PLAIN, 23));
		button3.setUI(new StyledButtonUI());
		button3.addActionListener(listener);


		startMenu.add(button);
		startMenu.add(button2);
		startMenu.add(button3);

		tmp.add(Box.createRigidArea(new Dimension(0,FRAME_HEIGHT)));
		tmp.add(startMenu);
		panel.add(tmp);
//		panel.add(startMenu);

	}

	public void createPauseMenu(BackgroundPanel panel){
		pausePanel = new JPanel(new FlowLayout());

		pauseMenu = new JPanel();
		pauseMenu.setPreferredSize(new Dimension((int)(FRAME_WIDTH*0.30),400));
		pauseMenu.setOpaque(false);

//		change the height of the button here
		int vGap = 25;
		pauseMenu.setLayout(new GridLayout(7,10,0,vGap));


		//RESUME button
		resumeb= new JButton("RESUME");
		resumeb.setBackground(new Color(0,0,0,210));
		resumeb.setForeground(Color.WHITE);
		resumeb.setFont(new Font("Calibri",Font.PLAIN, 23));
		resumeb.setUI(new StyledButtonUI());
		resumeb.addActionListener(listener);


		//EXIT button
		exitp = new JButton("EXIT");
		exitp.setBackground(new Color(0,0,0,200));
		exitp.setForeground(Color.WHITE);

		exitp.setFont(new Font("Calibri",Font.PLAIN, 23));
		exitp.setUI(new StyledButtonUI());
		exitp.addActionListener(listener);

		pauseMenu.add(resumeb);
		pauseMenu.add(exitp);

		pausePanel.add(Box.createRigidArea(new Dimension(0,FRAME_HEIGHT)));
		pausePanel.add(pauseMenu);
		panel.add(pausePanel);

//		panel.add(pauseMenu);

	}

	private void setResumeb(JButton jButton) {
		// TODO Auto-generated method stub

	}

	public void createBackMenu(BackgroundPanel panel){
		backPanel =new JPanel();

		backPanel.setBackground(new Color(0,0,0,125));

		backMenu = new JPanel();
		backMenu.setPreferredSize(new Dimension((int)(FRAME_WIDTH*0.30),400));
		backMenu.setOpaque(false);

//		change the height of the button here
		int vGap = 25;
		backMenu.setLayout(new GridLayout(7,10,0,vGap));

		//back Menu button
		backb = new JButton("BACK TO MENU");
		backb.setBackground(new Color(0,0,0,200));
		backb.setForeground(Color.WHITE);
		backb.setFont(new Font("Calibri",Font.PLAIN, 23));
		backb.setUI(new StyledButtonUI());
		backb.addActionListener(listener);

		//back Editor button
		backEd = new JButton("BACK TO EDITOR");
		backEd.setBackground(new Color(0,0,0,200));
		backEd.setForeground(Color.WHITE);
		backEd.setFont(new Font("Calibri",Font.PLAIN, 23));
		backEd.setUI(new StyledButtonUI());
		backEd.addActionListener(listener);

		//EXIT button
		exitp = new JButton("EXIT");
		exitp.setBackground(new Color(0,0,0,200));
		exitp.setForeground(Color.WHITE);

		exitp.setFont(new Font("Calibri",Font.PLAIN, 23));
		exitp.setUI(new StyledButtonUI());
		exitp.addActionListener(listener);

		backMenu.add(backb);
		backMenu.add(backEd);
		backMenu.add(exitp);

		backPanel.add(Box.createRigidArea(new Dimension(0,FRAME_HEIGHT)));
		backPanel.add(backMenu);
		panel.add(backPanel);

//		panel.add(backMenu);

	}
	public void createPlayerMenu(BackgroundPanel panel){
		JPanel tmp = new JPanel(new FlowLayout());

		playerMenu = new JPanel();
		playerMenu.setPreferredSize(new Dimension((int)(FRAME_WIDTH*0.30),400));
		playerMenu.setOpaque(false);

//		change the height of the button here
		int vGap = 25;
		playerMenu.setLayout(new GridLayout(7,10,0,vGap));


		//One Player button
		if(f1.exists() && !f1.isDirectory()) {
			oneP = new JButton("LEVEL 1");
		}
		else
			oneP = new JButton("EMPTY SLOT");
		oneP.setBackground(new Color(0,0,0,210));
		oneP.setForeground(Color.WHITE);
		oneP.setFont(new Font("Calibri",Font.PLAIN, 22));
		oneP.setUI(new StyledButtonUI());
		oneP.addActionListener(listener);

		//2 Play
		if(f1.exists() && !f1.isDirectory()) {
			twoP = new JButton("LEVEL 2");
		}
		else
			twoP = new JButton("EMPTY SLOT");
		twoP.setBackground(new Color(0,0,0,200));
		twoP.setForeground(Color.WHITE);
		twoP.setFont(new Font("Calibri",Font.PLAIN, 22));
		twoP.setUI(new StyledButtonUI());
		twoP.addActionListener(listener);

		//3Play
		if(f1.exists() && !f1.isDirectory()) {
			threeP = new JButton("LEVEL 3");
		}
		else
			threeP = new JButton("EMPTY SLOT");

		threeP.setBackground(new Color(0,0,0,200));
		threeP.setForeground(Color.WHITE);

		threeP.setFont(new Font("Calibri",Font.PLAIN, 22));
		threeP.setUI(new StyledButtonUI());
		threeP.addActionListener(listener);

		//4 Play
		if(f1.exists() && !f1.isDirectory()) {
			fourP = new JButton("LEVEL 4");
		}
		else
			fourP = new JButton("EMPTY SLOT");

		fourP.setBackground(new Color(0,0,0,200));
		fourP.setForeground(Color.WHITE);


		fourP.setFont(new Font("Calibri",Font.PLAIN, 22));
		fourP.setUI(new StyledButtonUI());
		fourP.addActionListener(listener);


		//back Menu button
		backb = new JButton("BACK");
		backb.setBackground(new Color(0,0,0,200));
		backb.setForeground(Color.WHITE);
		backb.setFont(new Font("Calibri",Font.PLAIN, 23));
		backb.setUI(new StyledButtonUI());
		backb.addActionListener(listener);

		playerMenu.add(oneP);
		playerMenu.add(twoP);
		playerMenu.add(threeP);
		playerMenu.add(fourP);
		playerMenu.add(backb);

		tmp.add(Box.createRigidArea(new Dimension(0,FRAME_HEIGHT)));
		tmp.add(playerMenu);
		panel.add(tmp);

//		panel.add(playerMenu);

	}
	public void createLevelMenu(BackgroundPanel panel){

		JPanel tmp = new JPanel(new FlowLayout());

		levelMenu = new JPanel();
		levelMenu.setPreferredSize(new Dimension((int)(FRAME_WIDTH*0.30),400));
		levelMenu.setOpaque(false);

//		change the height of the button here
		int vGap = 25;
		levelMenu.setLayout(new GridLayout(7,10,0,vGap));


		//One Player button
		if(f1.exists() && !f1.isDirectory()) {
			level1b = new JButton("LEVEL 1");
		}
		else
			level1b = new JButton("EMPTY SLOT");

		level1b.setBackground(new Color(0,0,0,210));
		level1b.setForeground(Color.WHITE);
		level1b.setFont(new Font("Calibri",Font.PLAIN, 22));
		level1b.setUI(new StyledButtonUI());
		level1b.addActionListener(listener);

		//2 Play
		if(f2.exists() && !f2.isDirectory()) {
			level2b = new JButton("LEVEL 2");
		}
		else
			level2b = new JButton("EMPTY SLOT");

		level2b.setBackground(new Color(0,0,0,200));
		level2b.setForeground(Color.WHITE);
		level2b.setFont(new Font("Calibri",Font.PLAIN, 22));
		level2b.setUI(new StyledButtonUI());
		level2b.addActionListener(listener);

		//3Play
		if(f3.exists() && !f3.isDirectory()) {
			level3b = new JButton("LEVEL 3");
		}
		else
			level3b = new JButton("EMPTY SLOT");

		level3b.setBackground(new Color(0,0,0,200));
		level3b.setForeground(Color.WHITE);

		level3b.setFont(new Font("Calibri",Font.PLAIN, 22));
		level3b.setUI(new StyledButtonUI());
		level3b.addActionListener(listener);

		//4 Play
		if(f4.exists() && !f4.isDirectory()) {
			level4b = new JButton("LEVEL 4");
		}
		else
			level4b = new JButton("EMPTY SLOT");

		level4b.setBackground(new Color(0,0,0,200));
		level4b.setForeground(Color.WHITE);


		level4b.setFont(new Font("Calibri",Font.PLAIN, 22));
		level4b.setUI(new StyledButtonUI());
		level4b.addActionListener(listener);

//		Back to Editor
		backEd = new JButton("BACK TO EDITOR");
		backEd.setBackground(new Color(0,0,0,200));
		backEd.setForeground(Color.WHITE);
		backEd.setFont(new Font("Calibri",Font.PLAIN, 23));
		backEd.setUI(new StyledButtonUI());
		backEd.addActionListener(listener);

		//back Menu button
		backb = new JButton("BACK TO MENU");
		backb.setBackground(new Color(0,0,0,200));
		backb.setForeground(Color.WHITE);
		backb.setFont(new Font("Calibri",Font.PLAIN, 23));
		backb.setUI(new StyledButtonUI());
		backb.addActionListener(listener);

		levelMenu.add(level1b);
		levelMenu.add(level2b );
		levelMenu.add(level3b );
		levelMenu.add(level4b );
		levelMenu.add(backEd);
		levelMenu.add(backb);


		tmp.add(Box.createRigidArea(new Dimension(0,FRAME_HEIGHT)));
		tmp.add(levelMenu);
		panel.add(tmp);

//		panel.add(playerMenu);

	}

	public void createEditor(BackgroundPanel panel){
		editorPanel = new JPanel();
		editorPanel.setLayout(new BorderLayout());

		editorPanel.setPreferredSize(new Dimension(FRAME_WIDTH,FRAME_HEIGHT));
		RadioButtons radio = new RadioButtons();


		editor = new EditorPanel(radio);
		saveButton = editor.getSaveButton();
		saveButton.addActionListener(listener);
		editor.setBackground(Color.WHITE);
		editorPanel.add(editor);
		editorPanel.add(radio, BorderLayout.EAST);

		panel.add(editorPanel);

	}
	public void createGame(BackgroundPanel panel){
		gamePanel = new JPanel();
		gamePanel.setLayout(new BorderLayout());

		gamePanel.setPreferredSize(new Dimension(frame.getWidth(),frame.getHeight()));


		FrameGolf game = new FrameGolf(1);

		gameVisible = true;

		gamePanel.add(game);
//        panel.add(game);
		panel.add(gamePanel);
//
		game.golf3D.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
					System.out.println("ESCAPE game");
					if(gameVisible){
						gamePanel.setVisible(false);

						createPauseMenu(panel);
						frame.pack();
					}
				}

			}

			public void keyTyped(KeyEvent e) {
			}
			public void keyReleased(KeyEvent arg0) {
			}
		});

	}

	public static JButton getResumeb() {
		return resumeb;
	}

}
