package CrazyGolf.Menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import CrazyGolf.Editor.Export;
import CrazyGolf.Editor.Import;
import CrazyGolf.Game.Game;

import CrazyGolf.Editor.EditorPanel;
import CrazyGolf.Editor.RadioButtons;

public class StartMenu {

	final int  FRAME_WIDTH = 1200 ;
	final int FRAME_HEIGHT = 900 ;

	private JFrame frame;
	private EditorPanel editor;
	private Game game ;

	public  BackgroundPanel main;
	private JPanel startMenu;
	private JPanel playerMenu;
	private JPanel pauseMenu;
	private JPanel editorPanel;
	public JPanel gamePanel;
	private JPanel pausePanel;
	private JPanel backPanel;
	private JPanel backMenu;
	private JPanel saveMenu;

	private JButton button3;

	private JButton button2;
	private JButton button;
	private JButton saveButton;
	private JButton loadButton;

	private JButton oneP;
	private JButton twoP;
	private JButton	threeP;
	private JButton fourP;

	private JButton level1b;
	private JButton level2b;
	private JButton	level3b;
	private JButton level4b;

	//	private static JButton resumeb;
	private JButton playp;
	private JButton exitp;
	private JButton backToMenu;
	private JButton PauseMBackToM;
	private JButton backEd;
	private JButton PauseMbackEd;

	private File f1;
	private File f2;
	private File f3;
	private File f4;

	private int slot;

	private ActionListener listener;

	private JButton load1;
	private JPanel loadMenu;
	private JButton load2;
	private JButton load3;
	private JButton load4;

