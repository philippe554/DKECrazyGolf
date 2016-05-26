package CrazyGolf.Editor;

import CrazyGolf.Bot.Brutefinder.Brutefinder;
import CrazyGolf.PhysicsEngine.World;
import CrazyGolf.PhysicsEngine.WorldContainer;
import CrazyGolf.PhysicsEngine.WorldGPUBotOpti;

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
        LinkedList<String> brutefinderData = null;
        WorldContainer world = new WorldContainer();
        for(int i=0;i<grid.length;i++) {
            world.loadWorld(grid[i].getStringGrid(), gridSize, 50*i);
        }
        LinkedList<String> worldData = world.outputWorldApi2();

        boolean calcDatabase = false;

        for(int i=0;i<playerChoice.length;i++)
        {
            if(playerChoice[i]=="1")
            {
                calcDatabase=true;
            }
        }

        if (calcDatabase){
            World worldWithPhysics = new WorldGPUBotOpti(worldData);
            brutefinder = new Brutefinder();
            brutefinder.init(worldWithPhysics);
            brutefinder.makeDatabase();
            brutefinderData = brutefinder.ouputDatabase();
        }

        returnData = new LinkedList<>();
        returnData.add("Master:World");
        for(int i=0;i<worldData.size();i++)
        {
            returnData.add(worldData.get(i));
        }

        returnData.add("Master:Gamemode");
        for (int i=0; i<playerChoice.length; i++){
            if(playerChoice[i]!="3") {
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
                    returnData.add(i+";"+j+";"+k+";"+data[j][k]);
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
