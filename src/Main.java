import java.util.ArrayList;
public class Main {
    public static void main(String[] args) {
        Game game=new Game();
        GameGUI gameGUI=game.getGameGUI();
        Minimax minimax = new Minimax();
        while (!game.isFinished()) {
            if (game.maxTurn) {
                System.out.println("Bot is thinking...");

                ArrayList<Game> potentialStates = game.getPotentialGameStates();
//                System.out.println("Number of potential states is: "+potentialStates.size());
                if (!potentialStates.isEmpty()) {
                    Game bestState = null;
                    int bestValue = Integer.MIN_VALUE;

                    for (Game potentialState : potentialStates) {
                        int value = minimax.minimax(potentialState, 3, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
                        if (value >= bestValue) {
                            bestValue = value;
                            bestState = potentialState;
                        }
//                        System.out.println("Checked potential state "+potentialStates.indexOf(potentialState));
                    }

                    if (bestState != null) {
                        int[] move= game.findPotentialGameState(potentialStates.indexOf(bestState));
//                        System.out.println("Move is from factory: "+move[0]+" tile of type: "+move[1]+" to pattern line "+move[2]);
                        if(move[0]!=-1&&move[1]!=-1&&move[2]!=-1) {
                            gameGUI.playTurn(move[0], move[1], move[2]);
                            System.out.println("Bot made a move.");
                            System.out.println("Your turn.");
                        }
                    }
                } else {
                    System.out.println("No valid moves for bot!");
                    break;
                }
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Game Over!");
        System.out.println("Final Score - Bot: " + game.getMax().getCurrentPoints() +
                ", Player: " + game.getMin().getCurrentPoints());
        gameGUI.gameOver(game.getMax().getCurrentPoints(), game.getMin().getCurrentPoints());
    }
}