	public StartMenu() {

		f1 = new File("Slot1.txt");
		f2 = new File("Slot2.txt");
		f3 = new File("Slot3.txt");
		f4 = new File("Slot4.txt");



		java.awt.Image background = new ImageIcon("game.jpg").getImage();

		main =new BackgroundPanel(background, BackgroundPanel.SCALED, 0.0f,0.0f);
//		main.setPreferredSize(new Dimension(FRAME_WIDTH,FRAME_HEIGHT));


		main.addKeyListener(new KeyListener() {


			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
					System.out.println("ESCAPE editor");

					if(editorPanel!= null && editorPanel.isVisible()){
						editorPanel.setVisible(false);
						if(backPanel==null)
							createBackMenu(main);
						else
							backPanel.setVisible(true);
					}

					else if (editorPanel!= null && !editorPanel.isVisible()){
						editorPanel.setVisible(true);
						backPanel.setVisible(false);
					}

					if(saveMenu!= null && saveMenu.isVisible())
						saveMenu.setVisible(false);

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
					Export export =new Export(editor.getGrid(),editor.pixelSIZE,editor.getPlayerChoice());
					export.run();
					editor.writeItDown(export.getData(),1);
					level1b.setText("LEVEL 1");
					main.repaint();
				}

				if(e.getSource() == level2b){
					Export export =new Export(editor.getGrid(),editor.pixelSIZE,editor.getPlayerChoice());
					export.run();
					editor.writeItDown(export.getData(),2);
					level2b.setText("LEVEL 2");
					main.repaint();;
				}
				if(e.getSource() == level3b){
					Export export =new Export(editor.getGrid(),editor.pixelSIZE,editor.getPlayerChoice());
					export.run();
					editor.writeItDown(export.getData(),3);
					level3b.setText("LEVEL 3");
					main.repaint();
				}
				if(e.getSource() == level4b){
					Export export =new Export(editor.getGrid(),editor.pixelSIZE,editor.getPlayerChoice());
					export.run();
					editor.writeItDown(export.getData(),4);
					level4b.setText("LEVEL 4");
					main.repaint();
				}
				if(e.getSource() == load1){
					LinkedList<String> f1List= fileToList(f1);
					Import impy = new Import(f1List);
					editor.setGrid(impy.getGrid());
					editor.setPlayerChoice(impy.getPlayerChoice());
					editor.pixelSIZE = (int) impy.getGridSize();
				}

				if(e.getSource() == load2){
					LinkedList<String> f2List= fileToList(f1);
					Import impy= new Import(f2List);
					editor.setGrid(impy.getGrid());
					editor.setPlayerChoice(impy.getPlayerChoice());
					editor.pixelSIZE = (int) impy.getGridSize();
				}
				if(e.getSource() == load3){
					LinkedList<String> f3List= fileToList(f1);
					Import impy = new Import(f3List);
					editor.setGrid(impy.getGrid());
					editor.setPlayerChoice(impy.getPlayerChoice());
					editor.pixelSIZE = (int) impy.getGridSize();
				}
				if(e.getSource() == load4){
					LinkedList<String> f4List= fileToList(f4);
					Import impy = new Import(f4List);
					editor.setGrid(impy.getGrid());
					editor.setPlayerChoice(impy.getPlayerChoice());
					editor.pixelSIZE = (int) impy.getGridSize();
				}

				if(e.getSource() == button2){
					startMenu.setVisible(false);
					createEditor(main);
				}
				if(e.getSource() ==button){
					if(startMenu.isVisible() == true){
						startMenu.setVisible(false);
					}
//					if(pauseVisible == true){
//						pauseMenu.setVisible(false);
//					}
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
					if(f1.exists() && !f1.isDirectory()) {
						slot =1;
						main.removeAll();
						createGame(slot,f1.getAbsolutePath());
					}
				}
				if(e.getSource() ==twoP){
					if(f2.exists() && !f2.isDirectory()) {
						slot=2;
						main.removeAll();
						createGame(slot,f2.getAbsolutePath());
					}
				}
				if(e.getSource() ==threeP){
					if(f3.exists() && !f3.isDirectory()) {
						slot=3;
						main.removeAll();
						createGame(slot,f3.getAbsolutePath());
					}
				}
				if(e.getSource() ==fourP){
					if(f4.exists() && !f4.isDirectory()) {
						slot=4;
						main.removeAll();
						createGame(slot,f4.getAbsolutePath());
					}
				}

//				if(e.getSource() == resumeb){
//
//					editorPanel.setVisible(true);
////					game.pause=true;
//					gamePanel.setVisible(true);
//					pausePanel.setVisible(false);
//					if(backPanel!=null)
//						main.remove(pausePanel);
////					main.remove(pausePanel);
//					main.repaint();
//			}

				if(e.getSource() == backToMenu){
					if(editorPanel!= null)
						main.remove(editorPanel);

					if(playerMenu!=null)
						playerMenu.setVisible(false);

					if(backPanel!=null)
						main.remove(backPanel);

					if(saveMenu!=null)
						saveMenu.setVisible(false);

					startMenu.setVisible(true);
				}
				if(e.getSource() == backEd){
					if(backPanel!=null)
						backPanel.setVisible(false);

					if(saveMenu!=null)
						saveMenu.setVisible(false);

					editorPanel.setVisible(true);
				}

				if(e.getSource() == PauseMBackToM){
					if(editorPanel!= null)
						main.remove(editorPanel);

					if(playerMenu!=null)
						playerMenu.setVisible(false);

					if(backPanel!=null)
						main.remove(backPanel);

					if(pausePanel!=null){
						pausePanel.setVisible(false);
						createMainMenu();
					}

					startMenu.setVisible(true);
				}

				if(e.getSource() == PauseMbackEd){
					if(backPanel!=null)
						main.remove(backPanel);

					editorPanel.setVisible(true);
				}

				if(saveButton!=null) {
					if (e.getSource() == saveButton) {
						editorPanel.setVisible(false);
						if (saveMenu != null)
							saveMenu.setVisible(true);
						else
							createSaveMenu(main);

					}
				}

				if(loadButton!=null) {
					if (e.getSource() == loadButton) {
						editorPanel.setVisible(false);
						System.out.println("Load works");
						if (loadMenu != null)
							loadMenu.setVisible(true);
						else
							createLoadMenu(main);
					}
				}

				if(e.getSource() == button3 || e.getSource() == exitp){
					System.exit(0);
				}
			}
		}

