package CrazyGolf.Editor;

import java.util.LinkedList;

/**
 * Created by pmmde on 5/26/2016.
 */
public class Import {
    private Grid[] grid;
    private double gridSize;
    private String[] playerChoice;
    public Import(LinkedList<String> file){
        int sort=0;
        LinkedList<String> EditData = new LinkedList<>();
        LinkedList<String> gamemodeData = new LinkedList<>();
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
            }else if(sort==1)
            {
                gamemodeData.add(file.get(i));
            }else if(sort==3)
            {
                EditData.add(file.get(i));
            }
        }
        //TODO: change the 5
        playerChoice = new String[5];
        for(int i=0;i<gamemodeData.size();i++){
            playerChoice[i]=gamemodeData.get(i);
        }
        for(int i=gamemodeData.size();i<5;i++){
            playerChoice[i]="4";
        }
        String[] size = EditData.get(0).split(";");
        grid = new Grid[Integer.parseInt(size[0])];
        gridSize = Double.parseDouble(size[3]);
        for(int i=0;i<grid.length;i++)
        {
            grid[i]=new Grid();
        }
        for(int i=1;i<EditData.size();i++){
            String[] data = EditData.get(i).split(";");
            grid[Integer.parseInt(data[0])].getStringGrid()[Integer.parseInt(data[1])][Integer.parseInt(data[2])]=data[3];
        }
    }
    public Grid[] getGrid(){
        return grid;
    }
    public String[] getPlayerChoice(){
        return playerChoice;
    }
    public double getGridSize(){
        return gridSize;
    }
}
