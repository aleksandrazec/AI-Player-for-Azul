public class Board {
    private int currentPoints;
    private final BoardWall boardWall;
    private final BoardFloorLine boardFloorLine;
    private final BoardPatternLine[] boardPatternLines;
    private boolean startingPlayerMarker;
    private final TileBox tileBox;

    public Board(TileBox tileBox){
        this.tileBox=tileBox;
        boardWall=new BoardWall();
        boardFloorLine=new BoardFloorLine(this,tileBox);
        boardPatternLines=new BoardPatternLine[]{
            new BoardPatternLine(1,boardWall,tileBox),
            new BoardPatternLine(2,boardWall,tileBox),
            new BoardPatternLine(3,boardWall,tileBox),
            new BoardPatternLine(4,boardWall,tileBox),
            new BoardPatternLine(5,boardWall,tileBox)
        };
        currentPoints=0;
        startingPlayerMarker=false;
    }
    public void setStartingPlayerMarker(boolean value){
        startingPlayerMarker=value;
    }
    public boolean isRowFinished(){
        return boardWall.isRowFinished();
    }
    public int getCurrentPoints(){
        return currentPoints;
    }
    public int getNumberOfFilledRows(){
        return boardWall.getNumberOfFullRows();
    }
    // actually args should have list of lines and amounts, if u want to put tiles in different spots
    // also value 6 should be to put in floor line ig
    public void playTurn(CenterOfTable centerOfTable,Factory factory, TileType type, int line){
        if(factory!=null){
            boardPatternLines[line].placeTiles(factory.takeTiles(type, centerOfTable));
        }else{
            boardPatternLines[line].placeTiles(centerOfTable.takeTiles(type));
        }
    }
    public void scorePoints(){
        for (BoardPatternLine patternLine : boardPatternLines) {
            currentPoints+=patternLine.scoreRow();
        }
    }
}
