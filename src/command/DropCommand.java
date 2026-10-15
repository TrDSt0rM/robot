package command;

import robot.Robot;

public class DropCommand implements Command {

    @Override
    public void execute(Robot robot) {
        robot.drop();
    }
}
