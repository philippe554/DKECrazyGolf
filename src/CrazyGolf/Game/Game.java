package CrazyGolf.Game;

import CrazyGolf.Bot.BotInterface;
import CrazyGolf.Bot.Brutefinder.Brutefinder;
import CrazyGolf.FileLocations;
import CrazyGolf.PhysicsEngine.World;
import CrazyGolf.PhysicsEngine.WorldGPU;
import CrazyGolf.PhysicsEngine.WorldCPU;

import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Created by pmmde on 4/22/2016.
 */
public class Game extends Thread{

    public boolean keepPlaying;
    public boolean pause;

    private Player[] players;
    private int currentPlayer;

    private Golf3D golf3D;

    public BotInterface brutefinder;

    public Game(Golf3D tGolf3D) {
        keepPlaying=true;
        pause=false;
        golf3D=tGolf3D;

        LinkedList<String> file = new LinkedList<>();
        try {
            Scanner s = new Scanner(new FileReader(FileLocations.level1));
            while (s.hasNextLine()) {
                file.add(s.nextLine());
            }
        } catch (IOException e) {
                        System.out.println("Error reading field plan from " + FileLocations.level1);
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

        World world = new WorldCPU(worldData);
        brutefinder = new Brutefinder();
        brutefinder.init(world);
        brutefinder.loadDatabase(brutefinderData);

        players = new Player[world.getAmountBalls()];
        currentPlayer = 0;
        for(int i=0;i<world.getAmountBalls();i++) {
            players[i] = new Player(golf3D, world, i,this);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                world.cleanUp();
            }
        }, "Shutdown-thread"));

        golf3D.loadWorld(players[currentPlayer].world);
    }

    public void run(){
        long lastTime=System.currentTimeMillis();
        while(keepPlaying)
        {
            long currentTime=System.currentTimeMillis();
            if(lastTime+1000.0/30.0<currentTime && !pause) {
                lastTime=currentTime;
                if (players[currentPlayer].step()) {
                    if (players[currentPlayer].getEndFlag()) {
                        keepPlaying = false;
                    } else {
                        currentPlayer++;
                        if (currentPlayer == players.length) {
                            currentPlayer = 0;
                        }

                        //golf3D.loadWorld(players[currentPlayer].world);
                        players[currentPlayer].resumeGame();
                    }
                }
            }
        }
    }

    public void launch()
    {
        players[currentPlayer].launch();
    }

}
