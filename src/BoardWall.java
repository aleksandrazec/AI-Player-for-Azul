public class BoardWall {
    public final TileType[][] wallPattern;
    private int[][] wall;
    private boolean rowFinished;
    public BoardWall(){
        wallPattern=initializeWallPattern();
        wall=new int[5][5];
        rowFinished=false;
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

    public boolean rowContainsTile(int row, TileType type){
        return wall[row][getColNumber(row, type)] == 1;
    }
    /**
     *
     * @param row int of the pattern line from which the tile is getting transferred
     * @param tile object of the tile being transferred
     * @return number of points gotten from the placement of the tile
     */
    public int placeTile(int row, Tile tile){
        if(tile.type==TileType.startingPlayerMarker){
            return 0;
        }
        int col=getColNumber(row,tile.type);
        wall[row][col]=1;
        int verticalNeighbors=getVerticalNeighbors(row,col);
        int horizontalNeighbors=getHorizontalNeighbors(row,col);
        if(verticalNeighbors+horizontalNeighbors==0){
            return 1;
        }
        if(verticalNeighbors==0){
            if(calculateNumOfHorizontal(row,col)==5){
                rowFinished=true;
            }
            return calculateNumOfHorizontal(row,col);
        }
        if(horizontalNeighbors==0){
            return calculateNumOfVertical(row,col);
        }
        if(calculateNumOfHorizontal(row,col)==5){
            rowFinished=true;
        }
        return calculateNumOfHorizontal(row,col) + calculateNumOfVertical(row,col);
    }
    public boolean isRowFinished(){
        return rowFinished;
    }
    public int getNumberOfFullRows(){
        int num=0;
        boolean temp;
        for (int i = 0; i < wall.length; i++) {
            temp=true;
            for (int j = 0; j < wall[0].length; j++) {
                if(wall[i][j]==0){
                    temp=false;
                }
            }
            if(temp){
                num++;
            }
        }
        return num;
    }
    private int calculateNumOfVertical(int row, int col){
        int points=0;
        int tempCol=col;
        while(wall[row][tempCol-1]==1){
            points++;
            tempCol--;
        }
        while (wall[row][col+1]==1){
            points++;
            col++;
        }
        return points+1;
    }
    private int calculateNumOfHorizontal(int row, int col){
        int points=0;
        int tempRow=row;
        while(wall[tempRow-1][col]==1){
            points++;
            tempRow--;
        }
        while (wall[row+1][col]==1){
            points++;
            row++;
        }
        return points+1;
    }
    private int getColNumber(int row,TileType type){
        int col=-1;
        switch (type){
            case blue -> col=row;
            case yellow -> col=(row+1)%5;
            case red -> col=(row+2)%5;
            case black -> col=(row+3)%5;
            case cyan -> col=(row+4)%5;
        }
        return col;
    }
    private int getVerticalNeighbors(int rowIndex, int colIndex){
        int up=0;
        int down=0;
        if(rowIndex!=4){
            up = wall[rowIndex + 1][colIndex];
        }
        if(rowIndex!=0){
            down = wall[rowIndex - 1] [colIndex];
        }
        return up+down;
    }
    private int getHorizontalNeighbors(int rowIndex, int colIndex){
        int right=0;
        int left=0;
        if(colIndex!=4){
            right = wall[rowIndex][colIndex+1];
        }
        if(colIndex!=0){
            left = wall[rowIndex] [colIndex-1];
        }
        return right+left;
    }
}
