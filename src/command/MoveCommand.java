package command;

import robot.Robot;

public class MoveCommand implements Command {

    private final int move;

    public MoveCommand(int move) {
        this.move = move;
    }

    @Override
    public void execute(Robot robot) {
        robot.move(move);
    }
}
