import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;

public class Game {
    private Board max;
    private Board min;
    // both should be represented as arrays of length 7, where at index
    // 0-6 u find how many tiles there are of that type
    private int[] tileBox;
    private int[] tileBag;
    // each factory has four or zero tiles and there is five factories
    private int[][] factories;
    // center of table has starting place marker when initialized
    private int[] centerOfTable;
    protected boolean maxTurn;
    private SecureRandom random;
    private boolean isFinished;
    GameGUI gameGUI;
    public Game(){
        random=new SecureRandom();
        maxTurn =false;
        isFinished=false;

        tileBox=new int[6];
        tileBag=new int[6];

        for (int i = 1; i < tileBag.length; i++) {
            tileBag[i]=20;
        }

        centerOfTable=new int[6];
        centerOfTable[0]=1;

        factories=new int[5][];
        for (int i = 0; i < 5; i++) {
            factories[i]=pullFourTilesFromBag();
        }
        max =new Board(this, true);
        min =new Board(this,false);

        try {
            gameGUI=new GameGUI(this,factories);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Game(int[] tileBox, int[] tileBag, int[][] factories, int[] centerOfTable, boolean playerOnesTurn) {
        random=new SecureRandom();
        this.tileBox = tileBox;
        this.tileBag = tileBag;
        this.factories = factories;
        this.centerOfTable = centerOfTable;
        this.maxTurn = playerOnesTurn;
        this.isFinished=false;
    }
    private void assignPlayers(Board playerOne, Board playerTwo){
        this.max =playerOne;
        this.min =playerTwo;
    }
    public Game copy(){
        int[] tileBoxCopy = new int[6];
        System.arraycopy(tileBox, 0, tileBoxCopy, 0, tileBox.length);

        int[] tileBagCopy = new int[6];
        System.arraycopy(tileBag, 0, tileBagCopy, 0, tileBag.length);

        int[][] factoriesCopy = new int[5][6];
        for (int i = 0; i < 5; i++) {
            System.arraycopy(factories[i], 0, factoriesCopy[i], 0, factories[i].length);
        }

        int[] centerOfTableCopy = new int[6];
        System.arraycopy(centerOfTable, 0, centerOfTableCopy, 0, centerOfTable.length);

        Game gameCopy = new Game(tileBoxCopy, tileBagCopy, factoriesCopy, centerOfTableCopy, maxTurn);
        gameCopy.assignPlayers(max.copy(gameCopy), min.copy(gameCopy));
        return gameCopy;
    }
//    protected int evaluateState(){
//        return random.nextInt(-1000,1000);
//    }
protected int evaluateState() {
    final int W_SCORE    = 100;
    final int W_PATTERN  = 20;
    final int W_PARTIAL  = 3;
    final int W_FLOOR    = 10;
    final int W_TOKEN    = 3;
    final int W_ROW      = 5;
    final int W_COL      = 10;
    final int W_DIAG     = 8;
    final int W_INCONV   = 4;
    final int W_GAMEOVER = 25;

    int score = 0;

    score += W_SCORE * (max.getCurrentPoints() - min.getCurrentPoints());

    score += W_PATTERN * (estimatePatternScore(max) - estimatePatternScore(min));

    score += W_PARTIAL * (weightedPartialTiles(max) - weightedPartialTiles(min));

    score += W_FLOOR * (calcFloorPenalty(min.getFloorLine()) - calcFloorPenalty(max.getFloorLine()));

    if (max.getFloorLine()[0] == 1) score += W_TOKEN;
    if (min.getFloorLine()[0] == 1) score -= W_TOKEN;

    score += W_ROW  * (calcRowProgress(max)  - calcRowProgress(min));
    score += W_COL  * (calcColProgress(max)  - calcColProgress(min));
    score += W_DIAG * (calcDiagProgress(max) - calcDiagProgress(min));

    score += W_INCONV * (calcInconvenience(min) - calcInconvenience(max));

    int totalLead = (max.getCurrentPoints() + estimatePatternScore(max))
            - (min.getCurrentPoints() + estimatePatternScore(min));

    if (totalLead > 0) {
        score += W_GAMEOVER * max.getNumberOfFullRows();
    } else if (totalLead < 0) {
        score -= W_GAMEOVER * max.getNumberOfFullRows();
    }
    score -= W_GAMEOVER * min.getNumberOfFullRows();

    return score;

}
    private int weightedPartialTiles(Board board) {
        int[][] patternLines = board.getPatternLines();
        int total = 0;
        for (int row = 0; row < 5; row++) {
            int count = evalCountTiles(patternLines[row]);
            if (count > 0 && count < row + 1) {
                total += count * count;
            }
        }
        return total;
    }
    private int estimatePatternScore(Board board) {
        int[][] patternLines = board.getPatternLines();
        int[][] wall         = board.getWall();
        int total = 0;
        for (int row = 0; row < 5; row++) {
            int type  = evalGetType(patternLines[row]);
            int count = evalCountTiles(patternLines[row]);
            if (type > 0 && count == row + 1) {
                int col = (row + type - 1) % 5;
                total += estimatePlacementScore(wall, row, col);
            }
        }
        return total;
    }

    private int estimatePlacementScore(int[][] wall, int row, int col) {
        int h = 1;
        for (int c = col - 1; c >= 0 && wall[row][c] == 1; c--) h++;
        for (int c = col + 1; c <  5 && wall[row][c] == 1; c++) h++;
        int v = 1;
        for (int r = row - 1; r >= 0 && wall[r][col] == 1; r--) v++;
        for (int r = row + 1; r <  5 && wall[r][col] == 1; r++) v++;
        if (h == 1 && v == 1) return 1;
        if (h == 1) return v;
        if (v == 1) return h;
        return h + v;
    }

    private int calcFloorPenalty(int[] floorLine) {
        int count = 0;
        for (int v : floorLine) count += v;
        count = Math.min(count, 7);
        int penalty = 0;
        for (int i = 0; i < count; i++) penalty -= StaticGameData.floorLineValues[i];
        return penalty;
    }

    private int calcRowProgress(Board board) {
        int[][] wall         = board.getWall();
        int[][] patternLines = board.getPatternLines();
        int progress = 0;
        for (int row = 0; row < 5; row++) {
            int tiles = 0;
            for (int col = 0; col < 5; col++) tiles += wall[row][col];
            int type  = evalGetType(patternLines[row]);
            int count = evalCountTiles(patternLines[row]);
            if (type > 0 && count == row + 1) tiles = Math.min(tiles + 1, 5);
            progress += tiles * tiles;
        }
        return progress;
    }

    private int calcColProgress(Board board) {
        int[][] wall         = board.getWall();
        int[][] patternLines = board.getPatternLines();
        int[] colTiles = new int[5];
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) colTiles[col] += wall[row][col];
            int type  = evalGetType(patternLines[row]);
            int count = evalCountTiles(patternLines[row]);
            if (type > 0 && count == row + 1) {
                int col = (row + type - 1) % 5;
                colTiles[col] = Math.min(colTiles[col] + 1, 5);
            }
        }
        int progress = 0;
        for (int t : colTiles) progress += t * t;
        return progress;
    }

