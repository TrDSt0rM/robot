package command;

import lombok.Getter;
import robot.Direction;
import robot.Robot;

public class DirectionCommand implements Command {

    @Getter
    private final Direction direction;
    private Direction previousDirection;

    public DirectionCommand(Direction direction) {
        this.direction = direction;
    }

    public DirectionCommand(Direction direction, Direction previousDirection) {
        this.direction = direction;
        this.previousDirection = previousDirection;
    }

    @Override
    public void execute(Robot robot) {
        this.previousDirection = robot.getDirection();
        robot.setDirection(direction);
    }

    @Override
    public Command inverse() {
        return new DirectionCommand(this.previousDirection, this.direction);
    }
}
