package CrazyGolf.Editor;

import CrazyGolf.Bot.Brutefinder.Brutefinder;
import CrazyGolf.PhysicsEngine.Physics12.World;
import CrazyGolf.PhysicsEngine.Physics12.WorldContainer;
import CrazyGolf.PhysicsEngine.Physics12.WorldGPUBotOpti;
import CrazyGolf.PhysicsEngine.Physics3.WorldData;
import javafx.geometry.Point3D;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by pmmde on 5/25/2016.
 */
public class Export implements Runnable {
    private Grid[] grid;
    private double gridSize;
    private String[] playerChoice;
    private boolean done;
    private LinkedList<String> returnData;
    private Brutefinder brutefinder;

    public Export(Grid[] g,double gs,String[] pc){
        grid=g;
        gridSize=gs;
        playerChoice=pc;
        done=false;
    }
    @Override public void run() {
        ArrayList<String> brutefinderData = null;
        WorldData world = new WorldData(false);
        for(int i=0;i<grid.length;i++) {
            world.load(grid[i].getStringGrid(), gridSize, new Point3D(0,0,i*40+80));
        }
        ArrayList<String> worldData = world.save();

        boolean calcDatabase = false;

        for(int i=0;i<playerChoice.length;i++)
        {
            if(playerChoice[i].equals("1"))
            {
                calcDatabase=true;
            }
        }

        if (calcDatabase){
            long time=System.currentTimeMillis();
            brutefinder = new Brutefinder();
            brutefinder.init(world);
            brutefinder.makeDatabase();
            brutefinderData = brutefinder.ouputDatabase();
            System.out.print(System.currentTimeMillis()-time);
        }

        returnData = new LinkedList<>();
        returnData.add("Master:World");
        for(int i=0;i<worldData.size();i++) {
            returnData.add(worldData.get(i));
        }

        returnData.add("Master:Gamemode");
        for (int i=0; i<playerChoice.length; i++){
            if(playerChoice[i]!="4") {
                returnData.add(playerChoice[i]);
            }
        }

        returnData.add("Master:Editdata");
        returnData.add(grid.length+";"+grid[0].getStringGrid().length+";"+grid[0].getStringGrid()[0].length+";"+gridSize);
        for(int i=0;i<grid.length;i++)
        {
            String[][] data = grid[i].getStringGrid();
            for(int j=0;j<data.length;j++)
            {
                for(int k=0;k<data[j].length;k++){
                    if(!data[j][k].equals("E")) {
                        returnData.add(i + ";" + j + ";" + k + ";" + data[j][k]);
                    }
                }
            }
        }


        if (calcDatabase){
            returnData.add("Master:Brutefinder");
            for(int i=0;i<brutefinderData.size();i++) {
                returnData.add(brutefinderData.get(i));
            }
        }
        done=true;
    }
    public boolean isDone(){return done;}
    public LinkedList<String> getData(){
        if(done){
            return returnData;
        }
        return null;
    }
    public String getProgress(){
        if(brutefinder!=null)
        {
            return brutefinder.getProgress();
        }
        return "Exporting...";
    }

}
