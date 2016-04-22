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

    private JLabel labelTitle;
    private JLabel labelCurrentPlayer;
    private JLabel[] labelPlayer;
    private JLabel labelWin;

    private Game game;

    private boolean keepPlaying;

    public FrameGolf(int amountOfPlayers) {

        setLayout( new BorderLayout() );

        golf3D = new Golf3D(0.05f);
        game= new Game(golf3D,amountOfPlayers);
        game.start();

        add(golf3D);

        //setupLabels();

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
                if(e.getKeyChar() == KeyEvent.VK_ENTER)game.launch();
            }
        });
        golf3D.setFocusable(true);
    }

    /*private void setupLabels() {
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
    }*/
}

//    public static void main(String[] args) { 
//    	new FrameGolf(player); }
//
//}
