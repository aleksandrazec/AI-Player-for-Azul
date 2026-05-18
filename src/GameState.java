import java.util.List;

public class GameState {
    private final Board max;
    private final Board min;
    private final List<Factory> factories;
    private final CenterOfTable centerOfTable;
    public GameState(Board max, Board min, List<Factory> factories, CenterOfTable centerOfTable) {
        this.centerOfTable = centerOfTable;
        this.factories = factories;
        this.max = max;
        this.min = min;
    }
    public boolean isTerminal() {
        return factories.isEmpty() && centerOfTable.isEmpty() && (max.isRowFinished() || min.isRowFinished());
    }
    public float getEvaluationValue(Player player){
        return 0;
    }

    public int getUtilityValue(Player player) throws NotATerminalState {
        if (!this.isTerminal()) {
            throw new NotATerminalState("Can't get heuristic value of a non-terminal state");
        }
        int utilityValue;
        if (max.getCurrentPoints() > min.getCurrentPoints()) {
            utilityValue = 1;
        } else if (max.getCurrentPoints() == min.getCurrentPoints()) {
            if (max.getNumberOfFilledRows() > min.getNumberOfFilledRows()) {
                utilityValue = 1;
            } else if (max.getNumberOfFilledRows() == min.getNumberOfFilledRows()) {
                utilityValue = 0;
            } else {
                utilityValue = -1;
            }
        } else {
            utilityValue = -1;
        }
        if (player == Player.max) {
            return utilityValue;
        } else {
            return utilityValue * (-1);
        }
    }
}
