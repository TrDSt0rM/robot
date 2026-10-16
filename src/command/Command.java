package command;

import robot.Robot;

import java.util.List;

public interface Command {
    void execute(Robot robot);

    default boolean isRecordable() {
        return true;
    }

    default Command inverse() {
        throw new UnsupportedOperationException("Cette commande ne possède pas d'inverse.");
    }

    default List<Command> mergeWith(Command other) {
        return List.of(this, other);
    }

    default List<Command> mergeWithMove(MoveCommand other) {
        return List.of(this, other);
    }

    default List<Command> mergeWithDirection(DirectionCommand other) {
        return List.of(this, other);
    }

}
