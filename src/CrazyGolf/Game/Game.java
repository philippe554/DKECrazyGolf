package CrazyGolf.Game;

import CrazyGolf.Bot.BotInterface;
import CrazyGolf.Bot.Brutefinder.Brutefinder;
import CrazyGolf.FileLocations;
import CrazyGolf.PhysicsEngine.*;
import javafx.geometry.Point3D;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Created by pmmde on 4/22/2016.
 */
public class Game extends GolfPanel implements Runnable{

    public boolean keepPlaying;
    public boolean pause;
    private Player[] players;
    private int currentPlayer;
    public BotInterface brutefinder;
    public World world;
    public boolean enterPressed=false;

    public Game(String fileName) {
        keepPlaying=true;
        pause=false;

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

        int sort=0;
        LinkedList<String> worldData = new LinkedList<>();
        LinkedList<String> brutefinderData = new LinkedList<>();

        for(int i=0;i<file.size();i++)
        {
            if(file.get(i).equals("Master:World")){
                sort=0;
            }else if(file.get(i).equals("Master:Brutefinder")){
                sort=1;
            }else if(sort==0)
            {
                worldData.add(file.get(i));
            }else if(sort==1)
            {
                brutefinderData.add(file.get(i));
            }
        }

        world = new WorldCPU(worldData);

        if(brutefinderData.size()>0) {
            brutefinder = new Brutefinder();
            brutefinder.init(world);
            brutefinder.loadDatabase(brutefinderData);
        }

        players = new Player[world.getAmountBalls()];
        currentPlayer = 0;
        for(int i=0;i<world.getAmountBalls();i++) {
            players[i] = new Player(this,i,i);
        }

        addKeyListener(new KeyListener() {
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
                if(e.getKeyChar() == KeyEvent.VK_ENTER)launch();
            }
        });

        loadWorld(world);
    }
    public void run(){
        if(World.DEBUG)System.out.println("Game: Start game with player "+currentPlayer);
        int stopCounter=0;
        boolean inputFlag=true;
        long lastTime=System.currentTimeMillis();
        while(keepPlaying)
        {
            long currentTime=System.currentTimeMillis();
            if(lastTime+1000.0/30.0<currentTime && !pause) {
                lastTime=currentTime;
                requestFocus();
                if(inputFlag) {
                    players[currentPlayer].updatePushParameters();
                    if(enterPressed) {
                        backupBallLocations();
                        players[currentPlayer].launch();
                        removeArrow();
                        enterPressed=false;
                        inputFlag=false;
                    }
                }else{
                    world.step(true);
                    updateBall();
                    boolean allBallsStop = true;
                    for (int i = 0; i < world.getAmountBalls(); i++) {
                        if (world.getBallPosition(i).getZ() > -100) {
                            if (world.getBallVelocity(i).magnitude() > 1.5) {
                                allBallsStop = false;
                            }
                        }
                    }
                    if (allBallsStop) {
                        stopCounter++;
                    }
                    if (stopCounter > 20) {
                        for (int i = world.getAmountBalls()-1; i >= 0; i--) {
                            if (world.getBallPosition(i).getZ() < -100) {
                                world.setBallPosition(i, players[i].oldLocation);
                                world.setBallVelocity(i, new Point3D(0, 0, 0));
                            }
                            if (world.checkBallInHole(i)) {
                                keepPlaying = false;
                                i=0;
                                if (World.DEBUG) System.out.println("Game: Player " + i + " Won!");
                            }
                        }
                        if(keepPlaying) {
                            currentPlayer++;
                            if (currentPlayer == players.length) {
                                currentPlayer = 0;
                            }
                            stopCounter = 0;
                            if (World.DEBUG) System.out.println("Game: Switched to player " + currentPlayer);
                            inputFlag = true;
                        }
                        updateBall();
                    }
                }
            }
        }
        world.cleanUp();
    }
    public void launch()
    {
        enterPressed=true;
    }
    public void backupBallLocations() {
        for(int i=0;i<world.getAmountBalls();i++)
        {
            players[i].oldLocation=world.getBallPosition(i).add(0,0,0);
        }
    }
}
