package command;

import robot.Robot;

public class WBackCommand implements Command {

    @Override
    public void execute(Robot robot) {
        robot.goBack();
    }

    @Override
    public boolean isRecordable() {
        return false;
    }
}
