import java.util.ArrayList;
import java.util.List;

public class TilesBag {
    // 20 tiles of each type
    private List<Tile> blackTiles;
    private List<Tile> cyanTiles;
    private List<Tile> blueTiles;
    private List<Tile> yellowTiles;
    private List<Tile> redTiles;
    public TilesBag(){
        blackTiles=initializeTileList(TileType.black);
        cyanTiles=initializeTileList(TileType.cyan);
        blueTiles=initializeTileList(TileType.blue);
        yellowTiles=initializeTileList(TileType.yellow);
        redTiles=initializeTileList(TileType.red);
    }
    private List<Tile> initializeTileList(TileType type){
        List<Tile> tileList=new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            tileList.add(new Tile(type));
        }
        return tileList;
    }
    public List<Tile> pullFourTiles(){
//    to be implemented
        return new ArrayList<Tile>();
    }
}
