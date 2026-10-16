package command;

import lombok.Getter;
import robot.Robot;

public class MoveCommand implements Command {

    @Getter
    private final int move;

    public MoveCommand(int move) {
        this.move = move;
    }

    @Override
    public void execute(Robot robot) {
        robot.move(move);
    }

    @Override
    public Command inverse() {
        return new MoveCommand(-move);
    }
}
