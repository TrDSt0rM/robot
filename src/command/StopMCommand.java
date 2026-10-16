package command;

public class StopMCommand implements Command {

    @Override
    public void execute(robot.Robot robot) {
        robot.stopRecording();
    }

    @Override
    public boolean isRecordable() {
        return false;
    }
}
