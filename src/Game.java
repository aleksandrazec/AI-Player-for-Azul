import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

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
    private boolean maxTurn;
    private SecureRandom random;
    private boolean isFinished;
    public Game(){
        random=new SecureRandom();
        maxTurn =true;
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
        Game gameCopy= new Game(tileBox, tileBag, factories, centerOfTable, maxTurn);
        gameCopy.assignPlayers(max.copy(gameCopy), min.copy(gameCopy));
        return gameCopy;
    }
    protected int evaluateState(){
        return 1;
    }
    protected boolean isTerminal(){
        if(isFinished){
            return true;
        }
        if(factoriesAreEmpty() && tileArrayIsEmpty(centerOfTable)){
            if(max.isRowFinished() || min.isRowFinished()) {
                max.scoreFinalPoints();
                min.scoreFinalPoints();
                isFinished = true;
                return true;
            }
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
    protected List<Game> getPotentialGameStates(){
        ArrayList<Game> potentialStates = new ArrayList<>();
        Game potentialState;
        if(!centerIsEmpty()){
//            for each type of tile in center
            for (int i = 0; i < 6; i++) {
                if (centerOfTable[i]>0){
//                    for each potential pattern line
                    for (int j = 0; j < 5; j++) {
                        potentialState=this.copy();
                        if(potentialState.playTurn(5,i,j)){
                            potentialStates.add(potentialState);
                        }
                    }
                }
            }
        }
//        for each factory possible
        for (int i = 0; i < 6; i++) {
            if(!factoryIsEmpty(i)){
//            for each type of tile in factory
                for (int j = 0; j < 6; j++) {
                    if(factories[i][j]>0){
//                        for each potential pattern line
                        for (int k = 0; k < 5; k++) {
                            potentialState=this.copy();
                            if(potentialState.playTurn(i,j,k)){
                                potentialStates.add(potentialState);
                            }
                        }
                    }
                }
            }
        }
        return potentialStates;
    }
    protected Game getCurrentGameState(){
        return this.copy();
    }
    protected boolean playTurn(int factoryIndex, int typeToTake, int patternLine){
        if(factoriesAreEmpty() && tileArrayIsEmpty(centerOfTable)){
            if(this.isTerminal()){
                return false;
            }else{
                max.scorePoints();
                min.scorePoints();
                for (int i = 0; i < 5; i++) {
                    factories[i]=pullFourTilesFromBag();
                }
                centerOfTable[0]=1;
                return true;
            }
        }
        if(maxTurn){
            return max.playTurn(factoryIndex, typeToTake, patternLine);
        }else{
            return min.playTurn(factoryIndex, typeToTake, patternLine);
        }
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
}
