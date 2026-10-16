package command;

public class ReplayCommand implements Command {

    @Override
    public void execute(robot.Robot robot) {
        robot.replay();
    }

    @Override
    public boolean isRecordable() {
        return false;
    }
}
