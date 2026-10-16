package command;

import lombok.Getter;
import robot.Robot;

import java.util.List;

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

    @Override
    public List<Command> mergeWith(Command other) {
        return other.mergeWithMove(this);
    }

    @Override
    public List<Command> mergeWithMove(MoveCommand previous) {
        return List.of(new MoveCommand(previous.getMove() + this.move));
    }
}
