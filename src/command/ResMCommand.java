package command;

public class ResMCommand implements Command {

    @Override
    public void execute(robot.Robot robot) {
        robot.resetHistory();
    }

    @Override
    public boolean isRecordable() {
        return false;
    }
}
