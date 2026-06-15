import java.util.List;

public class Minimax {
    // call initially with alpha=-inf, and beta=+inf
    protected int minimax(Game gameState, int depth, int alpha, int beta, boolean isMax){
        System.out.println("Minimax at depth "+depth);
        if(gameState.isTerminal()){
            return gameState.getUtilityValue(isMax);
        }
        if(depth<=0){
            return gameState.evaluateState();
        }
        if(isMax){
            int maxEval = Integer.MIN_VALUE;
            List<Game> potentialStates = gameState.getPotentialGameStates();
            for (Game potentialState : potentialStates) {
                int eval= minimax(potentialState, depth-1, alpha,beta,false);
                maxEval=Math.max(maxEval, eval);
                alpha=Math.max(alpha,eval);
                if(beta<=alpha){
                    break;
                }
            }
            return maxEval;
        }else{
            int minEval = Integer.MAX_VALUE;
            List<Game> potentialStates = gameState.getPotentialGameStates();
            for (Game potentialState : potentialStates) {
                int eval= minimax(potentialState, depth-1,alpha, beta, true);
                minEval=Math.min(minEval, eval);
                beta=Math.min(beta,eval);
                if(beta<=alpha){
                    break;
                }
            }
            return minEval;
        }
    }

}
