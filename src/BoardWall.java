public class BoardWall {
    public final TileType[][] wallPattern;
    private boolean[][] wall;
    public BoardWall(){
        wallPattern=initializeWallPattern();
        wall=new boolean[5][5];
    }
    private TileType[][] initializeWallPattern(){
        TileType[][] wallPatternMatrix=new TileType[5][5];
        for (int i = 0; i < wallPatternMatrix.length; i++) {
            wallPatternMatrix[i][i]=TileType.blue;
            wallPatternMatrix[i][(i+1)%5]=TileType.yellow;
            wallPatternMatrix[i][(i+2)%5]=TileType.red;
            wallPatternMatrix[i][(i+3)%5]=TileType.black;
            wallPatternMatrix[i][(i+4)%5]=TileType.cyan;
        }
        return wallPatternMatrix;
    }

}
