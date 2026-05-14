public class Board {
    private int currentPoints;
    private BoardWall boardWall;
    private BoardFloorLine boardFloorLine;
    private BoardPatternLine firstPatternLine;
    private BoardPatternLine secondPatternLine;
    private BoardPatternLine thirdPatternLine;
    private BoardPatternLine fourthPatternLine;
    private BoardPatternLine fifthPatternLine;

    public Board(){
        boardWall=new BoardWall();
        boardFloorLine=new BoardFloorLine();
        firstPatternLine=new BoardPatternLine(1);
        secondPatternLine=new BoardPatternLine(2);
        thirdPatternLine= new BoardPatternLine(3);
        fourthPatternLine=new BoardPatternLine(4);
        fifthPatternLine=new BoardPatternLine(5);
        currentPoints=0;
    }

}
