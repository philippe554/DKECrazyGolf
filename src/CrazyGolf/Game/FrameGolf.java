package CrazyGolf.Game;

import CrazyGolf.FileLocations;
import CrazyGolf.PhysicsEngine.World;
import javafx.geometry.Point3D;

import javax.swing.*;
import javax.vecmath.Color3f;
import java.awt.*;
import java.awt.event.*;


public class FrameGolf extends JFrame
{
    private Golf3D golf3D;
    private Player[] players;
    private int currentPlayer;
    private int amountOfPlayers=1;

    private JLabel labelTitle;
    private JLabel labelCurrentPlayer;
    private JLabel[] labelPlayer;
    private JLabel labelWin;

    private boolean keepPlaying;

    public FrameGolf(int player) {
        amountOfPlayers = player;

        setLayout( new BorderLayout() );

        golf3D = new Golf3D(0.05f);

        players = new Player[amountOfPlayers];
        currentPlayer = 0;
        for(int i=0;i<amountOfPlayers;i++) {
            World world = new World(FileLocations.level1);

            players[i] = new Player(golf3D, world, 0);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                for(int i=0;i<amountOfPlayers;i++) {
                    keepPlaying=false;
                    players[i].world.cleanUp();
                }
            }
        }, "Shutdown-thread"));

        golf3D.loadWorld(players[currentPlayer].world);
        add(golf3D);

        setupLabels();
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setResizable(true);
        setVisible(true);
        pack();

        golf3D.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_LEFT)Player.leftPressed=true;
                if(e.getKeyCode() == KeyEvent.VK_RIGHT)Player.rightPressed=true;
                if(e.getKeyCode() == KeyEvent.VK_UP)Player.upPressed=true;
                if(e.getKeyCode() == KeyEvent.VK_DOWN)Player.downPressed=true;
                if(e.getKeyCode() == KeyEvent.VK_CONTROL)Player.powerDownPressed=true;
                if(e.getKeyCode() == KeyEvent.VK_SHIFT)Player.powerUpPressed=true;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_LEFT)Player.leftPressed=false;
                if(e.getKeyCode() == KeyEvent.VK_RIGHT)Player.rightPressed=false;
                if(e.getKeyCode() == KeyEvent.VK_UP)Player.upPressed=false;
                if(e.getKeyCode() == KeyEvent.VK_DOWN)Player.downPressed=false;
                if(e.getKeyCode() == KeyEvent.VK_CONTROL)Player.powerDownPressed=false;
                if(e.getKeyCode() == KeyEvent.VK_SHIFT)Player.powerUpPressed=false;
                if(e.getKeyChar() == KeyEvent.VK_ENTER)players[currentPlayer].launch();
            }
        });
        golf3D.setFocusable(true);

        keepPlaying=true;
        long lastTime=System.currentTimeMillis();
        while(keepPlaying)
        {
            long currentTime=System.currentTimeMillis();
            if(lastTime+1000.0/30.0<currentTime) {
                lastTime=currentTime;
                if (players[currentPlayer].step()) {
                    if (players[currentPlayer].getEndFlag()) {
                        keepPlaying = false;
                    } else {
                        currentPlayer++;
                        if (currentPlayer == amountOfPlayers) {
                            currentPlayer = 0;
                        }

                        golf3D.loadWorld(players[currentPlayer].world);
                        players[currentPlayer].resumeGame();
                    }
                    updateLabels();
                }
            }
        }
        labelWin.setText("Player "+(currentPlayer+1)+" wins!");
    }

    private void setupLabels() {
        JComponent EastPanel = new JPanel();
        EastPanel.setPreferredSize( new Dimension(300, 800));
        EastPanel.setLayout( new GridLayout(0,1));

        labelTitle = new JLabel("Crazy Golf");
        labelTitle.setFont(new Font("Arial", Font.PLAIN, 50));
        EastPanel.add(labelTitle);

        labelCurrentPlayer = new JLabel("Current Player: "+(currentPlayer+1));
        labelCurrentPlayer.setFont(new Font("Arial", Font.PLAIN, 20));
        EastPanel.add(labelCurrentPlayer);

        labelPlayer=new JLabel[amountOfPlayers];
        for(int i=0;i<amountOfPlayers;i++)
        {
            labelPlayer[i]= new JLabel("p"+(i+1)+": "+players[i].turns);
            labelPlayer[i].setFont(new Font("Arial", Font.PLAIN, 20));
            EastPanel.add(labelPlayer[i]);
        }

        labelWin = new JLabel("KEEP PLAYING!!!");
        labelWin.setFont(new Font("Arial", Font.PLAIN, 30));
        EastPanel.add(labelWin);

        add(EastPanel, BorderLayout.EAST);
    }

    private void updateLabels() {
        labelCurrentPlayer.setText("Current Player: "+(currentPlayer+1));
        for(int i=0;i<amountOfPlayers;i++) {
            labelPlayer[i].setText("p" + (i + 1) + ": " + players[i].turns);
        }
    }
}

//    public static void main(String[] args) { 
//    	new FrameGolf(player); }
//
//}
