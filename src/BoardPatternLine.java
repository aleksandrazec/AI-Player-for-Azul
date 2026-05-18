import java.util.ArrayList;
import java.util.List;

public class BoardPatternLine {
    private List<Tile> patternLine;
    private final int length;
    private final BoardWall boardWall;
    private final TileBox tileBox;
    public BoardPatternLine(int length, BoardWall wall, TileBox tileBox){
        patternLine=new ArrayList<Tile>();
        this.length=length;
        this.boardWall =wall;
        this.tileBox=tileBox;
    }

    /**
     *
     * @param tiles list of tiles to be placed on pattern line
     * @return the success of the placement
     */
    public boolean placeTiles(List<Tile> tiles){
        if(boardWall.rowContainsTile(length,tiles.get(0).type)){
            return false;
        }
        for (Tile tile : tiles) {
            if(tile.type!=this.getType() || tile.type==TileType.startingPlayerMarker){
                return false;
            }
        }
        if(patternLine.size()+tiles.size()>length){
            return false;
        }
        patternLine.addAll(tiles);
        return true;
    }
    public boolean isEmpty(){
        return patternLine.isEmpty();
    }
    public boolean isFull(){
        return patternLine.size()==length;
    }
    public TileType getType(){
        return patternLine.get(0).type;
    }
    public int scoreRow(){
        int value=0;
        if(isFull()){
            value=boardWall.placeTile(length,patternLine.remove(0));
            tileBox.addTiles(patternLine);
            patternLine.clear();
        }
        return value;
    }
}
