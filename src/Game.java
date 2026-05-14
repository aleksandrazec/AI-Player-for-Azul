import java.util.ArrayList;
import java.util.List;

public class Game {
    private Board playerOne;
    private Board playerTwo;
    private List<Factory> factories;
    private CenterOfTable centerOfTable;
    private TilesBag tilesBag;
    public Game(){
        this.playerOne=new Board();
        this.playerTwo=new Board();
        this.tilesBag=new TilesBag();
        this.centerOfTable=new CenterOfTable();
        this.factories=initializeFactories();
    }
    private ArrayList<Factory> initializeFactories(){
//        to be implemented, need 5 factories
        return new ArrayList<Factory>();
    }
}
