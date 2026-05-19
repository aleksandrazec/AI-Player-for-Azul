import java.security.SecureRandom;

public class Game {
    private Board playerOne;
    private Board playerTwo;
    // both should be represented as arrays of length 7, where at index
    // 0-6 u find how many tiles there are of that type
    private int[] tileBox;
    private int[] tileBag;
    // each factory has four or zero tiles and there is five factories
    private int[][] factories;
    // center of table has starting place marker when initialized
    private int[] centerOfTable;
    private boolean playerOnesTurn;
    private SecureRandom random;
    public Game(){
        random=new SecureRandom();
        playerOnesTurn=true;

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
        playerOne=new Board(this, true);
        playerTwo=new Board(this,false);
    }

    public Game(int[] tileBox, int[] tileBag, int[][] factories, int[] centerOfTable, boolean playerOnesTurn) {
        random=new SecureRandom();
        this.tileBox = tileBox;
        this.tileBag = tileBag;
        this.factories = factories;
        this.centerOfTable = centerOfTable;
        this.playerOnesTurn = playerOnesTurn;
    }
    private void assignPlayers(Board playerOne, Board playerTwo){
        this.playerOne=playerOne;
        this.playerTwo=playerTwo;
    }
    public Game copy(){
        Game gameCopy= new Game(tileBox, tileBag, factories, centerOfTable, playerOnesTurn);
        gameCopy.assignPlayers(playerOne.copy(gameCopy), playerTwo.copy(gameCopy));
        return gameCopy;
    }

    protected Game getCurrentGameState(){
        return this.copy();
    }

    protected boolean playTurn(int factoryIndex, int typeToTake, int patternLine){
        if(factoriesAreEmpty() && tileArrayIsEmpty(centerOfTable)){
            if(playerOne.isRowFinished() || playerTwo.isRowFinished()){
                playerOne.scoreFinalPoints();
                playerTwo.scoreFinalPoints();
                return false;
            }else{
                playerOne.scorePoints();
                playerTwo.scorePoints();
                for (int i = 0; i < 5; i++) {
                    factories[i]=pullFourTilesFromBag();
                }
                centerOfTable[0]=1;
                return true;
            }
        }
        if(playerOnesTurn){
            playerOne.playTurn(factoryIndex, typeToTake, patternLine);
        }else{
            playerTwo.playTurn(factoryIndex, typeToTake, patternLine);
        }
        return true;
    }
    protected void setPlayerOnesTurn(boolean value){
        playerOnesTurn=value;
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
}
