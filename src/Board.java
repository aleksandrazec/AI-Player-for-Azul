public class Board {
    private int currentPoints;
    private Game game;
    private final boolean isPlayerOne;
    // should be 5 x 5, true if tile is set, false otherwise
    private int[][] wall;
    private boolean rowFinished;
    // should have max sum 7, has to reject access right away
    private int[] floorLine;
    // should have 5 pattern lines, of sums 1:5
    private int[][] patternLines;
    public Board(Game game, boolean playerOne) {
        this.game = game;
        this.isPlayerOne =playerOne;
        currentPoints = 0;
        wall = new int[5][5];
        rowFinished = false;
        floorLine = new int[6];
        patternLines = new int[5][6];
    }

    public Board(int currentPoints, Game game, boolean isPlayerOne, int[][] wall, boolean rowFinished, int[] floorLine, int[][] patternLines) {
        this.currentPoints = currentPoints;
        this.game = game;
        this.isPlayerOne = isPlayerOne;
        this.wall = wall;
        this.rowFinished = rowFinished;
        this.floorLine = floorLine;
        this.patternLines = patternLines;
    }
    public Board copy(Game game1){
        return new Board(currentPoints, game1, isPlayerOne, wall, rowFinished, floorLine, patternLines);
    }
    protected void playTurn(int factoryIndex, int typeToTake, int patternLine){
        if(factoryIndex==5){
            int[] takenTiles= game.takeTilesFromCenterOfTable(typeToTake);
            if(takenTiles[0]==1){
                takenTiles[0]=0;
                int[] minusPoints=new int[6];
                minusPoints[0]=1;
                addMinusPoints(minusPoints);
            }
             placeTilesOnPatternLine(patternLine, takenTiles);
        }else{
            placeTilesOnPatternLine(patternLine, game.takeTilesFromFactory(factoryIndex, typeToTake));
        }
    }
    protected int getCurrentPoints() {
        return currentPoints;
    }
    protected void scorePoints(){
        for (int i = 0; i < patternLines.length; i++) {
            currentPoints+=scoreRow(i);
        }
        currentPoints+=calculateMinusPoints();
    }
    protected void scoreFinalPoints(){
        currentPoints+=2*getNumberOfFullRows();
        currentPoints+=7*getNumberOfFullCols();
        currentPoints+=10*getNumberOfFullDiagonals();
    }
    protected boolean tileArrayIsEmpty(int[] array){
        for (int i = 0; i < array.length; i++) {
            if(array[i]>0){
                return false;
            }
        }
        return true;
    }
    protected boolean patternLineIsFull(int index){
        boolean full=false;
        for (int i = 1; i < 6; i++) {
            if(patternLines[index][i]==index){
                full=true;
                break;
            }
        }
        return full;
    }
    protected boolean placeTilesOnPatternLine(int index, int[] tiles){
        int type=0;
        for (int i = 1; i < 6; i++) {
            if(tiles[i]>0){
                type=i;
                break;
            }
        }
        int amount=tiles[type];
        if(wallRowContainsTile(index, type) || type==0){
            return false;
        }
        if(!tileArrayIsEmpty(patternLines[index])){
            if(getTypeOfPatternLine(index)!=type){
                return false;
            }
        }
        if(patternLineIsFull(index)){
            return false;
        }
        int numOfExtraTiles=getTotalTilesInArray(patternLines[index])+amount-index;
        if(numOfExtraTiles>0){
            int[] extraTiles = new int[6];
            extraTiles[type]=numOfExtraTiles;
            amount-=numOfExtraTiles;
            addMinusPoints(extraTiles);
        }
        patternLines[index][type]+=amount;
        return true;
    }
    protected int getTypeOfPatternLine(int index){
        int type=-1;
        for (int i = 1; i < 6; i++) {
            if(patternLines[index][i]>0){
                type=i;
                break;
            }
        }
        return type;
    }

    private int scoreRow(int index){
        int value=0;
        if(getTotalTilesInArray(patternLines[index])==index){
            value=placeTileOnWall(index, getTypeOfPatternLine(index));
            patternLines[index][getTypeOfPatternLine(index)]--;
            game.addTilesToTileBox(patternLines[index]);
        }
        return value;
    }
    private int getTotalTilesInArray(int[] tiles){
        int total=0;
        for (int i = 0; i < 6; i++) {
            total+=tiles[i];
        }
        return total;
    }
    protected void addMinusPoints(int[] tiles){
        if(getTotalTilesInArray(floorLine)+getTotalTilesInArray(tiles)<= StaticGameData.floorLineValues.length){
            for (int i = 0; i < floorLine.length; i++) {
                floorLine[i]+=tiles[i];
            }
        }else{
            int temp = StaticGameData.floorLineValues.length-getTotalTilesInArray(floorLine);
            int[] forTileBox=new int[6];
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < tiles[i]; j++) {
                    if(temp>0) {
                        floorLine[i]++;
                        tiles[i]--;
                        temp--;
                    }else{
                        forTileBox[i]++;
                        tiles[i]--;
                    }
                }
            }
            game.addTilesToTileBox(forTileBox);
        }
    }
    protected int calculateMinusPoints(){
        int minusPoints=0;
        int size = Math.min(getTotalTilesInArray(floorLine), 7);
        for (int i = 0; i < size; i++) {
            minusPoints+= StaticGameData.floorLineValues[i];
        }
        if(floorLine[0]==1){
            game.setPlayerOnesTurn(isPlayerOne);
            floorLine[0]=0;
        }
        game.addTilesToTileBox(floorLine);
        floorLine=new int[6];
        return minusPoints;
    }

    protected boolean isRowFinished(){
        return rowFinished;
    }
    protected boolean wallRowContainsTile(int row, int type){
        return wall[row][getColIndexBasedOnType(row,type)]==1;
    }
    private int getColIndexBasedOnType(int row, int type){
        int col=-1;
        switch (type){
            case 1 -> col=row;
            case 2 -> col=(row+1)%5;
            case 3 -> col=(row+2)%5;
            case 4 -> col=(row+3)%5;
            case 5 -> col=(row+4)%5;
        }
        return col;
    }
    protected int getNumberOfFullRows(){
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
    protected int getNumberOfFullCols(){
        int[] colAmount = new int[5];
        for (int i = 0; i < wall.length; i++) {
            for (int j = 0; j < wall[0].length; j++) {
                if(wall[i][j]==1){
                    colAmount[j]++;
                }
            }
        }
        return colAmount[0]+colAmount[1]+colAmount[2]+colAmount[3]+colAmount[4];
    }
    protected int getNumberOfFullDiagonals(){
        int[] tilesPerColor = new int[6];
        for (int i = 0; i < wall.length; i++) {
            for (int j = 0; j < wall[i].length; j++) {
                if(wall[i][j]==1){
                    tilesPerColor[StaticGameData.wallPattern[i][j]]++;
                }
            }
        }
        int num=0;
        for (int i = 1; i < 6; i++) {
            if(tilesPerColor[i]==5){
                num++;
            }
        }
        return num;
    }
    /**
     *
     * @param row int of the pattern line from which the tile is getting transferred
     * @param type of tile being transferred
     * @return number of points gotten from the placement of the tile
     */
    protected int placeTileOnWall(int row, int type){
        if(type==0){
            return 0;
        }
        int col= getColIndexBasedOnType(row,type);
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
