import java.util.ArrayList;
import java.util.List;

public class CenterOfTable {
    private List<Tile> tiles;
    public CenterOfTable(){
        this.tiles=new ArrayList<>();
        tiles.add(new Tile(TileType.startingPlayerMarker));
    }
    public boolean isEmpty(){
        return tiles.isEmpty();
    }
    public void addTiles(List<Tile> tilesToAdd){
        tiles.addAll(tilesToAdd);
    }
    public List<Tile> takeTiles(TileType type){
        List<Tile> takenTiles=new ArrayList<>();
        for (int i = tiles.size()-1; i >= 0; i--) {
            if(tiles.get(i).type==TileType.startingPlayerMarker){
                takenTiles.add(tiles.remove(i));
            }
            if(tiles.get(i).type==type) {
                takenTiles.add(tiles.remove(i));
            }
        }
        return takenTiles;
    }
}
