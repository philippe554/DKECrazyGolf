package CrazyGolf.Game;

import CrazyGolf.FileLocations;
import CrazyGolf.PhysicsEngine.World;

/**
 * Created by pmmde on 4/22/2016.
 */
public class Game extends Thread{

    public boolean keepPlaying;
    public boolean pause;

    private Player[] players;
    private int currentPlayer;

    private Golf3D golf3D;

    public Game(Golf3D tGolf3D) {
        keepPlaying=true;
        pause=false;
        golf3D=tGolf3D;

        World world = new World(FileLocations.level1);

        players = new Player[world.balls.size()];
        currentPlayer = 0;
        for(int i=0;i<world.balls.size();i++) {
            players[i] = new Player(golf3D, world, i);
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
