package CrazyGolf.Game;

import CrazyGolf.Bot.BotInterface;
import CrazyGolf.Bot.Brutefinder.Brutefinder;
import CrazyGolf.Game.OpenGL.GolfPanelOpenGL;
import CrazyGolf.Menu.Popup;
import CrazyGolf.Menu.StartMenu;
import CrazyGolf.PhysicsEngine.Objects.Parts.Ball;
import CrazyGolf.PhysicsEngine.Physics12.World;
import CrazyGolf.PhysicsEngine.Physics12.WorldCPU;
import CrazyGolf.PhysicsEngine.Physics3.WorldData;
import javafx.geometry.Point3D;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class Game extends GolfPanelOpenGL implements Runnable{

    public boolean keepPlaying;
    public boolean pause;
    private ArrayList<Player> players;
    private int currentPlayer;
    public BotInterface brutefinder;
    public WorldData world;
    public boolean enterPressed=false;
    public int slot;
    private StartMenu menu;
    public int winner;
    public int winnerTurns;

    public Game(StartMenu menu,String fileName,int slot) {
        this.menu=menu;
        this.slot=slot;
        keepPlaying=true;
        pause=false;

        if(World.DEBUG)System.out.println("Start reading file.");
        ArrayList<String> file = new ArrayList<>();
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
        ArrayList<String> worldData = new ArrayList<>();
        ArrayList<String> gamemodeData = new ArrayList<>();
        ArrayList<String> brutefinderData = new ArrayList<>();

        for(int i=0;i<file.size();i++)
        {
            if(file.get(i).equals("Master:World")){
                sort=0;
            }else if(file.get(i).equals("Master:Gamemode")){
                sort=1;
            }
            else if(file.get(i).equals("Master:Brutefinder")){
                sort=2;
            }else if(file.get(i).equals("Master:Editdata")){
                sort=3;
            }else if(sort==0)
            {
                worldData.add(file.get(i));
            }else if(sort==1)
            {
                gamemodeData.add(file.get(i));
            }else if(sort==2)
            {
                brutefinderData.add(file.get(i));
            }
        }

        world = new WorldData(false);
        world.load(worldData);
        load(world);

        if(brutefinderData.size()>0) {
            brutefinder = new Brutefinder();
            brutefinder.init(world);
            brutefinder.loadDatabase(brutefinderData);
            load((Brutefinder) brutefinder);
        }
        if(World.DEBUG)System.out.println("Finished reading file.");

        players = new ArrayList<>();
        currentPlayer = 0;
        for(int i=0;i<gamemodeData.size();i++) {
            int playerSort = Integer.parseInt(gamemodeData.get(i));
            if(playerSort!=3) {
                players.add(new Player(this, i, playerSort));
            }
        }

        Game game= this;
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
                if(e.getKeyCode() == KeyEvent.VK_F)world.swapTerainPhysics();
                game.keyPressed(e);
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
                if(e.getKeyChar() == KeyEvent.VK_F)world.swapTerainPhysics();
                game.keyReleased(e);
            }
        });

        //loadWorld(world,players.length);
    }
    public void run(){
        if(World.DEBUG)System.out.println("Game: Start game with player "+currentPlayer);
        boolean inputFlag=true;
        long lastTime=System.currentTimeMillis();
        while(keepPlaying)
        {
            long currentTime=System.currentTimeMillis();
            if(lastTime+1000.0/30.0<currentTime && !pause) {
                lastTime=currentTime;
                requestFocus();
                if(!players.get(currentPlayer).createdInWorld){
                    world.addNewBall();
                    players.get(currentPlayer).createdInWorld=true;
                }
                world.step(true);
                update();
                if(inputFlag) {
                    players.get(currentPlayer).updatePushParameters();
                    if(enterPressed) {
                        backupBallLocations();
                        players.get(currentPlayer).launch();
                        removeArrow();
                        enterPressed=false;
                        inputFlag=false;
                    }
                }else{
                    boolean allBallsStop = true;
                    for (int i = 0; i < world.getAmountBalls(); i++) {
                        if(!world.ballStoppedMoving(i)){
                            allBallsStop=false;
                        }
                        if(world.getBall(i).place.getZ() < Ball.minZ){
                            world.getBall(i).place=players.get(i).oldLocation;
                            world.getBall(i).velocity=new Point3D(0,0,0);
                        }
                    }
                    if (allBallsStop) {
                        for (int i = world.getAmountBalls()-1; i >= 0; i--) {
                            if (world.checkBallInHole(i)) {
                                keepPlaying = false;
                                if (World.DEBUG){
                                    System.out.println("Game: Player " + i + " Won with "+players.get(i).turns+" turns!");
                                }
                                winner= i;
                                winnerTurns= players.get(i).turns;
                                Popup pop = new Popup(menu);
                                pop.setLocationRelativeTo(null);
                                pop.setVisible(true);
                            }
                        }
                        if(keepPlaying) {
                            currentPlayer++;
                            if (currentPlayer == players.size()) {
                                currentPlayer = 0;
                            }
                            if (World.DEBUG) System.out.println("Game: Switched to player " + currentPlayer);
                            inputFlag = true;
                        }
                    }
                }
            }
        }
    }
    public void launch()
    {
        enterPressed=true;
    }
    public void backupBallLocations() {
        for(int i=0;i<world.getAmountBalls();i++)
        {
            players.get(i).oldLocation=world.getBall(i).place.add(0,0,0);
        }
    }
}
