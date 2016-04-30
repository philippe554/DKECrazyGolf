package CrazyGolf.Bot.Brutefinder;

/**
 * Created by pmmde on 4/29/2016.
 */
public class Connection {
    int direction;
    int power;

    Node start;
    Node end;

    public Connection(int d,int p,Node s,Node e){
        direction=d;
        power=p;
        start=s;
        end=e;
    }
}
