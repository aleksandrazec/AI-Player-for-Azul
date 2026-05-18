public class Board {
    private int currentPoints;
    private final BoardWall boardWall;
    private final BoardFloorLine boardFloorLine;
    private final BoardPatternLine[] boardPatternLines;
    private boolean startingPlayerMarker;
    private final TileBox tileBox;
    private final CenterOfTable centerOfTable;

//    public Board(int currentPoints, BoardWall boardWall, BoardFloorLine boardFloorLine, BoardPatternLine[] boardPatternLines, boolean startingPlayerMarker, TileBox tileBox) {
//        this.currentPoints = currentPoints;
//        this.boardWall = boardWall;
//        this.boardFloorLine = boardFloorLine;
//        this.boardPatternLines = boardPatternLines;
//        this.startingPlayerMarker = startingPlayerMarker;
//        this.tileBox = tileBox;
//    }

    public Board(TileBox tileBox, CenterOfTable centerOfTable){
        this.tileBox=tileBox;
        this.centerOfTable=centerOfTable;
        boardWall=new BoardWall();
        boardFloorLine=new BoardFloorLine(this,tileBox);
        boardPatternLines=new BoardPatternLine[]{
            new BoardPatternLine(1,boardWall,tileBox, boardFloorLine),
            new BoardPatternLine(2,boardWall,tileBox, boardFloorLine),
            new BoardPatternLine(3,boardWall,tileBox, boardFloorLine),
            new BoardPatternLine(4,boardWall,tileBox, boardFloorLine),
            new BoardPatternLine(5,boardWall,tileBox, boardFloorLine)
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
        currentPoints+=boardFloorLine.calculateMinusPoints();
    }
    public void scoreFinalPoints(){
        currentPoints+=2*boardWall.getNumberOfFullRows();
        currentPoints+=7*boardWall.getNumberOfFullCols();
        currentPoints+=10*boardWall.getNumberOfFullDiagonals();
    }

}
