package CrazyGolf.Game;

import CrazyGolf.FileLocations;
import CrazyGolf.PhysicsEngine.World;

/**
 * Created by pmmde on 4/22/2016.
 */
public class Game extends Thread{

    public boolean keepPlaying;
    private Player[] players;
    private int currentPlayer;

    private Golf3D golf3D;

    public Game(Golf3D tGolf3D,int amountOfPlayers) {
        keepPlaying=true;
        golf3D=tGolf3D;

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
    }

    public void run(){
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
                        if (currentPlayer == players.length) {
                            currentPlayer = 0;
                        }

                        golf3D.loadWorld(players[currentPlayer].world);
                        players[currentPlayer].resumeGame();
                    }
                    //updateLabels();
                }
            }
        }
    }

    public void launch()
    {
        players[currentPlayer].launch();
    }

}
