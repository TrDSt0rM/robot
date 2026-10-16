package command;

import robot.Robot;

public interface Command {
    void execute(Robot robot);

    default boolean isRecordable() {
        return true;
    }

    default Command inverse() {
        throw new UnsupportedOperationException("Cette commande ne possède pas d'inverse.");
    }

}