    private int calcDiagProgress(Board board) {
        int[][] wall         = board.getWall();
        int[][] patternLines = board.getPatternLines();
        int[] colorCount = new int[6];
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                if (wall[row][col] == 1) colorCount[StaticGameData.wallPattern[row][col]]++;
            }
            int type  = evalGetType(patternLines[row]);
            int count = evalCountTiles(patternLines[row]);
            if (type > 0 && count == row + 1) colorCount[type] = Math.min(colorCount[type] + 1, 5);
        }
        int progress = 0;
        for (int i = 1; i <= 5; i++) progress += colorCount[i] * colorCount[i];
        return progress;
    }

    private int calcInconvenience(Board board) {
        int[][] patternLines = board.getPatternLines();
        int stuck = 0;
        for (int row = 0; row < 5; row++) {
            int type  = evalGetType(patternLines[row]);
            int count = evalCountTiles(patternLines[row]);
            if (type > 0 && count < row + 1) stuck += (row + 1 - count);
        }
        return stuck;
    }

    private int evalGetType(int[] patternLine) {
        for (int i = 1; i < patternLine.length; i++) {
            if (patternLine[i] > 0) return i;
        }
        return -1;
    }

    private int evalCountTiles(int[] patternLine) {
        int total = 0;
        for (int i = 1; i < patternLine.length; i++) total += patternLine[i];
        return total;
    }
    protected boolean isTerminal(){
        if(isFinished){
            return true;
        }
        if(max.isRowFinished() || min.isRowFinished()){
            isFinished = true;
            return true;
        }
        return false;
    }
    /**
     * @param isMax true if is max player
    * @return utility value of player
     */
    protected int getUtilityValue(boolean isMax){
        int utilityValue;
        if(max.getCurrentPoints() > min.getCurrentPoints()){
            utilityValue=1;
        }else if(max.getCurrentPoints() == min.getCurrentPoints()) {
            if (max.getNumberOfFullRows() > min.getNumberOfFullRows()) {
                utilityValue = 1;
            } else if (max.getNumberOfFullRows() == min.getNumberOfFullRows()) {
                utilityValue = 0;
            } else {
                utilityValue = -1;
            }
        }else {
            utilityValue = -1;
        }
        if(isMax){
            return switch (utilityValue) {
                case -1 -> Integer.MIN_VALUE;
                case 1 -> Integer.MAX_VALUE;
                default -> 0;
            };
        }else{
            return switch (utilityValue) {
                case -1 -> Integer.MAX_VALUE;
                case 1 -> Integer.MIN_VALUE;
                default -> 0;
            };
        }
    }
    protected ArrayList<Game> getPotentialGameStates(){
        ArrayList<Game> potentialStates = new ArrayList<>();
        Game potentialState;
        if(!this.isFinished) {
            if (!centerIsEmpty()) {
//            for each type of tile in center
                for (int i = 0; i < 6; i++) {
                    if (centerOfTable[i] > 0) {
//                    for each potential pattern line
                        for (int j = 0; j < 5; j++) {
                            potentialState = this.copy();
                            if (potentialState.playTurn(5, i, j)) {
//                                System.out.println(5 + " " + i + " " + j);
                                potentialStates.add(potentialState);
                            }
                        }
                    }
                }
            }
//        for each factory possible
            for (int i = 0; i < 5; i++) {
                if (!factoryIsEmpty(i)) {
//            for each type of tile in factory
                    for (int j = 0; j < 6; j++) {
                        if (factories[i][j] > 0) {
//                        for each potential pattern line
                            for (int k = 0; k < 5; k++) {
                                potentialState = this.copy();
                                if (potentialState.playTurn(i, j, k)) {
//                                    System.out.println(i + " " + j + " " + k);
                                    potentialStates.add(potentialState);
                                }
                            }
                        }
                    }
                }
            }
            if (!centerIsEmpty()) {
//            for each type of tile in center
                for (int i = 0; i < 6; i++) {
                    if (centerOfTable[i] > 0) {
//                    for floor line
                        potentialState = this.copy();
                        if (potentialState.playTurn(5, i, 5)) {
//                            System.out.println(5 + " " + i + " " + 5);
                            potentialStates.add(potentialState);
                        }
                    }
                }
            }
//        for each factory possible
            for (int i = 0; i < 5; i++) {
                if (!factoryIsEmpty(i)) {
//            for each type of tile in factory
                    for (int j = 0; j < 6; j++) {
                        if (factories[i][j] > 0) {
//                        for floor line
                            potentialState = this.copy();
                            if (potentialState.playTurn(i, j, 5)) {
//                                System.out.println(i + " " + j + " " + 5);
                                potentialStates.add(potentialState);
                            }
                        }
                    }
                }
            }
        }
        return potentialStates;
    }
    protected int[] findPotentialGameState(int index){
//        index=index+1;
        Game potentialState;
        if(!this.isFinished) {
            if (!centerIsEmpty()) {
//            for each type of tile in center
                for (int i = 0; i < 6; i++) {
                    if (centerOfTable[i] > 0) {
//                    for each potential pattern line
                        for (int j = 0; j < 5; j++) {
                            potentialState = this.copy();
                            if (potentialState.playTurn(5, i, j)) {
                                if (index == 0) {
                                    return new int[]{5, i, j};
                                } else {
                                    index--;
                                }
                            }
                        }
                    }
                }
            }
//        for each factory possible
            for (int i = 0; i < 5; i++) {
                if (!factoryIsEmpty(i)) {
//            for each type of tile in factory
                    for (int j = 0; j < 6; j++) {
                        if (factories[i][j] > 0) {
//                        for each potential pattern line
                            for (int k = 0; k < 5; k++) {
                                potentialState = this.copy();
                                if (potentialState.playTurn(i, j, k)) {
                                    if (index == 0) {
                                        return new int[]{i, j, k};
                                    } else {
                                        index--;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (!centerIsEmpty()) {
//            for each type of tile in center
                for (int i = 0; i < 6; i++) {
                    if (centerOfTable[i] > 0) {
//                    for floor line
                        potentialState = this.copy();
                        if (potentialState.playTurn(5, i, 5)) {
                            if (index == 0) {
                                return new int[]{5, i, 5};
                            } else {
                                index--;
                            }
                        }
                    }
                }
            }
            //        for each factory possible
            for (int i = 0; i < 5; i++) {
                if (!factoryIsEmpty(i)) {
//            for each type of tile in factory
                    for (int j = 0; j < 6; j++) {
                        if (factories[i][j] > 0) {
//                        for floor line
                            potentialState = this.copy();
                            if (potentialState.playTurn(i, j, 5)) {
                                if (index == 0) {
                                    return new int[]{i, j, 5};
                                } else {
                                    index--;
                                }
                            }
                        }
                    }
                }
            }
        }
        return new int[]{-1,-1,-1};
    }
    protected Game getCurrentGameState(){
        return this.copy();
    }
    protected boolean playTurn(int factoryIndex, int typeToTake, int patternLine){
        if(this.isTerminal()){
            return false;
        }
        boolean turnValid=false;

        if(maxTurn){
            if(max.playTurn(factoryIndex, typeToTake, patternLine)){
                maxTurn=!maxTurn;
                turnValid=true;
            }
        }else{
            if(min.playTurn(factoryIndex, typeToTake, patternLine)){
                maxTurn=!maxTurn;
                turnValid=true;
            }
        }

        if(turnValid && factoriesAreEmpty() && tileArrayIsEmpty(centerOfTable)){
            max.scorePoints();
            min.scorePoints();
            if(max.isRowFinished() || min.isRowFinished()){
                max.scoreFinalPoints();
                min.scoreFinalPoints();
                isFinished = true;
            } else {
                if (gameGUI == null) {
                    refillFactoriesBasedOnDistribution();
                } else {
                    for (int i = 0; i < 5; i++) {
                        factories[i] = pullFourTilesFromBag();
                    }
                }
                centerOfTable[0] = 1;
            }
        }

        return turnValid;
    }
    private void refillFactoriesBasedOnDistribution(){
        int tileBagTotal=0;
        for (int i = 1; i < 6; i++) tileBagTotal += tileBag[i];

        if (tileBagTotal >= 20) {
            for (int i = 0; i < 5; i++) {
                refillFactoryBasedOnDistribution(tileBagTotal,i, 4);
            }
        }else{
            int factoryIndex=0;
            while (tileBagTotal>=4 && factoryIndex<5){
                refillFactoryBasedOnDistribution(tileBagTotal, factoryIndex,4);
                factoryIndex++;
                tileBagTotal-=4;
            }
            if(tileBagTotal>0){
                int toAssign=4-tileBagTotal;
                for (int i = 1; i < 6; i++) {
                    factories[factoryIndex][i]+=tileBag[i];
                    tileBagTotal-=tileBag[i];
                    tileBag[i]=0;
                }
                fillTileBag();
                for (int i = 1; i < 6; i++) tileBagTotal += tileBag[i];
                refillFactoryBasedOnDistribution(tileBagTotal, factoryIndex, toAssign);
                factoryIndex++;
                tileBagTotal-=toAssign;
                while (factoryIndex<5){
                    refillFactoryBasedOnDistribution(tileBagTotal, factoryIndex, 4);
                    factoryIndex++;
                    tileBagTotal-=4;
                }
            }else{
                fillTileBag();
                for (int i = 1; i < 6; i++) tileBagTotal += tileBag[i];
                while (factoryIndex<5) {
                    refillFactoryBasedOnDistribution(tileBagTotal, factoryIndex, 4);
                    factoryIndex++;
                    tileBagTotal-=4;
                }
            }
        }
    }
    private void refillFactoryBasedOnDistribution(int tileBagTotal, int factoryIndex, int toAssign){
        int[] tiles = new int[6];
        double[] remainders = new double[6];
        int assigned = 0;
        for (int i = 1; i < 6; i++) {
            double exact=(tileBag[i]* toAssign)/ (double) tileBagTotal;
            tiles[i]= (int) exact;
            remainders[i] = exact-tiles[i];
            assigned+=tiles[i];
        }
        while (assigned<toAssign){
            int best=1;
            for (int i = 2; i < 6; i++) {
                if(remainders[i]>remainders[best]) best=i;
            }
            tiles[best]++;
            remainders[best]=-1;
            assigned++;
        }
        for (int i = 1; i < 6; i++) {
            factories[factoryIndex][i]+=tiles[i];
            tileBag[i]-=tiles[i];
        }
//        System.out.println("assigned factory");
    }
    protected void setMaxTurn(boolean value){
        maxTurn =value;
    }

    private int[] pullFourTilesFromBag(){
        int[] tiles=new int[6];
        for (int i = 0; i < 4; i++) {
            if(tileArrayIsEmpty(tileBag)){
                fillTileBag();
            }
            int temp = 0;
            while(temp!=6) {
                temp = random.nextInt(1, 6);
                if (tileBag[temp] != 0) {
                    tiles[temp]++;
                    tileBag[temp]--;
                    temp=6;
                }
            }
        }
        return tiles;
    }
    protected boolean tileArrayIsEmpty(int[] array){
        for (int i = 0; i < array.length; i++) {
            if(array[i]>0){
                return false;
            }
        }
        return true;
    }
    private void fillTileBag(){
        for (int i = 0; i < tileBag.length; i++) {
            tileBag[i]+=tileBox[i];
            tileBox[i]=0;
        }
    }
    protected boolean addTilesToTileBox(int[] tilesToAdd){
        if(tilesToAdd.length!=6 || tilesToAdd[0]!=0){
            return false;
        }
        for (int i = 1; i < tileBox.length; i++) {
            tileBox[i]+=tilesToAdd[i];
        }
        return true;
    }
    protected boolean addTilesToCenterOfTable(int[] tilesToAdd){
        if(tilesToAdd.length!=6 || (tilesToAdd[0]!=0 && tilesToAdd[0]!=1)){
            return false;
        }
        for (int i = 0; i < centerOfTable.length; i++) {
            centerOfTable[i]+=tilesToAdd[i];
        }
        return true;
    }
    protected int[] takeTilesFromCenterOfTable(int type){
        int[] takenTiles=new int[6];
        if(type!=0){
            if(centerOfTable[0]==1){
                takenTiles[0]=1;
                centerOfTable[0]=0;
            }
            takenTiles[type]=centerOfTable[type];
            centerOfTable[type]=0;
        }
        return takenTiles;
    }
    protected int[] takeTilesFromFactory(int factoryIndex, int type){
        int[] takenTiles=new int[6];
        int[] tilesForCenter=new int[6];
        takenTiles[type]=factories[factoryIndex][type];
        factories[factoryIndex][type]=0;
        for (int i = 1; i < 6; i++) {
            tilesForCenter[i]=factories[factoryIndex][i];
            factories[factoryIndex][i]=0;
        }
        addTilesToCenterOfTable(tilesForCenter);
        return takenTiles;
    }
    protected boolean factoriesAreEmpty(){
        for (int i = 0; i < 5; i++) {
            if(!factoryIsEmpty(i)){
                 return false;
            }
        }
        return true;
    }
    protected boolean factoryIsEmpty(int factoryIndex){
        for (int i = 0; i < 6; i++) {
            if (factories[factoryIndex][i]>0){
                return false;
            }
        }
        return true;
    }
    protected boolean centerIsEmpty(){
        for (int i = 0; i < 6; i++) {
            if (centerOfTable[i]>0){
                return false;
            }
        }
        return true;
    }
    protected int[][] getFactories(){
        return this.factories;
    }

    public int[] getCenterOfTable() {
        return centerOfTable;
    }

    public Board getMax() {
        return max;
    }

    public Board getMin() {
        return min;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public GameGUI getGameGUI() {
        return gameGUI;
    }
}
