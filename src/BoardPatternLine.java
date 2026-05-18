import java.util.ArrayList;
import java.util.List;

public class BoardPatternLine {
    private List<Tile> patternLine;
    private final int length;
    private final BoardWall boardWall;
    private final BoardFloorLine boardFloorLine;
    private final TileBox tileBox;
    public BoardPatternLine(int length, BoardWall wall, TileBox tileBox, BoardFloorLine boardFloorLine){
        patternLine=new ArrayList<Tile>();
        this.length=length;
        this.boardWall =wall;
        this.tileBox=tileBox;
        this.boardFloorLine=boardFloorLine;
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

        if(patternLine.isEmpty()){
            for (int i = 1; i < tiles.size(); i++) {
                if(tiles.get(i).type!=tiles.get(i-1).type){
                    return false;
                }
            }
        }else {
            for (Tile tile : tiles) {
                if (tile.type != this.getType()) {
                    return false;
                }
            }
        }

        if(tiles.get(0).type==TileType.startingPlayerMarker){
            return false;
        }

        int numOfExtraTiles=patternLine.size()+tiles.size()-length;
        if(numOfExtraTiles>0){
            List<Tile> extraTiles = new ArrayList<Tile>(patternLine.subList(numOfExtraTiles, tiles.size()));
            for (int j=tiles.size()-1; j>numOfExtraTiles; --j) {
                tiles.remove(j);
            }
            boardFloorLine.addMinusPoints(extraTiles);
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
