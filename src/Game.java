import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private final Board playerOne;
    private final Board playerTwo;
    private final List<Factory> factories;
    private final CenterOfTable centerOfTable;
    private final TilesBag tilesBag;
    private final TileBox tileBox;
    private boolean playerOnesTurn;
    private final SecureRandom random;
    public Game(){
        this.tileBox=new TileBox();
        this.tilesBag=new TilesBag(tileBox);
        this.centerOfTable=new CenterOfTable();
        this.factories=initializeFactories();
        this.playerOne=new Board(tileBox,centerOfTable);
        this.playerTwo=new Board(tileBox,centerOfTable);
        random = new SecureRandom();
//        playerOnesTurn= random.nextBoolean();
        playerOnesTurn=true;
    }
    private ArrayList<Factory> initializeFactories(){
        ArrayList<Factory> factoriesList =new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            factoriesList.add(new Factory(tilesBag.pullFourTiles()));
        }
        return factoriesList;
    }
    public boolean isPlayerOnesTurn(){
        return playerOnesTurn;
    }
//    public GameState getCurrentGameState(){
//        return new GameState(playerOne, playerTwo, factories, centerOfTable);
//    }
//    public List<GameState> getPossibleGameStates(){
//        return new ArrayList<>();
//    }
}
