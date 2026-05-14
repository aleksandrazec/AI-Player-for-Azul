import java.util.ArrayList;
import java.util.List;

public class CenterOfTable {
    private List<Tile> tiles;
    private StartingPlayerMarker startingPlayerMarker;
    public CenterOfTable(){
        this.tiles=new ArrayList<Tile>();
        this.startingPlayerMarker= new StartingPlayerMarker();
    }
}