		main.setFocusable(true);

		listener = new ButtonListener();

		createMainMenu();
		frame = new JFrame();

		frame.add(main);

//		frame.setSize(new Dimension(FRAME_WIDTH,FRAME_HEIGHT));
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);

		frame.setVisible(true);


	}

	public void createMainMenu(){
		JPanel tmp = new JPanel();

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
		main.add(tmp);
//		panel.add(startMenu);

	}

	public void createPauseMenu(BackgroundPanel panel){
		pausePanel = new JPanel();
		pausePanel.setBackground(new Color(0,0,0,125));

		pauseMenu = new JPanel();
		pauseMenu.setPreferredSize(new Dimension((int)(FRAME_WIDTH*0.30),400));
		pauseMenu.setOpaque(false);

//		change the height of the button here
		int vGap = 25;
		pauseMenu.setLayout(new GridLayout(7,10,0,vGap));


//		//RESUME button
//		resumeb= new JButton("RESUME");
//		resumeb.setBackground(new Color(0,0,0,210));
//		resumeb.setForeground(Color.WHITE);
//		resumeb.setFont(new Font("Calibri",Font.PLAIN, 23));
//		resumeb.setUI(new StyledButtonUI());
//		resumeb.addActionListener(listener);

		//back Menu button
		PauseMBackToM = new JButton("BACK TO MENU");
		PauseMBackToM.setBackground(new Color(0,0,0,200));
		PauseMBackToM.setForeground(Color.WHITE);
		PauseMBackToM.setFont(new Font("Calibri",Font.PLAIN, 23));
		PauseMBackToM.setUI(new StyledButtonUI());
		PauseMBackToM.addActionListener(listener);


		//EXIT button
		exitp = new JButton("EXIT");
		exitp.setBackground(new Color(0,0,0,200));
		exitp.setForeground(Color.WHITE);

		exitp.setFont(new Font("Calibri",Font.PLAIN, 23));
		exitp.setUI(new StyledButtonUI());
		exitp.addActionListener(listener);

//		pauseMenu.add(resumeb);
		pauseMenu.add(PauseMBackToM);
		pauseMenu.add(exitp);

		pausePanel.add(Box.createRigidArea(new Dimension(0,FRAME_HEIGHT)));
		pausePanel.add(pauseMenu);
		panel.add(pausePanel);

//		panel.add(pauseMenu);

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
		PauseMBackToM = new JButton("BACK TO MENU");
		PauseMBackToM.setBackground(new Color(0,0,0,200));
		PauseMBackToM.setForeground(Color.WHITE);
		PauseMBackToM.setFont(new Font("Calibri",Font.PLAIN, 23));
		PauseMBackToM.setUI(new StyledButtonUI());
		PauseMBackToM.addActionListener(listener);

		//back Editor button
		PauseMbackEd = new JButton("BACK TO EDITOR");
		PauseMbackEd.setBackground(new Color(0,0,0,200));
		PauseMbackEd.setForeground(Color.WHITE);
		PauseMbackEd.setFont(new Font("Calibri",Font.PLAIN, 23));
		PauseMbackEd.setUI(new StyledButtonUI());
		PauseMbackEd.addActionListener(listener);

		//EXIT button
		exitp = new JButton("EXIT");
		exitp.setBackground(new Color(0,0,0,200));
		exitp.setForeground(Color.WHITE);

		exitp.setFont(new Font("Calibri",Font.PLAIN, 23));
		exitp.setUI(new StyledButtonUI());
		exitp.addActionListener(listener);

		backMenu.add(PauseMBackToM);
		backMenu.add(PauseMbackEd);
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
		if(f2.exists() && !f2.isDirectory()) {
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
		if(f3.exists() && !f3.isDirectory()) {
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
		if(f4.exists() && !f4.isDirectory()) {
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
		backToMenu = new JButton("BACK");
		backToMenu.setBackground(new Color(0,0,0,200));
		backToMenu.setForeground(Color.WHITE);
		backToMenu.setFont(new Font("Calibri",Font.PLAIN, 23));
		backToMenu.setUI(new StyledButtonUI());
		backToMenu.addActionListener(listener);

		playerMenu.add(oneP);
		playerMenu.add(twoP);
		playerMenu.add(threeP);
		playerMenu.add(fourP);
		playerMenu.add(backToMenu);

		tmp.add(Box.createRigidArea(new Dimension(0,FRAME_HEIGHT)));
		tmp.add(playerMenu);
		panel.add(tmp);

//		panel.add(playerMenu);

	}
	public void createLoadMenu(BackgroundPanel panel){

		JPanel tmp = new JPanel(new FlowLayout());

		loadMenu = new JPanel();
		loadMenu.setPreferredSize(new Dimension((int)(FRAME_WIDTH*0.30),400));
		loadMenu.setOpaque(false);

//		change the height of the button here
		int vGap = 25;
		loadMenu.setLayout(new GridLayout(7,10,0,vGap));

		//Title
		JLabel loadTitle =new JLabel();
		loadMenu.add(loadTitle);
		loadTitle.setText("LOAD GAME");
		loadTitle.setFont(new Font("Calibri",Font.PLAIN, 36));
		loadTitle.setForeground(Color.BLACK);
//		Border border = BorderFactory.createLineBorder(Color.GRAY, 5);
//		loadTitle.setBorder(border);
		loadTitle.setHorizontalAlignment(SwingConstants.CENTER);

		//Load1
		if(f1.exists() && !f1.isDirectory()) {
			load1 = new JButton("LEVEL 1");
		}
		else
			load1 = new JButton("EMPTY SLOT");

		load1.setBackground(new Color(0,0,0,210));
		load1.setForeground(Color.WHITE);
		load1.setFont(new Font("Calibri",Font.PLAIN, 22));
		load1.setUI(new StyledButtonUI());
		load1.addActionListener(listener);

		//Load2
		if(f2.exists() && !f2.isDirectory()) {
			load2 = new JButton("LEVEL 2");
		}
		else
			load2 = new JButton("EMPTY SLOT");

		load2.setBackground(new Color(0,0,0,200));
		load2.setForeground(Color.WHITE);
		load2.setFont(new Font("Calibri",Font.PLAIN, 22));
		load2.setUI(new StyledButtonUI());
		load2.addActionListener(listener);

		//3Play
		if(f3.exists() && !f3.isDirectory()) {
			load3 = new JButton("LEVEL 3");
		}
		else
			load3 = new JButton("EMPTY SLOT");

		load3.setBackground(new Color(0,0,0,200));
		load3.setForeground(Color.WHITE);

		load3.setFont(new Font("Calibri",Font.PLAIN, 22));
		load3.setUI(new StyledButtonUI());
		load3.addActionListener(listener);

		//4 Play
		if(f4.exists() && !f4.isDirectory()) {
			load4 = new JButton("LEVEL 4");
		}
		else
			load4 = new JButton("EMPTY SLOT");

		load4.setBackground(new Color(0,0,0,200));
		load4.setForeground(Color.WHITE);


		load4.setFont(new Font("Calibri",Font.PLAIN, 22));
		load4.setUI(new StyledButtonUI());
		load4.addActionListener(listener);

//		Back to Editor
		backEd = new JButton("BACK TO EDITOR");
		backEd.setBackground(new Color(0,0,0,200));
		backEd.setForeground(Color.WHITE);
		backEd.setFont(new Font("Calibri",Font.PLAIN, 23));
		backEd.setUI(new StyledButtonUI());
		backEd.addActionListener(listener);

		//back Menu button
		backToMenu = new JButton("BACK TO MENU");
		backToMenu.setBackground(new Color(0,0,0,200));
		backToMenu.setForeground(Color.WHITE);
		backToMenu.setFont(new Font("Calibri",Font.PLAIN, 23));
		backToMenu.setUI(new StyledButtonUI());
		backToMenu.addActionListener(listener);

		loadMenu.add(load1);
		loadMenu.add(load2);
		loadMenu.add(load3);
		loadMenu.add(load4 );
		loadMenu.add(backEd);
		loadMenu.add(backToMenu);


		tmp.add(Box.createRigidArea(new Dimension(0,FRAME_HEIGHT)));
		tmp.add(loadMenu);
		panel.add(tmp);

//		panel.add(playerMenu);

	}
	public void createSaveMenu(BackgroundPanel panel){

		JPanel tmp = new JPanel(new FlowLayout());

		saveMenu = new JPanel();
		saveMenu.setPreferredSize(new Dimension((int)(FRAME_WIDTH*0.30),400));
		saveMenu.setOpaque(false);

//		change the height of the button here
		int vGap = 25;
		saveMenu.setLayout(new GridLayout(7,10,0,vGap));

		//Title
		JLabel loadTitle =new JLabel();
		saveMenu.add(loadTitle);
		loadTitle.setText("SAVE GAME");
		loadTitle.setFont(new Font("Calibri",Font.PLAIN, 36));
		loadTitle.setForeground(Color.BLACK);
//		Border border = BorderFactory.createLineBorder(Color.GRAY, 5);
//		loadTitle.setBorder(border);
		loadTitle.setHorizontalAlignment(SwingConstants.CENTER);



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
		backToMenu = new JButton("BACK TO MENU");
		backToMenu.setBackground(new Color(0,0,0,200));
		backToMenu.setForeground(Color.WHITE);
		backToMenu.setFont(new Font("Calibri",Font.PLAIN, 23));
		backToMenu.setUI(new StyledButtonUI());
		backToMenu.addActionListener(listener);

		saveMenu.add(level1b);
		saveMenu.add(level2b );
		saveMenu.add(level3b );
		saveMenu.add(level4b );
		saveMenu.add(backEd);
		saveMenu.add(backToMenu);


		tmp.add(Box.createRigidArea(new Dimension(0,FRAME_HEIGHT)));
		tmp.add(saveMenu);
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
		loadButton= editor.getLoadButton();
		saveButton.addActionListener(listener);
		loadButton.addActionListener(listener);
		editor.setBackground(Color.WHITE);
		editorPanel.add(editor);
		editorPanel.add(radio, BorderLayout.EAST);

		panel.add(editorPanel);

	}
	public void createGame(int fileNum,String filePath){

		gamePanel = new JPanel();
		gamePanel.setLayout(new BorderLayout());


		game = new Game(this,filePath,fileNum);
		game.getCanvas3D().setSize(new Dimension(main.getWidth(),main.getHeight()));

		new Thread(game).start();

		//You can pause the game with this var:
		//game.pause=true;

		gamePanel.add(game);
		main.add(gamePanel);

		game.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
					System.out.println("ESCAPE game");
					if(gamePanel.isVisible()){
						gamePanel.setVisible(false);
						createPauseMenu(main);
					}

				}

			}

			public void keyTyped(KeyEvent e) {
			}
			public void keyReleased(KeyEvent arg0) {
			}
		});

	}
	public LinkedList<String> fileToList(File fileName){
		LinkedList<String> file = new LinkedList<>();
		try {
			Scanner s = new Scanner(new FileReader(fileName));
			while (s.hasNextLine()) {
				file.add(s.nextLine());
			}
		} catch (IOException e) {
			System.out.println("Error reading field plan from " + fileName);
			System.exit(0);
		}
		return file;
	}
//	public static JButton getResumeb() {
//		return resumeb;
//	}

}
