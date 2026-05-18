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

    public int getHeuristicValue(Player player) throws NotATerminalState {
        if (!this.isTerminal()) {
            throw new NotATerminalState("Can't get heuristic value of a non-terminal state");
        }
        int heuristicValue;
        if (max.getCurrentPoints() > min.getCurrentPoints()) {
            heuristicValue = 1;
        } else if (max.getCurrentPoints() == min.getCurrentPoints()) {
            if (max.getNumberOfFilledRows() > min.getNumberOfFilledRows()) {
                heuristicValue = 1;
            } else if (max.getNumberOfFilledRows() == min.getNumberOfFilledRows()) {
                heuristicValue = 0;
            } else {
                heuristicValue = -1;
            }
        } else {
            heuristicValue = -1;
        }
        if (player == Player.max) {
            return heuristicValue;
        } else {
            return heuristicValue * (-1);
        }
    }
}
